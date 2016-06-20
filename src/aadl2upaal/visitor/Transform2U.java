package aadl2upaal.visitor;

import java.util.ArrayList;

import aadl2upaal.aadl.AADLModel;
import aadl2upaal.aadl.ACompoent;
import aadl2upaal.aadl.ACompoentDeclare;
import aadl2upaal.aadl.ACompoentImpl;
import aadl2upaal.aadl.APort;
import aadl2upaal.aadl.AProperties;
import aadl2upaal.aadl.Annex;
import aadl2upaal.aadl.BLESSAnnex;
import aadl2upaal.aadl.CompImpl;
import aadl2upaal.aadl.Connection;
import aadl2upaal.aadl.DataPort;
import aadl2upaal.aadl.Flow;
import aadl2upaal.aadl.HybirdAnnex;
import aadl2upaal.aadl.SubComp;
import aadl2upaal.aadl.UncertaintyAnnex;
import aadl2upaal.upaal.Channel;
import aadl2upaal.upaal.Template;
import aadl2upaal.upaal.UModel;

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
	public void visit(UncertaintyAnnex ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processCompImpl(CompImpl compImpl) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
