package instantiableAgents;

import java.io.Serializable;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import webSocket.LoggerUtil;

@Stateful
public class Pong extends Agent implements Serializable
{
	@Override
	public void handleMessage(ACLMessage message)
	{
		switch(message.getPerformative())
		{
			case REQUEST:
				
				ACLMessage response = new ACLMessage();
				response.setPerformative(Performative.INFORM);
				response.setSender(this.getAid());
				response.setReceivers(new AID[]{message.getSender()});
				response.setConversationID(message.getConversationID());
				LoggerUtil.log("PONG: Ping pinged me from: {" + message.getSender().getHost().getAlias() + "}");

				new JMSQueue(response);
			break;
			
			default:
				LoggerUtil.log("PONG: handling message on: {" + aid.getHost().getAlias() + "}");

		}
	}
}
