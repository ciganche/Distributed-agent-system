package rest;


import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
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
import agentCenter.AgentCenterAPI;
import agentCenter.Node;
import message.Performative;

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
	public Response startAgents(@PathParam("type") String type,@PathParam("name") String name) 
	{
		
		//TODO: napraviti zastiu od nepoznatog tipa u urlu
		
		try 
		{
			Context context = new InitialContext();

		
			AgentAPI agent = (AgentAPI) context.lookup("java:module/" + type);
			AID aid = new AID(name,new Node(center.getAlias(),center.getAddress()), new AgentType(type,"agent"));
			Agent newAgent = (Agent) agent;
			newAgent.setAid(aid);
			center.addAgent(newAgent);
			context.close();
			
			System.out.println("APP INFO: Agent started: " + newAgent.getAid().getName() + " - " + newAgent.getAid().getType());
			
			
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
			e.printStackTrace();
			return Response.status(500).build();
		}
		
	}
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/addRunningAgent")
	public Response addRunningAgent(Agent newAgent) 
	{	
		center.addAgent(newAgent);

		System.out.println("APP INFO: Added to: " + center.getAlias() + " and agent started: " + newAgent.getAid().getName() + " - " + newAgent.getAid().getType());

		return Response.status(200).build();
	}
	
	
	
	
	
	@DELETE
	@Path("/running/{type}/{name}")
	public Response deleteAgent(@PathParam("type") String type,@PathParam("name") String name)
	{
		
		if(!center.removeRunningAgent(type,name))
		{
			System.out.println("PROCESS ABORTED: Delation of running agent for node " + center.getAlias() + " failed");
			return Response.status(500).build();
		}
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		for(Node n : center.getNodes())
		{
			
			if(n.getAddress().equals(center.getAddress()))
				continue;
			
			try
			{
				ResteasyWebTarget target = client.target("http://" + n.getAddress() +"/AgentTechnology/rest/agents/removeRunningAgent/" + type + "/" + name);
				
				Response response = target.request().delete(); 	
			
				if(response.getStatus()!=200)
				{
					throw new Exception("PROCESS ABORTED: Cannot delete agent.");
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
	@DELETE
	@Path("/removeRunningAgent/{type}/{name}")
	public Response removeRunningAgent(@PathParam("type") String type,@PathParam("name") String name)
	{
		if(!center.removeRunningAgent(type,name))
		{
			System.out.println("PROCESS ABORTED: Delation of running agent for node " + center.getAlias() + " failed");
			return Response.status(500).build();
		}
		return Response.status(200).build();
	}
	

	



	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Agent> test() 
	{
		return center.getAgents();
	}
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ArrayList<AgentType>> classes()
	{
		return center.getTypes().values();
	}


	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Performative[] getPerformatives()
	{
		return Performative.values();
	}





}
