package agent;

import java.io.Serializable;

import message.ACLMessage;

public interface AgentAPI extends Serializable
{
	
	public void init(AID aid);
	
	public void stop();
	
	public void handleMessage(ACLMessage message);
	
	public void setAid(AID aid);
	
	public AID getAid();
}
