package aadl2upaal.parser;
// XML  packages
import javax.xml.xpath.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.*;

public final class Utils
{
   final static XPath xp = XPathFactory.newInstance().newXPath();

   /** Returns the first XML node in xmlFile matching the XPath expression, xpathExp */
   public static Node getFirstNode(File xmlFile, String xpathExp) throws Exception {
      return getFirstNode(new FileInputStream(xmlFile), xpathExp);
   }

   /** Returns a list of XML nodes in xmlFile matching the XPath expression, xpathExp */
   public static NodeList getNodes(File xmlFile, String xpathExp) throws Exception {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(xmlFile);
      return getNodes(doc, xpathExp);
   }

   public static Node getFirstNode(InputStream istream, String xpathExp) throws Exception {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(istream);
      return getFirstNode(doc, xpathExp);
   }

   public static NodeList getNodes(InputStream istream, String xpathExp) throws Exception {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(istream);
      return getNodes(doc, xpathExp);
   }

   /** Returns the first XML node in the context matching the XPath expression, xpathExp */
   public static Node getFirstNode(Object context, String xpathExp) throws Exception {
      Node n = (Node) xp.evaluate(xpathExp, context, XPathConstants.NODE);
      if (n == null) throw new Exception(
         "No XML node found in context matching XPath expression: " + xpathExp);
      return n;
   }

   /** Same as getFirstNode(Object context, String xpathExp) except
     * no exception is thrown if no node matches xpathExp
     */
   public static Node getFirstNode(Object context, String xpathExp,
      boolean noRequirement) throws Exception
   {
      return (Node) xp.evaluate(xpathExp, context, XPathConstants.NODE);
   }

   /** Returns a list of XML nodes in the context matching the XPath expression, xpathExp */
   public static NodeList getNodes(Object context, String xpathExp) throws XPathExpressionException {
      return (NodeList) xp.evaluate(xpathExp, context, XPathConstants.NODESET);
   }

   public static String getAttrVal(Node node, String attrName) throws Exception {
      Attr attr = (Attr) node.getAttributes().getNamedItem(attrName);
      if (attr == null) throw new Exception(String.format(
         "Attribute, %s, is not specified in XML node: %s",
         attrName, node.getNodeName()));
      String val = attr.getValue().trim();
      if (val.length() == 0) throw new Exception(String.format(
         "No non-white space characters found in attribute, %s, of XML node: %s",
         attrName, node.getNodeName()));
      return val;
   }

   public static boolean hasAttribute(Node node, String attrName) {
      return
       node.getAttributes().getNamedItem(attrName) != null;
   }

    /** Throws an exception if the Node name does not equal name */
    public static void chkName(Node node, String name) throws Exception {
      if(!name.trim().equals(node.getNodeName()))
         throw new Exception(String.format(
            "Node %s does not have name %s",
            node.getNodeName(), name));
    }
}