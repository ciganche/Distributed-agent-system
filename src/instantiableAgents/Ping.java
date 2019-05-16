package instantiableAgents;

import javax.ejb.Stateful;

import agent.Agent;
import message.ACLMessage;

@Stateful
public class Ping extends Agent
{
	@Override
	public void handleMessage(ACLMessage message)
	{
		System.out.println("APP INFO: Ping handling message on: " + aid.getHost().getAlias());
	}
}
