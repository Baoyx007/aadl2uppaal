package aadl2upaal;

import java.io.File;

import aadl2upaal.aadl.AADLModel;
import aadl2upaal.nondeterministic.NondeterParser;
import aadl2upaal.parser.AAXLParser;
import aadl2upaal.visitor.UpaalGenerator;
import aadl2upaal.visitor.UpaalWriter;

public class Application {
	public static void main(String args[]) throws Exception {
		if (args.length < 2) {
			System.err
					.println("usage: java -jar aadl2upaal.jar <aaxl input file>  <upaal output file>");
			System.exit(1);
		}
		// aadl xml
		AAXLParser par = new AAXLParser(new File(args[0]));
		System.out.println("Generating AADL model from " + args[0]);
		AADLModel aadlModel = par.createAADLModel();
		System.out.println("AADL model created successfully");

		// ×ª»»
		UpaalGenerator gen = new UpaalGenerator();
		gen.processAADLModel(aadlModel);
		System.out.println("Generating Upaal model from AADL model");

		// uppaal xml
		UpaalWriter writer = new UpaalWriter(new File(args[1]));
		System.out.println("Writing Upaal model to: " + args[1]);
		writer.processUModel(gen.uModel);
		System.out.println("Upaal model written successfully");
	}
}