package aadl2upaal.aadl;


public class Hassignment {
	private AVar var;
	private int val;
    public String right="";

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
        if(right.length()>0){
           ret+=var.getName()+" = "+ right;
        }else{
           ret+= var.getName()+"="+val;

        }
		return ret;
	}
}
