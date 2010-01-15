package com.ochafik.lang.jnaerator.parser;

import static com.ochafik.lang.jnaerator.parser.Expression.*;

import java.util.Arrays;

import static com.ochafik.lang.jnaerator.parser.Identifier.*;
import static com.ochafik.lang.jnaerator.parser.Statement.*;

import com.ochafik.lang.jnaerator.parser.Expression.Constant;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
import java.util.ArrayList;
import java.util.List;
public class ElementsHelper {
	public static Expression memberRef(Expression x, MemberRefStyle style, String name) {
		return new Expression.MemberRef(x, style, new SimpleIdentifier(name));
	}
	public static Expression classLiteral(Class<?> c) {
		if (c == null)
			return null;
		if (c.isPrimitive()) {
			if (c == Integer.TYPE)
				c = Integer.class;
			else if (c == Long.TYPE)
				c = Long.class;
			else if (c == Short.TYPE)
				c = Short.class;
			else if (c == Byte.TYPE)
				c = Byte.class;
			else if (c == Character.TYPE)
				c = Character.class;
			else if (c == Boolean.TYPE)
				c = Boolean.class;
			else if (c == Float.TYPE)
				c = Float.class;
			else if (c == Double.TYPE)
				c = Double.class;
			
			return staticField(c, "TYPE");
		}
		return staticField(c, "class");
	}
	public static Expression staticField(Class<?> c, String name) {
		return memberRef(expr(typeRef(ident(c))), MemberRefStyle.Dot, name);
	}
	public static Expression classLiteral(TypeRef c) {
		return memberRef(expr(c), MemberRefStyle.Dot, "class");
	}
	public static Expression memberRef(Expression x, MemberRefStyle style, Identifier name) {
		return new Expression.MemberRef(x, style, name);
	}
	public static Expression varRef(String name) {
		return new Expression.VariableRef(new SimpleIdentifier(name));
	}
	public static Expression varRef(SimpleIdentifier name) {
		return new Expression.VariableRef(name);
	}
	public static Expression varRef(Identifier name) {
		return memberRef(expr(typeRef(name.resolveAllButLastIdentifier())), MemberRefStyle.Dot, name.resolveLastSimpleIdentifier());
	}
	public static Identifier ident(String... others) {
        if (others == null)
            return null;
        
        List<SimpleIdentifier> list = new ArrayList<SimpleIdentifier>();
        for (String o : others)
            if (o != null && (o = o.trim()).length() > 0)
                list.addAll(getClassSimpleIdentifiers(o));

        if (list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        
        QualifiedIdentifier id = new QualifiedIdentifier(QualificationSeparator.Dot);
        id.setIdentifiers(list);
        return id;
	}
    public static List<SimpleIdentifier> getClassSimpleIdentifiers(String className) {
        List<SimpleIdentifier> elts = new ArrayList<SimpleIdentifier>();
        for (String s : className.split("[.$]"))
            if (s.length() > 0)
                elts.add(new SimpleIdentifier(s));

        return elts;
    }
	public static Identifier ident(Class<?> cl, Expression... args) {
        if (cl == null)
            return null;
        if (cl == Void.TYPE || cl == Void.class)
            return ident("void");
        QualifiedIdentifier id = new QualifiedIdentifier(QualificationSeparator.Dot);
        id.setIdentifiers(getClassSimpleIdentifiers(cl.getName()));
        id.resolveLastSimpleIdentifier().setTemplateArguments(Arrays.asList(args));
        return id;
	}
	public static Identifier ident(Identifier ident, String name) {
		return ident(ident, ident(name));
	}
	public static Identifier ident(Identifier ident, Identifier... others) {
		if (ident == null) {
			if (others.length > 0)
				return ident(others[0], Arrays.copyOfRange(others, 1, others.length));
			return null;
		}
		return ident.derive(Identifier.QualificationSeparator.Dot, others);
	}
	public static Expression opaqueExpr(String s) {
		return new OpaqueExpression(s);
	}
	public static Expression cast(TypeRef t, Expression s) {
		return new Cast(t, s);
	}
	public static Expression nullExpr() {
		return new NullExpression();
	}
	public static Expression expr(TypeRef tr) {
		return new TypeRefExpression(tr);
	}
	public static Expression expr(boolean c) {
		return expr(Constant.Type.Bool, c);
	}
	public static Expression expr(double c) {
		return expr(Constant.Type.Double, c);
	}
	public static Expression expr(int c) {
		return expr(Constant.Type.Int, c);
	}
	public static Expression expr(String c) {
		return expr(Constant.Type.String, c);
	}
	public static Expression expr(Constant.Type type, Object value) {
		return new Constant(type, value);
	}
	public static Expression expr(UnaryOperator op, Expression b) {
		return new UnaryOp(b, op);
	}
	public static Expression expr(Expression a, BinaryOperator op, Expression b) {
		return new BinaryOp(a, op, b);
	}
	public static Expression expr(Expression a, AssignmentOperator op, Expression b) {
		return new AssignmentOp(a, op, b);
	}

    public static <T extends Element> List<T> clones(List<T> list) {
        List<T> clone = new ArrayList<T>(list.size());
        for (T e : list)
            clone.add((T)e.clone());
        return clone;
    }
	public static FunctionCall methodCall(Expression x, MemberRefStyle style, String name, Expression... exprs) {
		return new FunctionCall(memberRef(x, style, name), exprs);
	}
	public static FunctionCall methodCall(String name, Expression... exprs) {
		return new FunctionCall(memberRef(null, null, name), exprs);
	}
	public static TypeRef typeRef(Class<?> cl) {
		if (cl.isArray())
			return new TypeRef.ArrayRef(typeRef(cl.getComponentType()));
        if (cl.isPrimitive() || cl == Void.class)
            return new TypeRef.Primitive(cl.getSimpleName());
        
        return typeRef(ident(cl));
	}

	public static SimpleTypeRef typeRef(String name) {
        return typeRef(ident(name));
	}
	public static SimpleTypeRef typeRef(Identifier name) {
		return name == null ? null : new SimpleTypeRef(name);
	}
    public static Statement stat(Declaration d) {
        return new Statement.DeclarationStatement(d);
    }
	public static Statement stat(Expression x) {
		return new ExpressionStatement(x);
	}
	public static Statement stat(TypeRef tr, String varName, Expression ass) {
		VariablesDeclaration vd = new VariablesDeclaration(tr, new Declarator.DirectDeclarator(varName, ass));
		return new Statement.DeclarationStatement(vd);
	}
	public static Block block(Statement... x) {
		return new Block(x);
	}
	
	public static TaggedTypeRefDeclaration decl(TaggedTypeRef tr) {
		return tr == null ? null : new TaggedTypeRefDeclaration(tr);
	}
/*

	public static TypeRef typeRef(TypeRef parentTypeRef, Style style, Identifier subName) {
		if (parentTypeRef == null)
			return typeRef(subName);
		return new SubTypeRef(parentTypeRef, style, subName);
	}*/
//	public static TypeRef typeRef(TypeRef parentTypeRef, String subName) {
//		return typeRef(parentTypeRef, Style.Dot, subName);
//	}
	
	
}
