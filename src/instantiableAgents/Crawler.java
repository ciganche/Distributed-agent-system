package instantiableAgents;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import crawlerUtils.CrawlerUtil;
import crawlerUtils.Page;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import webSocket.LoggerUtil;

@Stateful
public class Crawler extends Agent
{
	
	public String location=null;
	
	@Override
	public void handleMessage(ACLMessage message)
	{
		switch(message.getPerformative())
		{
			case SET_LOCATION: 
				location = message.getContent();
				LoggerUtil.log("Crawling location set to: " + location);
			break;
			
			
			case CRAWL:
				
				if(location == null)
				{
					LoggerUtil.log("ERROR: A file location must be specified first.");
					return;
				}
				
				if(message.getContent() == null || message.getContent().equals(""))
				{
					LoggerUtil.log("ERROR: A crawling starting point must be set.");
					return;
				}

				
				CrawlerUtil crawler = new CrawlerUtil(location);
				crawler.crawl(message.getContent(), 100);
				crawler.saveToFile();
			break;
			
				
			case SEARCH:
				
				if(!message.getSender().getType().getName().equals("Agregator"))
				{
					LoggerUtil.log("ERROR: Only an Agregator agent is authorized to order a local search.");
					return;
				}
				
				if(message.getContent() == null || message.getContent().equals(""))
				{
					LoggerUtil.log("ERROR: A search query must be sent.");
					return;
				}
				
				handleSearch(message);
				
			break;
			
			
			default:
				LoggerUtil.log("CRAWL, SEARCH and SET_LOCATION are the only performatives this agent supports.");

		}
	}

	private void handleSearch(ACLMessage message) 
	{
	
		String query = message.getContent().toLowerCase();
		ArrayList<Page> retVal = new ArrayList<Page>();
		
		Path path = Paths.get(location + "/wiki.tsv");
		List<String> fileContent;
		
		try
		{
			fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
			for( String line : fileContent)
			{
				String attr[] = line.split("\t");
				if(attr[0].toLowerCase().contains(query))
				{
					System.out.println(attr[0]);
					retVal.add(new Page(attr[0],attr[1],attr[2]));
				}
			}
			
			ACLMessage response = new ACLMessage();
			response.setPerformative(Performative.INFORM);
			response.setSender(this.getAid());
			response.setReceivers(new AID[] {message.getSender()});
			response.setContentObj(retVal);
			response.setConversationID(message.getConversationID());
			
			new JMSQueue(response);
		}
		catch(Exception e)
		{
			LoggerUtil.log("ERROR: No file found.");
		}

			
	}
	
}
