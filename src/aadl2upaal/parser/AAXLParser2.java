package aadl2upaal.parser;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import aadl2upaal.aadl.AADLModel;

public class AAXLParser2 {
	public File aaxlFile;

	public AAXLParser2() {
		this(null);
	}

	public AAXLParser2(File aaxlFile) {
		this.aaxlFile = aaxlFile;
	}

	public AADLModel createAADLModel() throws Exception {
		Document document = parse(this.aaxlFile);

		Element root = document.getRootElement();
		listNodes(root);
		// // iterate through child elements of root
		// for (Iterator i = root.elementIterator(); i.hasNext();) {
		// Element element = (Element) i.next();
		// // do something
		// System.out.println(element.getName() + ":" + element.getText());
		// }
		//
		// // iterate through attributes of root
		// for (Iterator i = root.attributeIterator(); i.hasNext();) {
		// Attribute attribute = (Attribute) i.next();
		// System.out.println(attribute.getText());
		// }

		// treeWalk(root);

		// rbc
		 List list = document.selectNodes( "//ownedClassifier" );
		 for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			 Element ele = (Element) iterator.next();
			 System.out.println(ele.getPath());
			
		}
//		Node node = document.selectSingleNode("//ownedClassifier[@type='aadl2:DeviceType']");
//		System.out.println(node.asXML());
		return null;

	}

	public void treeWalk(Element element) {
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			Node node = element.node(i);
			if (node instanceof Element) {
				System.out.println(node.asXML());
				treeWalk((Element) node);
			} else {
				System.out.println(node.asXML());
			}
		}
	}

	private Document parse(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	// 遍历当前节点下的所有节点
	public void listNodes(Element node) {
		System.out.println("当前节点的名称：" + node.getName());
		// 首先获取当前节点的所有属性节点
		List<Attribute> list = node.attributes();
		// 遍历属性节点
		for (Attribute attribute : list) {
			System.out.println("属性" + attribute.getName() + "-"
					+ attribute.getValue());
		}
		// 如果当前节点内容不为空，则输出
		if (!(node.getTextTrim().equals(""))) {
			System.out.println(node.getName() + "-" + node.getText());
		}
		// 同时迭代当前节点下面的所有子节点
		// 使用递归
		Iterator<Element> iterator = node.elementIterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			listNodes(e);
		}
	}

	public static void main(String[] args) throws Exception {
		AAXLParser2 aaxlParser2 = new AAXLParser2(new File(
				"src/aadl2upaal/parser/MA.xml"));
		aaxlParser2.createAADLModel();
	}
}
