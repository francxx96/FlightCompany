package flightcompany;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
	            			if(userSer.registrationRequest(jo.getString("name"),jo.getString("surname"),jo.getString("nickname"),jo.getString("password"), admin))
	            				response = "User successfully registered!";
	            			else
	            				response = "Please, choose a different username";
	            			break;
	            		case "login": 
	            			if(userSer.loginRequest(jo.getString("nickname"),jo.getString("password")))
	            				response = "User successfully logged in!";
	            			else
	            				response = "Username not present";
	            			break;
	            		case "logout":
	            			if(userSer.logoutRequest(jo.getString("nickname")))
	            				response = "User successfully logged out!";
	            			else
	            				response = "Username not present";
	            			break;
	            		case "searchRoutes": 
	            			dateTime = LocalDateTime.parse(jo.getString("depTime"), formatter);
	            			depCity = AirportCity.valueOf(jo.getString("depCity").toUpperCase());
	            			arrCity = AirportCity.valueOf(jo.getString("arrCity").toUpperCase());
	            			response = flightsToString(userSer.searchRoutes(depCity,arrCity,dateTime));
	            			break;
	            		case "bookFlight":
	            			if(userSer.bookFlight(jo.getString("flightId"), jo.getString("nickname")))
	            				response = "User successfully booked!";
	            			else
	            				response = "Please, choose another flight";
	            			break;
	            		case "cancelFlight":
	            			if(userSer.cancelFlight(jo.getString("flightId"), jo.getString("nickname")))
	            				response = "User successfully cancelled!";
	            			else
	            				response = "Please, choose another flight";
	            			break;
	            		case "charge":
	            			if(userSer.chargeMoney((Double) jo.get("amount"), jo.getString("nickname")))
	            				response = "Account updated successfully!";
	            			else
	            				response = "Unable to update account";
	            			break;
	            		case "addFlight": 
	            			dateTime = LocalDateTime.parse(jo.getString("depTime"), formatter);
	            			depCity = AirportCity.valueOf(jo.getString("depCity").toUpperCase());
	            			arrCity = AirportCity.valueOf(jo.getString("arrCity").toUpperCase());
	            			AirplaneModel planeModel = AirplaneModel.valueOf(jo.getString("planeModel").toUpperCase());
	            			if(userSer.addFlight(jo.getString("flightId"), planeModel, depCity, arrCity, dateTime, jo.getString("nickname")))
	            				response = "Flight successfully added!";
	            			else
	            				response = "Unable to add the flight";
	            			break;
	            		case "removeFlight":
	            			if(userSer.removeFlight(jo.getString("flightId"), jo.getString("nickname")))
	            				response = "Flight successfully removed!";
	            			else
	            				response = "Unable to remove the flight";
	            			break;
	            		case "putDelay":
	            			if(userSer.putDelay(jo.getString("flightId"), jo.getInt("minutes"), jo.getString("nickname")))
	            				response = "Delay successfully added!";
	            			else
	            				response = "Unable to add the delay";
	            			break;
	            		case "putDeal":
	            			if(userSer.putDeal(jo.getString("flightId"), (Double) jo.get("dealPerc"), jo.getString("nickname")))
	            				response = "Deal successfully added!";
	            			else
	            				response = "Unable to add the deal";
	            			break;
                    }            
                } catch (RuntimeException | JSONException e) {
                    System.out.println(" [SERVER worker thread] " + e);
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8")); // (exchange,routingKey,properties,body)
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); // (deliveryTag, multiple) acknowledge one or several received messages
                    
                    System.out.println(Utilities.getUsers());
                    //System.out.println(Utilities.getAirports());
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
    
    
    public static String flightsToString(List<List<Flight>> routes) {
    	String routesString = "The following flights are registered: \n\n";
    	
    	for(List<Flight> route : routes) {
    		routesString += "Route consisting of " + route.size() + " flights:\n";
    		for(Flight f : route) {
    			routesString += f.toString() + "\n";
    		}
    		routesString += "\n---------------------------------------------------------------------\n";
    	}
    	
    	return routesString;
    }
    
    
}
