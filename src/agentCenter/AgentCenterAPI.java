package agentCenter;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;

import agent.AID;
import agent.Agent;
import agent.AgentAPI;
import agent.AgentType;

@Local
public interface AgentCenterAPI {

	void Init();

	void cleanUp();

	void registerAtMasterNode();

	void heartbratProtocol();

	void informOtherNodes(AgentCenter newNode);

	void deleteFromAllNodes(Node toBeDeleted);

	void handleMessage();

	String getAlias();

	void setAlias(String alias);

	String getAddress();

	void setAddress(String address);

	//TODO:
	ArrayList<Node> getNodes();

	void setNodes(ArrayList<Node> imenik);

	String getMasteraddress();

	void setMasteraddress(String masteraddress);

	HashMap<AID, AgentAPI> getAgents();

	ArrayList<AgentType> getTypes();

	void setTypes(ArrayList<AgentType> types);

	void addNode(AgentCenter a);

	Node findNode(String alias);

	void deleteNode(Node n);

	void addAgent(AgentAPI newAgent);

	void addType(AgentType type);

	void setAgents(HashMap<AID, AgentAPI> agents);

}