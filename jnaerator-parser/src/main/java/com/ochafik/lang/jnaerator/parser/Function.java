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
package com.ochafik.lang.jnaerator.parser;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

//import org.rococoa.ReturnType;

import com.ochafik.lang.jnaerator.parser.Expression.FunctionCall;
import com.ochafik.util.string.StringUtils;

public class Function extends Declaration implements Declarator.MutableByDeclarator {
	//private Struct owner;

	final List<Arg> args = new ArrayList<Arg>();
    final List<TypeRef> thrown = new ArrayList<TypeRef>();
	final List<FunctionCall> initializers = new ArrayList<FunctionCall>();
	Statement.Block body;
	Type type;

	Identifier name;
	
	public Function setName(Identifier name) {
		if (name != null && name.toString().equals("NSStringFromSelector"))
			name = name;
		
		this.name = changeValue(this, this.name, name);
		return this;
	}
	public Identifier getName() {
		return name;
	}
	public enum Type {
		CFunction, ObjCMethod, CppMethod, JavaMethod, StaticInit
	}

	public void setInitializers(List<FunctionCall> initializers) {
		changeValue(this, this.initializers, initializers);
	}
	public void addInitializer(FunctionCall i) {
		if (i == null)
			return;
		
		i.setParentElement(this);
		initializers.add(i);
	}
	public List<FunctionCall> getInitializers() {
		return unmodifiableList(initializers);
	}
	String asmName;
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}
	public String getAsmName() {
		return asmName;
	}
	@Override
	public Element getNextChild(Element child) {
		Element e = super.getNextChild(child);
		if (e != null)
			return e;
		e = getNextSibling(initializers, child);
		if (e != null)
			return e;
		e = getNextSibling(thrown, child);
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
		e = getPreviousSibling(initializers, child);
		if (e != null)
			return e;
		e = getPreviousSibling(thrown, child);
		if (e != null)
			return e;
		return getPreviousSibling(args, child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child == getBody()) {
			setBody((Statement.Block)by);
			return true;
		}
		if (child == getName()) {
			setName((Identifier)by);
			return true;
		}
		if (replaceChild(args, Arg.class, this, child, by))
			return true;

		if (replaceChild(thrown, TypeRef.class, this, child, by))
			return true;

		if (replaceChild(initializers, FunctionCall.class, this, child, by))
			return true;
		
		return super.replaceChild(child, by);
	}
	
	
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
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

	public TypeRef addThrown(TypeRef a) {
		if (a != null) {
			thrown.add(a);
			a.setParentElement(this);
		}
		return a;
	}
	public List<TypeRef> getThrown() {
		return unmodifiableList(thrown);
	}
	public void setThrown(List<TypeRef> thrown) {
		changeValue(this, this.thrown, thrown);
	}

	public Function setBody(Statement.Block body) {
		this.body = changeValue(this, this.body, body);
		return this;
	}

	//public static class CFunction extends Function {
	public Function() {}

	public Function(Type type, Identifier name, TypeRef returnType) {
		setType(type);
		setName(name);
		setValueType(returnType);
	}
	public Function(Type type, Identifier name, TypeRef returnType, Arg... args) {
		this(type, name, returnType, Arrays.asList(args));
	}
		
	public Function(Type type, Identifier name, TypeRef returnType, List<Arg> args) {
		setType(type);
		setName(name);
		setValueType(returnType);
		setArgs(args);
	}

    @Override
    public Function addAnnotation(Annotation a) {
        return (Function)super.addAnnotation(a);
    }

	@Override
	public Function addModifiers(Modifier... mds) {
		return (Function) super.addModifiers(mds);
	}
	
	@Override
	public String toString(CharSequence indent) {
		String s = "";
		TypeRef valueType = getValueType();
		Identifier name = getName();
		List<Modifier> modifiers = getModifiers();
		
		if (type == null)
			return "<no function type>";
		
		String pre = formatComments(indent, false, true, true);
		String post = (asmName == null ? "" : "__asm(\"" + asmName + "\") ") +
			(initializers.isEmpty() ? "" : " : " + implode(initializers, ", ", indent)) +
			(commentAfter == null ? "" : " " + commentAfter);//" /*" + commentAfter + " */";
		
		if (!getAnnotations().isEmpty())
			pre += StringUtils.implode(getAnnotations(), "\n" + indent) + "\n" + indent;
		
		String preMods;
		switch (type) {
		case StaticInit:
			preMods = StringUtils.implode(modifiers, " ") + (modifiers.isEmpty() ? "" : " ");
			return pre + preMods + (body == null ? ";" : body.toString(indent)) + post;
		case CFunction:
		case CppMethod:
		case JavaMethod:
			preMods = StringUtils.implode(modifiers, " ") + (modifiers.isEmpty() ? "" : " ");
			s = preMods + 
				(valueType == null ? "" : valueType + " ") +
				name + "(" +
				StringUtils.implode(args, ", ") +
				")";

            if (!thrown.isEmpty())
                s += " throws " + implode(thrown, ", ", indent);

			return pre + s + (body == null ? ";" : " " + body.toString(indent)) + post;
		case ObjCMethod:
			s = modifiers.contains(Modifier.Static) ? "+" : "-";
			StringBuilder argsStr = new StringBuilder();
			for (Arg arg : args) {
				if (arg.isVarArg()) {
					if (argsStr.length() > 0)
						argsStr.append(", ");
					argsStr.append("...");
				} else {
					if (argsStr.length() > 0)
					{
						argsStr.append(' ');
						argsStr.append(arg.getSelector());
					}
					argsStr.append(":(");
					argsStr.append(arg.createMutatedType());
					argsStr.append(')');
					argsStr.append(arg.getName());
				}
			}
			return pre + s + " " + (valueType == null ? "" : "(" + valueType + ")") + name + argsStr + ";" + post;
		default:
			throw new NoSuchElementException(type.toString());
		}
	}

	public void accept(Visitor visitor) {
		visitor.visitFunction(this);
	}
	public Statement.Block getBody() {
		return body;
	}

	public String computeSignature(boolean addReturnType) {
		StringBuilder b = new StringBuilder();
		
		if (type == Type.ObjCMethod) {
			b.append(modifiers.contains(Modifier.Static) ? "+" : "-");
			b.append("(");
			TypeRef t = getValueType();
			if (t == null)
				b.append("id");
			else {
				t = t.clone();
				t.setCommentAfter(null);
				t.setCommentBefore(null);
				b.append(t);
			}
			b.append(")");
			b.append(getName());
			boolean firstArg = true;
			for (Arg arg : args) {
				if (arg.isVarArg()) {
					if (!firstArg)
						b.append(", ");
					b.append("...");
				} else {
					if (!firstArg)
						b.append(" " + arg.getSelector());
					b.append(":(");
					t = arg.createMutatedType();
					if (t != null) {
						t.setCommentAfter(null);
						t.setCommentBefore(null);
						b.append(t);
					}
					b.append(')');
					//No arg name in signature : b.append(arg.getName()); 
				}
				firstArg = false;
			}
		} else {
			if (addReturnType && getValueType() != null) {
				TypeRef t = getValueType().clone();
				t.stripDetails();
				b.append(t);
				b.append(' ');
			}
			b.append(getName());
			boolean first = true;
			b.append('(');
			for (Arg arg : getArgs()) {
				if (first) {
					first = false;
				} else 
					b.append(", ");
				TypeRef t = arg.createMutatedType();
				if (t != null)
					t.stripDetails();
				b.append(t);
			}
			b.append(')');
		}
		return b.toString();
	}

	public static Function fromMethod(Method m) {
		Function f = new Function(
			Function.Type.JavaMethod, 
			ident(m.getName()),
			m.getReturnType() == null ? typeRef("void") : typeRef(m.getReturnType())
		);
		int i = 0;
		for (Class<?> c : m.getParameterTypes()) {
			f.addArg(new Arg("arg" + (i++), typeRef(c)));
		}
		int modifiers = m.getModifiers();
		if (java.lang.reflect.Modifier.isPrivate(modifiers))
			f.addModifiers(Modifier.Private);
		if (java.lang.reflect.Modifier.isProtected(modifiers))
			f.addModifiers(Modifier.Protected);
		if (java.lang.reflect.Modifier.isPublic(modifiers))
			f.addModifiers(Modifier.Public);
		if (java.lang.reflect.Modifier.isStatic(modifiers))
			f.addModifiers(Modifier.Static);
		
		if (java.lang.reflect.Modifier.isAbstract(modifiers))
			f.addModifiers(Modifier.Abstract);
		if (java.lang.reflect.Modifier.isFinal(modifiers))
			f.addModifiers(Modifier.Final);
		if (java.lang.reflect.Modifier.isNative(modifiers))
			f.addModifiers(Modifier.Native);
		if (java.lang.reflect.Modifier.isSynchronized(modifiers))
			f.addModifiers(Modifier.Synchronized);
		
		/*TODO org.rococoa.ReturnType returnType = m.getAnnotation(ReturnType.class);
		if (returnType != null && returnType.value() != null) {
			f.addAnnotation(new Annotation(ReturnType.class, classLiteral(returnType.value())));
		}*/
		return f;
	}

}
