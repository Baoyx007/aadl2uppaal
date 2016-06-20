package aadl2upaal.aadl;

import aadl2upaal.visitor.NodeVisitor;
import aadl2upaal.visitor.UpaalWriter;

public class Connection extends ANode {
	public FlowElem src = null, dst = null;
	public Integer latency = null;
	public CompImpl context = new CompImpl("none");
	private APort srcPort;
	private APort dstPort;
	private ACompoentImpl compoentImpl;

	public Connection(String name) {
		super(name);
	}

	public void accept(NodeVisitor visitor) {
		visitor.processConnection(this);
	}

	public DataPort getSourcePort() {
		return src.getSinkPort();
	}

	public DataPort getSinkPort() {
		return dst.getSourcePort();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Connection))
			return false;
		Connection c = (Connection) o;
		if (!src.equals(c.src))
			return false;
		if (!dst.equals(c.dst))
			return false;
		return context.equals(c.context);
	}

	public FlowElem getSrc() {
		return src;
	}

	public void setSrc(FlowElem src) {
		this.src = src;
	}

	public FlowElem getDst() {
		return dst;
	}

	public void setDst(FlowElem dst) {
		this.dst = dst;
	}

	public Integer getLatency() {
		return latency;
	}

	public void setLatency(Integer latency) {
		this.latency = latency;
	}

	public CompImpl getContext() {
		return context;
	}

	public void setContext(CompImpl context) {
		this.context = context;
	}

	public String fullName() {
		return context.toString() + UpaalWriter.sep + name;
	}

	public String upaalName() {
		return context.name + UpaalWriter.sep + name;
	}

	public String toString() {
		return fullName();
	}

	public APort getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(APort srcPort) {
		this.srcPort = srcPort;
	}

	public APort getDstPort() {
		return dstPort;
	}

	public void setDstPort(APort dstPort) {
		this.dstPort = dstPort;
	}

	public ACompoentImpl getCompoentImpl() {
		return compoentImpl;
	}

	public void setCompoentImpl(ACompoentImpl compoentImpl) {
		this.compoentImpl = compoentImpl;
	}

}