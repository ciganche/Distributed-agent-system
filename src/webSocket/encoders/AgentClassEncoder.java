package webSocket.encoders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import webSocket.dto.AgentClassesDTO;

public class AgentClassEncoder implements Encoder.Text<AgentClassesDTO>
{
	 
    private static Gson gson = new Gson();
 
	@Override
	public void init(EndpointConfig arg0) 
	{
		
	}

	@Override
	public String encode(AgentClassesDTO dto) throws EncodeException
	{
		return gson.toJson(dto);
	}

	@Override
	public void destroy() 
	{
		
	}
}