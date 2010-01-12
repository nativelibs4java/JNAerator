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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ochafik.lang.SyntaxUtils;

public class XPathUtils {
	public static Map<String, SoftReference<XPathExpression>> xPathExpressionsCache = new HashMap<String, SoftReference<XPathExpression>>();
	private static SoftReference<XPath> sharedXPath; 
	public static XPath getSharedXPath() {
		XPath xPath = null;
		if (sharedXPath != null)
			xPath = sharedXPath.get();
		if (xPath == null) {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			sharedXPath = new SoftReference<XPath>(xPath = xPathFactory.newXPath()); 
		}
		return xPath;
	}
	public static List<Node> findNodesByXPath(String xPathString, Object source) throws XPathExpressionException {
		return XMLUtils.list((NodeList)getXPathExpression(xPathString).evaluate(source, XPathConstants.NODESET));
	}
	public static Iterable<Node> findNodesIterableByXPath(String xPathString, Object source) throws XPathExpressionException {
		return SyntaxUtils.iterable((NodeList)getXPathExpression(xPathString).evaluate(source, XPathConstants.NODESET));
	}
	public static Node findNodeByXPath(String xPathString, Object source) throws XPathExpressionException {
		NodeList list = (NodeList)getXPathExpression(xPathString).evaluate(source, XPathConstants.NODESET);
		int len = list.getLength();
		return len == 0 ? null : list.item(0); 
	}
	public static String findStringByXPath(String xPathString, Object source) throws XPathExpressionException {
		return (String)getXPathExpression(xPathString).evaluate(source, XPathConstants.STRING);
	}
	public static XPathExpression getXPathExpression(String xPathString) throws XPathExpressionException {
		SoftReference<XPathExpression> ref = xPathExpressionsCache.get(xPathString);
		XPathExpression expression = ref == null ? null : ref.get();
		if (expression == null)
			xPathExpressionsCache.put(xPathString, new SoftReference<XPathExpression>(expression = getSharedXPath().compile(xPathString)));
				
		return expression;
	}

}
