package flightcompany;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.*;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final UserServices userSer = new UserServices();

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

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                	try {
						JSONObject jo = new JSONObject(message);
	                    System.out.println(" [SERVER worker thread] Received: " + jo.toString());                    
	                    switch(jo.getString("command")) {
		            		case "registration":
		            			if(userSer.registrationRequest(jo.getString("name"),jo.getString("surname"),jo.getString("nickname"),jo.getString("password")))
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
	                    }
					} catch (JSONException e) {
						e.printStackTrace();
					}                   
                } catch (RuntimeException e) {
                    System.out.println(" [SERVER worker thread] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8")); // (exchange,routingKey,properties,body)
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); // (deliveryTag, multiple) acknowledge one or several received messages
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
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
}
