package aadl2upaal.visitor;

import aadl2upaal.aadl.*;
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
	void visit(ACompoentDeclare declare);
	void visit(ACompoentImpl impl);
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