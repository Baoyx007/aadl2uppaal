package aadl2upaal.aadl;

import aadl2upaal.visitor.NodeVisitor;

public class APort extends ANode {
	public static int in = 1;
	public static int out = -1;
	public static int inout = 0;
	// -1 out
	// 0 in out
	// 1 in
	private int direction = 0;

	public APort(String name) {
		super(name);
		direction = 0;
	}

	public APort(String name, int direction) {
		super(name);
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public void accept(NodeVisitor visitor) {

	}

	public String getType() {
		return AType.None;
	}
	// @Override
	// public DataPort getSourcePort() {
	// return new DataPort(this.name, context);
	// }
	//
	// @Override
	// public DataPort getSinkPort() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
