package aadl2upaal.upaal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import aadl2upaal.aadl.CompImpl;
import aadl2upaal.aadl.Connection;
import aadl2upaal.aadl.DataPort;
import aadl2upaal.aadl.Flow;
import aadl2upaal.visitor.UpaalWriter;

public class Template extends UNode {
	private CompImpl impl;
	public List<Location> locs = new Vector<Location>();
	public List<Transition> trans = new Vector<Transition>();
	public List<Channel> chans = new Vector<Channel>();
	public Map<Channel, Channel> conn = new HashMap<Channel, Channel>();
	public String declarations = "";
	int locCtr = 1;
	private boolean hasBlessAnnex = false;

	public Template(String name) {
		super(name);
	}

	public Template(CompImpl impl) {
		this.impl = impl;
	}

//	public Transition addTransition(Connection c) {
//		return addTransition(c.getSourcePort(), c.getSinkPort(), c.latency,
//				c.toString());
//	}
//
//	public Transition addTransition(Flow f) {
//		return addTransition(f.getSourcePort(), f.getSinkPort(), null,
//				f.toString());
//	}

	public void addConnection(Channel from, Channel to) {
		this.conn.put(from, to);
	}

	public void addChannels(Channel c) {
		this.chans.add(c);
	}

//	public Transition addTransition(DataPort src, DataPort dst,
//			Integer latency, String name) {
//		return addTransition(new Location(src), new Location(dst), latency,
//				name);
//	}

	public Transition addTransition(Location src, Location dst,
			Integer latency, String name) {
		Transition t = new Transition(src, dst, latency, name);
		int index = trans.indexOf(t);
		if (index != -1)
			return trans.get(index);
		trans.add(t);
		index = locs.indexOf(src);
		if (index != -1)
			t.src = locs.get(index);
		else {
			locs.add(t.src);
			t.src.id = locCtr++;
		}
		index = locs.indexOf(dst);
		if (index != -1)
			t.dst = locs.get(index);
		else {
			locs.add(t.dst);
			t.dst.id = locCtr++;
		}
		return t;
	}

	public CompImpl getImpl() {
		return impl;
	}

	public List<Location> getLocs() {
		return locs;
	}

	public void setLocs(List<Location> locs) {
		this.locs = locs;
	}

	public List<Transition> getTrans() {
		return trans;
	}

	public void setTrans(List<Transition> trans) {
		this.trans = trans;
	}

	public boolean isHasBlessAnnex() {
		return hasBlessAnnex;
	}

	public void setHasBlessAnnex(boolean hasBlessAnnex) {
		this.hasBlessAnnex = hasBlessAnnex;
	}

	public String getDeclarations() {
		return declarations;
	}

	public void setDeclarations(String declarations) {
		this.declarations = declarations;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Template))
			return false;
		Template t = (Template) o;
		return impl.equals(t.impl);
	}

	public String toString() {
		return impl.toString().replaceAll("\\.", UpaalWriter.sep);
	}
}