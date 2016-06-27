package aadl2upaal.visitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aadl2upaal.aadl.APort;
import aadl2upaal.aadl.AVar;
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
        out.flush();
        //global declaration 部分

        // channel 部分
        out.print("<declaration>clock c;");
        for (Channel ch : model.channels())
            out.printf("broadcast chan %s;", ch);
        for (Channel ch : model.chans) {
            out.println("urgent chan " + ch.getName() + ";");
        }
        for (AVar var : model.values) {
            out.println(var.getType() + " " + var.getName() + ";");
        }
        // 参数
        out.println("const int PERIOD=2;\nconst int MAsize=3;\nconst int SR=3;\nconst int b=1;\nconst int start=0;\nconst int SB_Rate=-8;\nconst int EB_Rate=-10;\nconst double PI = 3.1415926;\n");

        out.println("//------------Lib for Distributions-------------------------");
        // normal_random
        out.println();
        out.println("double normal_random()");
        out.println("{");
        out.println("double u = random(1);");
        out.println("double v = random(1);");
        out.println("double x = sqrt((-2) * ln(u)) * cos(2 * PI * v);");
        out.println("if(x&lt;0){return x*-1;}else{ return x;}}");
        out.println();

        String normal_func = "double Normal(double mu, double sigma){\n" +
                "    double u = random(1);\n" +
                "    double v = random(1);\n" +
                "    double x = sqrt((-2) * ln(u)) * cos(2 * PI * v);\n" +
                "    double ret =  x*sigma + mu;\n" +
                "    return ret;\n" +
                "}";
        out.println(normal_func);

        out.println();
        String normal_time_func = "double Time_Normal(double mu, double sigma){\n" +
                "    double u = random(1);\n" +
                "    double v = random(1);\n" +
                "    double x = sqrt((-2) * ln(u)) * cos(2 * PI * v);\n" +
                "    double ret =  x*sigma + mu;\n" +
                "    if(ret&lt;0)\n" +
                "        return 0;\n" +
                "else\n" +
                "    return ret;\n" +
                "}";
        out.println(normal_time_func);

        out.println();
        String uniform = "double Uniform(int rangeLow, int rangeHigh) {\n" +
                "    double myRand = random(32767)/(1.0 + 32767); \n" +
                "    int range = rangeHigh - rangeLow + 1;\n" +
                "    double myRand_scaled = (myRand * range *1.0) + rangeLow;\n" +
                "    return myRand_scaled;\n" +
                "}";
        out.println(uniform);
        out.println();


        String possion_func = "int Poisson(double expectedValue) {\n" +
                "  int n = 0; //counter of iteration\n" +
                "  double limit; \n" +
                "  double x;  //pseudo random number\n" +
                "  limit = exp(-expectedValue);\n" +
                "  x = random(32767) / 32767; \n" +
                "  while (x &gt; limit) {\n" +
                "    n++;\n" +
                "    x = x* ( random(32767) / 32767);\n" +
                "  }\n" +
                "  return n;\n" +
                "}";
        out.println(possion_func);

        out.println();
        String expon_func = "double Expon(double x)\n" +
                "{\n" +
                "  double z;                     // Uniform random number (0 &lt; z  &lt; 1 )\n" +
                "  double exp_value;             // Computed exponential value to be returned\n" +
                "\n" +
                "  // Pull a uniform random number (0 &lt;  z &lt; 1 )\n" +
                "  do\n" +
                "  {\n" +
                "    z = random(1);\n" +
                "  }\n" +
                "  while ((z == 0) || (z == 1));\n" +
                "\n" +
                "  // Compute exponential random variable using inversion method\n" +
                "  exp_value = -x * log(z);\n" +
                "\n" +
                "  return(exp_value);\n" +
                "}";
        out.println(expon_func);

        String a_strategy = "\n" +
                "double a_strategy(){\n" +
                "    return  1.0;\n" +
                "}";
        out.println(a_strategy);

        out.println("typedef struct\n" +
                "{\n" +
                "    int ModeTypes;\n" +
                "    int v1;\n" +
                "    int v2;\n" +
                "    int e;\n" +
                "}Segment;\n" +
                "int iSeg,nSeg;\n" +
                "\n" +
                "typedef struct \n" +
                "{\n" +
                "    Segment seg[MAsize];\n" +
                "}MovementAuthority;\n" +
                "\n" +
                "MovementAuthority iMA;");

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
            out.print(itr.next().getName());
            if (itr.hasNext())
                out.print(',');
            else
                out.print(';');
        }
        out.println("</system>");

        // queries
        out.println("<queries>");
        for (String query : model.queries) {
            out.println("<query>");

            String right_query="";
            String[] unders = query.split("under");
            if(unders.length<2){
                right_query="Pr[<=1000](<>"+unders[0]+")";
            }else {
                right_query="Pr[<="+unders[1]+"](<>"+unders[0]+")";
            }


            out.println("<formula>" + right_query.replace("&","&amp;").replace(">","&gt;").replace("<","&lt;") + "</formula>");
            out.println("</query>");
        }
        out.println("</queries>");
        out.println("</nta>");
        out.close();
    }

    public void processTemplate(Template t, UModel model) throws IOException {
        out.println("<template>");
        out.printf("<name>%s</name>%n", t.getName());

        // declaration
        out.println("<declaration>");

        //local clock
        //out.println("clock "+t+"_clock");
        // 可以写在文件里
        //String clock = (t == model.getTopTemplate()) ? "c" : t.toString()
        //		+ "_clock";
        //if (t != model.getTopTemplate())
        //	out.printf("clock %s;%n", clock);
        out.println();
        //place initialize on the last
        Pattern compile = Pattern.compile("void initialize\\(\\)\\{[\\s\\S]*?\\}");
        Matcher matcher = compile.matcher(t.declarations);
        String init  = "";
        if (matcher.find()) {
            init = matcher.group();
            t.declarations = matcher.replaceFirst("");
        }
        t.declarations= t.declarations+init;
        out.print(t.declarations);
        out.println("</declaration>");

        // location
        int i = 1;
        for (Location l : t.locs) {
            l.id = i;
            out.printf("<location id=\"id%d\">%n", l.id);
            out.printf("<name>%s</name>%n", l.name);

            out.printf("<label kind=\"invariant\">%s </label>%n",
                    l.getInvariant());

            if (l.isUrgent()) {
                out.println("<urgent/>");
            }
            if (l.isCommitted)
                out.println("<committed/>");
            out.println("</location>");
            out.println();
            i++;
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
            String update = "";
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
            if (tr.chann != null) {
                if (tr.chann.getDirection() == APort.in) {
                    out.printf("<label kind=\"synchronisation\">%s?</label>%n", tr.chann.getName());
                    if (tr.chann.value.length() != 0)
                        update += tr.chann.value + "= v_" + tr.chann.getName().substring(2);
                } else {
                    out.printf("<label kind=\"synchronisation\">%s!</label>%n", tr.chann.getName());
                    if (tr.chann.value.length() != 0)
                        update += "v_" + tr.chann.getName().substring(2) + "=" + tr.chann.value;
                }
            }
            // chann 在synchronisation 和 assignment 都要加
            //if (tr.latency != null)
            //	out.printf("<label kind=\"guard\">%s &lt; %d </label>%n",
            //			clock, tr.latency);
            if (tr.getGuard() != null) {
                out.printf("<label kind=\"guard\" >%s</label>\n",
                        tr.getGuard());

            }
            if (tr.update != null) {
                if (update.length() > 0 && tr.update.length() > 0) {
                    update += ", " + tr.update;
                } else {
                    update += tr.update;
                }
            }
            out.printf("<label kind=\"assignment\">%s </label>%n",
                    update);

            out.println("</transition>");
            out.println();
        }
        out.println("</template>");
    }

//    private boolean getInfo(UModel model, PrintWriter out2) throws IOException {
//        if (model.name.contains("MA")) {
//            File file = new File("",
//                    "");
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String tempstr = "";
//            while ((tempstr = br.readLine()) != null) {
//                out.println(tempstr);
//            }
//            br.close();
//            out.flush();
//            out.close();
//            return true;
//        } else {
//            return false;
//        }
//    }
}
