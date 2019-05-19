package webSocket.encoders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import webSocket.dto.RunningAgentsDTO;

public class RunningAgentsEncoder implements Encoder.Text<RunningAgentsDTO>
{
	 
    private static Gson gson = new Gson();
 

	@Override
	public String encode(RunningAgentsDTO dto) throws EncodeException
	{
		return gson.toJson(dto);
	}


	@Override
	public void destroy() 
	{
		
	}


	@Override
	public void init(EndpointConfig arg0) 
	{
	
	}
}