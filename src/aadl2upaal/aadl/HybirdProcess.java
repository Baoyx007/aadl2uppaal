package aadl2upaal.aadl;

import java.util.ArrayList;

public class HybirdProcess {
	private String name;
	private Boolean skip=false;
	private ArrayList<HContinuous> evolutions;
    public HChoice choice;
	private HInterrupt interrupt;
	private ArrayList<Hassignment> asssigments;
    public boolean isRepete=false;
	public boolean isIinitial = false;
    public HybirdProcess subProcess=null;

    public HybirdProcess(){
        evolutions = new ArrayList<HContinuous>();
        asssigments = new ArrayList<Hassignment>();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSkip() {
		return skip;
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

	public ArrayList<HContinuous> getEvolutions() {
		return evolutions;
	}

	public void setEvolutions(ArrayList<HContinuous> evolutions) {
		this.evolutions = evolutions;
	}

	public HInterrupt getInterrupt() {
		return interrupt;
	}

	public void setInterrupt(HInterrupt interrupt) {
		this.interrupt = interrupt;
	}

	public ArrayList<Hassignment> getAsssigments() {
		return asssigments;
	}

	public void setAsssigments(ArrayList<Hassignment> asssigments) {
		this.asssigments = asssigments;
	}

    public String getStringAssignment(){
        String ret="";
        boolean isFirst=true;
        for(Hassignment ass:asssigments){
            if(isFirst){
                ret+=ass.toString();
                isFirst=false;
            }else{
                ret+=", "+ass.toString();
            }
        }
        return ret;
    }

    public String getStringContinuous(){
        String ret="";
        boolean isFirst=true;
        for(HContinuous con:this.evolutions){
            if(isFirst){
                ret+=con.toString();
                isFirst=false;
            }else{
                ret+="&amp;&amp; "+con.toString();
            }
        }
        return ret;
    }
}
