/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.studio;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.tree.TreeNode;

import com.ochafik.beans.BeansUtils;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.reflect.GettersAndSettersHelper;

class ElementNode extends AbstractNode {
	public ElementNode(TreeNode parent, String name, Element element) {
		super(parent, name);
		this.element = element;
		
	}
	final Element element;

	@SuppressWarnings("unchecked")
	@Override
	public void buildChildren() {
		if (children != null)
			return;
		GettersAndSettersHelper helper = element.getGettersAndSetters();
		children = new ArrayList<TreeNode>();
		for (String fieldName : helper.getFieldNames()) {
			if (fieldName.equals("parentElement"))
				continue;
			if (fieldName.equals("previousSibling"))
				continue;
			if (fieldName.equals("nextSibling"))
				continue;
			
			try {
//				if (helper.getGetter(fieldName) == null || helper.getSetter(fieldName) == null)
//					continue;
					
				Type c = helper.getFieldType(fieldName);
				Object beanValue = BeansUtils.get(element, fieldName);
				if (beanValue == null)
					continue;
				if (c instanceof Class && Element.class.isAssignableFrom((Class<?>)c)) {
					children.add(new ElementNode(this, fieldName, (Element)beanValue));
				} else if (c instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType)c;
					if (pt.getRawType() instanceof Class && Collection.class.isAssignableFrom((Class<?>)pt.getRawType())) {
						if (pt.getActualTypeArguments().length == 1) {
							Type ptt = pt.getActualTypeArguments()[0];
							if (ptt instanceof Class && Element.class.isAssignableFrom((Class<?>)ptt)) {
								Collection<Element> list = (Collection<Element>)beanValue;
								if (!list.isEmpty())
									children.add(new ElementListNode(this, fieldName, list));
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String getContent() {
		return element.toString();
	}

	@Override
	public String toString() {
		String s = super.toString();
		if (s == null) {
//			if (element instanceof Declaration) {
//				s = ((Declaration)element).getName();
//				if (s == null)
//					s = element.getClass().getSimpleName();
//				else
//					s += " (" + element.getClass().getSimpleName() + ")";
//			} else
				s = element.getClass().getSimpleName();
		} else
			s += " (" + element.getClass().getSimpleName() + ")";
		
		return s;
	}

	public Element getElement() {
		return element;
	}
}