package rest.agentRest;


import java.util.ArrayList;

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
import webSocket.LoggerUtil;
import webSocket.dto.AgentClassesDTO;
import webSocket.dto.RunningAgentsDTO;

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
		//try to create locally
		ArrayList<AgentType> temp = center.getTypes().get(center.getAlias());
		boolean found = false;
		for(AgentType t : temp)
		{
			if(t.getName().equals(type))
			{
				found = true;
			}
		}
		
		//if not found - search on another node
		if(!found)
		{
			Node theOne = center.findNodeWithAgentType(type);
			
			if(theOne==null)
			{
				LoggerUtil.log("PROCESS ABORTED: The system does not suppert this agent type.");
				return Response.status(404).build();
			}
			else
			{
				LoggerUtil.log("Agent: [" + name + " - " + type + "] will be created on node: {" + theOne.getAlias() + "}");
				ResteasyClient client = new ResteasyClientBuilder().build();
		        ResteasyWebTarget target = client.target("http://" + theOne.getAddress() +"/AgentTechnology/rest/agents/running/" + type + "/" + name);
				Response response = target.request().post(null);
				
				return response;
			}
		}
		
		
		
			
		try 
		{
			Context context = new InitialContext();

		
			AgentAPI agent = (AgentAPI) context.lookup("java:module/" + type);
			AID aid = new AID(name,new Node(center.getAlias(),center.getAddress()), new AgentType(type,"agent"));
			Agent newAgent = (Agent) agent;
			newAgent.setAid(aid);
			center.addAgent(newAgent);
			context.close();
			
			LoggerUtil.log("Agent started: [" + newAgent.getAid().getName() + " - " + newAgent.getAid().getType().getName() + "]");
			
			
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
			LoggerUtil.log("Delation of running agent for node {" + center.getAlias() + "} failed");
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
				LoggerUtil.log("PROCESS ABORTED: Cannot delete agent.");
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
			LoggerUtil.log("Delation of running agent for node {" + center.getAlias() + "} failed");
			return Response.status(500).build();
		}
		return Response.status(200).build();
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public RunningAgentsDTO test() 
	{
		return new RunningAgentsDTO(center.getAgents());
	}
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public AgentClassesDTO classes()
	{
		return new AgentClassesDTO(center.getTypes().values());
	}







}
