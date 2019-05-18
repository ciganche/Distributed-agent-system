package utils;

import java.util.HashMap;

import agent.AID;

public class ContractNetValueHolder 
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
