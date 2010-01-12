package com.ochafik.lang.jnaerator.parser;

import com.ochafik.util.string.StringUtils;

public class Property extends Declaration {
	Declaration declaration;
	public Property() {}
	public Property(Declaration declaration) {
		setDeclaration(declaration);
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visitProperty(this);
	}
	
	public void setDeclaration(Declaration declaration) {
		this.declaration = changeValue(this, this.declaration, declaration);
	}
	public Declaration getDeclaration() {
		return declaration;
	}
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child == getDeclaration()) {
			setDeclaration((Declaration)by);
			return true;
		}
		return super.replaceChild(child, by);
	}
	
	@Override
	public String toString(CharSequence indent) {
		String modStr = getModifiers().isEmpty() ? "" :
			"(" + StringUtils.implode(getModifiers(), " ") + ")";
		return "@property" + modStr  + " " + getDeclaration().toString(indent);
	}

}
