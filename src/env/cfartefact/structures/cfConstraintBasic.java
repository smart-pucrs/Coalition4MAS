package cfartefact.structures;

public class cfConstraintBasic extends cfConstraint {
	private String[] constraint;

	public cfConstraintBasic(String agentWhoAdded, String[] constraint){
		super(agentWhoAdded);
		this.constraint = constraint;
	}

	public String[] getConstraint() {
		return constraint;
	}
	public String[] getTotalConstraint() {
		String[] totalConstraints = new String[constraint.length + 1];
		System.arraycopy(constraint, 0, totalConstraints, 0, constraint.length);
		totalConstraints[totalConstraints.length-1] = getAgentWhoAdded();
		return totalConstraints;
	}
}