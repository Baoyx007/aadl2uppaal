package aadl2upaal.aadl;

import java.util.ArrayList;

public class HybirdProcess {
	private String name;
	private Boolean skip;
	private ArrayList<HContinuous> evolutions;
	private HInterrupt interrupt;
	private ArrayList<Hassignment> asssigments;
	private HybirdProcess repete = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSkip() {
		return skip;
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

	public ArrayList<HContinuous> getEvolutions() {
		return evolutions;
	}

	public void setEvolutions(ArrayList<HContinuous> evolutions) {
		this.evolutions = evolutions;
	}

	public HInterrupt getInterrupt() {
		return interrupt;
	}

	public void setInterrupt(HInterrupt interrupt) {
		this.interrupt = interrupt;
	}

	public ArrayList<Hassignment> getAsssigments() {
		return asssigments;
	}

	public void setAsssigments(ArrayList<Hassignment> asssigments) {
		this.asssigments = asssigments;
	}

	public HybirdProcess getRepete() {
		return repete;
	}

	public void setRepete(HybirdProcess repete) {
		this.repete = repete;
	}
	
}
