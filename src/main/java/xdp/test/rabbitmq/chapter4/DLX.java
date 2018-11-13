package xdp.test.rabbitmq.chapter4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DLX {

	

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
			

			
			channel.exchangeDeclare("exchange.dlx","direct",true);
			channel.exchangeDeclare("exchange.normal","fanout",true);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("x-message-ttl", 5000);//设置消息的过期时间
			map.put("x-dead-letter-exchange", "exchange.dlx");//配置死信队列
			map.put("x-dead-letter-routing-key", "deadKey");//设置死信队列的路由建


			channel.queueDeclare("queue.normal",true,false,false,map);//durable=true
			channel.queueBind("queue.normal", "exchange.normal", "rk");
			channel.queueDeclare("queue.dlx",true,false,false,null);//durable=true
			channel.queueBind("queue.dlx", "exchange.dlx", "deadKey");

			channel.basicPublish("exchange.normal","rk",
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					"dlx".getBytes());
			
		
			

			
		}finally {
		
			channel.close();
			connection.close();
		}
		
		

	}
	


}
