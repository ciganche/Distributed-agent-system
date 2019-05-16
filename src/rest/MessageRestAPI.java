package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import message.ACLMessage;
import message.Performative;

@Remote
public interface MessageRestAPI
{
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Performative[] getPerformatives();
	
	
	@GET
	@Path("/test")
	public void test();
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(ACLMessage message);
}
