package aadl2upaal.aadl;

import aadl2upaal.visitor.TypeMapping;

public class Hassignment {
	private AVar var;
	private int val;

	public AVar getVar() {
		return var;
	}

	public void setVar(AVar var) {
		this.var = var;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {

		this.val = val;
	}

	@Override
	public String toString() {
		String ret="";
		ret+= var.getName()+"="+val;
		return ret;
	}
}
