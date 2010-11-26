package com.ochafik.lang.jnaerator.parser;

import java.util.ArrayList;
import java.util.List;

public class ExternDeclarations extends Declarations {
	String language;
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visitExternDeclarations(this);
	}
}
