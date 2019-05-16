package instantiableAgents;

import javax.ejb.Stateful;

import agent.Agent;
import message.ACLMessage;

@Stateful
public class Pong extends Agent
{
	@Override
	public void handleMessage(ACLMessage message)
	{
		System.out.println("APP INFO: Pong handling message on: " + aid.getHost().getAlias());
	}
}
