package agent;

import javax.ejb.Stateful;

import message.ACLMessage;

@Stateful
public class Agent implements AgentAPI
{

	protected AID aid;
	
	public Agent()
	{
		
	}
	
	public Agent(AID aid) {
		super();
		this.aid = aid;
	}

	@Override
	public void init(AID aid) 
	{	
		this.aid = aid;
	}

	@Override
	public void stop() 
	{
		System.out.println("APP INFO: Stopping running agent: " + aid.getName() + ", from it's host: " + aid.getHost().getAlias() + " - " + aid.getHost().getAddress());
	}

	@Override
	public void setAid(AID aid) 
	{
		this.aid = aid;
	}

	@Override
	public AID getAid() {
		return aid;
	}

	@Override
	public void handleMessage(ACLMessage message) 
	{
		// TODO Auto-generated method stub	
	}

}
