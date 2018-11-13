package xdp.test.rabbitmq.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * create by dengpingxu on 2018/11/13
 * version 1.0.0
 */
public class RPCClient {
    private Connection connection;
    private Channel channel;
    private static final  String RPC_QUEUE_NAME = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("localhost");
        factory.setPort(5672);
        connection = factory.newConnection();
        channel = connection.createChannel();

        // 获取默认的队列名
        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName,true,consumer);

    }

    public String call(String message) throws IOException, InterruptedException {

        String response = null;
        String corrId = UUID.randomUUID().toString();
        // 设置回调队列
        AMQP.BasicProperties props = new AMQP.BasicProperties()
                .builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        // 将请求发送到rpc队列中
        channel.basicPublish("",RPC_QUEUE_NAME,props,message.getBytes());

        System.out.println("循环获取回调队列，获取指定客户端回复");
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if(delivery.getProperties().getCorrelationId().equals(corrId)){
                response = new String(delivery.getBody());
                break;
            }
        }
        return response;
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] args) throws Exception {

        RPCClient client = new RPCClient();
        String response = client.call("30");

        System.out.println("[.] Got fib response="+response);
        client.close();;
    }


}
