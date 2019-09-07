package cfartefact.structures;

public class cfConstraintSize extends cfConstraint{
	private long size 	= 0;
	private String type = "";

	public cfConstraintSize(String agentWhoAdded, long size, String type){
		super(agentWhoAdded);
		this.size = size;
		this.type = type;
	}

	public long getSize() {
		return size;
	}
	public String getType() {
		return type;
	}
}