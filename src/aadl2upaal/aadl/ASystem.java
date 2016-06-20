package aadl2upaal.aadl;

import java.util.ArrayList;

import aadl2upaal.visitor.NodeVisitor;

public class ASystem extends ANode {
	private ArrayList<SubComp> subComps;
	private Connection conn;

	public ASystem(String name) {
		super(name);
		subComps = new ArrayList<SubComp>();
	}

	public void addSubComp(SubComp sc) {
		subComps.add(sc);
	}

	public ArrayList<SubComp> getSubComps() {
		return subComps;
	}

	public void setSubComps(ArrayList<SubComp> subComps) {
		this.subComps = subComps;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		// TODO Auto-generated method stub

	}

}
