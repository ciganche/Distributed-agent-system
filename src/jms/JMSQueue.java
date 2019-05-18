package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import message.ACLMessage;

public class JMSQueue {

	public JMSQueue(ACLMessage message)
	{
		try 
		{
			Context context = new InitialContext();
			
			ConnectionFactory cf = (ConnectionFactory) context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
			Queue queue = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();
			
			 Connection connection;
			try
			{
				connection = cf.createConnection("user1", "user1");
				Session session = connection.createSession();
				
				connection.start();
				ObjectMessage objectMessage = session.createObjectMessage(message);
				MessageProducer messageProducer = session.createProducer(queue);
				
				messageProducer.send(objectMessage);
				
				messageProducer.close();
				session.close();
				connection.close();
				
			}
			catch (JMSException e)
			{
			
				e.printStackTrace();
			}
			 
			 
		} 
		catch (NamingException e) 
		{
			e.printStackTrace();
		}
		
	}
}