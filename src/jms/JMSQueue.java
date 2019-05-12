package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import message.ACLMessage;

public class JMSQueue {

	public JMSQueue(ACLMessage msg) {
		System.out.println("ACLMessage getting sent to the JMSQueue");
		try {

			Context context = new InitialContext();

			ConnectionFactory cf = (ConnectionFactory) context
					.lookup("java:jboss/exported/jms/RemoteConnectionFactory");

			Queue queue = (Queue) context.lookup("java:jboss/exported/jms/queue/agentQueue");
			context.close();

			Connection connection = cf.createConnection();
			Session session = connection.createSession();

			connection.start();

			ObjectMessage objMsg = session.createObjectMessage(msg);
			MessageProducer msgProd = session.createProducer(queue);
			
			msgProd.send(objMsg);
			
			msgProd.close();
			session.close();
			connection.close();
			
			System.out.println("Thats all folks");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}