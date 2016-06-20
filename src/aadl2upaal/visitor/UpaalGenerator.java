package aadl2upaal.visitor;

import java.util.Iterator;
import java.util.List;

import aadl2upaal.aadl.AADLModel;
import aadl2upaal.aadl.BlessAutoMata;
import aadl2upaal.aadl.Connection;
import aadl2upaal.aadl.Flow;
import aadl2upaal.aadl.FlowElem;
import aadl2upaal.aadl.SubComp;
import aadl2upaal.nondeterministic.NondeterParser;
import aadl2upaal.upaal.Template;
import aadl2upaal.upaal.Transition;
import aadl2upaal.upaal.UModel;

public class UpaalGenerator {
	public UModel uModel = null;

	/** Current template to add new locations to */

	public void processAADLModel(AADLModel aModel) {

		uModel = new UModel(aModel.name);
		// templates
		for (SubComp sc : aModel.getAsystem().getSubComps()) {
			Template temp = uModel.addTemplate(sc.convert2Impl());
			String tempName = temp.getImpl().getName();
			for (Iterator<Connection> iterator = aModel.connections.iterator(); iterator
					.hasNext();) {
				Connection conn = iterator.next();
				String source = conn.getSrc().getSourcePort().getContext()
						.getName();
				String dest = conn.getDst().getSinkPort().getContext()
						.getName();
				Transition tran = null;
				if (tempName.equals(source)) {
					tran = temp.addTransition(conn);
					tran.setSendChannel(new Flow(conn));
				}
				if (tempName.equals(dest)) {
					tran = temp.addTransition(conn);
					tran.setReceiveChannel(new Flow(conn));
				}

//				System.out.println("ddd");
			}
		}
		List<Template> templates = uModel.getTemplates();
		for (Template template : templates) {
			if(template.getImpl().getName().equals("RBC")){
				processRBC(template);
			}else if(template.getImpl().getName().equals("Train")){
				processTrain(template);
			}else{
				processController(template,aModel.getBa());
			}
		}
		
		
		// for (Flow f : aModel.flows)
		// processFlow(f, addTemplate);
		// Template t = uModel.addTemplate(aModel.topSys);
		// uModel.setTopTemplate(t);
		// curTemp = t;
		
	}

	private void processController(Template template, BlessAutoMata blessAutoMata) {
		template.setHasBlessAnnex(true);
		template.setLocs(blessAutoMata.getLocs());
		template.setTrans(blessAutoMata.getTrans());
		System.out.println("ddd");
	}

	private void processTrain(Template template) {
		List<Transition> trans = template.getTrans();
		trans.get(1).src=trans.get(2).dst;
		trans.get(1).dst=trans.get(0).src;
		trans.get(2).src=trans.get(0).dst;
		trans.get(1).src.setInitial(true);
//		for (Transition tran : trans) {
//			System.out.println(tran.name+","+tran.src+","+tran.dst);
//		}
	}

	private void processRBC(Template template) {
		List<Transition> trans = template.getTrans();
		trans.get(0).src=trans.get(1).dst;
		trans.get(0).dst=trans.get(2).src;
		trans.get(1).src=trans.get(2).dst;
		trans.get(0).src.setInitial(true);
//		for (Transition tran : trans) {
//			System.out.println(tran.name+","+tran.src+","+tran.dst);
//		}
//		System.out.println("dd");
	}

	public void processFlow(Flow flow, Template curTemp) {
		boolean first = true;
		for (Connection con : flow.connections) {
			Transition tran = curTemp.addTransition(con);
			if (first) {
				tran.src.isInitial = true;
				if (curTemp != uModel.getTopTemplate())
					tran.setSendChannel(flow);
				first = false;
			}
			FlowElem e = con.dst;
			if (e instanceof Flow) {
				Flow f = (Flow) e;
				// add high level transition to represent subflow
				tran = curTemp.addTransition(f);
				// Store parent template
				Template t = curTemp;
				// System.out.println("flow: " + f);
				// System.out.println("flow.context: " + f.context);
				curTemp = uModel.addTemplate(f);
				// Synchronize with subflow
				tran.setReceiveChannel(f);
				processFlow(f, curTemp);
				curTemp = t;
			}
		}
	}

	public UModel getuModel() {
		return uModel;
	}

	public void setuModel(UModel uModel) {
		this.uModel = uModel;
	}

	public void processNondeterministic(NondeterParser nondeter) {
		uModel.setDeclaration(nondeter.getDeclaration());
		for(Template t:uModel.getTemplates()){
			if(t.getImpl().getName().equals("RBC")){
				t.setDeclarations(nondeter.getRBCDeclaration());
			}else if(t.getImpl().getName().equals("Train")){
				t.setDeclarations(nondeter.getTrainDeclaration());
			}else{
				t.setDeclarations(nondeter.getControllerDeclaration());
			}
		}
	}

	// public static void main(String args[]) throws Exception {
	// if (args.length < 1) {
	// System.err.println("usage: java parser.AAXLParser <aaxl file>");
	// System.exit(1);
	// }
	// AAXLParser par = new AAXLParser(new File(args[0]));
	// AADLModel model = par.createAADLModel();
	// UpaalGenerator gen = new UpaalGenerator();
	// gen.processAADLModel(model);
	// System.out.println("Created: " + gen.uModel);
	// }
}