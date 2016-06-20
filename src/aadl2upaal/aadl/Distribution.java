package aadl2upaal.aadl;

import java.util.ArrayList;

public class Distribution {
	public static int Normal = 0;
	public static int Uniform = 1;
	public static int Random = 2;

	private UVar varName;
	private int distName;
	private ArrayList<Double> paras;

	public int getDistName() {
		return distName;
	}

	public void setDistName(int distName) {
		this.distName = distName;
	}

	public ArrayList<Double> getParas() {
		return paras;
	}

	public void setParas(ArrayList<Double> paras) {
		this.paras = paras;
	}

	public UVar getVarName() {
		return varName;
	}

	public void setVarName(UVar varName) {
		this.varName = varName;
	}

	@Override
	public String toString() {
		String ret="";
		switch (distName){
			case 0:
				ret+="Normal(";
				break;
			case 1:
				ret+="Uniform(";
				break;
			case 2:
				ret+="Random(";
				break;
		}
		for (Double v:paras){
			ret+=String.valueOf(v)+",";
		}
		ret=ret.substring(0,ret.length()-1);
		ret+=")";
		return ret;
	}
}
