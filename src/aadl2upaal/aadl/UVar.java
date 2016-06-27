package aadl2upaal.aadl;

public class UVar extends AVar {
	private APort applied;
	public Distribution dist;
	public String applied_var="";

	public UVar(String name, String type) {
		super(name, type);
		// TODO Auto-generated constructor stub
	}

	public APort getApplied() {
		return applied;
	}

	public void setApplied(APort applied) {
		this.applied = applied;
	}

}
