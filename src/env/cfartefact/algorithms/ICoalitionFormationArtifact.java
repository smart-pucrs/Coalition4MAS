package cfartefact.algorithms;

import java.util.Set;

import cfartefact.structures.cfAgent;
import cfartefact.structures.cfCoalitionStructure;
import cfartefact.structures.cfConstraintBasic;
import cfartefact.structures.cfConstraintSize;
import cfartefact.structures.cfRule;
import cfartefact.structures.cfTask;

public interface ICoalitionFormationArtifact {
	public void keepAnyTimeStatistics(boolean keep);

	public void initialization();

	public cfCoalitionStructure solveCoalitionStructureGeneration(Set<cfAgent> agents, Set<cfConstraintBasic> positiveConstraints,
			Set<cfConstraintBasic> negativeConstraints, Set<cfConstraintSize> sizeConstraints, Set<cfTask> tasks, Set<cfRule> rules);

	public void getCSNow();

	public void clear();
}