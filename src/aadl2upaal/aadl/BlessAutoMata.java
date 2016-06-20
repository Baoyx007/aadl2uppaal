package aadl2upaal.aadl;

import java.util.ArrayList;

import aadl2upaal.upaal.Location;
import aadl2upaal.upaal.Transition;

public class BlessAutoMata {
	private ArrayList<Location> locs;
	private ArrayList<Transition> trans;

	public BlessAutoMata() {
		locs = new ArrayList<>();
		trans = new ArrayList<>();
	}

	public ArrayList<Location> getLocs() {
		return locs;
	}

	public void setLocs(ArrayList<Location> locs) {
		this.locs = locs;
	}

	public ArrayList<Transition> getTrans() {
		return trans;
	}

	public void setTrans(ArrayList<Transition> trans) {
		this.trans = trans;
	}

}
