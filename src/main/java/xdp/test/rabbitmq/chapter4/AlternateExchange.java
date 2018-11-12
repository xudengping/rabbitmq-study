package xdp.test.rabbitmq.chapter4;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AlternateExchange {
	
	private static final String EXCHANGE_NAME="normalExchange";
	private static final String QUEUE_NAME="normalQueue";
	private static final String ROUTING_KEY="normalKey";
	

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
			map.put("alternate-exchange","myAE");
			
			channel.exchangeDeclare("normalExchange","direct",true,false,map);
			channel.exchangeDeclare("myAE","fanout",true,false,null);
			// 主队列
			channel.queueDeclare("normalQueue",true,false,false,null);
			channel.queueBind("normalQueue","normalExchange","normalKey");
			// 备份队列
			channel.queueDeclare("unrouteQueue",true,false,false,null);
			channel.queueBind("unrouteQueue","myAE","");

			channel.basicPublish(EXCHANGE_NAME, "rk", true,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					"alternate exchange test".getBytes());
			

		}finally {
			// 先不能关闭连接，否则接受不到返回的消息
			channel.close();
			connection.close();
		}
		
		

	}

}
