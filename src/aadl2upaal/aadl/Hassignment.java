package aadl2upaal.aadl;


public class Hassignment {
    private AVar var;
    private int val;
    private double val_d;
    private boolean isDouble = false;
    public String right = "";

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

    public void setVal(double val) {
        isDouble = true;
        val_d = val;
    }

    public double getVal_d() {
        return val_d;
    }

    @Override
    public String toString() {
        String ret = "";
        if (right.length() > 0) {
            ret += var.getName() + " = " + right;
        } else {
            if (isDouble) {
                ret += var.getName() + "=" + val_d;
            } else {
                ret += var.getName() + "=" + val;
            }
        }
        return ret;
    }
}
