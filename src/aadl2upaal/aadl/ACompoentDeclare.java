package aadl2upaal.aadl;

import java.util.ArrayList;

import aadl2upaal.visitor.NodeVisitor;

public class ACompoentDeclare extends ANode {

	public ArrayList<APort> ports;
	private ArrayList<AProperties> props;

	public ACompoentDeclare(String name) {
		super(name);
	    ports = new ArrayList<APort>();
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public ArrayList<APort> getPorts() {
		return ports;
	}

	public void setPorts(ArrayList<APort> ports) {
		this.ports = ports;
	}

	public ArrayList<AProperties> getProps() {
		return props;
	}

	public void setProps(ArrayList<AProperties> props) {
		this.props = props;
	}

}
