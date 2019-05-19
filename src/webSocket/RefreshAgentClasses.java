package webSocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import agent.Agent;
import agent.AgentType;
import webSocket.dto.AgentClassesDTO;
import webSocket.dto.RunningAgentsDTO;
import webSocket.encoders.AgentClassEncoder;
import webSocket.encoders.RunningAgentsEncoder;

//{"list":["tip1","tip2"....]}
@ServerEndpoint(value = "/refreshAgentClasses", encoders = {AgentClassEncoder.class, RunningAgentsEncoder.class})
public class RefreshAgentClasses
{
	private static List<Session> sessions = new ArrayList<Session>();
	
	public static void refresh(Collection<ArrayList<AgentType>> newCollection)
	{
		ArrayList<ArrayList<AgentType>> list = new ArrayList<ArrayList<AgentType>>(newCollection); 
		ArrayList<String> uniqueTypes = new ArrayList<String>();
		
		for( int i = 0 ; i < list.size() ; i ++ )
		{
			for( int j = 0 ; j < list.get(i).size() ; j ++)
			{
				String temp = list.get(i).get(j).getName();
				if(!uniqueTypes.contains(temp))
				{
					uniqueTypes.add(temp);
				}
			}
		}
		
		
		for (Session session : RefreshAgentClasses.sessions)
		{
			try 
			{
				
				AgentClassesDTO classesDTO = new AgentClassesDTO(uniqueTypes);
				
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
