package instantiableAgents;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import webSocket.LoggerUtil;

@Stateful
public class Wiki extends Agent
{
	@Override
	public void handleMessage(ACLMessage message)
	{
		switch(message.getPerformative())
		{
			case REQUEST:

			break;
			
			default:
				LoggerUtil.log("REQUEST is the only method this agent supports");

		}
	}
}
