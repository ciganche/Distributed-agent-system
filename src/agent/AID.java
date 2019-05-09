package agent;
import java.io.Serializable;

import agentCenter.Node;

public class AID implements Serializable
{
	private String name;

	private Node host;
	
	private AgentType type;
	
	public AID()
	{
		
	}
	
	
	public AgentType getType() {
		return type;
	}


	public void setType(AgentType type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Node getHost() {
		return host;
	}

	public void setHost(Node host) {
		this.host = host;
	}
	
}
