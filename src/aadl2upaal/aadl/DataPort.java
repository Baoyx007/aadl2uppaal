package aadl2upaal.aadl;

import aadl2upaal.visitor.*;

public class DataPort extends ANode implements FlowElem {
	public CompImpl context = null;

	public DataPort(String name, CompImpl context) {
		super(name);
		this.context = context;
	}

	public DataPort getSourcePort() {
		return this;
	}

	public DataPort getSinkPort() {
		return this;
	}

	public void accept(NodeVisitor visitor) {
		visitor.processDataPort(this);
	}

	public boolean equals(Object o) {
		if (!(o instanceof DataPort))
			return false;
		DataPort d = (DataPort) o;
		// System.out.println("this port: " + this);
		// System.out.println("other port: " + d);
		if (!context.equals(d.context))
			return false;
		return name.equals(d.name);
	}

	public CompImpl getContext() {
		return context;
	}

	public void setContext(CompImpl context) {
		this.context = context;
	}

	public String fullName() {
		return context.toString() + UpaalWriter.sep + name;
	}

	public String upaalName() {
		return context.name + UpaalWriter.sep + name;
	}

	public String toString() {
		return upaalName();
	}
}