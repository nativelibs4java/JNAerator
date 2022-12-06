/*
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
 This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
	
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

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.tree.TreeNode;

import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.util.string.StringUtils;

class ElementListNode extends AbstractNode {

    public ElementListNode(TreeNode parent, String name, Collection<Element> elements) {
        super(parent, name);
        this.elements = elements;
    }
    final Collection<Element> elements;

    @Override
    public void buildChildren() {
        children = new ArrayList<TreeNode>();
        for (Element e : elements) {
            if (e != null) {
                children.add(new ElementNode(this, null, e));
            }
        }
    }

    @Override
    public String getContent() {
        return StringUtils.implode(elements, "\n");
    }
}