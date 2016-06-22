package aadl2upaal.parser;
// XML  packages

import javax.xml.xpath.*;

import aadl2upaal.aadl.*;
import aadl2upaal.upaal.Location;
import org.w3c.dom.*;

import javax.xml.parsers.*;

import java.io.*;
import java.util.ArrayList;

public final class Utils {
    final static XPath xp = XPathFactory.newInstance().newXPath();

    /**
     * Returns the first XML node in xmlFile matching the XPath expression, xpathExp
     */
    public static Node getFirstNode(File xmlFile, String xpathExp) throws Exception {
        return getFirstNode(new FileInputStream(xmlFile), xpathExp);
    }

    /**
     * Returns a list of XML nodes in xmlFile matching the XPath expression, xpathExp
     */
    public static NodeList getNodes(File xmlFile, String xpathExp) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        return getNodes(doc, xpathExp);
    }

    public static Node getFirstNode(InputStream istream, String xpathExp) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(istream);
        return getFirstNode(doc, xpathExp);
    }

    public static NodeList getNodes(InputStream istream, String xpathExp) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(istream);
        return getNodes(doc, xpathExp);
    }

    /**
     * Returns the first XML node in the context matching the XPath expression, xpathExp
     */
    public static Node getFirstNode(Object context, String xpathExp) throws Exception {
        Node n = (Node) xp.evaluate(xpathExp, context, XPathConstants.NODE);
        if (n == null) throw new Exception(
                "No XML node found in context matching XPath expression: " + xpathExp);
        return n;
    }

    /**
     * Same as getFirstNode(Object context, String xpathExp) except
     * no exception is thrown if no node matches xpathExp
     */
    public static Node getFirstNode(Object context, String xpathExp,
                                    boolean noRequirement) throws Exception {
        return (Node) xp.evaluate(xpathExp, context, XPathConstants.NODE);
    }

    /**
     * Returns a list of XML nodes in the context matching the XPath expression, xpathExp
     */
    public static NodeList getNodes(Object context, String xpathExp) throws XPathExpressionException {
        return (NodeList) xp.evaluate(xpathExp, context, XPathConstants.NODESET);
    }

    public static String getAttrVal(Node node, String attrName) throws Exception {
        Attr attr = (Attr) node.getAttributes().getNamedItem(attrName);
        if (attr == null) throw new Exception(String.format(
                "Attribute, %s, is not specified in XML node: %s",
                attrName, node.getNodeName()));
        String val = attr.getValue().trim();
        if (val.length() == 0) throw new Exception(String.format(
                "No non-white space characters found in attribute, %s, of XML node: %s",
                attrName, node.getNodeName()));
        return val;
    }

    public static boolean hasAttribute(Node node, String attrName) {
        return
                node.getAttributes().getNamedItem(attrName) != null;
    }

    /**
     * Throws an exception if the Node name does not equal name
     */
    public static void chkName(Node node, String name) throws Exception {
        if (!name.trim().equals(node.getNodeName()))
            throw new Exception(String.format(
                    "Node %s does not have name %s",
                    node.getNodeName(), name));
    }


    public static AADLModel getMockModel(String name) {
        AADLModel model = new AADLModel(name);
        ACompoent train = new ACompoent("Train");
        ACompoent rbc = new ACompoent("RBC");
        ACompoent controller = new ACompoent("Controller");
        model.comps.add(train);
        model.comps.add(rbc);
        model.comps.add(controller);

        //train
        ACompoentDeclare declare = new ACompoentDeclare("Train");
        ADataPort ts = new ADataPort("ts");
        ts.setDirection(APort.out);
        ts.setType("CTCS_Types::Position");
        ADataPort tv = new ADataPort("tv");
        tv.setDirection(APort.out);
        tv.setType("CTCS_Types::Velocity");
        ADataPort ta = new ADataPort("ta");
        ta.setDirection(APort.in);
        ta.setType("CTCS_Types::Acceleration");
        declare.ports.add(ts);
        declare.ports.add(tv);
        declare.ports.add(ta);
        train.setCompoentDeclare(declare);

        HybirdAnnex hybirdAnnex = new HybirdAnnex("");
        hybirdAnnex.getVariables().add(new AVar("s", "CTCS_Types::Position"));
        hybirdAnnex.getVariables().add(new AVar("v", "CTCS_Types::Velocity"));
        hybirdAnnex.getVariables().add(new AVar("a", "CTCS_Types::Acceleration"));
        hybirdAnnex.getVariables().add(new AVar("t", "CTCS_Types::Time"));
        hybirdAnnex.getVariables().add(new AVar("fr", "CTCS_Types::Deceleration"));

        HybirdProcess train_process = new HybirdProcess();
        train_process.setName("Train");
        train_process.isRepete = true;
        HybirdProcess conti_process = new HybirdProcess();
        conti_process.setName("Continue");
        conti_process.setSkip(true);
        HybirdProcess RunningTrain = new HybirdProcess();
        RunningTrain.setName("RunningTrain");
        RunningTrain.isIinitial = true;
        hybirdAnnex.getBehavior().add(train_process);
        hybirdAnnex.getBehavior().add(conti_process);
        hybirdAnnex.getBehavior().add(RunningTrain);

        HContinuous hContinuous;
        hContinuous = new HContinuous();
        hContinuous.setRank(1);
        hContinuous.setLeft(new AVar("s", ""));
        hContinuous.setRight(new AVar("v", ""));
        train_process.getEvolutions().add(hContinuous);
        hContinuous = new HContinuous();
        hContinuous.setRank(1);
        hContinuous.setLeft(new AVar("v", ""));
        hContinuous.setRight(new AVar("a+fr", ""));
        train_process.getEvolutions().add(hContinuous);
        hContinuous = new HContinuous();
        hContinuous.setRank(1);
        hContinuous.setLeft(new AVar("t", ""));
        hContinuous.setRight(new AVar("1", ""));
        train_process.getEvolutions().add(hContinuous);


        HInterrupt hInterrupt = new HInterrupt();
        hInterrupt.setEnd(conti_process);
        HCommunication hc1;
        hc1 = new HCommunication();
        hc1.setDirection(APort.out);
        hc1.setP(new ADataPort("ts"));
        hc1.setVar(new AVar("s", ""));
        hInterrupt.getComm().add(hc1);
        hc1 = new HCommunication();
        hc1.setDirection(APort.out);
        hc1.setP(new ADataPort("tv"));
        hc1.setVar(new AVar("v", ""));
        hInterrupt.getComm().add(hc1);
        hc1 = new HCommunication();
        hc1.setDirection(APort.in);
        hc1.setP(new ADataPort("ta"));
        hc1.setVar(new AVar("a", ""));
        hInterrupt.getComm().add(hc1);
        train_process.setInterrupt(hInterrupt);

        Hassignment hassignment = new Hassignment();
        hassignment.setVal(0);
        hassignment.setVar(new AVar("s", ""));
        RunningTrain.getAsssigments().add(hassignment);
        hassignment.setVal(0);
        hassignment.setVar(new AVar("a", ""));
        RunningTrain.getAsssigments().add(hassignment);
        hassignment.setVal(0);
        hassignment.setVar(new AVar("v", ""));
        RunningTrain.getAsssigments().add(hassignment);
        RunningTrain.subProcess = train_process;

        ACompoentImpl train_impl = new ACompoentImpl("Train");
        train_impl.getAnnexs().add(hybirdAnnex);

        UncertaintyAnnex ua_train = new UncertaintyAnnex("");
        UVar v_delay = new UVar("v_delay", "time");
        Distribution normal_train = new Distribution();
        normal_train.setDistName(Distribution.TimeNormal);
        ArrayList<Double> params_train = new ArrayList<>();
        params_train.add(0.15);
        params_train.add(0.04);
        normal_train.setParas(params_train);
        v_delay.setApplied(ts);
        v_delay.dist = normal_train;
        ua_train.getVars().add(v_delay);
        ua_train.getDists().add(normal_train);
        train_impl.getAnnexs().add(ua_train);

        train.setCompoentImpl(train_impl);

        //rbc
        ACompoentDeclare rbc_declare = new ACompoentDeclare("RBC");
        AEventPort r = new AEventPort("r");
        r.setDirection(APort.in);
        ADataPort m = new ADataPort("m");
        m.setDirection(APort.out);
        m.setType("CTCS_Types::MovementAuthority");
        ADataPort ea = new ADataPort("ea");
        ea.setDirection(APort.out);
        ea.setType("CTCS_Types::EOA");
        rbc_declare.ports.add(r);
        rbc_declare.ports.add(m);
        rbc_declare.ports.add(ea);
        rbc.setCompoentDeclare(rbc_declare);

        ACompoentImpl rbc_impl = new ACompoentImpl("RBC");

        UncertaintyAnnex ua = new UncertaintyAnnex("");
        UVar r_delay = new UVar("r_delay", "time");
        Distribution normal = new Distribution();
        normal.setDistName(Distribution.Normal);
        ArrayList<Double> params = new ArrayList<>();
        params.add(0.1);
        params.add(0.05);
        normal.setParas(params);
        r_delay.setApplied(r);
        r_delay.dist = normal;
        ua.getVars().add(r_delay);
        ua.getDists().add(normal);
        ua.getQueries().add("Pr[&lt;=300](&lt;&gt; v&lt;=0 &amp;&amp;  s&lt;6000 &amp;&amp; s&gt;0");
        rbc_impl.getAnnexs().add(ua);
        rbc.setCompoentImpl(rbc_impl);


        //controller
        ACompoentDeclare controller_declare = new ACompoentDeclare("controller");

        controller.setCompoentDeclare(controller_declare);

        ACompoentImpl controller_impl = new ACompoentImpl("controller");
        BLESSAnnex ba = new BLESSAnnex("");
        BVar i = new BVar("i", "Base_Types::Integer");
        BVar b = new BVar("b", "CTCS_Types::Deceleration");
        BVar v = new BVar("v", "CTCS_Types::");
        BVar s = new BVar("s", "CTCS_Types::");
        BVar e = new BVar("e", "CTCS_Types::EOA");
        BVar xl = new BVar("xl", "CTCS_Types::");
        BVar iMA = new BVar("iMA", "CTCS_Types::MovementAuthority");
        BVar iSeg = new BVar("iSeg", "CTCS_Types::Segment");
        ba.getVariables().add(i);
        ba.getVariables().add(b);
        ba.getVariables().add(v);
        ba.getVariables().add(s);
        ba.getVariables().add(e);
        ba.getVariables().add(iMA);
        ba.getVariables().add(iSeg);
        ba.getVariables().add(xl);

        Location ready = new Location("READY", null);
        ready.setInitial(true);
        Location GMA = new Location("GMA", null);
        Location CMA = new Location("CMA", null);
        CMA.setCommitted(true);
        Location RETRY = new Location("RETRY", null);
        RETRY.setCommitted(true);
        Location MFR = new Location("MFR", null);
        Location CMF = new Location("CMF", null);
        CMF.setCommitted(true);
        Location SBI = new Location("SBI", null);
        Location CSB = new Location("CSB", null);
        CSB.setCommitted(true);
        Location EBI = new Location("EBI", null);
        Location STOP = new Location("STOP", null);
        ba.getLocs().add(ready);
        ba.getLocs().add(GMA);
        ba.getLocs().add(CMA);
        ba.getLocs().add(RETRY);
        ba.getLocs().add(MFR);
        ba.getLocs().add(CMF);
        ba.getLocs().add(SBI);
        ba.getLocs().add(CSB);
        ba.getLocs().add(EBI);
        ba.getLocs().add(STOP);

        BTransition bt;
        bt = new BTransition();
        bt.setSrc(ready);
        bt.setDst(GMA);
        ArrayList<BUpdate> bu;
        bu = new ArrayList<>();
        r.setDirection(APort.out);
        bu.add(new BUpdate(null, r, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(GMA);
        bt.setDst(CMA);
        bu = new ArrayList<>();
        m.setDirection(APort.in);
        ea.setDirection(APort.in);
        bu.add(new BUpdate(null, m, iMA));
        bu.add(new BUpdate(null, ea, e));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CMA);
        bt.setDst(MFR);
        bt.setGuard("iMA!=null");
        bu = new ArrayList<>();
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(MFR);
        bt.setDst(CMF);
        bu = new ArrayList<>();
        ts.setDirection(APort.in);
        tv.setDirection(APort.in);
        bu.add(new BUpdate(null, tv, v));
        bu.add(new BUpdate(null, ts, s));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CMF);
        bt.setDst(SBI);
        bt.setGuard("not ((s=CTCS_Properties::start) or ((v &lt; iSeg.v2))) or not ((s=CTCS_Properties::start) or  ((((v**2) + (2*b*s)) &lt; (iMA[nSeg.v2] + (2*b*iSeg.e)))))");
        bu = new ArrayList<>();
        ta.setDirection(APort.out);
        bu.add(new BUpdate(null, ta, new AVar("0.8", "")));
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CMF);
        bt.setDst(MFR);
        bt.setGuard("( (s=CTCS_Properties::start) or ((v &lt; iSeg.v2))) or  (((v**2) + (2*b*s)) &lt; (iMA[nSeg.v2] + (2*b*iSeg.e)))");
        bu = new ArrayList<>();
        ta.setDirection(APort.out);
        bu.add(new BUpdate(null, ta, xl));
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(SBI);
        bt.setDst(CSB);
        bu = new ArrayList<>();
        bu.add(new BUpdate(null, ts, s));
        bu.add(new BUpdate(null, tv, v));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CSB);
        bt.setDst(MFR);
        bt.setGuard("((s=CTCS_Properties::start) or ((v &lt; iSeg.v2))) and ((s=CTCS_Properties::start) or ((((v**2) + (2*b*s)) &lt; (iMA[nSeg.v2] + (2*b*iSeg.e)))))");
        bu = new ArrayList<>();
        bu.add(new BUpdate(null, ta, xl));
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CSB);
        bt.setDst(SBI);
        bt.setGuard("not (((s=CTCS_Properties::start) or ((v &lt; iSeg.v2))) and ((s=CTCS_Properties::start) or ((((v**2) + (2*b*s)) &lt; (iMA[nSeg.v2] + (2*b*iSeg.e))))))");
        bu = new ArrayList<>();
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CMA);
        bt.setDst(RETRY);
        bt.setGuard("iMA==null");
        bu = new ArrayList<>();
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(RETRY);
        bt.setDst(GMA);
        bu = new ArrayList<>();
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(EBI);
        bt.setDst(STOP);
        bt.setGuard("v==0");
        bu = new ArrayList<>();
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(CMF);
        bt.setDst(EBI);
        bt.setGuard("not (v &lt; iSeg.v1) or not ((((v**2) + (2*b*s)) &lt; (iMA[nSeg.v1] + (2*b*iSeg.e))))");
        bu = new ArrayList<>();
        bu.add(new BUpdate(null, ta, b));
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        bt = new BTransition();
        bt.setSrc(SBI);
        bt.setDst(EBI);
        bt.setGuard("not (v &lt; iSeg.v1) or not ((((v**2) + (2*b*s)) &lt; (iMA[nSeg.v1] + (2*b*iSeg.e))))");
        bu = new ArrayList<>();
        bu.add(new BUpdate(null, ta, b));
        bu.add(new BUpdate("update()", null, null));
        bt.setUpdate(bu);
        ba.getTrans().add(bt);

        controller_impl.getAnnexs().add(ba);
        controller.setCompoentImpl(controller_impl);
        return model;
    }


}