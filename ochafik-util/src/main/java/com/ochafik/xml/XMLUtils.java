/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.xml;
import java.io.File;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.string.RegexUtils;
//import ochafik.babel.*;
public class XMLUtils  { 
	public static String getAttribute(Node node, String name) {
		NamedNodeMap m=node.getAttributes();
		if (m==null) return null;
		Node att=m.getNamedItem(name);
		if (att==null) return null;
		return att.getNodeValue();
	}
	public static Node getFirstNamedNode(Node node, String name) {
		if (node.getNodeName().equalsIgnoreCase(name)) return node;
		else {
			NodeList list=node.getChildNodes();
			for (int i=0,len=list.getLength();i<len;i++) {
				Node n=getFirstNamedNode(list.item(i),name);
				if (n!=null) return n;
			}
			return null;
		}
	}
	public static List<Node> getByName(Node node, String name) {
		List<Node> result = new LinkedList<Node>();
		getByName(node, name, result);
		return result;
	}
	
	private static void getByName(Node node, String name, List<Node> result) {
		if (node.getNodeName().equalsIgnoreCase(name)) {
			result.add(node);
		} else {
			NodeList list=node.getChildNodes();
			for (int i=0,len=list.getLength();i<len;i++) {
				getByName(list.item(i), name, result);
			}
		}
	}
	public static Collection<Node> getChildrenByName(Node node, String name) {
		Collection<Node> nodes=new ArrayList<Node>();
		getChildrenByName(node,name,nodes);
		return nodes;
	}
	private static void getChildrenByName(Node node, String name, Collection<Node> nodes) {
		NodeList list=node.getChildNodes();
		for (int i=0,len=list.getLength();i<len;i++) {
			Node child = list.item(i);
			if (child.getNodeName().equalsIgnoreCase(name)) {
				nodes.add(child);
			}
		}
	}
	public static List<Node> getChildElements(Node node) {
		NodeList children = node.getChildNodes();
		int childCount = children.getLength();
		List<Node> nodes = new ArrayList<Node>(childCount);
		for (int i = 0; i < childCount; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				nodes.add(child);
			}
		}
		return nodes;
	}
	public static List<Node> getAttributes(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		int attrCount = attrs.getLength();
		List<Node> nodes = new ArrayList<Node>(attrCount);
		for (int i = 0; i < attrCount; i++) {
			nodes.add(attrs.item(i));
		}
		return nodes;
	}
	public static StringBuffer gatherTextPCDATAAndCDATADescendants(Node node,StringBuffer sb,String separator) {
		if (sb==null) sb=new StringBuffer();
		int t=node.getNodeType();
		if (t==Node.CDATA_SECTION_NODE || t==Node.TEXT_NODE) {
			//sb.append("================= "+node.getNodeName()+" =================");
			sb.append(node.getNodeValue());
		} else {
			NodeList list = node.getChildNodes();
			for (int i=0,len=list.getLength();i<len;i++) {
				Node n=list.item(i);
				gatherTextPCDATAAndCDATADescendants(n,sb,separator);
				if (i<len-1) sb.append(separator);
			}
		}
		return sb;
	}
	static TransformerFactory tFactory = TransformerFactory.newInstance();
	public static String nodeToString(Node node) {
		try {
			Transformer transformer=tFactory.newTransformer();
			DOMSource source = new DOMSource(node);
			StringWriter writer=new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static final String escapeEntities(String s) {
		return s.replace("&","&amp;").replace("<","&lt;");//.replace(">", "&gt;");
	}
	

	private static final Pattern xmlTagPattern=Pattern.compile("<!--.*?-->|</?\\w+[^\">]*(?:(?:\"[^\"]*\")[\">]*)*>");
	private static final Pattern spacesPattern=Pattern.compile("\\s+");
	private static final MessageFormat spaceMessageFormat=new MessageFormat(" ");
	
	public static String stripTags(String xmlString) {
		String strippedHtml = RegexUtils.regexReplace(xmlTagPattern, xmlString, (Adapter<String[], String>)null);
		return RegexUtils.regexReplace(spacesPattern,strippedHtml,spaceMessageFormat).trim();
	}
	public static Document readXML(File file) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder.parse(file);
	}
	public static List<Node> list(NodeList nodeList) {
		if (nodeList == null)
			return null;
		
		int len = nodeList.getLength();
		List<Node> list = new ArrayList<Node>(len);
		for (int i = 0; i < len; i++)
			list.add(nodeList.item(i));
		return list;
	}
	
	
} 
