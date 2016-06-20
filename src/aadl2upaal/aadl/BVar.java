package aadl2upaal.aadl;

public class BVar extends AVar {
	private double initVal;

	public BVar(String name, String type) {
		super(name, type);
		// TODO Auto-generated constructor stub
	}

	public double getInitVal() {
		return initVal;
	}

	public void setInitVal(double initVal) {
		this.initVal = initVal;
	}

}
