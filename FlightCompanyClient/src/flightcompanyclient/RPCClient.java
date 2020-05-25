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

/**
 * Use the open-source message-broker RabbitMQ to build an RPC client
 * @author Emilio, Francesco
 */
public class RPCClient implements AutoCloseable {

    private Connection connection; 
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException, JSONException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory(); // "factory" class to facilitate opening a Connection to an AMQP broker
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }
    
    
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

    public void close() throws IOException {
        connection.close();
    }
}
