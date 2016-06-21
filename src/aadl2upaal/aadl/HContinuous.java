package aadl2upaal.aadl;

public class HContinuous {
	private AVar left;
	private AVar right;
	public int rank;//导数的阶
	public AVar getLeft() {
		return left;
	}
	public void setLeft(AVar left) {
		this.left = left;
	}
	public AVar getRight() {
		return right;
	}
	public void setRight(AVar right) {
		this.right = right;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

    @Override
    public String toString() {
        String ret="";
        if(rank==1){
            ret+=left.getName()+"'=="+right.getName();
        }
        return ret;
    }
}
