package cfartefact.characteristicFunction;

import cfartefact.structures.cfCoalition;

public interface ICharacteristicFunction
{
    public void putAdditionalInformation(Object...information);
	public void removeAdditionalInformation(Object information);

    public void generateValues(int numOfAgents);

    public void clear();

    public double getCoalitionValue(cfCoalition coalition);

	public double[] getCoalitionValues();

	public void storeToFile(String fileName);
	public void readFromFile(String fileName);
}