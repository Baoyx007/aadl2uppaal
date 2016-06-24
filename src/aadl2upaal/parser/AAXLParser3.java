package aadl2upaal.parser;

import aadl2upaal.aadl.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haven on 16/6/14.
 */
public class AAXLParser3 {

    public File aaxlFile;
    private AADLModel amodel;
    private XPathFactory xpfac;
    private Document doc;

    public AAXLParser3(File aaxlFile) {
        this.aaxlFile = aaxlFile;
        xpfac = XPathFactory.instance();
    }

    //That's EZ
    public AADLModel createAADLModel() throws Exception {

        SAXBuilder builder = new SAXBuilder(false);
        doc = builder.build(aaxlFile);

        Namespace aadl2 = Namespace.getNamespace("aadl2", "http://aadl.info/AADL/2.0");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        Element sys = doc.getRootElement();
        Attribute name = sys.getAttribute("name");
        amodel = new AADLModel(name.getValue());

        List<Element> compoents = sys.getChild("ownedPublicSection").getChildren("ownedClassifier");
        for (Element ele : compoents) {
            Attribute type = ele.getAttribute("type", xsi);
            Attribute comp_name = ele.getAttribute("name");

            // 遍历每个component
            if (type.getValue().contains("System")) {
                if (type.getValue().contains("Implementation")) {

                } else {

                }
            } else if (type.getValue().contains("Abstract")) {
                if (type.getValue().contains("Implementation")) {
                    //把impl 加入到 component 中
                    ACompoentImpl ab_impl = new ACompoentImpl(comp_name.getValue());
                    for (ACompoent comp : amodel.comps) {
                        if (comp.getName().equals(comp_name.getValue().substring(0, comp_name.getValue().indexOf(".")))) {
                            comp.setCompoentImpl(ab_impl);
                        }
                    }

                    //遍历所有annex
                    for (Element annex : ele.getChildren("ownedAnnexSubclause")) {
                        Element parsedAnnexSubclause = annex.getChild("parsedAnnexSubclause");
                        String annex_name = parsedAnnexSubclause.getAttributeValue("name");
                        process_annex(annex_name, parsedAnnexSubclause, ab_impl);
                    }


                } else {
                    ACompoent abstract_comp = new ACompoent(comp_name.getValue());
                    ACompoentDeclare ab_declare = new ACompoentDeclare(comp_name.getValue());

                    process_ports(ele, ab_declare);

                    amodel.comps.add(abstract_comp);
                    abstract_comp.setCompoentDeclare(ab_declare);
                }
            } else if (type.getValue().contains("Process")) {
                if (type.getValue().contains("Implementation")) {

                } else {

                }
            } else if (type.getValue().contains("Thread")) {

                if (type.getValue().contains("Implementation")) {

                } else {

                    ACompoent thread_comp = new ACompoent(comp_name.getValue());
                }
            } else if (type.getValue().contains("Device")) {

                if (type.getValue().contains("Implementation")) {
                    //把impl 加入到 component 中
                    ACompoentImpl de_impl = new ACompoentImpl(comp_name.getValue());
                    for (ACompoent comp : amodel.comps) {
                        if (comp.getName().equals(comp_name.getValue().substring(0, comp_name.getValue().indexOf(".")))) {
                            comp.setCompoentImpl(de_impl);
                        }
                    }

                    //遍历所有annex
                    for (Element annex : ele.getChildren("ownedAnnexSubclause")) {
                        Element parsedAnnexSubclause = annex.getChild("parsedAnnexSubclause");
                        String annex_name = parsedAnnexSubclause.getAttributeValue("name");
                        process_annex(annex_name, parsedAnnexSubclause, de_impl);
                    }
                } else {
                    ACompoent device_comp = new ACompoent(comp_name.getValue());
                    ACompoentDeclare de_declare = new ACompoentDeclare(comp_name.getValue());

                    process_ports(ele, de_declare);

                    amodel.comps.add(device_comp);
                    device_comp.setCompoentDeclare(de_declare);

                }
            } else {
                throw new RuntimeException("not support compoent");
            }


            System.out.println("comp");
        }

        System.out.println("parsed aadl xml to model by jdom");

//      Connection 的作用是去重!!
        // 所以controller 就不用管它的port了!!
//        getConnections(model);
//
        //TODO 最好能有一个字符串 到 对象的映射表  , 比如 tv 有它对应的对象, 这样以后引用tv的时候 就不用创建新的了
        // 这也可以每次都遍历已有的对象, 但可能会有命名冲突的问题
        // 还有一个问题是 可能引用还没有创建的对象
        return amodel;
    }

    private void process_ports(Element ele, ACompoentDeclare ab_declare) {
        //get all port
        for (Element port : ele.getChildren("ownedDataPort")) {
            process_date_port(ab_declare, port);
        }
        for (Element port : ele.getChildren("ownedEventDataPort")) {
            process_date_port(ab_declare, port);
        }
        for (Element port : ele.getChildren("ownedEventPort")) {
            process_event_port(ab_declare, port);
        }
    }

    private void process_annex(String annex_name, Element parsedAnnexSubclause, ACompoentImpl impl) {
        if (annex_name.equals("hybrid")) {
            HybirdAnnex hybirdAnnex = new HybirdAnnex("");

            //variable
            Element var = parsedAnnexSubclause.getChild("var");
            for (Element v : var.getChildren("behavior_variable")) {
                String name = v.getChild("var").getAttributeValue("name");
                String type = v.getAttributeValue("type");
                type = Utils.parse_var_type(type);
                hybirdAnnex.getVariables().add(new AVar(name, type));
            }

            //constants
            Element con = parsedAnnexSubclause.getChild("con");

            //behavior
            Element beh = parsedAnnexSubclause.getChild("beh");
            for (Element behavior_process : beh.getChildren("behavior_process")) {
                HybirdProcess hp = new HybirdProcess();
                hp.setName(behavior_process.getAttributeValue("name"));

                // for skip
                if (behavior_process.getChild("process").getAttributeValue("skip", "false").equals("skip")) {
                    hp.setSkip(true);
                    hybirdAnnex.getBehavior().add(hp);
                    continue;
                }

                //process
                for (Element process : behavior_process.getChildren("process")) {
                    //continuous_evolution
                    Element continuous_evolution = process.getChild("continuous_evolution");
                    if (continuous_evolution != null) {
                        // var
                        Element left = continuous_evolution.getChild("lhs").getChild("diff").getChild("time_derivative");
                        String order = left.getAttributeValue("order");


                        XPathExpression xp = xpfac.compile(Utils.convert2xpath(left.getAttributeValue("x")), Filters.element());
                        Element x = (Element) xp.evaluateFirst(doc.getRootElement());

                        String right_path = continuous_evolution.getChild("rhs").getChild("diff").getAttributeValue("variable", "-1");
                        String right_name = "1";
                        if (right_path.equals("-1")) {
                            right_name = continuous_evolution.getChild("rhs").getChild("diff").getAttributeValue("numeric_literal");
                        } else {
                            Element right = (Element) xpfac.compile(Utils.convert2xpath(right_path), Filters.element()).evaluateFirst(doc.getRootElement());
                            right_name = right.getAttributeValue("name");
                        }
                        HContinuous hContinuous = new HContinuous();
                        hContinuous.setRank(Integer.valueOf(order));
                        hContinuous.setLeft(new AVar(x.getAttributeValue("name"), ""));
                        hContinuous.setRight(new AVar(right_name, ""));

                        hp.getEvolutions().add(hContinuous);


                        //interrupt
                        Element interrupt = continuous_evolution.getChild("interrupt");
                        if (interrupt != null) {
                            HInterrupt hInterrupt = new HInterrupt();
                            Element communication_interrupt = interrupt.getChild("communication_interrupt");
                            //String bp_path = communication_interrupt.getAttributeValue("bp");

                            //hInterrupt.setEnd();

                            for (Element communication : communication_interrupt.getChildren("communication")) {
                                HCommunication hc1;
                                hc1 = new HCommunication();
                                if (communication.getAttributeValue("direction").equals("!")) {
                                    hc1.setDirection(APort.out);
                                } else {
                                    hc1.setDirection(APort.in);
                                }
                                String port_path = communication.getAttributeValue("port");
                                Element h_port = (Element) xpfac.compile(Utils.convert2xpath(port_path), Filters.element()).evaluateFirst(doc.getRootElement());
                                hc1.setP(new ADataPort(h_port.getAttributeValue("name")));
                                String variable_path = communication.getAttributeValue("variable");
                                Element h_var = (Element) xpfac.compile(Utils.convert2xpath(variable_path), Filters.element()).evaluateFirst(doc.getRootElement());
                                hc1.setVar(new AVar(h_var.getAttributeValue("name"), ""));
                                hInterrupt.getComm().add(hc1);
                            }

                            hp.setInterrupt(hInterrupt);
                        }

                    }

                    //assignment
                    Element assignment = process.getChild("assignment");
                    if (assignment != null) {
                        String local_variable_path = assignment.getAttributeValue("local_variable");
                        Element local_variable = (Element) xpfac.compile(Utils.convert2xpath(local_variable_path), Filters.element()).evaluateFirst(doc.getRootElement());

                        String val = assignment.getChild("expression").getChild("term").getAttributeValue("integer_literal");

                        Hassignment hassignment = new Hassignment();
                        hassignment.setVal(Integer.valueOf(val));
                        hassignment.setVar(new AVar(local_variable.getAttributeValue("name"), ""));
                        hp.getAsssigments().add(hassignment);
                    }
                    //repete
                    Element repetition = process.getChild("repetition");
                    if (repetition != null) {
                        String repete_process_path = repetition.getAttributeValue("behavior_process");
                        Element repete_process = (Element) xpfac.compile(Utils.convert2xpath(repete_process_path), Filters.element()).evaluateFirst(doc.getRootElement());

                        for (HybirdProcess p : hybirdAnnex.getBehavior()) {
                            if (p.getName().equals(repete_process.getAttributeValue("name"))) {
                                p.isRepete = true;
                                hp.subProcess = p;
                            }

                        }
                        hp.isIinitial = true;
                    }
                }
                hybirdAnnex.getBehavior().add(hp);
            }


            impl.getAnnexs().add(hybirdAnnex);
        } else if (annex_name.equals("BLESS")) {

        } else if (annex_name.equals("Uncertainty")) {
            UncertaintyAnnex ua = new UncertaintyAnnex("");

            String sourceText = parsedAnnexSubclause.getAttributeValue("sourceText");

            String SEP = "\n \t\t\t";

            sourceText = sourceText.replace("\t", "");
            System.out.println(sourceText);

//            String[] split = sourceText.split(SEP);
//            for (String s: split){
//                System.out.println("line:"+s);
//            }

            //var
            Pattern compile = Pattern.compile("variables([\\s\\S]*)distributions");
            Matcher matcher = compile.matcher(sourceText);
//            matcher.
            if (matcher.find()) {
                String variables = matcher.group(1);
//                System.out.println(variables);
//                time v_delay applied to Train.ts
//                        -- modeling connection delay
//                static price v_fr applied to Train.ta
//                        -- modeling rail friction
                String[] split = variables.split("\n");
                for (String line : split) {
                    //System.out.println("line"+line);
                    if (line.contains("time")) {
                        compile = Pattern.compile("time (.*?) applied to ([\\w\\.]*)");
                        matcher = compile.matcher(line);
                        matcher.find();
                        //System.out.println(matcher.group(1)+matcher.group(2));
                        UVar time_delay = new UVar(matcher.group(1), "time");
                        String[] comp_var = matcher.group(2).split("\\.");

                        APort port_by_name = Utils.find_port_by_name(amodel, comp_var[0], comp_var[1]);
                        time_delay.setApplied(port_by_name);

                        ua.getVars().add(time_delay);
                    } else if (line.contains("static price")) {
                        compile = Pattern.compile("static price (.*?) applied to ([\\w\\.]*)");
                        matcher = compile.matcher(line);
                        matcher.find();
                        //System.out.println(matcher.group(1)+matcher.group(2));
                        UVar time_delay = new UVar(matcher.group(1), "static price");
                        String[] comp_var = matcher.group(2).split("\\.");

                        APort port_by_name = Utils.find_port_by_name(amodel, comp_var[0], comp_var[1]);
                        time_delay.setApplied(port_by_name);

                        ua.getVars().add(time_delay);
                    } else if (line.contains("price")) {
                        compile = Pattern.compile("price (.*?) applied to ([\\w\\.]*)");
                        matcher = compile.matcher(line);
                        matcher.find();
                        //System.out.println(matcher.group(1)+matcher.group(2));
                        UVar time_delay = new UVar(matcher.group(1), "price");
                        String[] comp_var = matcher.group(2).split("\\.");

                        APort port_by_name = Utils.find_port_by_name(amodel, comp_var[0], comp_var[1]);
                        time_delay.setApplied(port_by_name);

                        ua.getVars().add(time_delay);
                    }
                }
            }

            //dist
            compile = Pattern.compile("distributions([\\s\\S]*)queries");
            matcher = compile.matcher(sourceText);
//            matcher.
            if (matcher.find()) {
                String variables = matcher.group(1);

                String[] split = variables.split("\n");
                for (String line : split) {
                    compile = Pattern.compile("([\\w_]*)?\\s*?=\\s*?(\\w*?)\\(([\\d\\.-]*?),([\\d\\.-]*?)\\)");
                    matcher = compile.matcher(line);
                    if (matcher.find()) {
                        System.out.println(matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4));
                        Distribution dist = new Distribution();
                        dist.setDistName(matcher.group(2) );

                        ArrayList<Double> params = new ArrayList<>();
                        params.add(Double.valueOf(matcher.group(3)));
                        params.add(Double.valueOf(matcher.group(4)));
                        dist.setParas(params);

                        for(UVar var : ua.getVars()){
                            if(var.getName().equals(matcher.group(1))){
                                var.dist = dist;
                            }
                        }
                        ua.getDists().add(dist);
                    }
                }
            }

            //query
            compile = Pattern.compile("queries([\\s\\S]*)");
            matcher = compile.matcher(sourceText);
//            matcher.
            if (matcher.find()) {
                String variables = matcher.group(1);

                String[] split = variables.split("\n");
                for(String line : split){
                    if(line.contains("=")){
                        ua.getQueries().add(line.substring(line.indexOf("=")+1));
                    }
                }
            }

                impl.getAnnexs().add(ua);
        } else {
            throw new RuntimeException("not supprot annex");
        }
    }

    private void process_date_port(ACompoentDeclare ab_declare, Element port) {
        ADataPort dp = new ADataPort(port.getAttributeValue("name"));
        if (port.getAttributeValue("out", "false").equals("true")) {
            dp.setDirection(APort.out);
        } else {
            dp.setDirection(APort.in);
        }
        String port_type = port.getAttributeValue("dataFeatureClassifier");
        port_type = port_type.substring(port_type.indexOf("#") + 1).replace(".", "::");
        dp.setType(port_type);
        ab_declare.ports.add(dp);
    }

    private void process_event_port(ACompoentDeclare ab_declare, Element port) {
        AEventPort ep = new AEventPort(port.getAttributeValue("name"));
        if (port.getAttributeValue("out", "false").equals("true")) {
            ep.setDirection(APort.out);
        } else {
            ep.setDirection(APort.in);
        }
        ab_declare.ports.add(ep);
    }

    public static void main(String[] args) throws Exception {
        AAXLParser3 aaxlParser3 = new AAXLParser3(new File(
                "src/aadl2upaal/parser/MA.xml"));
        aaxlParser3.createAADLModel();
    }
}
