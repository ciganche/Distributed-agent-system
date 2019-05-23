package webSocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import agent.AgentType;

import webSocket.dto.AgentClassesDTO;
import webSocket.encoders.AgentClassEncoder;

//{"list":["tip1","tip2"....]}
@ServerEndpoint(value = "/refreshAgentClasses", encoders = AgentClassEncoder.class)
public class RefreshAgentClasses
{
	
	private static List<Session> sessions = new ArrayList<Session>();
	
	public static void refresh(Collection<ArrayList<AgentType>> newCollection)
	{
		
		AgentClassesDTO classesDTO = new AgentClassesDTO(newCollection);
		
		for (Session session : RefreshAgentClasses.sessions)
		{
			try 
			{
				
				session.getBasicRemote().sendObject(classesDTO);
			}
			catch (Exception e)
			{
				System.out.println("WEB SOCKET TRANSFER FAILED: Cannot send agent classes - retrying.");
			}
		}	
	}
	
	@OnOpen
	public void onOpen(Session session) 
	{
		RefreshAgentClasses.sessions.add(session);
	}	
}
