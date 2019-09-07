package cfartefact.algorithms.clink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import cfartefact.characteristicFunction.CharacteristicFunction;
import cfartefact.structures.cfAgent;
import cfartefact.structures.cfCoalition;
import cfartefact.structures.cfCoalitionStructure;

public class CLINK {
	public enum eColors{
		green,
		red
	}
	private CharacteristicFunction characteristicFunction;
	private Map<String, cfCoalition> coalitionClass = new LinkedHashMap<>();
	private Map<String, Set> coalitionSet 			= new LinkedHashMap<>();
//	private MutableGraph<Set> best;
	private Set<Set> best;
	
	 Map<String,cfAgent> SetOfagent;
		
	public cfCoalitionStructure getBestCS() {
		return convertSetCS(this.best);
	}

	public void run(Set<String> agents, MutableGraph<Set> interationGraph, CharacteristicFunction cf, Map<String,cfAgent> SetOfagent) {
		this.characteristicFunction = cf;	
		this.SetOfagent = SetOfagent;
		
		// initialise partitions to singletons
		Set<Set> cs0 = new HashSet<>();
		for (String agent : agents) {
			Set<String> a = ImmutableSet.of(agent);
			cs0.add(a);
		}
		
		// initialise the network for the current partition (i.e. singletons) with the interaction graph
		MutableGraph<Set> net0 = Graphs.copyOf(interationGraph);
		
		// initialise PL for each agent pair, considering the network connectivity
		PartitionLinkage pl = new PartitionLinkage(cs0, interationGraph);		
//		System.out.println("Table "+pl.partitionLinkage);
		
		// main loop
		while((pl.max() >= 0) && (cs0.size() > 1)) {	
			
			// Update partition
			Set newCoalition = Sets.union(coalitionSet.get(pl.argmax().idI),coalitionSet.get(pl.argmax().idJ));
			cs0.remove(coalitionSet.get(pl.argmax().idI));
			cs0.remove(coalitionSet.get(pl.argmax().idJ));
			cs0.add(newCoalition);
			
			// update netcs0
			net0 = edgeContraction(net0, coalitionSet.get(pl.argmax().idI), coalitionSet.get(pl.argmax().idJ));
			
			// update pl
			pl = new PartitionLinkage(cs0, net0);
//			System.out.println("Table "+pl.partitionLinkage);
//			System.out.println("CS: "+cs0);
		}
		
		this.best = cs0;
//		System.out.println("Coalition Structure: "+cs0);
	}
	
	/*private boolean isVerticesConnected(cfCoalition c1, cfCoalition c2, MutableGraph<String> graph) {
		for (EndpointPair<String> edge : graph.edges()) {
			if (	(c1.getId().equals(edge.nodeU()) && c2.getId().equals(edge.nodeV()))
				||	(c1.getId().equals(edge.nodeV()) && c2.getId().equals(edge.nodeU()))) {
				return true;
			}
		}
		return false;
	}*/
	
	
	
	private MutableGraph<Set>  edgeContraction(MutableGraph<Set> interationGraph, EndpointPair<Set> e) {
		MutableGraph<Set> newGraph = Graphs.copyOf(interationGraph);
				
		Set union = Sets.union(e.nodeU(), e.nodeV());
		
		Set<Set> neighbour = Sets.union(newGraph.predecessors(e.nodeU()),newGraph.predecessors(e.nodeV()));
		for (Set set : neighbour) {
			newGraph.putEdge(union, set);
		}
		
		newGraph.removeNode(e.nodeU());
		newGraph.removeNode(e.nodeV());
		
		return newGraph;
	}
	private MutableGraph<Set>  edgeContraction(MutableGraph<Set> interationGraph, Set s1, Set s2) {
		MutableGraph<Set> newGraph = Graphs.copyOf(interationGraph);
				
		Set union = Sets.union(s1, s2);
		
		Set<Set> neighbour = Sets.union(newGraph.predecessors(s1),newGraph.predecessors(s2)).immutableCopy();
		for (Set set : neighbour) {
			newGraph.putEdge(union, set);
		}
		
		newGraph.removeNode(s1);
		newGraph.removeNode(s2);
		
		return newGraph;
	}

	

	private Double lf(cfCoalition nodeU, cfCoalition nodeV) {	
		return gain(nodeU.getAgentsName(), nodeV.getAgentsName());
	}
	private Double lf(Set nodeU, Set nodeV) {
		return gain(nodeU, nodeV);
	}
	
	private double gain(Set c1, Set c2) {
		double gain = 0;
		Set union = Sets.union(c1, c2);
		
//		gain = getCoalitionValue(union) - getCoalitionValue(c1) - getCoalitionValue(c2);
		gain = getCoalitionValue(union) - (getCoalitionValue(c1) + getCoalitionValue(c2));
//		System.out.println("I: "+(getCoalitionValue(c1) + getCoalitionValue(c2))+" U: "+getCoalitionValue(union)+" G: "+gain);
//		if (union.stream().anyMatch(a -> a.toString().startsWith("job"))) {
//			System.out.println("There is "+union);
//			gain += 1;
//		}
		
//		System.out.println("Gain: "+gain+" coalition: "+union);
//		System.out.println("Union "+getCoalitionValue(union));
//		System.out.println("C1 "+getCoalitionValue(c1));
//		System.out.println("C2 "+getCoalitionValue(c2));
		
		return gain;
	}
	private double getCoalitionValue(Set node){
		double value = 0;
		
		cfCoalition coalition = convertSetCoaliton(node);
//		System.out.println(coalition);
		value = characteristicFunction.getCoalitionValue(coalition);
		
		return value;
	}
	
//	private cfCoalitionStructure convertSetCS(MutableGraph<Set> graph){
//		cfCoalitionStructure cs = new cfCoalitionStructure("cs");
//		
//		for (Set node : graph.nodes())
//			cs.addCoalition(convertSetCoaliton(node));
//		
//		return cs;
//	}
	private cfCoalitionStructure convertSetCS(Set<Set> SetCS){
		cfCoalitionStructure cs = new cfCoalitionStructure();
		
		for (Set node : SetCS) {
			cfCoalition c = convertSetCoaliton(node);
			c.setValue(characteristicFunction.getCoalitionValue(c));
			cs.addCoalition(c);
		}
		
		return cs;
	}
	private cfCoalition convertSetCoaliton(Set node){
		cfCoalition c = new cfCoalition("c");
		
		String strCoalition = node.toString().replaceAll("\\[|\\]| ", "");
//		String strCoalition = node.toString().replace("\\[(.*?)\\]", "$1");
		
		String[] agents = strCoalition.split(",");
		
		for (String string : agents) {
//			c.addAgent(new cfAgent(string, "none"));
			c.addAgent(SetOfagent.get(string));
		}
		
		if (!coalitionClass.containsKey(c.getId()))
			coalitionClass.put(c.getId(), c);
		if (!coalitionSet.containsKey(c.getId()))
			coalitionSet.put(c.getId(), node);
		
		return c;
	}
	

	
	private class PartitionLinkage{
		private Table<String, String, Double> partitionLinkage;
		private Pair 	bestIndex;
		private double 	bestValue;
		private Cell<String, String, Double> best;
		
		public PartitionLinkage(Set<Set> csSingletons, MutableGraph<Set> interationGraph){
			partitionLinkage = HashBasedTable.create();
			initalisePLTable(csSingletons, interationGraph);
		}
		
		public Pair argmax() {
			return new Pair(best.getRowKey(), best.getColumnKey());
		}
		
		public double max() {
			return best.getValue();
		}
		
		private void initalisePLTable(Set<Set> csSingletons, MutableGraph<Set> interationGraph) {	
			ArrayList<Set> targetList = Lists.newArrayList(csSingletons);
			
			for (int i = 0; i < targetList.size(); i++) {
				cfCoalition c1 = convertSetCoaliton(targetList.get(i));
//				for (int j = i+1; j < targetList.size(); j++) {
				for (int j = i; j < targetList.size(); j++) {
					cfCoalition c2 = convertSetCoaliton(targetList.get(j));
					if (isVerticesConnected(c1, c2, interationGraph)) {
						partitionLinkage.put(c1.getId(), c2.getId(), lf(c1, c2));
					}
					else {
						partitionLinkage.put(c1.getId(), c2.getId(), Double.NEGATIVE_INFINITY);
					}									
				}
			}
			
			updateMax();
		}
		
		public void updatePL(Set<Set> cs0, MutableGraph<Set> interationGraph) {
			ArrayList<Set> targetList = Lists.newArrayList(cs0);
			
			for (int i = 0; i < targetList.size(); i++) {
				cfCoalition c1 = convertSetCoaliton(targetList.get(i));
//				for (int j = i+1; j < targetList.size(); j++) {
				for (int j = i; j < targetList.size(); j++) {
					cfCoalition c2 = convertSetCoaliton(targetList.get(j));
					if (isVerticesConnected(c1, c2, interationGraph)) {
						partitionLinkage.put(c1.getId(), c2.getId(), lf(c1, c2));
					}
					else {
						partitionLinkage.put(c1.getId(), c2.getId(), Double.NEGATIVE_INFINITY);
					}									
				}
			}
			
			updateMax();
		}
		
		private void updateMax() {
//			System.out.println("Fez Tabela "+partitionLinkage.cellSet());
//			try {
			Ordering<Cell<String, String, Double>> o = new Ordering<Cell<String, String, Double>>() {
			    public int compare(Cell<String, String, Double> left, Cell<String, String, Double> right) {
			        return Doubles.compare(left.getValue(), right.getValue());
			    }
			};
			best = o.max(partitionLinkage.cellSet());
//			} catch (Exception e) {
//				System.out.println("Deu pau: "+e.getMessage());
//				System.out.println(partitionLinkage.cellSet());
//				e.printStackTrace();
//				int g = 5/0;
//			}
		}
		
		/*private boolean isVerticesConnected(Set c1, Set c2, MutableGraph<String> graph) {
			for (EndpointPair<String> edge : graph.edges()) {
				if (	(c1.getId().equals(edge.nodeU()) && c2.getId().equals(edge.nodeV()))
					||	(c1.getId().equals(edge.nodeV()) && c2.getId().equals(edge.nodeU()))) {
					return true;
				}
			}
			return false;
		}*/
		private boolean isVerticesConnected(cfCoalition c1, cfCoalition c2, MutableGraph<Set> graph) {		
			for (EndpointPair<Set> edge : graph.edges()) {
				cfCoalition s1 = convertSetCoaliton(edge.nodeU());
				cfCoalition s2 = convertSetCoaliton(edge.nodeV());
				
				if (	(c1.getId().equals(s1.getId()) && c2.getId().equals(s2.getId()))
					||	(c1.getId().equals(s2.getId()) && c2.getId().equals(s1.getId()))) {
					return true;
				}
			}
			return false;
		}
	}
	
	private class Pair{
		private String idI;
		private String idJ;
		
		public Pair(String i, String j) {
			this.idI = i;
			this.idJ = j;
		}
		
		public String getI() {
			return idI;
		}
		public String getJ() {
			return idJ;
		}
	}
}
