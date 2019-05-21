package webSocket.encoders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import webSocket.dto.PagesDTO;

public class PageClassEncoder implements Encoder.Text<PagesDTO>
{
	 
    private static Gson gson = new Gson();
 

	@Override
	public String encode(PagesDTO list) throws EncodeException 
	{
		return gson.toJson(list);
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