package webSocket;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import agent.Agent;
import webSocket.dto.RunningAgentsDTO;
import webSocket.encoders.RunningAgentsEncoder;

@ServerEndpoint(value = "/refreshRunningAgents", encoders = RunningAgentsEncoder.class)
public class RefreshRunningAgents
{
	private static List<Session> sessions = new ArrayList<Session>();
	
	public static void refresh(ArrayList<Agent> list)
	{
		RunningAgentsDTO dto = new RunningAgentsDTO(list);
		
		for (Session session : RefreshRunningAgents.sessions)
		{
			try 
			{
				session.getBasicRemote().sendObject(dto);
			}
			catch (Exception e)
			{
				System.out.println("WEB SOCKET TRANSFER FAILED: Cannot send running agents - retrying.");
			}
		}	
	}
	
	@OnOpen
	public void onOpen(Session session) 
	{
		RefreshRunningAgents.sessions.add(session);
	}
	
}
