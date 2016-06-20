package aadl2upaal.upaal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import aadl2upaal.aadl.CompImpl;
import aadl2upaal.aadl.Flow;

public class UModel {
	public List<Template> templates = new Vector<Template>();
	private Template topTemplate = null;
	public String name;
	private String declaration = "";
	// back-end variables
	private ArrayList<HashMap> vals;
	private ArrayList<Channel> chans;

	public UModel(String name) {
		this.name = name;
		this.vals = new ArrayList<>();
	}

	public Template addTemplate(Flow f) {
		return addTemplate(f.context);
	}

	public void addTemplate(Template t) {
		this.templates.add(t);
	}

	public Template addTemplate(CompImpl impl) {
		// System.out.printf("addTemplate(%s)%n", impl);
		Template t = new Template(impl);
		int index = templates.indexOf(t);
		if (index == -1)
			templates.add(t);
		else
			t = templates.get(index);
		return t;
	}

	public ArrayList<HashMap> getVals() {
		return vals;
	}

	public void setVals(ArrayList<HashMap> vals) {
		this.vals = vals;
	}

	public List<Channel> channels() {
		List<Channel> set = new Vector<Channel>();
		for (Template tmp : templates) {
			for (Transition tran : tmp.trans) {
				if (tran.snd != null && !set.contains(tran.snd))
					set.add(tran.snd);
				if (tran.rec != null && !set.contains(tran.rec))
					set.add(tran.rec);
			}
		}
		return set;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTopTemplate(Template t) {
		if (!templates.contains(t))
			throw new NoSuchElementException(String.format(
					"Template %s does not exist in UModel %s", t, this));
		topTemplate = t;
	}

	public Template getTopTemplate() {
		return topTemplate;
	}

	public String toString() {
		return name;
	}

	public String getDeclaration() {
		return declaration;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	public ArrayList<Channel> getChans() {
		return chans;
	}

	public void setChans(ArrayList<Channel> chans) {
		this.chans = chans;
	}

}