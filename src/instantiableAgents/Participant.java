package instantiableAgents;

import java.io.Serializable;
import java.util.Random;

import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import jms.JMSQueue;
import message.ACLMessage;
import message.Performative;

@Stateful
public class Participant extends Agent implements Serializable
{
	
	@Override
	public void handleMessage(ACLMessage message)
	{
		switch(message.getPerformative())
		{
			case CALL_FOR_PROPOSAL:
				handleCallForProposal(message);
			break;
			
			case REJECT_PROPOSAL:
				handleRejection(message);
			break;
			
			case ACCEPT_PROPOSAL:
				handleAcceptance(message);
			break;
			
			default:
				System.out.println("APP INFO: Not yet implemented.");
		}
	}

	private void handleAcceptance(ACLMessage message) 
	{
		ACLMessage informMessage = new ACLMessage();
		informMessage.setConversationID(message.getConversationID());
		informMessage.setSender(this.getAid());
		informMessage.setReceivers(new AID[] {message.getSender()});
		informMessage.setContent("SKIBIDI PA PA PA PA PA");
		informMessage.setPerformative(Performative.INFORM);
		
		new JMSQueue(informMessage);
	}

	private void handleRejection(ACLMessage message)
	{
		System.out.println("APP INFO: Agent: " + this.getAid().getName() + " - " + this.getAid().getType().getName() + " ok:(");
	}

	private void handleCallForProposal(ACLMessage message)
	{
		Random rand = new Random();
		boolean refuseOrPropose = rand.nextBoolean();
		
		if(refuseOrPropose) //send propose
		{
			ACLMessage proposeMessage = new ACLMessage();
			proposeMessage.setConversationID(message.getConversationID());
			proposeMessage.setSender(this.getAid());
			proposeMessage.setReceivers(new AID[] {message.getSender()});
			proposeMessage.setPerformative(Performative.PROPOSE);
			
			proposeMessage.setContentObj(rand.nextInt(101)); //send a random number [0 - 100]
			
			new JMSQueue(proposeMessage);
		}
		else					//send refuse
		{
			ACLMessage rejectMessage = new ACLMessage();
			rejectMessage.setConversationID(message.getConversationID());
			rejectMessage.setSender(this.getAid());
			rejectMessage.setReceivers(new AID[] {message.getSender()});
			rejectMessage.setPerformative(Performative.REFUSE);

			new JMSQueue(rejectMessage);
		}
	}
}
