package aadl2upaal.aadl;

import java.util.ArrayList;

public class UncertaintyAnnex extends Annex {

	private ArrayList<UVar> vars;
	private ArrayList<String> queries;
	private ArrayList<Distribution> dists;

	public UncertaintyAnnex(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<UVar> getVars() {
		return vars;
	}

	public void setVars(ArrayList<UVar> vars) {
		this.vars = vars;
	}

	public ArrayList<String> getQueries() {
		return queries;
	}

	public void setQueries(ArrayList<String> queries) {
		this.queries = queries;
	}

	public ArrayList<Distribution> getDists() {
		return dists;
	}

	public void setDists(ArrayList<Distribution> dists) {
		this.dists = dists;
	}

}
