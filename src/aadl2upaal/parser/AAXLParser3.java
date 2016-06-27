package aadl2upaal.parser;

import aadl2upaal.aadl.*;
import aadl2upaal.upaal.Location;
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
                    String comp_name_string = comp_name.getValue().substring(0, comp_name.getValue().indexOf("."));
                    ACompoentImpl ab_impl = new ACompoentImpl(comp_name_string);
                    for (ACompoent comp : amodel.comps) {
                        if (comp.getName().equals(comp_name_string)) {
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
                    //把impl 加入到 component 中
                    String comp_name_string = comp_name.getValue().substring(0, comp_name.getValue().indexOf("."));
                    ACompoentImpl th_impl = new ACompoentImpl(comp_name_string);
                    for (ACompoent comp : amodel.comps) {
                        if (comp.getName().equals(comp_name_string)) {
                            comp.setCompoentImpl(th_impl);
                        }
                    }

                    //遍历所有annex
                    for (Element annex : ele.getChildren("ownedAnnexSubclause")) {
                        Element parsedAnnexSubclause = annex.getChild("parsedAnnexSubclause");
                        String annex_name = parsedAnnexSubclause.getAttributeValue("name");
                        process_annex(annex_name, parsedAnnexSubclause, th_impl);
                    }
                } else {
                    ACompoent thread_comp = new ACompoent(comp_name.getValue());
                    ACompoentDeclare th_declare = new ACompoentDeclare(comp_name.getValue());
//                    process_ports(ele, de_declare);

                    amodel.comps.add(thread_comp);
                    thread_comp.setCompoentDeclare(th_declare);
                }
            } else if (type.getValue().contains("Device")) {

                if (type.getValue().contains("Implementation")) {
                    //把impl 加入到 component 中
                    String comp_name_string = comp_name.getValue().substring(0, comp_name.getValue().indexOf("."));
                    ACompoentImpl de_impl = new ACompoentImpl(comp_name_string);
                    for (ACompoent comp : amodel.comps) {
                        if (comp.getName().equals(comp_name_string)) {
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


            //System.out.println("comp");
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
//            Element con = parsedAnnexSubclause.getChild("con");
//            for(Element cn : con.getChildren("const")){
//                String c_name = cn.getChild("constant").getAttributeValue("name");
//                HConstant c = new HConstant(c_name, "");
//                //c.setInitVal(0.7);
//                hybirdAnnex.getConstants().add(c);
//
//            }

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
                    //choice
                    Element choice = process.getChild("choice");
                    if (choice != null) {
                        for (Element alt : choice.getChildren("alt")) {
                            Element relation = alt.getChild("boolean_expression").getChild("bool").getChild("relation");
                            String relation_symbol = relation.getAttributeValue("relation_symbol");
                            relation_symbol=relation_symbol.replace("<","&lt;").replace(">","&gt;");

                            String variable_path = relation.getChild("lhs").getChild("term").getAttributeValue("variable");
                            Element variable_element = xpfac.compile(Utils.convert2xpath(variable_path), Filters.element()).evaluateFirst(doc.getRootElement());
                            AVar left = null;
                            for (AVar v : hybirdAnnex.getVariables()) {
                                if (v.getName().equals(variable_element.getAttributeValue("name"))) {
                                    left = v;
                                    break;
                                }
                            }

                            String integer_literal = relation.getChild("rhs").getChild("term").getAttributeValue("integer_literal");

                            String to_process_path = alt.getAttributeValue("behavior_process");
                            Element to_process_element = xpfac.compile(Utils.convert2xpath(to_process_path), Filters.element()).evaluateFirst(doc.getRootElement());
                            //for skip
                            if (to_process_element.getChild("process").getAttributeValue("skip", "xx").equals("skip")) {
                                HContinuous hContinuous = new HContinuous();
                                hContinuous.setRank(-1);
                                hContinuous.setLeft(left);
                                hContinuous.alt=relation_symbol;
                                hContinuous.setRight(Integer.valueOf(integer_literal));
                                hp.getEvolutions().add(hContinuous);
                            }else {
                                HChoice hChoice = new HChoice();
                                String process_name = to_process_element.getAttributeValue("name");
                                hChoice.guard=left.getName()+relation_symbol+integer_literal;
                                hChoice.end=hp;
                                hp.choice=hChoice;
                            }
                        }
                    }

                    //continuous_evolution
                    Element continuous_evolution = process.getChild("continuous_evolution");
                    if (continuous_evolution != null) {
                        // var
                        Element left = continuous_evolution.getChild("lhs").getChild("diff").getChild("time_derivative");
                        String order = left.getAttributeValue("order");


                        XPathExpression xp = xpfac.compile(Utils.convert2xpath(left.getAttributeValue("x")), Filters.element());
                        Element x = (Element) xp.evaluateFirst(doc.getRootElement());

                        String right_path = continuous_evolution.getChild("rhs").getChild("diff").getAttributeValue("variable", "-1");
                        String right_name;
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
                        Hassignment hassignment = new Hassignment();
                        String local_variable_path = assignment.getAttributeValue("local_variable");
                        Element local_variable = xpfac.compile(Utils.convert2xpath(local_variable_path), Filters.element()).evaluateFirst(doc.getRootElement());

                        String val = assignment.getChild("expression").getChild("term").getAttributeValue("integer_literal", "null");

                        String val_d = assignment.getChild("expression").getChild("term").getAttributeValue("real_literal", "null");
                        String minus = assignment.getChild("expression").getChild("term").getAttributeValue("unary_minus", "false");
                        if (val.equals("null") && val_d.equals("null")) {
                            //*
                            String right = "";
                            Element expression = assignment.getChild("expression");
                            String operator = expression.getAttributeValue("operator");
                            List<Element> terms = expression.getChildren("term");
                            String var_1 = xpfac.compile(Utils.convert2xpath(terms.get(0).getAttributeValue("variable")), Filters.element()).evaluateFirst(doc.getRootElement()).getAttributeValue("name");
                            if (terms.get(0).getAttributeValue("unary_minus", "false").equals("true")) {
                                var_1 = "-" + var_1;
                            }
                            String var_2 = xpfac.compile(Utils.convert2xpath(terms.get(1).getAttributeValue("variable")), Filters.element()).evaluateFirst(doc.getRootElement()).getAttributeValue("name");
                            if (terms.get(1).getAttributeValue("unary_minus", "false").equals("true")) {
                                var_2 = "-" + var_2;
                            }

                            right = var_1 + operator + var_2;
                            hassignment.right = right;
                        }
                        if (!val.equals("null")) {
                            if (minus.equals("true")) {
                                hassignment.setVal(Integer.valueOf(val) * -1);
                            } else {
                                hassignment.setVal(Integer.valueOf(val));
                            }
                        }
                        if (!val_d.equals("null")) {
                            if (minus.equals("true")) {
                                hassignment.setVal(Double.valueOf(val_d) * -1);
                            } else {
                                hassignment.setVal(Double.valueOf(val_d));
                            }
                        }

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
                                break;
                            }

                        }
                        hp.isIinitial = true;
                    }
                }
                hybirdAnnex.getBehavior().add(hp);
            }


            impl.getAnnexs().add(hybirdAnnex);
        } else if (annex_name.equals("BLESS")) {
            BLESSAnnex ba = new BLESSAnnex("");

            // invariant
            Element invariant = parsedAnnexSubclause.getChild("invariant");
            if (invariant != null) {
                ;
            }

            //var
            Element var = parsedAnnexSubclause.getChild("var");
            if (var != null) {
                for (Element bv : var.getChildren("bv")) {
                    String v_name = bv.getChild("variable_names").getChild("behavior_variable").getAttributeValue("var");
                    String v_type = bv.getChild("type").getAttributeValue("data_component_reference");
                    v_type = v_type.substring(v_type.indexOf("#") + 1).replace(".", "::");
                    v_type = "double";
                    BVar v = new BVar(v_name, v_type);

                    Element expression = bv.getChild("expression");
                    if (expression != null) {
                        Element init_val = expression.getChild("se").getChild("v").getChild("const ");
                        if (init_val != null) {
                            int val = Integer.valueOf(init_val.getAttributeValue("integer_literal", "0"));
                            v.setInitVal(val);
                        }
                    }

                    ba.getVariables().add(v);
                }
            }

            //states
            List<Element> states = parsedAnnexSubclause.getChildren("states");
            for (Element state : states) {

                Location loc = new Location(state.getAttributeValue("name"), null);
                String tag = state.getAttributeValue("tag", "null");
                if (tag.equals("initial")) {
                    loc.isInitial = true;
                } else if (tag.equals("complete")) {
                    loc.isCommitted = true;
                }
                ba.getLocs().add(loc);
            }
            //transitions
            Element transitions = parsedAnnexSubclause.getChild("transitions");
            if (transitions != null) {
                for (Element bt : transitions.getChildren("bt")) {
                    String sources = bt.getAttributeValue("sources");
                    Element source_ele = (Element) xpfac.compile(Utils.convert2xpath(sources), Filters.element()).evaluateFirst(doc.getRootElement());
                    Location src_loc = Utils.find_state_by_name(source_ele.getAttributeValue("name", ""), ba.getLocs());

                    String destination = bt.getAttributeValue("destination");
                    Element dst_ele = (Element) xpfac.compile(Utils.convert2xpath(destination), Filters.element()).evaluateFirst(doc.getRootElement());
                    Location dst_loc = Utils.find_state_by_name(dst_ele.getAttributeValue("name", ""), ba.getLocs());

                    String bt_name = bt.getChild("transition_label").getAttributeValue("name");
                    BTransition bt_aadl = new BTransition();
                    bt_aadl.setSrc(src_loc);
                    bt_aadl.setDst(dst_loc);
                    bt_aadl.setName(bt_name);

                    //guard
                    Element execute = bt.getChild("execute");
                    if (execute != null) {
                        String source_annex = parsedAnnexSubclause.getParentElement().getAttributeValue("sourceText");
                        Pattern compile = Pattern.compile(bt_name + ".*?-\\[([\\s\\S]*?)\\]-");
                        Matcher matcher = compile.matcher(source_annex);
                        String guard = "";
                        if (matcher.find()) {
                            guard = matcher.group(1);
                            guard = guard.replaceAll("&#x9;", "");
                            guard = guard.replaceAll("&#xA;", "");
                            guard = guard.replaceAll("\n", " ");
                            guard = guard.replaceAll("\t", "");
//                            System.out.println(guard);
                            guard = guard.replaceAll("<", "&lt;");
                            bt_aadl.setGuard(guard);
                        }

                    }

                    //dispatch
                    Element dispatch = bt.getChild("dispatch");
                    if (dispatch != null) {
                        ;
                    }

                    //action
                    ArrayList<BUpdate> bu = new ArrayList<>();


                    Element behavior = bt.getChild("behavior");
                    List<Element> action = null;
                    if (behavior != null) {
                        //normal
                        action = behavior.getChildren("action");

                        if (action != null) {

                            process_actions(ba, bu, action);
                        }
                    }
                    bt_aadl.setUpdate(bu);
                    ba.getTrans().add(bt_aadl);
                }
            }

            impl.getAnnexs().add(ba);

        } else if (annex_name.equals("Uncertainty")) {
            UncertaintyAnnex ua = new UncertaintyAnnex("");

            String sourceText = parsedAnnexSubclause.getAttributeValue("sourceText");

            String SEP = "\n \t\t\t";

            sourceText = sourceText.replace("\t", "");
            //System.out.println(sourceText);

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
                        if (port_by_name == null) {
                            time_delay.applied_var = comp_var[1];
                        } else {
                            time_delay.setApplied(port_by_name);
                        }
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
                        //System.out.println(matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4));
                        Distribution dist = new Distribution();
                        dist.setDistName(matcher.group(2));

                        ArrayList<Double> params = new ArrayList<>();
                        params.add(Double.valueOf(matcher.group(3)));
                        params.add(Double.valueOf(matcher.group(4)));
                        dist.setParas(params);

                        for (UVar var : ua.getVars()) {
                            if (var.getName().equals(matcher.group(1))) {
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
                for (String line : split) {
                    if (line.contains("=")) {
                        ua.getQueries().add(line.substring(line.indexOf("=") + 1));
                    }
                }
            }

            impl.getAnnexs().add(ua);
        } else {
            throw new RuntimeException("not supprot annex");
        }
    }

    private void process_actions(BLESSAnnex ba, ArrayList<BUpdate> bu, List<Element> action) {
        for (Element act : action) {
            //precondition
            //act
            Element each_act = act.getChild("action");

            //for not basic -elq
            Element elq = each_act.getChild("elq");
            if (elq != null) {
                List<Element> children = elq.getChild("actions").getChildren("action");
                process_actions(ba, bu, children);

            }

            Element basic = each_act.getChild("basic");
            if (basic != null) {
                //communication
                Element communication = basic.getChild("communication");
                if (communication != null) {
                    //inport
                    if (communication.getChild("pi") != null) {
                        String port_name = communication.getChild("pi").getAttributeValue("port");
                        String val = communication.getChild("pi").getChild("var").getChild("pn").getAttributeValue("identifier");
                        BVar varByName = Utils.getVarByName(val, ba.getVariables());
                        //TODO 应该用connection来查找
                        APort port_by_name = Utils.find_port_by_name(amodel, port_name);
                        if (port_by_name == null) {
                            //还没初始化出来
                            port_by_name = new APort(port_name, APort.out);
                        }
                        APort same_oppo_port = port_by_name.get_same_oppo_port();
                        bu.add(new BUpdate(null, same_oppo_port, varByName));
                    } else if (communication.getChild("po") != null) {
                        //outport
                        //event
                        String port_name = communication.getChild("po").getAttributeValue("port");
                        APort port_by_name = Utils.find_port_by_name(amodel, port_name);
                        if (port_by_name == null) {
                            //还没初始化出来
                            port_by_name = new APort(port_name, APort.in);
                        }
                        APort same_oppo_port = port_by_name.get_same_oppo_port();

                        //data
                        //Attribute val = (Attribute) xpfac.compile("//pn/@identifier", Filters.attribute()).evaluateFirst(communication);
                        Element eor = communication.getChild("po").getChild("eor");
                        String val = null;
                        boolean minus = false;
                        if (eor != null) {
                            Element child = eor.getChild("exp1").getChild("se");

                            String minus1 = child.getAttributeValue("minus", "false");
                            if (minus1.equals("true")) {
                                minus = true;
                            }
                            Element variable = child.getChild("v").getChild("variable");
                            if (variable != null) {
                                val = child.getChild("v").getChild("variable").getChild("pn").getAttributeValue("identifier");
                            }
                            Element const_v = child.getChild("v").getChild("const");
                            if (const_v != null) {
                                //sb:rate
                                //val=
                            }
                        }

                        if (val != null) {
                            BVar varByName = Utils.getVarByName(val, ba.getVariables());
                            if (minus) {
                                varByName.setInitVal(varByName.getInitVal() * -1);
                            }
                            bu.add(new BUpdate(null, same_oppo_port, varByName));
                        } else {
                            bu.add(new BUpdate(null, same_oppo_port, null));
                        }
                    }

                }
                //mutil_assgin

            }
            //postcondition
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
