package cfartefact.structures;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class cfAgent {
	private String name = "";
	private String type = "";
	private ArrayList<String> skills = new ArrayList<String>();

	public cfAgent(String name){
		initialise(name, "none");
	}
	public cfAgent(String name, String type){
		initialise(name, type);
	}

	private void initialise(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public ArrayList<String> getSkills() {
		return this.skills;
	}
	public boolean hasSkill(String skill) {
		return this.skills.stream().anyMatch(s -> s.equals(skill));
	}

	public void addSkill(String skill) {
		this.skills.add(skill);
	}
	public void addSkills(ArrayList<String> skills) {
		this.skills.addAll(skills);
	}

	@Override
	public boolean equals(Object obj) {
		return obj.hashCode() == this.hashCode();
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name+"["+this.type+" ");
//		this.skills.forEach(sb::append);
		sb.append(this.skills.stream().map(i -> i.toString()).collect(Collectors.joining(",")));
		sb.append("] ");

		return sb.toString();
	}
}