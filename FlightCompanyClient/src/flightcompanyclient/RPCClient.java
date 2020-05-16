package flightcompanyclient;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;


public class RPCClient implements AutoCloseable {

    private Connection connection; 
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String nickname;

    public RPCClient() throws IOException, TimeoutException, JSONException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory(); // "factory" class to facilitate opening a Connection to an AMQP broker
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        
        execute();
    }

    
    
    public static void main(String[] argv) {
        try {
        	RPCClient rpc = new RPCClient();
        } catch (IOException | TimeoutException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    private void execute() throws JSONException, IOException, InterruptedException {
    	Scanner s = new Scanner(System.in);
    	String response = "";
    	
    	while(true) {
        	JSONObject jo = new JSONObject();
        	System.out.println(" Choose one of the following operations:");
        	System.out.println(" reg; login; logout; routes;book;cancel;charge;add;remove;delay;deal");
        	switch(s.nextLine()) {
        		case "reg": 
        			System.out.println(" Insert name, surname, nickname, password, admin (yes,no): ");
					jo.put("command", "registration");
					jo.put("name", s.nextLine());
					jo.put("surname", s.nextLine());
        			jo.put("nickname", s.nextLine());
        			jo.put("password", s.nextLine());  
        			jo.put("admin", s.nextLine()); 
        			break;
        		case "login": 
        			System.out.println(" Insert nickname and password: ");				
					jo.put("command", "login");
        			jo.put("nickname", s.nextLine());
        			jo.put("password", s.nextLine());  
        			break;
        		case "logout":
					jo.put("command", "logout");
        			jo.put("nickname", nickname); 
        			break;
        		case "routes":
        			System.out.println(" Insert depCity, arrCity and depTime(yyyy-MM-dd HH:mm): ");
        			jo.put("command", "searchRoutes");
        			jo.put("depCity", s.nextLine());
        			jo.put("arrCity", s.nextLine()); 
        			jo.put("depTime", s.nextLine()); //LocalDateTime
        			break;
        		case "book":
        			System.out.println(" Insert flightId: ");
        			jo.put("command", "bookFlight");
        			jo.put("flightId", s.nextLine());
        			jo.put("nickname", nickname);
        			break;
        		case "cancel":
        			System.out.println(" Insert flightId: ");
        			jo.put("command", "cancelFlight");
        			jo.put("flightId", s.nextLine());
        			jo.put("nickname", nickname);
        			break;
        		case "charge":
        			System.out.println(" Insert amount: ");
        			jo.put("command", "charge");
        			jo.put("amount", s.nextLine());
        			jo.put("nickname", nickname);
        			break;
        		case "add":
					System.out.println(" Insert flightId, planeModel(BOEING_737,AIRBUS_A320,EMBRAER), depCity, arrCity and depTime(yyyy-MM-dd HH:mm): ");
					jo.put("command", "addFlight");
        			jo.put("flightId", s.nextLine());
        			jo.put("planeModel", s.nextLine());
					jo.put("depCity", s.nextLine());
					jo.put("arrCity", s.nextLine()); 
					jo.put("depTime", s.nextLine());
        			jo.put("nickname", nickname);
					break;
        		case "remove":
        			System.out.println(" Insert flightId: ");
        			jo.put("command", "removeFlight");
        			jo.put("flightId", s.nextLine());
        			jo.put("nickname", nickname);
        			break;
        		case "delay":
        			System.out.println(" Insert flightId and minutes: ");
        			jo.put("command", "putDelay");
        			jo.put("flightId", s.nextLine());
        			jo.put("minutes", s.nextLine());
        			jo.put("nickname", nickname);
        			break;
        		case "deal":
        			System.out.println(" Insert flightId and dealPerc: ");
        			jo.put("command", "putDeal");
        			jo.put("flightId", s.nextLine());
        			jo.put("dealPerc", s.nextLine());
        			jo.put("nickname", nickname);
        			break;
        	}
        	
			response = call(jo.toString());
			if(response.equals("User successfully logged in!")) 
				nickname = jo.getString("nickname");
            System.out.println(" [CLIENT] Got '" + response + "'");
    	}
    }
    
    
    private String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();	// non-durable, exclusive, autodelete queue with a generated name
        AMQP.BasicProperties props = new AMQP.BasicProperties		// other properties for the message
                .Builder()
                .correlationId(corrId)		// correlate RPC responses with requests
                .replyTo(replyQueueName)	// name a callback queue
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));	// (exchange,routingKey,properties,body)

        // A Queue that additionally supports operations that wait for the queue to become non-empty when retrieving an element, 
        // and wait for space to become available in the queue when storing an element. 
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {	//  match a response with a request
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });	// (queue,autoAck,callback,consumerTag)

        String result = response.take();
        channel.basicCancel(ctag);	// Cancel a consumer
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
