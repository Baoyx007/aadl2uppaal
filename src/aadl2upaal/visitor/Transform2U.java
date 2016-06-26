package aadl2upaal.visitor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aadl2upaal.aadl.*;
import aadl2upaal.upaal.*;

import javax.management.RuntimeErrorException;


public class Transform2U implements NodeVisitor {

    private UModel umodel = null;
    private AADLModel amodel = null;

    public Transform2U(AADLModel amodel) {
        this.amodel = amodel;
        this.umodel = new UModel(amodel.name);
    }

    public UModel transform() {
        //process compoents

        for (ACompoent comp : amodel.comps) {
            comp.getCompoentDeclare().accept(this);
            comp.getCompoentImpl().accept(this);
        }
        return umodel;
    }


    public void processCompImpl(ACompoentImpl impl) throws Exception {
        // TODO Auto-generated method stub
        String implName = impl.getName();
        Template implTemplate = null;
        for (Template template : this.umodel.getTemplates()) {
            if (template.getName().equals(implName)) {
                implTemplate = template;
                break;
            }
        }
        try {
            if (implTemplate == null)
                throw new Exception("null implTemplate");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // connections
        for (Connection c : impl.getConns()) {
            // template.addChannels(new Channel(c.getName(), direction));
            Channel src = null;
            Channel dst = null;

            for (Channel chan : this.umodel.getChans()) {
                if (chan.getName().equals(c.getSrcPort().getName())) {
                    src = chan;
                } else if (chan.getName().equals(c.getDstPort().getName())) {
                    dst = chan;
                }
            }
            implTemplate.addConnection(src, dst);
        }
        // subcompoent
        // just thread

        // annex
        // // annxe
        // ArrayList<Annex> annexs = compoentImpl.getAnnexs();
        // if(annexs == null){
        // // like RBC
        // for
        // }
    }

    @Override
    public void processConnection(Connection con) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processDataPort(DataPort port) {
        // TODO Auto-generated method stub

    }


    @Override
    public void processSubComp(SubComp comp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ACompoent acomp) {
        Template template = new Template(acomp.getName());
        this.umodel.addTemplate(template);
        // process declare
        ACompoentDeclare compoentDeclare = acomp.getCompoentDeclare();
        // port
        ArrayList<APort> ports = compoentDeclare.getPorts();
        for (APort aPort : ports) {
            template.addChannels(new Channel(aPort.getName(), aPort
                    .getDirection(), aPort.getType()));
        }
        // prop
        ArrayList<AProperties> props = compoentDeclare.getProps();
        for (AProperties prop : props) {
            this.umodel.getVals().add(prop.getItems());
        }

        // implement
        // conns 只是说明端口之间的连接的 ,是这个实现用到的
        ACompoentImpl compoentImpl = acomp.getCompoentImpl();
        for (Connection c : compoentImpl.getConns()) {
            // template.addChannels(new Channel(c.getName(), direction));
        }
        // compoentImpl.getConns()

    }

    @Override
    public void visit(HybirdAnnex ha, Template t) {
        //variables and constants
        for (AVar var : ha.getVariables()) {
            t.declarations += TypeMapping.instance.getMappingType(var.getType()) + " " + var.getName() + ";\n";
        }
        for (AVar var : ha.getConstants()) {
            t.declarations += TypeMapping.instance.getMappingType(var.getType()) + " " + var.getName() + ";\n";
        }

        //behavior
        for (HybirdProcess process : ha.getBehavior()) {
            //每个process 都是一个location
            if (process.getSkip()) {
                continue;
            } else {
                //不重复添加location
                Location loc = null;
                for (Location l : t.locs) {
                    if (l.name.equals(process.getName())) {
                        loc = l;
                        break;
                    }
                }
                if (loc == null) {
                    loc = new Location(process.getName(), null);
                }

                t.locs.add(loc);
                if (process.isIinitial) {
                    loc.setInitial(true);
                    loc.setUrgent(true);
                    Location sub_process = null;
                    for (Location l : t.locs) {
                        if (l.name.equals(process.subProcess.getName())) {
                            sub_process = l;
                            break;
                        }
                    }
                    if (sub_process == null) {
                        sub_process = new Location(process.subProcess.getName(), null);
                        t.locs.add(sub_process);
                    }
                    Transition transition = new Transition(loc, sub_process, 0, "");
                    transition.setUpdate(process.getStringAssignment());
                    transition.update += ",initialize()";
                    t.trans.add(transition);

                } else {
                    loc.invariant += process.getStringContinuous().toString();
                    Location original_loc = loc;
                    //处理中断
                    HInterrupt interrupt = process.getInterrupt();
                    if (interrupt != null) {
                        int i = 0;
                        for (HCommunication comm : interrupt.getComm()) {
                            //每个中断都会生成一条边和location
                            Location int_loc = new Location("int" + String.valueOf(i), null);
                            int_loc.setInvariant(process.getStringContinuous().toString());
                            Transition add_trans = new Transition(loc, int_loc, 0, "");
                            add_trans.chann = new Channel("c_" + comm.getP().getName(), comm.getDirection(), "");
                            if (comm.getDirection() == APort.out) {
                                loc.setUrgent(true);
                            }
                            add_trans.chann.value = comm.getVar().getName();
                            t.trans.add(add_trans);
                            t.locs.add(int_loc);
                            loc = int_loc;
                            i++;
                        }
                    }
                    //处理loop
                    if (process.isRepete && t.trans.size() > 0) {

                        t.trans.get(t.trans.size() - 1).dst = original_loc;
                        t.locs.remove(t.locs.size() - 1);
                    }
                    //处理 choice
                    HChoice choice = process.choice;
                    if (choice != null) {
                        if (choice.end.getName().equals(process.getName())) {
                            Location dst = loc;
                        } else {

                        }
                        Transition transition = new Transition(loc, loc, 0, "");
                        transition.setGuard(choice.guard);
                        transition.setUpdate(process.getAsssigments().get(0).toString());
                        t.trans.add(transition);
                    }
                }
            }
        }

    }

    @Override
    public void visit(BLESSAnnex ba, Template t) {
        // asserts and invariant is ignored
        t.declarations += "void update(){return;}";
        //variables
        for (BVar var : ba.getVariables()) {
            if (var.getName().equals("iMA") || var.getName().equals("iSeg")) {
                continue;
            }
            t.declarations += TypeMapping.instance.getMappingType(var.getType()) + " " + var.getName() + ";\n";
        }
        //states
        t.locs.addAll(ba.getLocs());

        //transition
        int i = 0;
        int j = 0;
        for (BTransition transition : ba.getTrans()) {
            //transition.guard
            String tmp_guard = parseExpression2Uppaal(transition.guard, ba.getVariables());
            if (tmp_guard.contains("*")) {
                String func = "\n\nbool guard_" + String.valueOf(j) + "(){ return " + tmp_guard + ";}\n\n";
                t.declarations += func;
                transition.guard = "guard_" + String.valueOf(j) + "()";
                j++;
            } else {
                if (tmp_guard.contains("=null")) {
                    tmp_guard=tmp_guard.replace("=null", ".seg[0].v1=0");
                }
                if(tmp_guard.contains("=")){
                    tmp_guard=tmp_guard.replace("=","==");
                }
                transition.guard = tmp_guard;
            }


            ArrayList<BUpdate> listOfUpdate = transition.getUpdate();

            //将每个transition 中包含信道的action 扩展成多个transition
            Transition template_trans = new Transition(transition.src, transition.dst, 0, transition.name);
            template_trans.setGuard(transition.guard);
            t.trans.add(template_trans);
            boolean need_extend_trans = false;
            for (BUpdate update : listOfUpdate) {
                if (update.getPort() == null) {
                    // need func to process this
                    //(iSeg',nSeg',i',v',s',b',iMA':=iSeg, nSeg,i,v,s,b,iMA)
                    if (template_trans.update.length() <= 0) {
                        template_trans.update += update.getExpression();
                    } else {
                        template_trans.update += ", " + update.getExpression();
                    }
                } else {
                    Channel channel = new Channel("c_" + update.getPort().getName(), update.getPort().getDirection(), "");
                    if (need_extend_trans) {
                        Location tmp_loc = new Location("extra_loc_" + i, null);
                        Transition tmp_trans = new Transition(tmp_loc, template_trans.dst, 0, "tmp_tans_" + i);
                        tmp_trans.chann = channel;
                        template_trans.dst = tmp_loc;
                        template_trans = tmp_trans;
                        t.locs.add(tmp_loc);
                        t.trans.add(tmp_trans);
                        i++;
                    } else {
                        if (update.getVar() != null) {
                            if (update.getVar().getName().contains("iMA")) {
                                ;
                            } else {
                                channel.value = update.getVar().getName();
                            }
                        }
                        template_trans.chann = channel;
                        need_extend_trans = true;
                    }
                }
            }
        }

    }


    @Override
    public void visit(UncertaintyAnnex ua, Template t) {
        // local var
        t.declarations += "\n";
        int[] i = {0};
        for (UVar v : ua.getVars()) {
            t.declarations += "double " + v.getName() + ";\n";

            if (v.getType().equals("time")) {
                t.declarations += "clock d_t;\n";// for delay location
                //在前端插入一条边和一个location
                Location delay_location = null;
                Transition delay_trans = null;
                for (Transition trans : t.getTrans()) {
                    if (trans.chann != null && trans.chann.getName().endsWith(v.getApplied().getName())) {
                        delay_location = new Location("temp" + String.valueOf(i[0]), null);
                        if (trans.dst.invariant == "") {
                            delay_location.invariant = " d_t &lt;=" + v.getName();
                        } else {
                            delay_location.invariant = trans.dst.invariant + " &amp;&amp; d_t &lt;=" + v.getName();
                        }
                        Location src = trans.src;
                        trans.src = delay_location;
                        trans.setGuard("d_t &gt;=" + v.getName());

                        src.setUrgent(true);
                        delay_trans = new Transition(src, delay_location, 0, "");
                        delay_trans.setUpdate("d_t=0," + v.getName() + "=" + v.dist);

                        i[0]++;
                    }
                }
                t.trans.add(delay_trans);
                t.locs.add(delay_location);
            } else if (v.getType().equals("static price")) {
                //在declaration 中初始化
                String insertDeclared = v.getName() + "=" + v.dist.toString() + ";";
                int insertPosition = t.declarations.lastIndexOf("initialize(){") + 13;
                t.declarations = t.declarations.substring(0, insertPosition) + insertDeclared + t.declarations.substring(insertPosition, t.declarations.length());
                //for (t.locs)
            } else if (v.getType().equals("dynamic price")) {
                //TODO search all occurrence, and replace by distribution

            }
        }

        //add query in this ua
        //TODO need little transform
        this.umodel.queries.addAll(ua.getQueries());

    }

    @Override
    public void visit(ACompoentDeclare declare) {
        declare.ports.stream().forEach(aPort -> umodel.chans.add(new Channel("c_" + aPort.getName(), aPort.getDirection(), "")));
        declare.ports.stream().filter(aPort -> aPort.getType() != AType.None).forEach(aPort -> umodel.values.add(new AVar("v_" + aPort.getName(), TypeMapping.instance.getMappingType(aPort.getType()))));
    }

    @Override
    public void visit(ACompoentImpl impl) {
        Template template = new Template(impl.getName());

        // for connection

        //for annex
        ArrayList<Annex> annexs = impl.getAnnexs();
        UncertaintyAnnex ua = null;
        for (Annex annex : annexs) {
            if (annex.getClass() == UncertaintyAnnex.class) {
                ua = (UncertaintyAnnex) annex;
            } else if (annex.getClass() == HybirdAnnex.class) {
                visit((HybirdAnnex) annex, template);
            } else {
                visit((BLESSAnnex) annex, template);
            }
        }


        if (ua != null) {
            if (annexs.size() == 1) {
                //仅仅包含UA
                ACompoentDeclare declare = null;
                for (ACompoent comp : amodel.comps) {
                    if (comp.getCompoentImpl() == impl) {
                        declare = comp.getCompoentDeclare();
                    }
                }
                if (declare == null) {
                    throw new NullPointerException("declare can not be null");
                }
                ArrayList<APort> inports = new ArrayList<>();
                ArrayList<APort> outports = new ArrayList<>();

                for (APort p : declare.ports) {
                    if (p.getDirection() == APort.in) {
                        inports.add(p);
                    } else {
                        outports.add(p);
                    }
                }
                //生成前端图 , 先接受后发送的循环图
                Location start = null;
                Transition lastTranstion = null;
                boolean isStart = true;
                for (APort p : inports) {
                    if (isStart) {
                        start = new Location(p.getName().toUpperCase(), null);
                        start.setInitial(true);
                        lastTranstion = new Transition(start, null, 0, "");
                        lastTranstion.chann = new Channel("c_" + p.getName(), p.getDirection(), "");
                        if(p.getClass()!=AEventPort.class) {
                            lastTranstion.chann.value = "v_" + p.getName();
                        }
                        isStart = false;
                        template.locs.add(start);
                        template.trans.add(lastTranstion);
                    } else {
                        Location tmp_loc = new Location(p.getName().toUpperCase(), null);
                        lastTranstion.dst = tmp_loc;
                        lastTranstion = new Transition(tmp_loc, null, 0, "");
                        lastTranstion.chann = new Channel("c_" + p.getName(), p.getDirection(), "");
                        lastTranstion.chann.value = "v_" + p.getName();
                        template.trans.add(lastTranstion);
                        template.locs.add(tmp_loc);
                    }
                }
                for (APort p : outports) {
                    Location tmp_loc = new Location(p.getName().toUpperCase(), null);
                    tmp_loc.setUrgent(true);
                    lastTranstion.dst = tmp_loc;
                    lastTranstion = new Transition(tmp_loc, null, 0, "");
                    lastTranstion.chann = new Channel("c_" + p.getName(), p.getDirection(), "");
                    if (p.getClass() == AEventPort.class) {
                        lastTranstion.chann.value = "";
                    } else {
                        lastTranstion.chann.value = "v_" + p.getName();
                    }

                    template.trans.add(lastTranstion);
                    template.locs.add(tmp_loc);
                }
                lastTranstion.dst = start;
            }
            template.declarations += "\n void initialize(){\n" +
                    " }";
            visit(ua, template);

        }
        umodel.addTemplate(template);
    }


    private String parseExpression2Uppaal(String guard, ArrayList<BVar> variables) {

        Pattern compile = Pattern.compile("(\\w)\\*\\*2");
        Matcher matcher = compile.matcher(guard);
        if (matcher.find()) {
            guard = matcher.replaceAll(matcher.group(1) + "*" + matcher.group(1));
        }
//        guard.replaceAll("\\*2",guard.indexOf());

        compile = Pattern.compile("iMA.=null");
        matcher = compile.matcher(guard);
        if (matcher.find()) {
            guard = matcher.replaceAll("iMA.seg[0].v1!=0");
        }

        compile = Pattern.compile("iSeg");
        matcher = compile.matcher(guard);
        if (matcher.find()) {
            guard = matcher.replaceAll("iMA.seg[iSeg]");
        }

        compile = Pattern.compile("\\[nSeg\\.(.*?)\\]");
        matcher = compile.matcher(guard);
        if (matcher.find()) {
            guard = matcher.replaceAll(".seg[nSeg]." + matcher.group(1));
        }

        compile = Pattern.compile("CTCS_Properties::start");
        matcher = compile.matcher(guard);
        if (matcher.find()) {
            guard = matcher.replaceAll("=0");
        }

//        compile = Pattern.compile("or");
//        matcher = compile.matcher(guard);
//        if (matcher.find()) {
//            guard = matcher.replaceAll("||");
//        }
//        compile = Pattern.compile("and");
//        matcher = compile.matcher(guard);
//        if (matcher.find()) {
//            guard = matcher.replaceAll("&amp;&amp;");
//        }
//        compile = Pattern.compile("not");
//        matcher = compile.matcher(guard);
//        if (matcher.find()) {
//            guard = matcher.replaceAll("!");
//        }
        return guard;
    }
}
