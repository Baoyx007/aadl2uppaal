package aadl2upaal.visitor;

import aadl2upaal.aadl.*;
import aadl2upaal.upaal.Template;

public interface NodeVisitor {
    void processConnection(Connection con);

    void processDataPort(DataPort port);

    void processSubComp(SubComp comp);

    void visit(ACompoent acomp);


    void visit(HybirdAnnex ha, Template t);

    void visit(BLESSAnnex ba, Template t);

    void visit(UncertaintyAnnex ua, Template t);

    void visit(ACompoentDeclare declare);

    void visit(ACompoentImpl impl);

}