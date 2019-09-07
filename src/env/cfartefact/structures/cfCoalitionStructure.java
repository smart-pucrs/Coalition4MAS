package cfartefact.structures;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class cfCoalitionStructure {
	private List<cfCoalition> coalitions;
	private String name;
	private Double value;

	public cfCoalitionStructure(){
		initialise("none");
	}
	public cfCoalitionStructure(String name){
		initialise(name);
	}

	private void initialise(String name) {
		this.coalitions = new ArrayList<>();
		this.name 		= name;
		this.value 		= Double.NaN;
	}

	public void addCoalition(cfCoalition coalition) {
		this.coalitions.add(coalition);
		this.value += coalition.getValue();
	}

	public cfCoalition[] getCoalitions() {
		return this.coalitions.toArray(new cfCoalition[this.coalitions.size()]);
	}
	public cfCoalition getValuableCoalition() {
		coalitions.sort(new Comparator<cfCoalition>() {
			@Override
			public int compare(cfCoalition o1, cfCoalition o2) {
				return o1.getValue() >= o2.getValue()?-1 :1;
			}
		});

		return coalitions.get(0);
	}

	public String getCSName(){
		return this.name;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Coalition Structure "+name+": ");

		sb.append('[');
		for (cfCoalition coaliton : coalitions)
			sb.append(coaliton);
		sb.append(']');

		return sb.toString();
	}
}