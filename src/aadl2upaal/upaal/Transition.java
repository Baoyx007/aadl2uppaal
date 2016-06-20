package aadl2upaal.upaal;

import aadl2upaal.aadl.Flow;

public class Transition {
	public Location src, dst;
	public Integer latency;// guard
	public Channel snd = null, rec = null;
	public String update = "";
	private String guard = "";
	public String name;

	public Transition(Location src, Location dst, Integer latency, String name) {
		this.src = src;
		this.dst = dst;
		this.latency = latency;
		this.name = name;
	}

	// sync
	public void setSendChannel(Flow f) {
		snd = new Channel(f);
	}

	public void setReceiveChannel(Flow f) {
		rec = new Channel(f);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Transition))
			return false;
		Transition t = (Transition) o;
		if (!src.equals(t.src))
			return false;
		return dst.equals(t.dst);
	}

	public Channel getSnd() {
		return snd;
	}

	public void setSnd(Channel snd) {
		this.snd = snd;
	}

	public Channel getRec() {
		return rec;
	}

	public void setRec(Channel rec) {
		this.rec = rec;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public String toString() {
		return name;
	}

	public String update() {
		return update;
	}
}