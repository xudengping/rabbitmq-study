package xdp.test.rabbitmq.chapter4;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Mondatory {
	
	private static final String EXCHANGE_NAME="exchange1";
	private static final String QUEUE_NAME="queue1";
	private static final String ROUTING_KEY="routingKet1";
	

	public static void main(String[] args) throws Exception {
		Connection connection = null;
		Channel channel = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("guest");
			factory.setPassword("guest");
			factory.setVirtualHost("/");
			factory.setHost("localhost");
			factory.setPort(5672);
			
			connection = factory.newConnection();
			
			channel = connection.createChannel();
			
			// mandotory = true 消息发送失败会返回给生产者
			channel.basicPublish(EXCHANGE_NAME, "", true,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					"mandatory test".getBytes());
			
			channel.addReturnListener(new ReturnListener() {
				
				public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, BasicProperties arg4, byte[] body)
						throws IOException {
					System.out.println("Basic.Return 返回结果是："+new String(body));
					
				}
			});
		}finally {
			// 先不能关闭连接，否则接受不到返回的消息
//			channel.close();
//			connection.close();
		}
		
		

	}

}
