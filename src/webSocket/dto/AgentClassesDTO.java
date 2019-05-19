package webSocket.dto;

import java.util.ArrayList;

public class AgentClassesDTO 
{
	private ArrayList<String> list;
	
	public AgentClassesDTO()
	{
		
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
