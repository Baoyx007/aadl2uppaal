package aadl2upaal.aadl;

import aadl2upaal.visitor.*;
import java.util.*;

public class Flow extends ANode implements FlowElem {
	public CompImpl context = new CompImpl("none");
	public List<Connection> connections = new Vector<Connection>();

	public Flow(String name) {
		super(name);
	}

	public Flow(Connection conn) {
		super(conn.getName());
		connections.add(conn);
	}

	public DataPort getSourcePort() {
		return connections.get(0).getSourcePort();
	}

	public DataPort getSinkPort() {
		return connections.get(connections.size() - 1).getSinkPort();
	}

	public void accept(NodeVisitor visitor) {
		visitor.processFlow(this);
	}

	public String fullName() {
		return context.toString() + UpaalWriter.sep + name;
	}

	public String upaalName() {
		return context.name + UpaalWriter.sep + name;
	}

	public String toString() {
		return fullName();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Flow))
			return false;
		Flow f = (Flow) o;
		// System.out.println("this flow: " + this);
		// System.out.println("other port: " + f);
		if (!context.equals(f.context))
			return false;
		return name.equals(f.name);
	}
}
