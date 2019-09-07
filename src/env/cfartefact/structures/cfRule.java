package cfartefact.structures;

import java.util.Set;

import com.google.common.collect.Sets;

public class cfRule {
	public Set<String> positiveRule = null;
	public Set<String> negativeRule = null;
	public double value = 0;
	boolean hasNegation = false;

	public cfRule(String[] positiverule, String[] negativerule, double value){
		positiveRule = Sets.newHashSet(positiverule);
		negativeRule = Sets.newHashSet(negativerule);
		this.value = value;
	}
}