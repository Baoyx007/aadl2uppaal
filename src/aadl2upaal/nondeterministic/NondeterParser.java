package aadl2upaal.nondeterministic;

import java.io.File;

import org.w3c.dom.NodeList;

import aadl2upaal.parser.Utils;

public class NondeterParser {
	public File nondFile;
	private String declaration = "";
	private String trainDeclaration = "";
	private String RBCDeclaration = "";
	private String ControllerDeclaration = "";

	public NondeterParser() {
		this(null);
	}

	public NondeterParser(File nondFile) {
		this.nondFile = nondFile;
		try {
			parseXML();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	void parseXML() throws Exception{
		NodeList conns = Utils.getNodes(nondFile, "//declaration");
		declaration=conns.item(0).getTextContent();
		trainDeclaration=Utils.getNodes(nondFile, "//train").item(0).getTextContent();
		RBCDeclaration=Utils.getNodes(nondFile, "//RBC").item(0).getTextContent();
		ControllerDeclaration=Utils.getNodes(nondFile, "//Controller").item(0).getTextContent();
		
		trainDeclaration=relpaceString(trainDeclaration);
		RBCDeclaration=relpaceString(RBCDeclaration);
		ControllerDeclaration=relpaceString(ControllerDeclaration);
		declaration=relpaceString(declaration);
		System.out.println("dd");
	}
	
	private String relpaceString(String s) {
		return s.replaceAll("&", "&amp;").replaceAll("<", " &lt;");
//		s.replaceAll("&", "&amp;");
//		s.replaceAll("&", "&amp;");
	}

	public String getDeclaration() {
		return declaration;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	public String getTrainDeclaration() {
		return trainDeclaration;
	}

	public void setTrainDeclaration(String trainDeclaration) {
		this.trainDeclaration = trainDeclaration;
	}

	public String getRBCDeclaration() {
		return RBCDeclaration;
	}

	public void setRBCDeclaration(String rBCDeclaration) {
		RBCDeclaration = rBCDeclaration;
	}

	public String getControllerDeclaration() {
		return ControllerDeclaration;
	}

	public void setControllerDeclaration(String controllerDeclaration) {
		ControllerDeclaration = controllerDeclaration;
	}

}
