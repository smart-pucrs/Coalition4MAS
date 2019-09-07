package cfartefact.characteristicFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfartefact.structures.cfAgent;
import cfartefact.structures.cfCoalition;

public abstract class CharacteristicFunction
{
	private List<cfAgent> setOfAgents;
	private Map<Integer, cfCoalition> maskToCoalition;

	public void setAgents(List<cfAgent> setOfAgents) {
		this.setOfAgents 		= setOfAgents;
		this.maskToCoalition 	= new HashMap<Integer, cfCoalition>();
	}

    public abstract void putAdditionalInformation(Object...information);
	public abstract void removeAdditionalInformation(Object information);

    public abstract void generateValues(int numOfAgents);

    public abstract void clear();

    public double getCoalitionValue(int coalitionInBitFormat) {
    	if (!this.maskToCoalition.containsKey(coalitionInBitFormat)) {
    		cfCoalition c = new cfCoalition();

    		int[] curCoalition = convertCombinationFromBitToByteFormat(coalitionInBitFormat, setOfAgents.size());

			for(int j=0; j<curCoalition.length; j++)
				c.addAgent(setOfAgents.get(curCoalition[j]-1));

    		this.maskToCoalition.put(coalitionInBitFormat, c);
    	}

    	return getCoalitionValue(this.maskToCoalition.get(coalitionInBitFormat));
	}

	/**
	 * Method to convert a combination from bit format to int format (e.g. given 4 agents, 0110 becomes {2,3})
	 * @author Talal Rahwan
	 */
	private int[] convertCombinationFromBitToByteFormat(int combinationInBitFormat, int numOfAgents){
		int combinationSize = Integer.bitCount(combinationInBitFormat);

		int[] combinationInByteFormat = new int[combinationSize];
		int j=0;
		for(int i=0; i<numOfAgents; i++){
			if ((combinationInBitFormat & (1<<i)) != 0){
				combinationInByteFormat[j]= (int)(i+1);
				j++;
			}
		}
		return( combinationInByteFormat );
	}

    public abstract double getCoalitionValue(cfCoalition coalition);

	public abstract double[] getCoalitionValues();

	public void storeToFile(String fileName){
		getCoalitionValues();
	}
	public void readFromFile(String fileName){

	}
}