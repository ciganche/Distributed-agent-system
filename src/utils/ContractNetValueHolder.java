package utils;

import java.io.Serializable;
import java.util.HashMap;

import agent.AID;

@SuppressWarnings("serial")
public class ContractNetValueHolder implements Serializable
{
	
	public ContractNetValueHolder() 
	{
		super();
	}
	
	private HashMap<AID, Integer> offers = new HashMap<>();

	public HashMap<AID, Integer> getOffers() {
		return offers;
	}

	public void setPonude(HashMap<AID, Integer> ponude) {
		this.offers = ponude;
	}
}
