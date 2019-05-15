package agentCenter;

import java.io.Serializable;

public class Node implements Serializable
{
	private String alias;
	private String address;
	
	public Node()
	{
		
	}
	
	public Node(Node n)
	{
		this.alias = n.getAlias();
		this.address = n.getAddress();
		
	}
	
	public Node(String alias, String address) {
		super();
		this.alias = alias;
		this.address = address;
	}
	
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}
