package aadl2upaal.aadl;

public class HContinuous {
    private AVar left;
    private AVar right;
    private int right_int;
    private boolean isRightInt;
    public int rank;//导数的阶
    public String alt="";

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

    public void setRight(int i) {
        isRightInt = true;
        right_int = i;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        String ret = "";
        if (isRightInt) {
            if (rank == 1) {
                ret += left.getName() + "'==" + right_int;
            } else if (rank == 0) {
                ret += left.getName() + "=" + right_int;
            }else if(rank==-1){
                ret+=left.getName()+alt+right_int;
            }
        } else {
            if (rank == 1) {
                ret += left.getName() + "'==" + right.getName();
            } else if (rank == 0) {
                ret += left.getName() + "=" + right.getName();
            }else if(rank==-1){
                ret+=left.getName()+alt+right.getName();;
            }
        }

        return ret;
    }
}
