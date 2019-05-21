package instantiableAgents;

import java.util.ArrayList;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import crawlerUtils.Page;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import webSocket.LoggerUtil;
import webSocket.SendSearchResults;
import webSocket.dto.PagesDTO;

@Stateful
public class Agregator extends Agent
{
	
	public PagesDTO list = new PagesDTO();
	
	@Override
	public void handleMessage(ACLMessage message)
	{
		switch(message.getPerformative())
		{
			case REQUEST:
				handleRequest(message);
				waitForParticipans(message, 10);
			break;
			
			case INFORM:
				handleSearchResult(message);
			break;
			
			case RESUME:
				sendResults();
			break;
			
			default:
				LoggerUtil.log("Only supports REQUEST.");

		}
	}

	private void sendResults() 
	{
		SendSearchResults.refresh(list);
	}

	private void handleSearchResult(ACLMessage message) 
	{
		@SuppressWarnings("unchecked")
		ArrayList<Page> result = (ArrayList<Page>) message.getContentObj();
		list.addUnique(result);
		System.out.println("* * *Primio rezultate");
	}

	private void waitForParticipans(ACLMessage message, int sleepSeconds)
	{		
		LoggerUtil.log("Agent: [" + this.getAid().getName() + " - Initiator] waits for data to collect for " + sleepSeconds + " seconds.");
		
		ACLMessage pause = new ACLMessage();
		pause.setReceivers(new AID[]{this.getAid()});
		pause.setConversationID(message.getConversationID());
		pause.setSender(this.getAid());
		pause.setPerformative(Performative.RESUME);	
		Thread t = new Thread()
		{
	        @Override
	        public void run() 
	        {
	        	 try 
	        	 {
					Thread.sleep(sleepSeconds*1000);
					new JMSQueue(pause);
	        	 }
	        	 catch (InterruptedException e)
	        	 {
					e.printStackTrace();
	        	 }
	        }
		};
		t.start();		
	}

	private void handleRequest(ACLMessage message)
	{
		ACLMessage searchMessage = new ACLMessage();
		searchMessage.setSender(this.getAid());
		
		
		//get all PaAIDcipant id's 
		ArrayList<AID> receivers = new ArrayList<AID>();
		
		@SuppressWarnings("unchecked")
		ArrayList<Agent> arrayList = (ArrayList<Agent>) message.getContentObj();
		for(Agent a : arrayList)
		{
			if(a.getAid().getType().getName().equals("Crawler"))
			{
				receivers.add(a.getAid());
				System.out.println("* * * For crawler: " + a.getAid().getName());
			}	
			
		}
		
		if(receivers.size() == 0)
		{
			LoggerUtil.log("PROCESS ABORTED: No running Participant agents found.");
			return;
		}
		
		AID[] reciversArray = receivers.toArray(new AID[receivers.size()]);
		searchMessage.setReceivers(reciversArray);

		searchMessage.setConversationID(message.getConversationID());
		searchMessage.setContent(message.getContent());
		searchMessage.setPerformative(Performative.SEARCH);
		
		new JMSQueue(searchMessage);
	}
}
