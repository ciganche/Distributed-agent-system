package webSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/logger")
public class LoggerUtil {

	private static final Logger LOGGER = Logger.getLogger("SERVER LOGGER");
	private static List<Session> sessions = new ArrayList<Session>();

	public static Logger getLogger()
	{
		return LOGGER;
	}
	
	@OnOpen
	public void onOpen(Session session) 
	{
		LOGGER.log(Level.INFO, "Client [" + session.getId() + "] joined.");
		System.out.println("Client [" + session.getId() + "] joined.");
		LoggerUtil.sessions.add(session);
	}
	
	

	public static void log(String message) 
	{
		LOGGER.info(message);

		for (Session session : LoggerUtil.sessions)
		{
			try 
			{
				session.getBasicRemote().sendText(message);
				System.out.println(message);

			}
			catch (Exception e)
			{
				System.out.println("Cannot log - retrying.");
			}

		}

	}

	@OnMessage
	public void onMessage(String message, Session session)
	{
		LOGGER.log(Level.INFO, "Client [" + session.getId() + "]: " + message);
		System.out.println("Client [" + session.getId() + "]: " + message);	
	}

	@OnClose
	public void onClose(Session session)
	{
		LOGGER.log(Level.INFO, "Client [" + session.getId() + "] lost connection.");
		System.out.println("Client [" + session.getId() + "] lost connection.");
	}


}