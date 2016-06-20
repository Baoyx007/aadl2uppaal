package aadl2upaal.upaal;

import aadl2upaal.aadl.*;
import aadl2upaal.visitor.*;

public class Location {
	final DataPort port;
	public boolean isInitial = false;
	public boolean isCommitted = false;
	public boolean isUrgent = false;
	public Integer id = null;
	public String invariant = "";

	public Location(String name,DataPort port) {
		this.port = port;
	}
	public Location(DataPort port){
		this.port = port;
	}
	public boolean equals(Object o) {
		if (!(o instanceof Location))
			return false;
		Location l = (Location) o;
		return port.equals(l.port);
	}

	public String toString() {
		return port.toString().replaceAll("\\.", UpaalWriter.sep);
	}

	public boolean isUrgent() {
		return isUrgent;
	}

	public boolean isInitial() {
		return isInitial;
	}

	public void setInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	public boolean isCommitted() {
		return isCommitted;
	}

	public void setCommitted(boolean isCommitted) {
		this.isCommitted = isCommitted;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInvariant() {
		return invariant;
	}

	public void setInvariant(String invariant) {
		this.invariant = invariant;
	}

	public void setUrgent(boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

}