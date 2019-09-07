package cfartefact.characteristicFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import cfartefact.structures.cfAgent;
import cfartefact.structures.cfCoalition;
import cfartefact.structures.cfRule;

public class vTesteClink extends CharacteristicFunction{
	private Map<String, Double> coalitionValues = new LinkedHashMap<>();
	private ArrayList<cfRule> 	rules 			= new ArrayList<>();;

	@Override
	public void putAdditionalInformation(Object... information) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAdditionalInformation(Object information) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateValues(int numOfAgents) {
		rules.add(new cfRule(new String[]{"a1","a2"}, new String[]{}, -3));
		rules.add(new cfRule(new String[]{"a1","a3"}, new String[]{}, 10));
		rules.add(new cfRule(new String[]{"a1","a4"}, new String[]{}, 14));
		rules.add(new cfRule(new String[]{"a2","a3"}, new String[]{}, 6));
		rules.add(new cfRule(new String[]{"a2","a4"}, new String[]{}, 10));
		rules.add(new cfRule(new String[]{"a3","a4"}, new String[]{}, 20));
		rules.add(new cfRule(new String[]{"a3","a4","a1"}, new String[]{}, 19));
		rules.add(new cfRule(new String[]{"a3","a4","a2"}, new String[]{}, 13));
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getCoalitionValue(cfCoalition coalition) {
		if (!coalitionValues.containsKey(coalition.generateHashCode()))
    		coalitionValues.put(coalition.generateHashCode(), applyRules(coalition));    	
    		
        return coalitionValues.get(coalition.generateHashCode());
	}
	
	private double applyRules(cfCoalition coalition){
    	double value = 0;    	   
    	for(cfRule rule : rules)
    		if((rule.positiveRule.size() == coalition.getAgentsName().size())&&(Sets.difference(rule.positiveRule, coalition.getAgentsName()).size() == 0))
    			value += rule.value;  
    	return value;
    }
	/*private double applyRules(cfCoalition coalition){
    	double value = 0;    	   
    	for(cfRule rule : rules)
    		if(Sets.difference(rule.positiveRule, coalition.getAgentsName()).size() == 0)
    			if((rule.negativeRule.size()==0) || (Sets.difference(rule.negativeRule, coalition.getAgentsName()).size() > 0))
    				value += rule.value;  
    	return value;
    }*/

	@Override
	public double[] getCoalitionValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeToFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFromFile(String fileName) {
		// TODO Auto-generated method stub
		
	}

}
