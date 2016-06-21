package aadl2upaal.visitor;

import java.util.ArrayList;

import aadl2upaal.aadl.*;
import aadl2upaal.upaal.*;


public class Transform2U implements NodeVisitor {

    private UModel umodel = null;

    public Transform2U(UModel u) {
        // TODO Auto-generated constructor stub
        this.umodel = u;
    }

    public UModel transform(AADLModel amodel) {
        if (umodel == null) {
            umodel = new UModel(amodel.name);
        }
        //process compoents

        //process impl

        //process annex

        return umodel;
    }

    @Override
    public void processAADLModel(AADLModel model) {
        // TODO Auto-generated method stub

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
    public void processFlow(Flow flow) {
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
                    Location sub_process = new Location(process.subProcess.getName(), null);
                    Transition transition = new Transition(loc, sub_process, 0, "");
                    transition.setUpdate(process.getStringAssignment());
                    transition.update += ",initialize()";
                    t.trans.add(transition);

                } else {
                    loc.invariant += process.getStringContinuous().toString();
                    Location original_loc=loc;
                    //处理中断
                    HInterrupt interrupt = process.getInterrupt();
                    if (interrupt != null) {
                        int i = 0;
                        for (HCommunication comm : interrupt.getComm()) {
                            //每个中断都会生成一条边和location
                            Location int_loc = new Location("int" + String.valueOf(i), null);
                            int_loc.setInvariant(process.getStringContinuous().toString());
                            Transition add_trans = new Transition(loc, int_loc, 0, "");
                            add_trans.chann = new Channel("c_" + comm.getP().getName(), comm.getP().getDirection(), "");
                            add_trans.chann.value = comm.getVar().getName();
                            t.trans.add(add_trans);
                            t.locs.add(int_loc);
                            loc=int_loc;
                            i++;
                        }
                    }
                    //处理loop
                    if(process.isRepete){
                        t.trans.get(t.trans.size()-1).dst=original_loc;
                        t.locs.remove(t.locs.size()-1);
                    }

                }
            }
        }

    }

    @Override
    public void visit(BLESSAnnex ba, Template t) {
        // asserts and invariant is ignored

        //variables
        for (BVar var : ba.getVariables()) {
            t.declarations += TypeMapping.instance.getMappingType(var.getType()) + " " + var.getName() + ";\n";
        }
        //states
        t.locs.addAll(ba.getLocs());

        //transition
        for (BTransition transition : ba.getTrans()) {
            transition.guard = parseExpression2Uppaal(transition.guard, ba.getVariables());
            ArrayList<BUpdate> listOfUpdate = transition.getUpdate();

            //将每个transition 中包含信道的action 扩展成多个transition
            Transition template_trans = new Transition(transition.src, transition.dst, 0, transition.name);
            template_trans.setGuard(transition.guard);
            t.trans.add(template_trans);
            boolean need_extend_trans = false;
            int i = 0;
            for (BUpdate update : listOfUpdate) {
                if (update.getPort() == null) {
                    // need func to process this
                    //(iSeg',nSeg',i',v',s',b',iMA':=iSeg, nSeg,i,v,s,b,iMA)
                    if (template_trans.update.equals("")) {
                        template_trans.update += update.getExpression();
                    } else {
                        template_trans.update += ", " + update.getExpression();
                    }
                } else {
                    Channel channel = new Channel("c_"+update.getPort().getName(), update.getPort().getDirection(), "");
                    if (need_extend_trans) {
                        Location tmp_loc = new Location("tmp_loc_" + i, null);
                        Transition tmp_trans = new Transition(tmp_loc, template_trans.dst, 0, "tmp_tans_" + i);
                        tmp_trans.chann = channel;
                        template_trans.dst = tmp_loc;
                        template_trans = tmp_trans;
                        t.trans.add(tmp_trans);
                    } else {
                        channel.value = update.getVar().getName();
                        template_trans.chann = channel;
                        need_extend_trans = true;
                    }
                }
                i++;
            }
        }

    }


    @Override
    public void visit(UncertaintyAnnex ua, Template t) {
        // local var
        t.declarations += "\n";
        for (UVar v : ua.getVars()) {
            t.declarations += "double " + v.getName() + ";\n";
            t.declarations += "clock d_t";// for delay location
            if (v.getType().equals("time")) {
                //在前端插入一条边和一个location
                int[] i = {0};
                t.getTrans().stream().filter(trans -> trans.getSnd() != null && trans.getSnd().getName().endsWith(v.getApplied().getName())).forEach(trans -> {
                    Location delay_location = new Location("temp" + String.valueOf(i[0]), null);
                    delay_location.invariant += "&& d_t<=" + v.getName();
                    Location src = trans.src;
                    trans.src = delay_location;
                    trans.setGuard("d_t>=" + v.getName());

                    src.setUrgent(true);
                    Transition delay_trans = new Transition(src, delay_location, 0, "");
                    delay_trans.setGuard(v.getName() + "=" + v.dist.toString() + ";");
                    delay_trans.setUpdate("d_t=0");

                    i[0]++;
                });
            } else if (v.getType().equals("static price")) {
                //在declaration 中初始化
                String insertDeclared = v.getName() + "=" + v.dist.toString() + ";";
                int insertPosition = t.declarations.lastIndexOf("initialize(){");
                t.declarations = t.declarations.substring(0, insertPosition) + insertDeclared + t.declarations.substring(insertPosition, t.declarations.length());
            } else if (v.getType().equals("dynamic price")) {
                //TODO search all occurrence, and replace by distribution

            }
        }

        //add query in this ua
        //TODO need little transform
        this.umodel.queries.addAll(ua.getQueries());

    }

    @Override
    public void processCompImpl(CompImpl compImpl) throws Exception {
        // TODO Auto-generated method stub
        // 每个后端都添加一个空的initialize

    }

    private String parseExpression2Uppaal(String guard, ArrayList<BVar> variables) {
        return guard;
    }
}
