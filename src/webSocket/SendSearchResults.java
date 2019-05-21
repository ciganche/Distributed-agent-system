package webSocket;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import webSocket.dto.PagesDTO;
import webSocket.encoders.PageClassEncoder;

@ServerEndpoint(value = "/searchResults", encoders = PageClassEncoder.class)
public class SendSearchResults
{
	
	private static List<Session> sessions = new ArrayList<Session>();
	
	
	public static void refresh(PagesDTO result)
	{
		for (Session session : SendSearchResults.sessions)
		{
			try 
			{				
				session.getBasicRemote().sendObject(result);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("WEB SOCKET TRANSFER FAILED: Cannot send search results.");
			}
		}	
	}
	
	@OnOpen
	public void onOpen(Session session) 
	{
		SendSearchResults.sessions.add(session);
	}	
}