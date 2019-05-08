package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import agentCenter.AgentCenter;


@Remote
public interface RestAPI
{
	
	@GET
	@Path("/test")
	public String test();
	
	@GET
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isAlive();
	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Object registerOrNotify(AgentCenter newNode);
	
	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(String id);
}
