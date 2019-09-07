package cfartefact.structures;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class cfTask {
	private String name = "";
	private ArrayList<String> skills = new ArrayList<String>();

	public cfTask(String name, ArrayList<String> skills){
		this.name 	= name;
		this.skills = skills;
	}

	public String getName() {
		return name;
	}
	public ArrayList<String> getSkills() {
		return this.skills;
	}
	public boolean hasSkill(String skill) {
		return this.skills.stream().anyMatch(s -> s.equals(skill));
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
		sb.append(this.name+"[");
		sb.append(this.skills.stream().map(i -> i.toString()).collect(Collectors.joining(",")));
		sb.append("] ");

		return sb.toString();
	}
}