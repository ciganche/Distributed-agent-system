package webSocket.dto;

import java.io.Serializable;
import java.util.ArrayList;
import agent.Agent;

@SuppressWarnings("serial")
public class RunningAgentsDTO implements Serializable
{
	private ArrayList<Agent> list = new ArrayList<Agent>();

	
	public RunningAgentsDTO()
	{
		super();
	}

	public RunningAgentsDTO(ArrayList<Agent> agentList)
	{
		super();
		for(Agent a : agentList)
		{
			list.add(a);
		}
	}

	public ArrayList<Agent> getList()
	{
		return list;
	}

	public void setList(ArrayList<Agent> list)
	{
		this.list = list;
	}
	
	
}
