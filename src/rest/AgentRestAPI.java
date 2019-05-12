package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import agent.Agent;


@Remote
public interface AgentRestAPI
{
	@POST
	@Path("/running/{type}/{name}")
	public Response startAgents(@PathParam("type") String type, @PathParam("name") String name);
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/addRunningAgent")
	public Response addRunningAgent(Agent newAgent);
	
	@GET
	@Path("/test")
	public String test();
	
}
