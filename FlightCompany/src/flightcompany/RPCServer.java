package flightcompany;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import com.rabbitmq.client.*;

/**
 * Use the open-source message-broker RabbitMQ to build a scalable RPC-style server
 * @author Emilio, Francesco
 */
public class RPCServer {
	private static String RABBITMQ_URI = "amqp://hksxizgg:IM7wz6lQXcPECyKWbr5DMETsP9RQs06G@reindeer.rmq.cloudamqp.com/hksxizgg";
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final String EXCHANGE_NAME = "notifications";
    private static final UserServices userSer = new UserServices();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			
    public static void main(String[] argv) {
    	ConnectionFactory factory = new ConnectionFactory(); // "factory" class to facilitate opening a Connection to an AMQP broker
    	//factory.setHost("localhost");	// "guest" by default, limited to localhost connections
    	try {
			factory.setUri(RABBITMQ_URI);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
    	
        try (Connection connection = factory.newConnection();
        		Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null); // (queue, passive,durable,exclusive,autoDelete,arguments)
            channel.queuePurge(RPC_QUEUE_NAME); // Purges the contents of the given queue (all of its messages deleted)
            channel.basicQos(1); // tells RabbitMQ not to give more than one message to a worker at a time (in order to spread the load equally over multiple servers we need)
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout"); // Actively declare a non-autodelete, non-durable exchange of fanout type for notification
            
            System.out.println(" [SERVER thread] Awaiting RPC requests ... ");
            
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {	// (String,Delivery) a callback that will do the work and send the response back
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties	// other properties for the message
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())	// Useful to correlate RPC responses with requests
                        .build();

                String response = "", notification;
                LocalDateTime dateTime;
    			AirportCity depCity;
    			AirportCity arrCity;

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
					JSONObject jo = new JSONObject(message);
                    System.out.println(" [SERVER thread] Received: " + jo.toString());                    
                    switch(jo.getString("command")) {
	            		case "registration":
	            			boolean admin = Boolean.parseBoolean(jo.getString("admin"));
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
	            		case "allFlights":
	            			response = userSer.printFlights();
	            			break;
	            		case "bookFlight":
	            			response = userSer.bookFlight(jo.getString("flightId"), jo.getString("nickname"));
	            			break;
	            		case "bookedFlight":
	            			response = userSer.bookedFlight(jo.getString("nickname"));
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
	            			if(!response.contains("[Error]")) {
	            				notification = "Delay on flight " + jo.getString("flightId") + " of " + jo.getInt("minutes") + " minutes";
		            			channel.basicPublish(EXCHANGE_NAME, "", null, notification.getBytes("UTF-8"));
	            			}
	            			break;
	            		case "putDeal":
	            			response = userSer.putDeal(jo.getString("flightId"), Double.parseDouble(jo.getString("dealPerc")), jo.getString("nickname")); 
	            			if(!response.contains("[Error]")) {
	            				notification = "Deal on flight " + jo.getString("flightId") + " of " + jo.getString("dealPerc") + "%";
	            				channel.basicPublish(EXCHANGE_NAME, "", null, notification.getBytes("UTF-8"));
	            			}
	            			break;
                    }            
                } catch (RuntimeException | JSONException e) {
                    System.out.println(" [SERVER thread] " + e);
                    response = "[Error] User not authorized";
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8")); // (exchange,routingKey,properties,body)
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); // (deliveryTag, multiple) acknowledge that the received message has been processed
                    
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
        } catch (IOException | TimeoutException e1) {
			System.out.println(e1);
		}
    }
}
