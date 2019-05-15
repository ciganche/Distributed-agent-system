package rest;


import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import agentCenter.AgentCenter;
import agentCenter.AgentCenterAPI;
import agentCenter.Node;

@Path("/agentCenter")
@LocalBean
@Stateless
@AccessTimeout(-1)
public class NodeRestBean implements NodeRestAPI{


	@EJB 
	AgentCenterAPI center;

	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)	
	public Object registerOrNotify(AgentCenter newNode) 
	{						
		if(center.getAlias().equals("master"))
		{
			System.out.println("REST INFO: Returning all nodes from master. Size of node list: " + center.getNodes().size() + ".");
			center.addNode(newNode);
			center.addType(newNode.getTypes().get(newNode.getAlias()), newNode.getAlias());
			center.informOtherNodes(newNode);
			return center;
		}
		else
		{
			center.addNode(newNode);
			center.addType(newNode.getTypes().get(newNode.getAlias()), newNode.getAlias());
			System.out.println("REST INFO: Informing " + center.getAlias() + " node about a non-master node. Size of node list: " + center.getNodes().size() + ".");
			return true;
		}
	}

	
	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(@PathParam("alias") String alias) 
	{	
		
		System.out.println("REST INFO: Delation api hit: " + alias);
		
		if(center.getAlias().equals("master"))
		{
			Node toBeDeleted = center.findNode(alias);
			if(toBeDeleted == null)
			{
				System.out.println("PROCESS ABORTED: Node " + alias + " is unregistered at node " + center.getAlias());
				
				return Response.status(404).build();
			}
			
			center.deleteFromAllNodes(toBeDeleted);
			center.deleteNode(toBeDeleted);
			
			return Response.status(200).build();

		}
		else
		{
			Node toBeDeleted = center.findNode(alias);
			if(toBeDeleted == null)
			{
				System.out.println("PROCESS ABORTED: Node " + alias + " is unregistered" + center.getAlias());
				
				return Response.status(404).build();

			}
			center.deleteNode(toBeDeleted);
			return Response.status(200).build();

		}
	}
	
	
	@GET
	@Path("/node")
	public Response isAlive()
	{
		System.out.println("APP INFO: Alive");
		return Response.status(200).build();
	}
	
	
	@Override
	public String test()
	{
		String retVal = center.getAddress() + ":\n";
		for(Node n : center.getNodes())
		{
			retVal = retVal + n.getAlias() + "\n";
		}
		System.out.println("REST INFO: Endpoint accessed.");
		return retVal;
	}


	
	


}
