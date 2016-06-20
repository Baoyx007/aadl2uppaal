package aadl2upaal.aadl;

import aadl2upaal.visitor.NodeVisitor;

public class ACompoent extends ANode {

	private ACompoentDeclare compoentDeclare;
	private ACompoentImpl compoentImpl;

	public ACompoent(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept(NodeVisitor visitor) {
		// TODO Auto-generated method stub

	}

	public ACompoentDeclare getCompoentDeclare() {
		return compoentDeclare;
	}

	public void setCompoentDeclare(ACompoentDeclare compoentDeclare) {
		this.compoentDeclare = compoentDeclare;
	}

	public ACompoentImpl getCompoentImpl() {
		return compoentImpl;
	}

	public void setCompoentImpl(ACompoentImpl compoentImpl) {
		this.compoentImpl = compoentImpl;
	}

}
