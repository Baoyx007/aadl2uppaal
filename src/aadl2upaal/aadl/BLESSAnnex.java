package aadl2upaal.aadl;

import java.util.ArrayList;
import java.util.Map;

import aadl2upaal.upaal.Location;

public class BLESSAnnex extends Annex {
	private ArrayList<Location> locs;
	private ArrayList<BTransition> trans;
	//private ArrayList<String> asserts;
	private ArrayList<AVar> invariant;
	private ArrayList<BVar> variables;
	public Map<String,String> asserts;

	public BLESSAnnex(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Location> getLocs() {
		return locs;
	}

	public void setLocs(ArrayList<Location> locs) {
		this.locs = locs;
	}

	public ArrayList<BTransition> getTrans() {
		return trans;
	}

	public void setTrans(ArrayList<BTransition> trans) {
		this.trans = trans;
	}

	//public ArrayList<String> getAsserts() {
//		return asserts;
//	}

//	public void setAsserts(ArrayList<String> asserts) {
//		this.asserts = asserts;
//	}
//
	public ArrayList<AVar> getInvariant() {
		return invariant;
	}

	public void setInvariant(ArrayList<AVar> invariant) {
		this.invariant = invariant;
	}

	public ArrayList<BVar> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<BVar> variables) {
		this.variables = variables;
	}

}
