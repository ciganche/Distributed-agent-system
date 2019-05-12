package rest;


import java.util.Collection;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AID;
import agent.Agent;
import agent.AgentAPI;
import agent.AgentType;
import agentCenter.AgentCenter;
import agentCenter.AgentCenterAPI;
import agentCenter.Node;

@Path("/agents")
@LocalBean
@Stateless
@AccessTimeout(-1)
public class AgentRestBean implements AgentRestAPI
{

	@EJB 
	AgentCenterAPI center;

	@POST
	@Path("/running/{type}/{name}")
	public Response startAgents(String type, String name) 
	{
		
		//TODO: napraviti zastiu od nepoznatog tipa u urlu
		
		try 
		{
			Context context = new InitialContext();
			/*
			NamingEnumeration<NameClassPair> agentList = context.list("java:jboss/clustering");
			
			while (agentList.hasMore())
			{
				String ejbName = agentList.next().getName();
				String ejbClassName = agentList.next().getClassName();
				
				System.out.println(ejbName + " - " + ejbClassName);
				
			}
			*/
			AgentAPI newAgent = (AgentAPI) context.lookup("java:module/" + type);
			AID aid = new AID(name,new Node(center.getAlias(),center.getAddress()), new AgentType(name,null));
			newAgent.setAid(aid);
			center.addAgent(newAgent);
			
			context.close();
			
			//inform all other nods that a agent is active
			ResteasyClient client = new ResteasyClientBuilder().build();
			for(Node n : center.getNodes())
			{
				
				if(n.getAddress().equals(center.getAddress()))
					continue;
				
				try
				{
					ResteasyWebTarget target = client.target("http://" + n.getAddress() +"/AgentTechnology/rest/agents/addRunningAgent");
					Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(newAgent, MediaType.APPLICATION_JSON)); 	
				
					if(response.getStatus()!=200)
					{
						throw new Exception("PROCESS ABORTED: Addition of running agent for node " + n.getAlias() + " failed");
					}

				}
				catch(Exception e)
				{
					e.printStackTrace();
					return Response.status(500).build();
				}
			}
			
			return Response.status(200).build();
 
			
		}
		catch (NamingException e) 
		{
			return Response.status(500).build();
		}
		
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/addRunningAgent")
	public Response addRunningAgent(Agent newAgent) 
	{	
		center.addAgent(newAgent);

		System.out.println("APP INFO: Running agent added to: " + center.getAlias());
		
		return Response.status(200).build();
	}

	@Override
	public String test() 
	{
		String retVal = center.getAlias() + ":\n";
		Collection<AgentAPI> list = center.getAgents().values();
		for(AgentAPI a : list)
		{
			retVal = retVal + a.getAid().getName() + " - " + a.getAid().getType() + "\n";
		}
		
		return retVal;
	}
	
	

}
