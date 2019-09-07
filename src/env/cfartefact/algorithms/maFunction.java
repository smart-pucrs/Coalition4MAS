package cfartefact.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import cfartefact.characteristicFunction.CharacteristicFunction;
import cfartefact.structures.cfAgent;
import cfartefact.structures.cfCoalition;
import cfartefact.structures.cfConstraintSize;
import cfartefact.structures.cfRule;

public class maFunction extends CharacteristicFunction{
	private Map<String, Double> coalitionValues = new LinkedHashMap<>();
	private Set<cfRule> rules;
	
	private double numberOfAgents;
	private Set<cfConstraintSize> sizeConstraints;
	private double valueMaxSubadditive = 10;
	
	public maFunction(double numberOfAgents, Set<cfRule> rules, Set<cfConstraintSize> sizeConstraints){
		this.numberOfAgents 	= numberOfAgents;
		this.sizeConstraints 	= sizeConstraints;
		this.rules 				= rules;
	}	
	
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getCoalitionValue(cfCoalition coalition) {
//		long permittedSize = this.sizeConstraints.iterator().next().getSize();
//		if (coalition.getAgents().size() < permittedSize)
//			return Double.NEGATIVE_INFINITY;
		
		if (!coalitionValues.containsKey(coalition.generateHashCode())){
//			double coalitionValue = subadditive(coalition)+superadditive(coalition);
			double coalitionValue = characteristicFunction(coalition);
//			System.out.println("COalition value: "+coalitionValue+"C "+coalition.toString());
    		coalitionValues.put(coalition.generateHashCode(), coalitionValue);    	
		}
    		
//		System.out.println("CoalitionValue: "+coalitionValues.get(coalition.generateHashCode())+" C: "+coalition.toString());	
        return coalitionValues.get(coalition.generateHashCode());
	}
	
	/*private double characteristicFunction(cfCoalition coalition) {
		if (passedOnCheckTypeConstraints(coalition))
			return subadditive(coalition)+superadditive(coalition);
		else 
			return 0;
	}*/
	private double characteristicFunction(cfCoalition coalition) {
		double punishmentValue = constraintsPunishment(coalition);
		if (punishmentValue < 0) 
			return punishmentValue;
		
		return subadditive(coalition)+superadditive(coalition);
	}
	private double superadditive(cfCoalition coalition){
		double value = 0;   
    	for(cfRule rule : rules)
    		if(Sets.difference(rule.positiveRule, coalition.getAgentsName()).size() == 0)
    			if((rule.negativeRule.size()==0) || (Sets.difference(rule.negativeRule, coalition.getAgentsName()).size() > 0)) {
    				value += rule.value;
    			}
    	return value;
	}
	/*private double subadditive(cfCoalition coalition){
		double value = 0;
		
//		for (Iterator iterator = coalition.getAgents().iterator(); iterator.hasNext();) {
//			cfAgent agent = (cfAgent) iterator.next();
//			
//		}
		
//		type punishment
		
//		free agents punishment
		value += subCoalitionSize(coalition);
		
//		System.out.println("Sub: "+value);
		return value;
	}*/	
	private double subadditive(cfCoalition coalition){
		double value = 0;
		
//		long permittedSize = this.sizeConstraints.iterator().next().getSize();
//		if (coalition.getAgents().size() > permittedSize)
//		if (checkSizeConstraints(coalition))
			value += subCoalitionSize(coalition);
		
//		System.out.println("Sub: "+value);
		return value;
	}
	private double subCoalitionSize(cfCoalition coalition){
		double value = 0;
		value = -1*(Math.pow(coalition.getAgents().size()-1,3.0));
//		value = -1*(Math.pow(coalition.getAgents().size(),1.6));
		return value; 
	}
	
//	Check if the coalition formed is good
	private boolean passedOnCheckTypeConstraints(cfCoalition coalition) {	
		for (cfConstraintSize cfSize : sizeConstraints) {	
			if (coalition.getNumberByType(cfSize.getType()) < cfSize.getSize())
				return false;
		}
//		hard constraint
		if (coalition.getNumberByType("job") != 1)
			return false;
		
//		System.out.println("drone "+coalition.getNumberByType("drone"));
//		System.out.println("car "+coalition.getNumberByType("car"));
//		System.out.println("motorcycle "+coalition.getNumberByType("motorcycle"));
//		System.out.println("truck "+coalition.getNumberByType("truck"));
		return true;
	}
	/*private double constraintsPunishment(cfCoalition coalition) {
		int 	numberOfPunishment 	= 0;
		double 	punishment 			= 0;
		for (cfConstraintSize cfSize : sizeConstraints) {	
			double temp = cfSize.getSize() - coalition.getNumberByType(cfSize.getType());
			
			numberOfPunishment += (int) Math.max(temp, 0.0);
		}
//		numberOfPunishment = Math.max(numberOfPunishment, 0);
		System.out.println("Number of Punishment: "+numberOfPunishment);
		
//		punishment += -1*((Math.pow(3,numberOfPunishment)-1));
//		punishment += -1*100*numberOfPunishment*coalition.getAgents().size();
//		punishment += -1*((Math.pow(coalition.getAgents().size(),numberOfPunishment)-1));
//		punishment = (Math.pow(2,numberOfPunishment)-1);
		punishment = numberOfPunishment;
		
//		if (coalition.getNumberByType("job") != 1)
//			punishment += 10;
		if (coalition.getNumberByType("job") == 1)
			punishment = 0;
		
		if (punishment > 0)
			punishment += (Math.pow(coalition.getAgents().size()-1,2.0));
			
		System.out.println("Punishment coalition: "+punishment);
		return punishment;
	}*/
	private double constraintsPunishment(cfCoalition coalition) {
		if (coalition.getNumberByType("job") != 1)
			return -10.0;
		
		int 	numberOfPunishment 	= 0;
		for (cfConstraintSize cfSize : sizeConstraints) {	
			double temp = cfSize.getSize() - coalition.getNumberByType(cfSize.getType());
			
			numberOfPunishment += (int) Math.max(temp, 0.0);
		}
		return -1*numberOfPunishment;
	}
//	Check if it has the size permited
	private boolean checkSizeConstraints(cfCoalition coalition) {
		for (cfConstraintSize cfSize : sizeConstraints) {
			if (cfSize.getType().equals("none")) {
				if (coalition.getAgents().size() >= cfSize.getSize())
					return true;
			}
		}
		return false;
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub		
	}

	@Override
	public double[] getCoalitionValues() {
		// TODO Auto-generated method stub
		return null;
	}
}
