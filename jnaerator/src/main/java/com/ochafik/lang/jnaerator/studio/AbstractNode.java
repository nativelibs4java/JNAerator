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

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.ochafik.util.IteratorEnumeration;

abstract class AbstractNode implements TreeNode {
	
	protected final String name;
	protected final TreeNode parent;
	protected List<TreeNode> children;
	
	public AbstractNode(TreeNode parent, String name) {
		this.name = name;
		this.parent = parent;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration children() {
		buildChildren();
		return new IteratorEnumeration(children.iterator());
	}
	@Override
	public boolean getAllowsChildren() {
		buildChildren();
		return !children.isEmpty();
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		buildChildren();
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		buildChildren();
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		buildChildren();
		return children.indexOf(node);
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		buildChildren();
		return children.isEmpty();
	}
	public abstract void buildChildren();
	
	@Override
	public String toString() {
		return name;
	}
	
	public abstract String getContent();
}