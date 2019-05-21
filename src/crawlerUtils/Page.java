package crawlerUtils;

import java.io.Serializable;

public class Page implements Serializable
{
	private String name;
	private String description;
	private String url;
	
	public Page()
	{
		
	}
	
	public Page(String naziv)
	{
		url = naziv;
	}
	
	public Page(String name, String description, String url)
	{
		super();
		this.name = name;
		this.description = description;
		this.url = url;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url) 
	{
		this.url = url;
	}

	@Override
	public String toString()
	{
		name = name.replaceAll("\t", " ");
		description = description.replaceAll("\t", " ");
	
		name = name.replaceAll("\n", " ");
		description = description.replaceAll("\n", " ");
		
		return (name + "\t" + description + "\t" + url);
	}
	
}

