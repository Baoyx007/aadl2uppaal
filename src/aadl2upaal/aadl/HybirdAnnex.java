package aadl2upaal.aadl;

import aadl2upaal.visitor.NodeVisitor;

import java.util.ArrayList;

public class HybirdAnnex extends Annex {
	private ArrayList<AVar> variables;
	private ArrayList<AVar> constants;
	private ArrayList<HybirdProcess> behavior;

	public HybirdAnnex(String name) {
		super(name);
		this.variables = new ArrayList<AVar>();
		this.constants = new ArrayList<AVar>();
		this.behavior = new ArrayList<HybirdProcess>();
	}

	public ArrayList<AVar> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<AVar> variables) {
		this.variables = variables;
	}

	public ArrayList<AVar> getConstants() {
		return constants;
	}

	public void setConstants(ArrayList<AVar> constants) {
		this.constants = constants;
	}

	public ArrayList<HybirdProcess> getBehavior() {
		return behavior;
	}

	public void setBehavior(ArrayList<HybirdProcess> behavior) {
		this.behavior = behavior;
	}

	public AVar getVarByName(String name) {
		for (AVar aVar : variables) {
			if (aVar.getName().equals(name)) {
				return aVar;
			}
		}
		return null;
	}
}
