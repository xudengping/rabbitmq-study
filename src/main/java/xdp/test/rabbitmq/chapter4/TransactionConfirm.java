package xdp.test.rabbitmq.chapter4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TransactionConfirm {

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
				//将信道设置为事务模式
				channel.txSelect();
				channel.basicPublish("exchange1","routingKet1",
						MessageProperties.PERSISTENT_TEXT_PLAIN,
						"transaction confirm test".getBytes());
				// 测试回滚
				int i = 1/0;
				// 提交事务
				channel.txCommit();
			}catch(Exception e) {
				e.printStackTrace();
				// 回滚事务
				channel.txRollback();
			}
		}finally {
			channel.close();
			connection.close();
		}
		System.out.println("end");
	}
	
	@Test
	public  void testBatch() throws Exception {
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
				//将信道设置为事务模式
				channel.txSelect();
				for(int i=0;i<=10;i++) {
					channel.basicPublish("exchange1","routingKet1",
							MessageProperties.PERSISTENT_TEXT_PLAIN,
							("transaction confirm test"+i).getBytes());	
					channel.txCommit();
				}
				
//				// 测试回滚
//				int res = 1/0;
				// 提交事务
				
			}catch(Exception e) {
				e.printStackTrace();
				// 回滚事务
				channel.txRollback();
			}
		}finally {
			channel.close();
			connection.close();
		}
		System.out.println("end");
	}
	

}
