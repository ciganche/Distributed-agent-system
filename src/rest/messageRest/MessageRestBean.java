package rest.messageRest;

import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agent.AID;
import agentCenter.AgentCenterAPI;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import webSocket.LoggerUtil;

@Path("/messages")
@LocalBean
@Stateless
@AccessTimeout(-1)
public class MessageRestBean 
{
	
	@EJB
	AgentCenterAPI center;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Performative[] getPerformatives()
	{
		return Performative.values();
	}
	
	//sends a message to a running agent
	@GET
	@Path("/test")
	public void test()
	{
		ACLMessage message = new ACLMessage();
		
		if(center.getAgents().size() == 0)
		{
			LoggerUtil.log("PROCESS ABORTED: There are no running agents" );
		}
		
		AID randomReciver = center.getAgents().get(0).getAid();
		AID randomReciver2 = center.getAgents().get(1).getAid();
		
		System.out.println("* * * Agent1: " + randomReciver.getName() + " - " + randomReciver.getType().getName());
		System.out.println("* * * Agent2: " + randomReciver2.getName() + " - " + randomReciver2.getType().getName());
		
		AID recivers[]= {randomReciver,randomReciver2};
		message.setReceivers(recivers);
		
		new JMSQueue(message);	
	}
	
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(ACLMessage message)
	{
		new JMSQueue(message);
	}
	
}
