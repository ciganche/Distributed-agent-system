package jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AID;
import agent.Agent;
import agentCenter.AgentCenterAPI;
import message.ACLMessage;


@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/queue/mojQueue") })
public class JMSQueueListener implements MessageListener
{

	@EJB
	AgentCenterAPI center;
	
	@Override
	public void onMessage(Message msg) 
	{
		ObjectMessage objectMessage = (ObjectMessage) msg;
		try
		{
			ACLMessage message = (ACLMessage) objectMessage.getObject();
			
			if(message.getReceivers() == null || message.getReceivers().length == 0 )
			{
				System.out.println("PROCESS ABORTED: No recivers in the message.");
				return;
			}
			for(AID reciverAID : message.getReceivers())
			{
				//is thic agent center running this agent
				if(center.getAddress().equals(reciverAID.getHost().getAddress()))
				{		
					try
					{
						
						Agent a = center.findAgent(reciverAID);
						
						
						if(a == null)
						{
							throw new Exception("PROCESS ABORTED: Cannot access agent: " + reciverAID.getName());
						}
						
						a.handleMessage(message);
						System.out.println("APP INFO: Message handeled on: " + center.getAlias());
						
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else //the message is for an agent running on another node
				{
					ACLMessage newMessage = new ACLMessage(message, reciverAID);
					
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client.target("http://" + reciverAID.getHost().getAddress() +"/AgentTechnology/rest/messages/");
					target.request(MediaType.APPLICATION_JSON).post(Entity.entity(newMessage, MediaType.APPLICATION_JSON));

				}
			}
			
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}
	
}
