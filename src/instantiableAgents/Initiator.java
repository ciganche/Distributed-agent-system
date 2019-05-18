package instantiableAgents;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;

import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;
import utils.ContractNetValueHolder;

@Stateful
public class Initiator extends Agent
{
	//TODO: initiatoru pre gasenja na sve ponude da odg -1
	
	//the key is the conversation id
	private HashMap<String, ContractNetValueHolder> sessions = new HashMap<String, ContractNetValueHolder>();
	private HashMap<String,Integer> sentCallsNumber = new HashMap<String,Integer>(); 
	
	@Override
	public void handleMessage(ACLMessage message)
	{	
		
		nap();
		
		//client initiated - sends call for proporsal to all running agents
		switch(message.getPerformative())
		{
			case REQUEST: //cliend initiated, sends cpf to all running agents
				handleRequest(message);
			break;
			
			case REFUSE:
				handleRefuse(message);
			break;
			
			case PROPOSE:
				handlePropose(message);
			break;
			
			case FAILURE:
				System.out.println("PROCESS ABORTED: Choosen agent: " + message.getSender().getName() + " - " + message.getSender().getType().getName() + " failed to execute the task.");
			break;
			
			case INFORM:
				System.out.println("APP INFO:  Choosen agent: " + message.getSender().getName() + " - " + message.getSender().getType().getName() + " successfully executed the task. Returned: " + message.getContent());
			break;
			
			default:
				System.out.println("APP INFO: Not yet implemented.");
		}
		
	}

	private void handlePropose(ACLMessage message)
	{
		try 
		{
			int offer = (int) message.getContentObj();
			addOfferToSession(message.getConversationID(),message.getSender(),offer);
			System.out.println("APP INFO: Agent: " + message.getSender().getName() + " - " + message.getSender().getType().getName() + " offered: " + offer );
		}
		catch(Exception e)
		{
			System.out.println("OFFER CANCELED: Agent: " + message.getSender().getName() + " - " + message.getSender().getType().getName() + " sends an invalid value.");
			addOfferToSession(message.getConversationID(),message.getSender(),-1);
		}
		
		if(sessions.get(message.getConversationID()).getOffers().size() == sentCallsNumber.get(message.getConversationID()))
		{
			chooseAgent(message);
		}
		
	}

	private void handleRefuse(ACLMessage message) 
	{
		System.out.println("APP INFO: Agent: " + message.getSender().getName() + " - " + message.getSender().getType().getName() + "  refused call for proposal." );
		//if refused - value is -1
		addOfferToSession(message.getConversationID(),message.getSender(),-1);
		
		if(sessions.get(message.getConversationID()).getOffers().size() == sentCallsNumber.get(message.getConversationID()))
		{
			chooseAgent(message);
		}
	}


	private void handleRequest(ACLMessage message)
	{
		
		//create a new request bidding session
		sessions.put(message.getConversationID(), new ContractNetValueHolder());
		
		
		ACLMessage callForProposalMessage = new ACLMessage();
		callForProposalMessage.setContent("CONTRACT NET: Call for proposal - from Initiator");
		callForProposalMessage.setSender(this.getAid());
		
		
		//get all PaAIDcipant id's 
		ArrayList<AID> receivers = new ArrayList<AID>();
		ArrayList<Agent> arrayList = (ArrayList<Agent>) message.getContentObj();
		for(Agent a : arrayList)
		{
			if(a.getAid().getType().getName().equals("Participant"))
				receivers.add(a.getAid());
		}
		
		if(receivers.size() == 0)
		{
			System.out.println("PROCESS ABORTED: No running Participant agents found.");
			return;
		}
		
		AID[] reciversArray = receivers.toArray(new AID[receivers.size()]);
		sentCallsNumber.put(message.getConversationID(), receivers.size());
		callForProposalMessage.setReceivers(reciversArray);

		callForProposalMessage.setConversationID(message.getConversationID());
		callForProposalMessage.setPerformative(Performative.CALL_FOR_PROPOSAL);
		
		new JMSQueue(callForProposalMessage);
	}
	
	
	
	
	
	
	
	private void chooseAgent(ACLMessage message) 
	{
		AID acceptedAgent = getBestOffer(message.getConversationID());
		
		if(acceptedAgent == null)
		{
			System.out.println("APP INFO: All agents refused to propose.");
			return;
		}
		
		System.out.println("APP INFO: The choosen agent: " + acceptedAgent.getName() + " - " + acceptedAgent.getType().getName());
		ArrayList<AID> agents = new ArrayList<AID>(sessions.get(message.getConversationID()).getOffers().keySet());

		for(AID agent: agents)
		{
			if(agent.equals(acceptedAgent)) //send accept proposal
			{
				ACLMessage acceptMessage = new ACLMessage();
				acceptMessage.setSender(this.getAid());
				acceptMessage.setConversationID(message.getConversationID());
				acceptMessage.setReceivers(new AID[] {acceptedAgent});
				acceptMessage.setPerformative(Performative.ACCEPT_PROPOSAL);
				
				new JMSQueue(acceptMessage);
			}
			else 							//send decline proposal
			{
				ACLMessage declineMessage = new ACLMessage();
				declineMessage.setSender(this.getAid());
				declineMessage.setConversationID(message.getConversationID());
				declineMessage.setReceivers(new AID[] {agent});
				declineMessage.setPerformative(Performative.REJECT_PROPOSAL);
				
				new JMSQueue(declineMessage);
			}
		}
		
	}

	
	
	
	
	// * * * UTILS * * * 
	
	private void addOfferToSession(String conversationID, AID aid, int value)
	{
		sessions.get(conversationID).getOffers().put(aid, value);
	}
	
	private AID getBestOffer(String conversationID)
	{
		ArrayList<AID> agents = new ArrayList<AID>(sessions.get(conversationID).getOffers().keySet());
		ArrayList<Integer> offers = new ArrayList<Integer>(sessions.get(conversationID).getOffers().values());
		
		int bestOffer = 0;
		int bestOfferIndex = -1;
		
		for( int i = 0 ; i < offers.size() ; i ++ )
		{
			if(offers.get(i) > bestOffer)
			{
				bestOffer = offers.get(i);
				bestOfferIndex = i;
			}
		}
		
		if(bestOfferIndex == -1 || bestOffer == -1)
			return null;
		
		return agents.get(bestOfferIndex);
	}
	
	
	public void nap()
	{
		new Thread()
		{
	        @Override
	        public void run() {
	        	
	        	 try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		};
	}
	
}
