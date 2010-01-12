package com.ochafik.lang.jnaerator.parser;

import java.util.ArrayList;
import java.util.List;

public class ExternDeclarations extends Declaration {
	String language;
	List<Declaration> declarations = new ArrayList<Declaration>();
	public List<Declaration> getDeclarations() {
		return unmodifiableList(declarations);
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public void setDeclarations(List<Declaration> declarations) {
		changeValue(this, this.declarations, declarations);
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visitExternDeclarations(this);
	}
	@Override
	public Element getNextChild(Element child) {
		Element e = getNextSibling(declarations, child);
		if (e != null)
			return e;
		return super.getNextChild(child);
	}
	@Override
	public Element getPreviousChild(Element child) {
		Element e = getPreviousSibling(declarations, child);
		if (e != null)
			return e;
		return super.getPreviousChild(child);
	}
	public void addDeclarations(List<Declaration> ds) {
		if (ds == null)
			return;
		for (Declaration d : ds)
			d.setParentElement(this);
		declarations.addAll(ds);
	}
	
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (replaceChild(declarations, Declaration.class, this, child, by))
			return true;
		return super.replaceChild(child, by);
	}
	@Override
	public String toString(CharSequence indent) {
		return "extern \"" + getLanguage() + "\" {" + implode(getDeclarations(), "\n\t" + indent, indent + "\t") + "\n" + indent + "}";
	}

}
