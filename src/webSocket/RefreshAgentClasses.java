package webSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import agent.AgentType;
import agentCenter.AgentCenterAPI;
import webSocket.dto.AgentClassesDTO;
import webSocket.encoders.AgentClassEncoder;
import webSocket.encoders.RunningAgentsEncoder;

//{"list":["tip1","tip2"....]}
@ServerEndpoint(value = "/refreshAgentClasses", encoders = AgentClassEncoder.class)
public class RefreshAgentClasses
{
	
	private static List<Session> sessions = new ArrayList<Session>();
	
	public static void refresh(Collection<ArrayList<AgentType>> newCollection)
	{
		for (Session session : RefreshAgentClasses.sessions)
		{
			try 
			{
				
				AgentClassesDTO classesDTO = new AgentClassesDTO(newCollection);
				
				session.getBasicRemote().sendObject(classesDTO);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("WEB SOCKET TRANSFER FAILED: Cannot send agent classes.");
			}
		}	
	}
	
	@OnOpen
	public void onOpen(Session session) 
	{
		RefreshAgentClasses.sessions.add(session);
	}	
}
