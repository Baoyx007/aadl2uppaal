package aadl2upaal.visitor;

import java.util.ArrayList;

import aadl2upaal.aadl.AADLModel;
import aadl2upaal.aadl.ACompoent;
import aadl2upaal.aadl.ACompoentDeclare;
import aadl2upaal.aadl.ACompoentImpl;
import aadl2upaal.aadl.APort;
import aadl2upaal.aadl.AProperties;
import aadl2upaal.aadl.UVar;
import aadl2upaal.aadl.BLESSAnnex;
import aadl2upaal.aadl.CompImpl;
import aadl2upaal.aadl.Connection;
import aadl2upaal.aadl.DataPort;
import aadl2upaal.aadl.Flow;
import aadl2upaal.aadl.HybirdAnnex;
import aadl2upaal.aadl.SubComp;
import aadl2upaal.aadl.UncertaintyAnnex;
import aadl2upaal.upaal.*;

public class Transform2U implements NodeVisitor {

    private UModel umodel = null;

    public Transform2U(UModel u) {
        // TODO Auto-generated constructor stub
        this.umodel = u;
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
    public void visit(BLESSAnnex ba) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(HybirdAnnex ha) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(UncertaintyAnnex ua, Template t) {
        // local var
        t.declarations += "\n";
        for (UVar v : ua.getVars()) {
            t.declarations += "double " + v.getName() + ";\n";
            t.declarations+="clock d_t";// for delay location
            if (v.getType().equals("time")) {
                //在前端插入一条边和一个location
                int[] i = {0};
                t.getTrans().stream().filter(trans -> trans.getSnd() != null && trans.getSnd().getName().endsWith(v.getApplied().getName())).forEach(trans -> {
                    Location delay_location = new Location("temp" + String.valueOf(i[0]), null);
                    delay_location.invariant+="&& d_t<="+v.getName();
                    Location src =trans.src;
                    trans.src=delay_location;
                    trans.setGuard("d_t>="+v.getName());

                    src.setUrgent(true);
                    Transition delay_trans = new Transition(src, delay_location, 0, "");
                    delay_trans.setGuard(v.getName()+"="+v.dist.toString()+";");
                    delay_trans.setUpdate("d_t=0");

                    i[0]++;
                });
            } else if (v.getType().equals("static price")) {
                //在declaration 中初始化
                String insertDeclared = v.getName()+"="+v.dist.toString()+";";
                int insertPosition = t.declarations.lastIndexOf("initialize(){");
                t.declarations = t.declarations.substring(0,insertPosition)+insertDeclared+t.declarations.substring(insertPosition,t.declarations.length());
            }else if(v.getType().equals("dynamic price")){
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
}
