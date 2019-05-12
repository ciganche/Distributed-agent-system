package agent;

import javax.ejb.Stateful;

import message.ACLMessage;

@Stateful
public class Agent implements AgentAPI
{

	protected AID aid;
	
	@Override
	public void init(AID aid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAid(AID aid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AID getAid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

}
