package aadl2upaal.visitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import aadl2upaal.upaal.Channel;
import aadl2upaal.upaal.Location;
import aadl2upaal.upaal.Template;
import aadl2upaal.upaal.Transition;
import aadl2upaal.upaal.UModel;

public class UpaalWriter {
	public final static String sep = "_";
	public File outFile;
	PrintWriter out;

	public UpaalWriter(File f) {
		outFile = f;
	}

	public void processUModel(UModel model) throws IOException {
		out = new PrintWriter(outFile);
		//if (getInfo(model, out)) {
		//	return;
		//}
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		out.println("<!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_2.dtd'>");
		out.println("<nta>");

		//global declaration 部分

		// channel 部分
		out.print("<declaration>clock c;");
		for (Channel ch : model.channels())
			out.printf("broadcast chan %s;", ch);
		// 参数
		out.println("const int PERIOD=2;\nconst int MAsize=3;\nconst int SR=3;\nconst int b=1;\nconst int start=0;\nconst int SB_Rate=-8;\nconst int EB_Rate=-10;\nconst double PI = 3.1415926;\n");

        // normal_random
		out.println("double normal_random()");
		out.println("{");
		out.println("double u = random(1);");
		out.println("double v = random(1);");
		out.println("double x = sqrt((-2) * ln(u)) * cos(2 * PI * v);");
		out.println("if(x&lt;0){return x*-1;}else{ return x;}}");

		String normal_func="double Normal(double mu, double sigma){\n" +
				"    double u = random(1);\n" +
				"    double v = random(1);\n" +
				"    double x = sqrt((-2) * ln(u)) * cos(2 * PI * v);\n" +
				"    double ret =  x*sigma + mu;\n" +
				"    if(ret&lt;0)\n" +
				"        return ret*-1;\n" +
				"else\n" +
				"    return ret;\n" +
				"}";
		out.println(normal_func);

		String a_strategy = "\n" +
				"double a_strategy(){\n" +
				"    return  1.0;\n" +
				"}";
		out.print(a_strategy);


		out.print(model.getDeclaration());
		out.println("</declaration>");

		// System.out.println("model.templates.size(): " +
		// model.templates.size());

        //process template
		for (Template t : model.templates) {
			out.println();
			processTemplate(t, model);
			out.println();
		}

		out.print("<system> system ");
		Iterator<Template> itr = model.templates.iterator();
		while (itr.hasNext()) {
			out.print(itr.next());
			if (itr.hasNext())
				out.print(',');
			else
				out.print(';');
		}
		out.println("</system>");

		// queries
		out.println("<queries>");
		out.println("</queries>");

		out.println("</nta>");
		out.close();
	}

	public void processTemplate(Template t, UModel model) throws IOException {
		out.println("<template>");
		out.printf("<name>%s</name>%n", t);

		// declaration
		out.println("<declaration>");

		//local clock
		//out.println("clock "+t+"_clock");
		// 可以写在文件里
		String clock = (t == model.getTopTemplate()) ? "c" : t.toString()
				+ "_clock";
		if (t != model.getTopTemplate())
			out.printf("clock %s;%n", clock);
		out.println();

		out.print(t.declarations);
		out.println("</declaration>");

		// location
		for (Location l : t.locs) {
			out.printf("<location id=\"id%d\">%n", l.id);
			out.printf("<name>%s</name>%n", l);

			out.printf("<label kind=\"invariant\">%s </label>%n",
					l.getInvariant());

			if (l.isUrgent()) {
				out.println("<urgent/>");
			}
			if (l.isCommitted)
				out.println("<committed/>");
			out.println("</location>");
			out.println();
		}
		// printing initial locations
		for (Location l : t.locs) {
			if (!l.isInitial)
				continue;
			out.printf("<init ref=\"id%d\"/>%n", l.id);
			out.println();
		}

		// transition
		for (Transition tr : t.trans) {
			out.printf("<!-- %s -->%n", tr);
			out.println("<transition>");
			out.printf("<source ref=\"id%d\"/>%n", tr.src.id);
			out.printf("<target ref=\"id%d\"/>%n", tr.dst.id);
			if (tr.snd != null)
				out.printf("<label kind=\"synchronisation\">%s!</label>%n",
						tr.snd);
			if (tr.rec != null)
				out.printf("<label kind=\"synchronisation\">%s?</label>%n",
						tr.rec);
			if (tr.latency != null)
				out.printf("<label kind=\"guard\">%s &lt; %d </label>%n",
						clock, tr.latency);
			if (tr.update() != null) {
				out.printf("<label kind=\"assignment\">%s </label>%n",
						tr.update);
			}
			out.println("</transition>");
			out.println();
		}
		out.println("</template>");
	}

	private boolean getInfo(UModel model, PrintWriter out2) throws IOException {
		if (model.name.contains("MA")) {
			File file = new File("src/examples",
					"test4.xml");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String tempstr = "";
			while ((tempstr = br.readLine()) != null) {
				out.println(tempstr);
			}
			br.close();
			out.flush();
			out.close();
			return true;
		} else {
			return false;
		}
	}
}
