package instantiableAgents;

import java.io.Serializable;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import webSocketLogger.LoggerUtil;

@Stateful
public class Ping extends Agent implements Serializable
{
	@Override
	public void handleMessage(ACLMessage message)
	{

		
		switch(message.getPerformative())
		{
			case REQUEST:
				ACLMessage response = new ACLMessage();
				response.setPerformative(Performative.REQUEST);
				response.setSender(this.getAid());
				response.setReceivers(new AID[]{message.getSender()});
				response.setConversationID(message.getConversationID());
				
				new JMSQueue(response);
			break;
			
			case INFORM:
				LoggerUtil.log("PING: Pong has successfully responded from: {" + message.getSender().getHost().getAlias() + "}");
			break;
			
			default:
				LoggerUtil.log("PING: handling message on: {" + aid.getHost().getAlias() + "}");

		}
	}
}
