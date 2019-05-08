package config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadConfigUtil 
{
    
	private String [] elements;
    
    public ReadConfigUtil()
    {

        try
        {
            String fileName = "config.csv";
            InputStream s = this.getClass().getResourceAsStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s));
            String line = bufferedReader.readLine();
            
            elements = line.split(",");
            
            if(elements.length != 3)
            	throw new IOException("Illegal argument number");
            
            
            
        }
        catch(Exception e)
        {
        	System.err.println("Config file reading error: " + e);
        }
    }
    
    public ArrayList<String> getParams()
    {
    	ArrayList<String> retVal = new ArrayList<String>();
    	
    	try
    	{
    		String alias = elements[0].split("=")[1];
        	retVal.add(alias);
        	
        	String ip = elements[1].split("=")[1];
        	retVal.add(ip);
        	
        	String masteraddress = elements[2].split("=")[1];
        	retVal.add(masteraddress);
        	
    	}
    	catch(Exception e)
    	{
        	System.err.println("Config file parameter reading error: " + e);
    	}
    	
    	
    	
    	
    	
    	return retVal;
   
    }
    
}
