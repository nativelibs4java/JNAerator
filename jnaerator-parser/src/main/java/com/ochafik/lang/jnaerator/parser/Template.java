package com.ochafik.lang.jnaerator.parser;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.ident;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.typeRef;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ochafik.lang.jnaerator.parser.Expression.FunctionCall;
import com.ochafik.lang.jnaerator.parser.Function.Type;

public class Template extends Declaration {
	final List<Arg> args = new ArrayList<Arg>();
	Declaration declaration;
	
	public Declaration getDeclaration() {
		return declaration;
	}
	public void setDeclaration(Declaration declaration) {
		this.declaration = changeValue(this, this.declaration, declaration);
	}
	@Override
	public Element getNextChild(Element child) {
		Element e = super.getNextChild(child);
		if (e != null)
			return e;
		return getNextSibling(args, child);
	}
	
	@Override
	public Function clone() {
		return (Function) super.clone();
	}
	
	@Override
	public Element getPreviousChild(Element child) {
		Element e = super.getPreviousChild(child);
		if (e != null)
			return e;
		return getPreviousSibling(args, child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
		if (replaceChild(args, Arg.class, this, child, by))
			return true;
		
		if (getDeclaration() == child) {
			setDeclaration((Declaration)by);
			return true;
		}
		
		return super.replaceChild(child, by);
	}
	
	
	public Arg addArg(Arg a) {
		if (a != null) {
			args.add(a);
			a.setParentElement(this);
		}
		return a;
	}
	public List<Arg> getArgs() {
		return unmodifiableList(args);
	}
	public void setArgs(List<Arg> args) {
		changeValue(this, this.args, args);
	}

	public void accept(Visitor visitor) {
		visitor.visitTemplate(this);
	}

}
