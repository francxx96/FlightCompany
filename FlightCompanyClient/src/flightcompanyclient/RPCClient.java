package flightcompanyclient;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import javax.swing.JOptionPane;

import org.json.JSONException;

/**
 * Use the open-source message-broker RabbitMQ to build an RPC client
 * @author Emilio, Francesco
 */
public class RPCClient implements AutoCloseable {
	private static String RABBITMQ_URI = "amqp://hksxizgg:IM7wz6lQXcPECyKWbr5DMETsP9RQs06G@reindeer.rmq.cloudamqp.com/hksxizgg";
    private final String EXCHANGE_NAME = "notifications";
    private final String requestQueueName = "rpc_queue";
    private Connection connection; 
    private Channel channel;

    public RPCClient() throws IOException, TimeoutException, JSONException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory(); // "factory" class to facilitate opening a Connection to an AMQP broker 	
        //factory.setHost("localhost"); // "guest" by default, limited to localhost connections
        
        try {
			factory.setUri(RABBITMQ_URI);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
        
        connection = factory.newConnection();
        channel = connection.createChannel();
    }
    
    
    /**
     * Subscribe the user to notifications of flight delays or offers
     * @throws IOException
     */
    public void subscribeNotification() throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            ClientGUI.showNotify(message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    
    
    /**
     * Sends an RPC request and blocks until the answer is received
     * @param message to send
     * @return the response provided by the server
     * @throws IOException, InterruptedException
     */
    public String call(String message) throws IOException, InterruptedException {
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

    
    /**
     * Close the connection and all its channels
     */
    public void close() throws IOException {
        connection.close();
    }
}
