package cfartefact.structures;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// import javax.xml.bind.DatatypeConverter;

import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;

public class cfCoalition {
	private Set<cfAgent> 	agents;
	private String 			name;
	private String 			id;
	private Double 			value;
	private Map<String, Integer> agentTypes = new HashMap<String, Integer>();

	public cfCoalition(){
		initialise("none");
	}
	public cfCoalition(String name){
		initialise(name);
	}

	private void initialise(String name) {
		this.agents = new HashSet<cfAgent>();
		this.name 	= name;
		this.value 	= Double.NaN;
	}

	public void addAgent(cfAgent agent) {
		this.agents.add(agent);

		updateTypes(agent);

		this.id = generateHashCode();
	}
	private void updateTypes(cfAgent agent) {
		String key = agent.getType();

		agentTypes.put(key, agentTypes.getOrDefault(key, 0)+1);
	}
	public Integer getNumberByType(String type) {
		return agentTypes.getOrDefault(type, 0);
	}
	/*public String[] getAgents() {
		return this.agents.toArray(new String[this.agents.size()]);
	}*/
	/*public String[] getAgentsNameArray() {
		String[] names = new String[agents.size()];
		for(int i=0; i<agents.size(); i++)
			names[i] = agents.get(i).getName();
		return names;
	}*/
	public Set<cfAgent> getAgents() {
		return agents;
	}
	/*public String[] getAgentsName() {
		String[] array = new String[agents.size()];
		int i = 0;
		for (cfAgent agent : agents) {
			array[i] = agent.getName();
			i++;
		}
		return array;
	}*/
	public Set<String> getAgentsName() {
		Set<String> s = new HashSet<>();
		for (cfAgent agent : agents)
			s.add(agent.getName());

		return s;
	}
	/*public int[] getAgentsHash() {
		int[] array = new int[agents.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = agents.get(i).hashCode();			
		return array;
	}*/
	public String getCoalitionName(){
		return this.name;
	}
	public String getId() {
		return id;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Coalition "+name+": ");

		for (cfAgent cfAgent : agents){
			sb.append('[');
			sb.append(cfAgent.getName());
			sb.append(',');
			sb.append(cfAgent.getType());
			sb.append(']');
		}

		return sb.toString();
	}

//	@Override
//	public int hashCode() {
//		String[] array = new String[agents.size()];
//		for (int i = 0; i < array.length; i++)
//			array[i] = agents.get(i).getName();
//		return Arrays.hashCode(array);
//	}
	/*public String generateHashCode() {
		String hash = null;
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			for (cfAgent cfAgent : agents) {
				messageDigest.update(cfAgent.getName().getBytes());
			}
			hash = DatatypeConverter.printHexBinary(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
		}
		return hash;
	}*/
	public String generateHashCode() {
		StringBuilder sb = new StringBuilder();
		for (cfAgent cfAgent : agents)
			sb.append(cfAgent.getName());

		return Hashing.sha256().hashString(sb.toString(), StandardCharsets.UTF_8).toString();
	}
}