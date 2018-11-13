package xdp.test.rabbitmq.chapter4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PublishConfirm {

	

	@Test
	public  void test() throws Exception {
		System.out.println("begin");
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

			try {
				//将信道设置为confirm模式
				channel.confirmSelect();
				channel.basicPublish("exchange1","routingKet1",
						MessageProperties.PERSISTENT_TEXT_PLAIN,
						"publish confirm test".getBytes());
			
				// 当Basic.Ack/.Nack 或者被中断时返回
				if(!channel.waitForConfirms()) {
					System.out.println("send message failed");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}finally {
			channel.close();
			connection.close();
		}
		System.out.println("end");
	}
	
}
