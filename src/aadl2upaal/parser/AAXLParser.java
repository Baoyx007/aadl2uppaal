package aadl2upaal.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aadl2upaal.aadl.AADLModel;
import aadl2upaal.aadl.APort;
import aadl2upaal.aadl.ASystem;
import aadl2upaal.aadl.AType;
import aadl2upaal.aadl.AVar;
import aadl2upaal.aadl.BlessAutoMata;
import aadl2upaal.aadl.CompImpl;
import aadl2upaal.aadl.Connection;
import aadl2upaal.aadl.DataPort;
import aadl2upaal.aadl.Flow;
import aadl2upaal.aadl.FlowElem;
import aadl2upaal.aadl.HCommunication;
import aadl2upaal.aadl.HConstant;
import aadl2upaal.aadl.HContinuous;
import aadl2upaal.aadl.HInterrupt;
import aadl2upaal.aadl.HybirdAnnex;
import aadl2upaal.aadl.HybirdProcess;
import aadl2upaal.aadl.SubComp;
import aadl2upaal.upaal.Location;
import aadl2upaal.upaal.Transition;

// XML packages

public class AAXLParser {
	public File aaxlFile;

	public AAXLParser() {
		this(null);
	}

	public AAXLParser(File aaxlFile) {
		this.aaxlFile = aaxlFile;
	}

	public AADLModel createAADLModel() throws Exception {
		// NodeList nodes = Utils.getNodes(aaxlFile,
		// "/*/systemImpl/flows/endToEndFlow");
		// int numOfNodes = nodes.getLength();
		// if (numOfNodes == 0) return null;
		// AADLModel model = new AADLModel(aaxlFile.getName());
		// model.flows = new Vector<Flow>(numOfNodes);
		// Node node;
		// for(int i = 0; i < numOfNodes; i++) {
		// node = nodes.item(i);
		// // All endToEndFlow elements should be within one systemImpl
		// if(model.topSys == null) {
		// model.topSys = createCompImpl(node.getParentNode().getParentNode());
		// }
		// Flow f = createFlow(node);
		// f.context = model.topSys;
		// model.flows.add(f);
		// }

		AADLModel model = new AADLModel(aaxlFile.getName());
		model.setAsystem(getSystem());

		getPort(model);

		getConnections(model);

		model.conn2Flow();

		// for bless
		// String name = model.getAsystem().getSubComps().get(2).getName();
		// SubComp controller = model.getAsystem().getSubComps().get(2);
		getBless(model);

		getHybird(model);
		System.out.println("parsed aadl xml to model");
		return model;
	}

	private void getHybird(AADLModel model) throws Exception {
		// TODO Auto-generated method stub
		HybirdAnnex ha = new HybirdAnnex("hybird");
		NodeList nodes = Utils.getNodes(aaxlFile,
				"//parsedAnnexSubclause[@name='hybrid']");
		// System.out.println(nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
		// hybird

		// for (int i = 0; i < nodes.getLength(); i++){
		// System.out.println(nodes.item(i).getNodeName());
		// }
		NodeList childNodes = nodes.item(0).getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			System.out.println(childNodes.item(i));
			if (childNodes.item(i).getNodeName() == "var") {

				for (Node node = childNodes.item(i).getFirstChild(); node != null; node = node
						.getNextSibling()) {
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Node type = getFirstElementChild(node);
						AVar var = new AVar(type.getAttributes().item(0)
								.getNodeValue(), AType.aadlreal);
						// System.out.println(var.getName()+","+var.getType());
						ha.getVariables().add(var);
					}
				}

			} else if (childNodes.item(i).getNodeName() == "con") {
				HConstant hConstant = new HConstant("b", AType.aadlreal);
				hConstant.setInitVal(1);
				ha.getConstants().add(hConstant);
			} else if (childNodes.item(i).getNodeName() == "beh") {
				// behavior_process
				for (Node node = childNodes.item(i).getFirstChild(); node != null; node = node
						.getNextSibling()) {
					if (node.getNodeType() == Node.ELEMENT_NODE) {

					}
				}
				// process

				HybirdProcess train = new HybirdProcess();
				train.setName("Train");
				HybirdProcess conti = new HybirdProcess();
				conti.setName("Continue");
				HybirdProcess RunningTrain = new HybirdProcess();
				RunningTrain.setName("RunningTrain");

				train.setSkip(false);
				HContinuous hContinuous = new HContinuous();
				hContinuous.setLeft(ha.getVarByName("ts"));
				hContinuous.setRight(ha.getVarByName("tv"));
				HContinuous hContinuous2 = new HContinuous();
				hContinuous2.setLeft(ha.getVarByName("tv"));
				hContinuous2.setRight(ha.getVarByName("ta"));
//				train.getEvolutions().add(hContinuous);
//				train.getEvolutions().add(hContinuous2);

				HInterrupt hInterrupt = new HInterrupt();
				hInterrupt.setEnd(conti);
				HCommunication hc1 = new HCommunication();
				hc1.setDirection(APort.out);
				// hc1.setP(model.getPorts().);
				// train.getInterrupt().

			}
		}

		System.out.println("hybird annex parse");

	}

	private Node getFirstElementChild(Node n) {
		for (Node node = n.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return node;
			}
		}
		return null;
	}

	private void getBless(AADLModel model) throws Exception {
		BlessAutoMata am = new BlessAutoMata();
		NodeList states = Utils.getNodes(aaxlFile, "//states");
		for (int i = 0; i < states.getLength(); i++) {
			String stateName = states.item(i).getAttributes()
					.getNamedItem("name").getNodeValue();
			Location location = new Location("bless",new DataPort(stateName,
					new CompImpl("pController")));
			location.setId(i + 1);
			String tag = "";
			try {
				tag = states.item(i).getAttributes().getNamedItem("tag")
						.getNodeValue();
			} catch (Exception e) {
				tag = "";
			}
			if (tag.equals("initial")) {
				location.setInitial(true);
			} else if (tag.equals("complete")) {
				location.setCommitted(true);
			}
			am.getLocs().add(location);

			// System.out.println(stateName);
		}

		NodeList transList = Utils.getNodes(aaxlFile, "//bt");
		for (int i = 0; i < transList.getLength(); i++) {
			String sources = transList.item(i).getAttributes()
					.getNamedItem("sources").getNodeValue();
			String dest = transList.item(i).getAttributes()
					.getNamedItem("destination").getNodeValue();
			String name = transList.item(i).getChildNodes().item(1)
					.getAttributes().getNamedItem("name").getNodeValue();
			Transition transition = new Transition(am.getLocs().get(
					sources.charAt(sources.length() - 1) - '0'), am.getLocs()
					.get(dest.charAt(dest.length() - 1) - '0'), null, name);

			am.getTrans().add(transition);

		}
		model.setBa(am);
		System.out.println("ddd");
	}

	private void getConnections(AADLModel model) throws Exception {
		NodeList conns = Utils.getNodes(aaxlFile, "//ownedPortConnection");
		for (int i = 0; i < conns.getLength(); i++) {
			String connName = conns.item(i).getAttributes()
					.getNamedItem("name").getNodeValue();
			Connection conn = new Connection(connName);

			// dest --//@ownedPublicSection/@ownedClassifier.4/@ownedDataPort.3
			setConnDest(conns, i, conn);

			// src #//@ownedPublicSection/@ownedClassifier.2/@ownedDataPort.1
			setConnSrc(conns, i, conn);
			// latency ц╩сп

			// context
			// setConnContext(conns, i, conn);
			// System.out.println("ddd");
			// String compimpl = nodeValue.substring(beginIndex, endIndex)

			model.addConnection(conn);
		}

	}

	private void setConnDest(NodeList conns, int i, Connection conn)
			throws Exception {
		NodeList childNodes2 = conns.item(i).getChildNodes();
		String nodeValue = childNodes2.item(1).getAttributes()
				.getNamedItem("connectionEnd").getNodeValue();
		int compimpl = nodeValue.charAt(nodeValue.lastIndexOf("/") - 1) - '0' + 1;
		Node implNode = Utils.getFirstNode(aaxlFile,
				"//ownedPublicSection/ownedClassifier[" + compimpl + "]");
		String substring = nodeValue.substring(nodeValue.lastIndexOf("@") + 1,
				nodeValue.lastIndexOf("."));
		NodeList childNodes = implNode.getChildNodes();
		int index = nodeValue.charAt(nodeValue.length() - 1) - '0';
		int j = 0;
		for (j = 0; j < childNodes.getLength(); j++) {
			String nodeName = childNodes.item(j).getNodeName();
			if (nodeName.equals(substring)) {
				index--;
			}
			if (index < 0) {
				break;
			}
		}
		String nodeName = childNodes.item(j).getAttributes()
				.getNamedItem("name").getNodeValue();
		conn.setDst(new DataPort(nodeName, new CompImpl(implNode
				.getAttributes().getNamedItem("name").getNodeValue())));
	}

	private void setConnSrc(NodeList conns, int i, Connection conn)
			throws Exception {
		NodeList childNodes2 = conns.item(i).getChildNodes();
		String nodeValue = childNodes2.item(3).getAttributes()
				.getNamedItem("connectionEnd").getNodeValue();
		int compimpl = nodeValue.charAt(nodeValue.lastIndexOf("/") - 1) - '0' + 1;
		Node implNode = Utils.getFirstNode(aaxlFile,
				"//ownedPublicSection/ownedClassifier[" + compimpl + "]");
		String substring = nodeValue.substring(nodeValue.lastIndexOf("@") + 1,
				nodeValue.lastIndexOf("."));
		NodeList childNodes = implNode.getChildNodes();
		int index = nodeValue.charAt(nodeValue.length() - 1) - '0';
		int j = 0;
		for (j = 0; j < childNodes.getLength(); j++) {
			String nodeName = childNodes.item(j).getNodeName();
			if (nodeName.equals(substring)) {
				index--;
			}
			if (index < 0) {
				break;
			}
		}
		String nodeName = childNodes.item(j).getAttributes()
				.getNamedItem("name").getNodeValue();
		conn.setSrc(new DataPort(nodeName, new CompImpl(implNode
				.getAttributes().getNamedItem("name").getNodeValue())));
	}

	public void setConnContext(NodeList conns, int i, Connection conn)
			throws Exception {
		NodeList childNodes2 = conns.item(i).getChildNodes();
		String nodeValue = childNodes2.item(3).getAttributes()
				.getNamedItem("context").getNodeValue();
		int compimpl = nodeValue.charAt(nodeValue.lastIndexOf("/") - 1) - '0' + 1;
		Node implNode = Utils.getFirstNode(aaxlFile,
				"//ownedPublicSection/ownedClassifier[" + compimpl + "]");
		String substring = nodeValue.substring(nodeValue.lastIndexOf("@") + 1,
				nodeValue.lastIndexOf("."));
		NodeList childNodes = implNode.getChildNodes();
		int index = nodeValue.charAt(nodeValue.length() - 1) - '0';
		int j = 0;
		for (j = 0; j < childNodes.getLength(); j++) {
			String nodeName = childNodes.item(j).getNodeName();
			if (nodeName.equals(substring)) {
				index--;
			}
			if (index < 0) {
				break;
			}
		}
		String nodeName = childNodes.item(j).getAttributes()
				.getNamedItem("name").getNodeValue();
		conn.setContext(new CompImpl(nodeName));
	}

	private void getPort(AADLModel model) throws Exception {
		NodeList nodesDataPort = Utils.getNodes(aaxlFile, "//ownedDataPort");
		NodeList nodesEventDataPort = Utils.getNodes(aaxlFile,
				"//ownedEventDataPort");
		HashMap<String, String> ports = new HashMap<>();
		String portName = "";
		String implName = "";
		for (int i = 0; i < nodesDataPort.getLength(); i++) {
			portName = nodesDataPort.item(i).getAttributes()
					.getNamedItem("name").getNodeValue();
			implName = nodesDataPort.item(i).getParentNode().getAttributes()
					.getNamedItem("name").getNodeValue();
			ports.put(portName, implName);
		}
		for (int i = 0; i < nodesEventDataPort.getLength(); i++) {
			portName = nodesEventDataPort.item(i).getAttributes()
					.getNamedItem("name").getNodeValue();
			implName = nodesEventDataPort.item(i).getParentNode()
					.getAttributes().getNamedItem("name").getNodeValue();
			ports.put(portName, implName);
		}
		for (String key : ports.keySet()) {
			DataPort dataPort = new DataPort(key, new CompImpl(ports.get(key)));
			model.addPort(dataPort);
			// System.out.println("Key = " + key);

		}

		// System.out.println("dd");
	}

	private ASystem getSystem() {
		ASystem system = null;
		try {
			Node systemType = Utils.getFirstNode(aaxlFile, "//ownedClassifier");
			Node namedItem = systemType.getAttributes().getNamedItem("name");
			system = new ASystem(namedItem.getNodeValue());

			Node train = Utils.getFirstNode(aaxlFile,
					"//ownedAbstractSubcomponent");
			SubComp subCompTrain = new SubComp(train.getAttributes()
					.getNamedItem("name").getNodeValue());
			Node device = Utils.getFirstNode(aaxlFile,
					"//ownedDeviceSubcomponent");
			SubComp subCompDevice = new SubComp(device.getAttributes()
					.getNamedItem("name").getNodeValue());
			Node process = Utils.getFirstNode(aaxlFile,
					"//ownedProcessSubcomponent");
			SubComp subCompProcess = new SubComp(process.getAttributes()
					.getNamedItem("name").getNodeValue());

			system.addSubComp(subCompTrain);
			system.addSubComp(subCompDevice);
			system.addSubComp(subCompProcess);

			return system;
			// System.out.println("ddd");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return system;

		// sys = new aadl2upaal.aadl.System(node.getAttributes());

	}

	Flow createFlow(Node node) throws Exception {
		Flow flow = new Flow(getName(node));
		NodeList nodes = Utils.getNodes(node, "flowElement");
		int numOfNodes = nodes.getLength();
		if (numOfNodes < 3)
			throw new Exception("Invalid flow path specified in node: "
					+ node.getNodeName());
		FlowElem src = createFlowElem(nodes.item(0));
		for (int i = 1; i < numOfNodes; i += 2) {
			Connection c = createConnection(nodes.item(i));
			c.src = src;
			c.dst = createFlowElem(nodes.item(i + 1));
			flow.connections.add(c);
			src = c.dst;
		}
		return flow;
	}

	Flow createFlowPath(Node node) throws Exception {
		// Get context
		String xpath = getAttrXPath(node, "flowContext");
		Node contextNode = Utils.getFirstNode(aaxlFile, xpath);
		CompImpl impl = createCompImpl(contextNode);
		// Get flowSpec node
		xpath = getAttrXPath(node, "flowSpec");
		Node specNode = Utils.getFirstNode(aaxlFile, xpath);
		FlowElem src = getSourcePort(specNode, impl);
		FlowElem dst = getSinkPort(specNode, impl);
		String flowName = getName(specNode);
		// if subcomponent, get its comp impl node
		if (contextNode.getNodeName().endsWith("Subcomponent")) {
			xpath = getAttrXPath(contextNode, "classifier");
			contextNode = Utils.getFirstNode(aaxlFile, xpath);
		}
		NodeList nodes = Utils.getNodes(contextNode, String.format(
				"flows/flowPathImpl[@name=\'%s\']/flowElement", flowName));
		int numOfNodes = nodes.getLength();
		// Finally, start creating the flow
		Flow f = new Flow(flowName);
		f.context = impl;
		Connection c;
		if (numOfNodes == 0) { // Create implicit connection
			c = new Connection("implicit");
			c.context = f.context;
			c.src = src;
			c.dst = dst;
			f.connections.add(c);
			return f;
		}
		for (int i = 0; i < numOfNodes - 1; i += 2) {
			c = createConnection(nodes.item(i));
			c.src = src;
			c.dst = createFlowElem(nodes.item(i + 1));
			f.connections.add(c);
			src = c.dst;
			// System.out.printf("connection con: %s. latency: %d%n", c,
			// c.latency);
		}
		c = createConnection(nodes.item(numOfNodes - 1));
		c.src = src;
		c.dst = dst;
		f.connections.add(c);
		// System.out.printf("connection con: %s. latency: %d%n", c, c.latency);
		return f;
	}

	FlowElem createFlowElem(Node node) throws Exception {
		String xpath = getAttrXPath(node, "flowContext");
		Node contextNode = Utils.getFirstNode(aaxlFile, xpath);
		CompImpl impl = createCompImpl(contextNode);
		xpath = getAttrXPath(node, "flowSpec");
		Node specNode = Utils.getFirstNode(aaxlFile, xpath);
		String nodename = specNode.getNodeName();
		if (nodename.equals("flowSourceSpec"))
			return getSinkPort(specNode, impl);
		else if (nodename.equals("flowSinkSpec"))
			return getSourcePort(specNode, impl);
		else if (nodename.equals("flowPathSpec"))
			return createFlowPath(node);
		else
			throw new Exception("Unrecognized flowSpec node: "
					+ node.getNodeName());
	}

	Connection createConnection(Node node) throws Exception {
		// if(visited.containsKey(node)) return (Connection) visited.get(node);
		String xpath = getAttrXPath(node, "connection");
		node = Utils.getFirstNode(aaxlFile, xpath);
		Connection con = new Connection(getName(node));
		// visited.put(node, con);
		// Get src data port
		Node n = node.getParentNode().getParentNode();
		con.context = createCompImpl(n);
		// Check for latency for this connection
		con.latency = getLatency(node, con);
		return con;
	}

	/**
	 * latencyXPath = "../../properties/propertyAssociation
	 * [contains(@propertyDefinition, 'propertyDefinition[@name=Latency]')]
	 * [contains(@appliesTo,'dataConnection[@name=<connection name>]')]
	 */
	final static String latencyXPath = "../../properties/propertyAssociation"
			+ "[contains(@propertyDefinition, \'propertyDefinition[@name=Latency]\')]"
			+ "[contains(@appliesTo,\'dataConnection[@name=%s]\')]";

	public static Integer getLatency(Node node, Connection con)
			throws Exception {
		Node n = Utils.getFirstNode(node,
				String.format(latencyXPath, con.name), true);
		if (n == null)
			return null;
		n = Utils.getFirstNode(n, "propertyValue");
		return Integer.parseInt(Utils.getAttrVal(n, "value"));
	}

	DataPort getSourcePort(Node node, CompImpl context) throws Exception {
		String xpath = getAttrXPath(node, "src");
		Node n = Utils.getFirstNode(aaxlFile, xpath);
		return createDataPort(n, context);
	}

	DataPort getSinkPort(Node node, CompImpl context) throws Exception {
		String xpath = getAttrXPath(node, "dst");
		Node n = Utils.getFirstNode(aaxlFile, xpath);
		return createDataPort(n, context);
	}

	DataPort createDataPort(Node node, CompImpl context) throws Exception {
		return new DataPort(getName(node), context);
	}

	CompImpl createCompImpl(Node node) throws Exception {
		// if(visited.containsKey(node)) return (CompImpl) visited.get(node);
		String name = node.getNodeName();
		if (name.endsWith("Subcomponent"))
			return createSubComp(node);
		else if (name.endsWith("Impl"))
			return createCompImplFromImpl(node);
		else
			throw new Exception("Unrecognized node type: " + name);
	}

	private SubComp createSubComp(Node node) throws Exception {
		SubComp sub = new SubComp(getName(node));
		String xpath = getAttrXPath(node, "classifier");
		Node n = Utils.getFirstNode(aaxlFile, xpath);
		sub.classifier = createCompImpl(n);
		// Getting context component of the connection
		n = node.getParentNode().getParentNode();
		sub.context = createCompImpl(n);
		return sub;
	}

	private CompImpl createCompImplFromImpl(Node node) throws Exception {
		// if(visited.containsKey(node)) return (CompImpl) visited.get(node);
		CompImpl impl = new CompImpl(getName(node));
		// visited.put(node, impl);
		return impl;
	}

	// Regex for matching unquoted attribute values in an XPath expressions
	// Regex: \[@(\w+)=(\w+)\]
	private final static Pattern p2 = Pattern.compile("\\[@(\\w+)=(.+?)\\]");

	public static String quoteAttrVals(String xpath) {
		Matcher m = p2.matcher(xpath);
		if (!m.find())
			return xpath;
		return m.replaceAll("\\[@$1=\'$2\'\\]");
	}

	public static String makeRootWild(String xpath) {
		if (!xpath.startsWith("/aadlSpec"))
			return xpath;
		return ("/*" + xpath.substring(xpath.indexOf('/', 1)));
	}

	public static String getAttrXPath(Node node, String attrName)
			throws Exception {
		return makeRootWild(quoteAttrVals(Utils.getAttrVal(node, attrName)));
	}

	public static String getName(Node node) throws Exception {
		return Utils.getAttrVal(node, "name");
	}

	public static void printFlow(Flow f, String tab) {
		System.out.printf(tab + "flow %s%n", f);
		for (Connection c : f.connections) {
			System.out.printf(tab + "con %s: (%s, %s)%n", c, c.getSourcePort(),
					c.getSinkPort());
			if (c.dst instanceof Flow)
				printFlow((Flow) c.dst, tab + "    ");
		}
	}

	// public static void main(String args[]) throws Exception {
	// if(args.length < 1) {
	// System.err.println("usage: java parser.AAXLParser <aaxl file>");
	// System.exit(1);
	// }
	// AAXLParser par = new AAXLParser(new File(args[0]));
	// AADLModel model = par.createAADLModel();
	// for(Flow f : model.flows) printFlow(f,"");
	// }
}
