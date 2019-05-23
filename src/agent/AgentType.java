package agent;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AgentType implements Serializable
{
	private String name;
	private String module;
	
	public AgentType(String name, String module) 
	{
		this.name = name;
		this.module = module;
	}

	public AgentType() 
	{
	}

	public AgentType(AgentType type) {
		name = type.getName();
		module = type.getModule();
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getModule() 
	{
		return module;
	}

	public void setModule(String module) 
	{
		this.module = module;
	}

	@Override
	public String toString() 
	{
		return "AgentType [name=" + name + ", module=" + module + "]";
	}

}
