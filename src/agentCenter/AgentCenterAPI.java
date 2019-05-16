package agentCenter;

import java.util.ArrayList;
import java.util.HashMap;


import javax.ejb.Local;


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

	ArrayList<Agent> getAgents();

	HashMap<String,ArrayList<AgentType>> getTypes();

	void setTypes(HashMap<String,ArrayList<AgentType>> types);

	void addNode(AgentCenter a);

	Node findNode(String alias);

	void deleteNode(Node n);

	void addAgent(Agent newAgent);

	void addType(ArrayList<AgentType> list, String name);

	void setAgents(ArrayList<Agent> agents);

	Agent findAgent(AID aid);

	boolean removeRunningAgent(String type, String name);

	ArrayList<AgentType> getCreatableAgentTypes();

	Node findNodeWithAgentType(AgentType type);

	void removeNodeTypes(String alias);

}