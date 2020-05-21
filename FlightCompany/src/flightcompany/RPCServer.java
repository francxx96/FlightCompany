package flightcompany;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import com.rabbitmq.client.*;


public class RPCServer {
	
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final UserServices userSer = new UserServices();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			
    public static void main(String[] argv) throws Exception {
    	ConnectionFactory factory = new ConnectionFactory(); // "factory" class to facilitate opening a Connection to an AMQP broker
        factory.setHost("localhost");
        
        try (Connection connection = factory.newConnection();
        		Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null); // (queue, passive,durable,exclusive,autoDelete,arguments)
            channel.queuePurge(RPC_QUEUE_NAME); // Purges the contents of the given queue (all of its messages deleted)

            channel.basicQos(1); // tells RabbitMQ not to give more than one message to a worker at a time (in order to spread the load equally over multiple servers we need)

            System.out.println(" [SERVER worker thread] Awaiting RPC requests ... ");
            
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {	// (String,Delivery) a callback that will do the work and send the response back
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties	// other properties for the message
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())	// Useful to correlate RPC responses with requests
                        .build();

                String response = "";
                LocalDateTime dateTime;
    			AirportCity depCity;
    			AirportCity arrCity;

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
					JSONObject jo = new JSONObject(message);
                    System.out.println(" [SERVER worker thread] Received: " + jo.toString());                    
                    switch(jo.getString("command")) {
	            		case "registration":
	            			boolean admin;
	            			if(jo.getString("admin").equals("yes"))
	            				admin = true;
	            			else
	            				admin = false;
	            			
	            			response = userSer.registrationRequest(jo.getString("name"),jo.getString("surname"),jo.getString("nickname"),jo.getString("password"), admin);
	            			break;
	            		case "login":
	            			response = userSer.loginRequest(jo.getString("nickname"),jo.getString("password"));
	            			break;
	            		case "logout":
	            			response = userSer.logoutRequest(jo.getString("nickname")); 
	            			break;
	            		case "searchRoutes": 
	            			dateTime = LocalDateTime.parse(jo.getString("depTime"), formatter);
	            			depCity = AirportCity.valueOf(jo.getString("depCity").toUpperCase());
	            			arrCity = AirportCity.valueOf(jo.getString("arrCity").toUpperCase());
	            			response = userSer.searchRoutes(depCity,arrCity,dateTime);
	            			break;
	            		case "bookFlight":
	            			response = userSer.bookFlight(jo.getString("flightId"), jo.getString("nickname"));
	            			break;
	            		case "cancelFlight":
	            			response = userSer.cancelFlight(jo.getString("flightId"), jo.getString("nickname"));
	            			break;
	            		case "charge":
	            			response = userSer.chargeMoney(Double.parseDouble(jo.getString("amount")), jo.getString("nickname"));
	            			break;
	            		case "addFlight": 
	            			dateTime = LocalDateTime.parse(jo.getString("depTime"), formatter);
	            			depCity = AirportCity.valueOf(jo.getString("depCity").toUpperCase());
	            			arrCity = AirportCity.valueOf(jo.getString("arrCity").toUpperCase());
	            			AirplaneModel planeModel = AirplaneModel.valueOf(jo.getString("planeModel").toUpperCase());
	            			
	            			response = userSer.addFlight(jo.getString("flightId"), planeModel, depCity, arrCity, dateTime, jo.getString("nickname"));
	            			break;
	            		case "removeFlight":
	            			response = userSer.removeFlight(jo.getString("flightId"), jo.getString("nickname")); 
	            			break;
	            		case "putDelay":
	            			response = userSer.putDelay(jo.getString("flightId"), jo.getInt("minutes"), jo.getString("nickname"));
	            			break;
	            		case "putDeal":
	            			response = userSer.putDeal(jo.getString("flightId"), Double.parseDouble(jo.getString("dealPerc")), jo.getString("nickname")); 
	            			break;
                    }            
                } catch (RuntimeException | JSONException e) {
                    System.out.println(" [SERVER worker thread] " + e);
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8")); // (exchange,routingKey,properties,body)
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); // (deliveryTag, multiple) acknowledge one or several received messages
                    
                    System.out.println(Utilities.getUsers());
                    System.out.println(Utilities.getAirports());
                    System.out.println(Utilities.getFlights());
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { })); // (queue,autoAck,callback,consumerTag)
            
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                        System.out.println(" [SERVER] Call Executed");
                    } catch (InterruptedException e) {
                    	System.out.println(e);
                    }
                }
            }
        }
    }
}
