package aadl2upaal.aadl;

import java.util.ArrayList;

public class HInterrupt {
	private HybirdProcess end;
	private ArrayList<HCommunication> comm;

	public HybirdProcess getEnd() {
		return end;
	}

	public void setEnd(HybirdProcess end) {
		this.end = end;
	}

	public ArrayList<HCommunication> getComm() {
		return comm;
	}

	public void setComm(ArrayList<HCommunication> comm) {
		this.comm = comm;
	}

}
