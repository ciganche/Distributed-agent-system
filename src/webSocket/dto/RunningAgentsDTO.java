package webSocket.dto;

import java.util.ArrayList;

import agent.AID;
import agent.Agent;

public class RunningAgentsDTO 
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
