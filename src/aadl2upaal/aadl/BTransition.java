package aadl2upaal.aadl;

import java.util.ArrayList;

import aadl2upaal.upaal.Location;

public class BTransition {
	public Location src, dst;
	public String guard = "";
	private ArrayList<BUpdate> update;
	public String name;

	public Location getSrc() {
		return src;
	}

	public void setSrc(Location src) {
		this.src = src;
	}

	public Location getDst() {
		return dst;
	}

	public void setDst(Location dst) {
		this.dst = dst;
	}

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public ArrayList<BUpdate> getUpdate() {
		return update;
	}

	public void setUpdate(ArrayList<BUpdate> update) {
		this.update = update;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
