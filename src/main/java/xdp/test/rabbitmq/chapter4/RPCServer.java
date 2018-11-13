package xdp.test.rabbitmq.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * create by dengpingxu on 2018/11/13
 * version 1.0.0
 */
public class RPCServer {

    private static final  String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws Exception {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");
            factory.setHost("localhost");
            factory.setPort(5672);
            connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(RPC_QUEUE_NAME,false,false,false,null);//durable=false
            channel.basicQos(1);
            System.out.println("[x] awaiting RPC request");

            Consumer consumer = new DefaultConsumer(channel){

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("接收到消息，开始处理");
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(properties.getCorrelationId()).build();
                    String response = "";
                    try{

                        String message = new String(body,"UTF-8");
                        int i = Integer.parseInt(message);
                        System.out.println("[.] fib("+message+")");
                        response += fib(i);
                    }catch(Exception e){
                        System.out.println("[.] "+e.toString());
                    }finally {
                        // 将返回消息发送到客户端回调队列
                        channel.basicPublish("",properties.getReplyTo(),replyProps,response.getBytes());
                        channel.basicAck(envelope.getDeliveryTag(),false);
                    }
                }
            };

            System.out.println("消费端启动。。。");
            channel.basicConsume(RPC_QUEUE_NAME,false,consumer);

        }finally {
            connection.close();
        }
    }

    private static int fib(int n){

        if(n == 0) return 0;
        if(n == 1) return 1;
        return fib(n-1) + fib(n-2);
    }
}
