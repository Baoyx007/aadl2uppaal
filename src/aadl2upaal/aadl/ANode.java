package aadl2upaal.aadl;

import aadl2upaal.visitor.NodeVisitor;

/** Represents an AADL node */
public abstract class ANode {
	public String name;

	public ANode(String name) {
		this.name = name;
	}

	public abstract void accept(NodeVisitor visitor) ;

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
