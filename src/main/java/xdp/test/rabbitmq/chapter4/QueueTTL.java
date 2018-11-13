package xdp.test.rabbitmq.chapter4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

public class QueueTTL {
	
	private static final String EXCHANGE_NAME="exchange3";
	private static final String QUEUE_NAME="queue3";
	private static final String ROUTING_KEY="routingKet3";
	

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
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("x-expires", 1000);//1s
			
			channel.exchangeDeclare(EXCHANGE_NAME,"direct",false,false,map);
			channel.queueDeclare(QUEUE_NAME,false,false,false,null);//durable=true
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
			
		
			
//			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, false,
//					MessageProperties.PERSISTENT_TEXT_PLAIN,
//					"mandatory test".getBytes());
			
		}finally {
		
			channel.close();
			connection.close();
		}
		
		

	}
	
	@Test
	public  void test() throws Exception {
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
			
			
	
		
			
			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, false,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					"mandatory test".getBytes());
			
		}finally {
		
			channel.close();
			connection.close();
		}
		
		

	}

}
