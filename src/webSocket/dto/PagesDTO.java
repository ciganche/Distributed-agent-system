package webSocket.dto;

import java.io.Serializable;
import java.util.ArrayList;

import crawlerUtils.Page;

@SuppressWarnings("serial")
public class PagesDTO implements Serializable
{
	ArrayList<Page> list = new ArrayList<Page>();

	
	public PagesDTO()
	{
		list = new ArrayList<Page>();
	}
	
	public PagesDTO(ArrayList<Page> list)
	{
		super();
		this.list = list;
	}

	public ArrayList<Page> getList() 
	{
		return list;
	}

	public void setList(ArrayList<Page> list)
	{
		this.list = list;
	}
	
	public void addUnique(ArrayList<Page> newList)
	{
		boolean found;
		for(Page p1 : newList)
		{
			found = false;
			for(Page p2 : list)
			{
				if(p1.getUrl().equals(p2.getUrl()))
				{
					found = true;
				}
			}	
			if(!found)
				list.add(p1);
		}
	}

	public void empty() 
	{
		list.clear();
	}
}
