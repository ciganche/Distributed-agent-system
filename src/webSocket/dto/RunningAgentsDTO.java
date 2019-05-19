package webSocket.dto;

import java.util.ArrayList;

import agent.AID;
import agent.Agent;

public class RunningAgentsDTO 
{
	private ArrayList<AID> list = new ArrayList<AID>();

	
	public RunningAgentsDTO()
	{
		super();
	}

	public RunningAgentsDTO(ArrayList<Agent> agentList)
	{
		super();
		for(Agent a : agentList)
		{
			list.add(a.getAid());
		}
	}

	public ArrayList<AID> getList()
	{
		return list;
	}

	public void setList(ArrayList<AID> list)
	{
		this.list = list;
	}
	
	
}
