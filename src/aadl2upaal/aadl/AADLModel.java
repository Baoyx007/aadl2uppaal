package aadl2upaal.aadl;

import aadl2upaal.visitor.*;

import java.util.*;

public class AADLModel extends ANode {
	public ArrayList<Flow> flows = null;
	public ArrayList<Connection> connections = null;
	public CompImpl topSys = null;
	private ASystem asystem = null;
	private ArrayList<DataPort> ports = null;
	private BlessAutoMata ba = new BlessAutoMata();
	private HybirdAnnex ha ;
	public ArrayList<ACompoent> comps =null;

	public AADLModel(String name) {
		super(name);
		connections = new ArrayList<Connection>();
		ports = new ArrayList<DataPort>();
		flows = new ArrayList<Flow>();
		comps = new ArrayList<ACompoent>();
	}

	public ArrayList<DataPort> getPorts() {
		return ports;
	}

	public void setPorts(ArrayList<DataPort> ports) {
		this.ports = ports;
	}

	public void addPort(DataPort ap) {
		ports.add(ap);
	}

	public void addConnection(Connection conn) {
		connections.add(conn);
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}

	public void accept(NodeVisitor visitor) {
	}

	public List<Flow> getFlows() {
		return flows;
	}

	public void setFlows(ArrayList<Flow> flows) {
		this.flows = flows;
	}

	public CompImpl getTopSys() {
		return topSys;
	}

	public void setTopSys(CompImpl topSys) {
		this.topSys = topSys;
	}

	public ASystem getAsystem() {
		return asystem;
	}

	public void setAsystem(ASystem asystem) {
		this.asystem = asystem;
	}

	public BlessAutoMata getBa() {
		return ba;
	}

	public void setBa(BlessAutoMata ba) {
		this.ba = ba;
	}

	public void conn2Flow() {
		for (Iterator<Connection> iterator = connections.iterator(); iterator
				.hasNext();) {
			Connection connection = iterator.next();
			Flow flow = new Flow(connection.getName());
			flow.connections.add(connection);
			flow.context = connection.context;
			flows.add(flow);
		}
	}
	
	// public APort getPortByName(String name){
	// for (DataPort dataPort : ports) {
	// if(da)
	// }
	// for (AVar aVar : variables) {
	// if(aVar.getName().equals(name)){
	// return aVar;
	// }
	// }
	// return null;
	// }

}