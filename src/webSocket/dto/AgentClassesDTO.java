package webSocket.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import agent.AgentType;

@SuppressWarnings("serial")
public class AgentClassesDTO implements Serializable
{
	private ArrayList<String> list;
	
	public AgentClassesDTO(Collection<ArrayList<AgentType>> newCollection)
	{
		ArrayList<ArrayList<AgentType>> list = new ArrayList<ArrayList<AgentType>>(newCollection); 
		ArrayList<String> uniqueTypes = new ArrayList<String>();
		
		for( int i = 0 ; i < list.size() ; i ++ )
		{
			for( int j = 0 ; j < list.get(i).size() ; j ++)
			{
				String temp = list.get(i).get(j).getName();
				if(!uniqueTypes.contains(temp))
				{
					uniqueTypes.add(temp);
				}
			}
		}
		this.list = uniqueTypes;
	}
	
	public ArrayList<String> getList()
	{
		return list;
	}
	
	public AgentClassesDTO(ArrayList<String> list) 
	{
		super();
		this.list = list;
	}

	public void setList(ArrayList<String> list)
	{
		this.list = list;
	}	
}
