package aadl2upaal.visitor;

import aadl2upaal.aadl.AADLModel;
import aadl2upaal.aadl.ACompoent;
import aadl2upaal.aadl.ACompoentImpl;
import aadl2upaal.aadl.BLESSAnnex;
import aadl2upaal.aadl.CompImpl;
import aadl2upaal.aadl.Connection;
import aadl2upaal.aadl.DataPort;
import aadl2upaal.aadl.Flow;
import aadl2upaal.aadl.HybirdAnnex;
import aadl2upaal.aadl.SubComp;
import aadl2upaal.aadl.UncertaintyAnnex;
import aadl2upaal.upaal.Template;

public interface NodeVisitor {
	public void processAADLModel(AADLModel model);

	public void processConnection(Connection con);

	public void processDataPort(DataPort port);

	public void processFlow(Flow flow);

	public void processSubComp(SubComp comp);

	public void visit(ACompoent acomp);


	void visit(HybirdAnnex ha, Template t);

	void visit(BLESSAnnex ba, Template t);

	void visit(UncertaintyAnnex ua, Template t);

	public void processCompImpl(CompImpl compImpl) throws Exception;

	// public void processEnd2EndFlow(End2EndFlow eflow) {}
	// public void processFlowImpl(FlowImpl impl) {}
	// public void processFlowSpec(FlowSpec spec) {}
	// public void processCompType(CompType type) {}
	// public void processDataType(DataType type) {}
	// public void processProcessImpl(ProcessImpl impl) {}
	// public void processProcessType(ProcessType type) {}
	// public void processPropertyAssociation(PropertyAssociation prop) {}
	// public void processSystemType(SystemType type) {}
	// public void processSystemImpl(SystemImpl impl) {}
}