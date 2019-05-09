package agentCenter;



import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import config.ReadConfigUtil;



@Startup
@Singleton
@Lock(LockType.READ)
public class AgentCenter 
{
	private String alias; //primary key
	private String address; //IP address
	private String masteraddress; //IP addrress of the master node
	private ArrayList<Node> nodes;
	ResteasyClient client = new ResteasyClientBuilder().build();
	//private HashMap<AID, Agent> agents = new HashMap<AID, Agent>();

	
	public AgentCenter()
	{
		
	}
	
	
	@PostConstruct
	public void Init() 
	{
		//load APP INFO from a config file
		 ReadConfigUtil configReader = new ReadConfigUtil();
		 ArrayList<String> params = configReader.getParams();
		 
		 if(params.size() != 3)
		 {
			 System.out.println("PROCESS ABORTED: Agent center terminated - illegal config.csv");
			 System.exit(-1);
		 }
		 alias = params.get(0);
		 address = params.get(1);
		 
		 //if a masterNode, a new empty register of nodes is created
		 if(alias.equals("master"))
		 {
			 masteraddress = address;
			 nodes = new ArrayList<Node>(); 
			 addNode(this); //insert itself to the node list
			 System.out.println("APP INFO: The master node has registered.");

		 }
			 
		 //if a non-masterNode, it contacts the master node to register and get the list of all nodes
		 else
		 {
			 masteraddress = params.get(2);
			 registerAtMasterNode();
			 System.out.println("APP INFO: A non-master node has registered:" + alias + ".");

		 }
		 
	}
	
	
	@PreDestroy
	public void cleanUp() 
	{
		if(!alias.equals("master"))
		{
			System.out.println("APP INFO: PreDestroy");
			ResteasyWebTarget target = client.target("http://" + masteraddress +"/AgentTechnology/rest/agentCenter/node/" + alias);
			Response response = target.request().delete();
			System.out.println("APP INFO: Non-master node " + alias + " is shutting down.");
		}
	}
	
	
	private void registerAtMasterNode() 
	{
        ResteasyWebTarget target = client.target("http://" + masteraddress +"/AgentTechnology/rest/agentCenter/node");
        try
        {
            Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(this, MediaType.APPLICATION_JSON));
            this.nodes = response.readEntity(new GenericType<ArrayList<Node>>(){});
            System.out.println("APP INFO: Non-master node recived the list of all nodes from master. Size: " + nodes.size());    	
        }
        catch(Exception e)
        {
        	System.out.println("PROCESS ABORTED: The new agent center cannot connect to the cluster.");
        }
        
    }
	
	
	@Schedule(hour="*", minute="*", second="*/15")
	@AccessTimeout(-1)
	private void heartbratProtocol()
	{
		for(Node n : nodes)
		{
			if(n.getAlias().equals(this.alias))
				continue;
			
			ResteasyWebTarget target = client.target("http://" + n.getAddress() +"/AgentTechnology/rest/agentCenter/node");
			
			System.out.println("APP INFO: Heartbeat check by: " + alias + " for: " + n.getAlias() + ".");
			try
			{
				Response response = target.request().get();
				if(response.getStatus()!=200)
				{
					throw new Exception();
				}
			}
			catch(Exception e)
			{
				try
				{
					System.out.println("APP INFO: Salje se na: " + masteraddress);
					ResteasyWebTarget target2 = client.target("http://" + masteraddress +"/AgentTechnology/rest/agentCenter/node/" + n.getAlias());
					Response response = target2.request().delete();
					System.out.println("APP INFO: Node " + n.getAlias() + " is being shut down because it is not responding according to the heartbeat protocol.");				
					System.out.println("APP INFO: Delation returned status: " + response.getStatus());

				
				}
				catch(Exception e2)
				{
					System.out.println("APP INFO: greska?");
				}
				/*
				System.out.println("APP INFO: Node " + n.getAlias() + " is not responding - another check-up is requiered.");
				//repeat the request
				try
				{
					Response response = target.request().get();
					
					if(response.getStatus()!=200)
					{
						throw new Exception();
					}
				}
				catch(Exception e2)
				{
					//shut down node
					ResteasyWebTarget target2 = client.target("http://" + masteraddress +"/AgentTechnology/rest/agentCenter/node/" + n.getAlias());
					Response response = target2.request().delete();
					System.out.println("APP INFO: Node " + n.getAlias() + " is being shut down because it is not responding according to the heartbeat protocol.");

				}
				
				*/
				
			}

		}

	}
	

	
	
	// * * * MASTER NODE FUNCTIONS * * *	
	public void  informOtherNodes(AgentCenter newNode) 
	{
		if(!alias.equals("master"))
		{
        	System.out.println("PROCESS ABORTED: Only the master node can inform other nodes about a new member.");
			return;
		}
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		int counter = 0;
		
		for(Node n : nodes)
		{
			if(n.getAlias().equals("master") || n.getAlias().equals(newNode.alias))
				continue;
			
			ResteasyWebTarget target = client.target("http://" + n.getAddress() +"/AgentTechnology/rest/agentCenter/node");
			try
			{
	            Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(newNode, MediaType.APPLICATION_JSON));
	            if(response.readEntity(Boolean.class) == true)
	            {
	            	counter++;
	                System.out.println("APP INFO: " + n.getAlias() + " node has been informed about: " + newNode.alias + ".");    	
	            }
	            else
	            {
	            	throw(new Exception());
	            }	
			}
			catch(Exception e)
			{
	        	System.out.println("PROCESS ABORTED: Unable to inform " + n.getAlias() + " node about a new member.");
			}	
		}
        System.out.println("APP INFO: " + counter + " nodes were informed about the new member: " + newNode.address + ".");    	
	}

	
	public void deleteFromAllNodes(Node toBeDeleted) 
	{
		if(!alias.equals("master"))
		{
        	System.out.println("PROCESS ABORTED: Only the master node can delete from other nodes");
			return;
		}
		
		for(Node n : nodes)
		{
			if(n.getAlias().equals("master") || n.getAlias().equals(toBeDeleted.getAlias()))
				continue;
			
			ResteasyWebTarget target = client.target("http://" + n.getAddress() +"/AgentTechnology/rest/agentCenter/node/" + toBeDeleted.getAlias());
			try
			{
	            Response response = target.request().delete();
	            if(response.getStatus() == 404)
	            {
	            	throw(new Exception());
	            }
	            else
	            {
	                System.out.println("APP INFO: " + toBeDeleted.getAlias() + " node has been deleted from: " + n.getAlias() + ".");    	
	            }
			}
			catch(Exception e)
			{
	        	System.out.println("PROCESS ABORTED: Unable to delete from " + n.getAlias() + ".");
			}
		}
		
	}
	
	
	
	
	
	
	public void handleMessage()
	{
		
	}
	
	
	// * * * GET & SET * * * 
	
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

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> imenik) {
		this.nodes = imenik;
	}	
	
	public String getMasteraddress() {
		return masteraddress;
	}

	public void setMasteraddress(String masteraddress) {
		this.masteraddress = masteraddress;
	}
	
	
	// * * * NODES LIST FUNCTIONS * * *
	
	public void addNode(AgentCenter a)
	{
		nodes.add(new Node(a.alias,a.address));
	}


	public Node findNode(String alias) 
	{
		for(Node n : nodes)
		{
			if(n.getAlias().equals(alias))
			{
				return n;
			}
		}
		return null;
	}
	
	public void deleteNode(Node n)
	{
		nodes.remove(n);
	}




	
	
}
