/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.util.listenable.Pair;

public abstract class Expression extends Element {
	private static final long MAX_UINT_VALUE = 2L * Integer.MAX_VALUE;
	
    public static class ExpressionsBlock extends ExpressionSequence {

        @Override
        public void accept(Visitor visitor) {
            visitor.visitExpressionsBlock(this);
        }
        
    }
	public static class ExpressionSequence extends Expression {
		final List<Expression> expressions = new ArrayList<Expression>();
		public ExpressionSequence() {
			
		}
		public ExpressionSequence(Expression... expressions) {
			this(Arrays.asList(expressions));
		}
        public ExpressionSequence(List<Expression> expressions) {
			setExpressions(expressions);
		}
        public void addExpression(Expression e) {
            if (e != null) {
                expressions.add(e);
                e.setParentElement(this);
            }
		
        }
        public List<Expression> getExpressions() {
            return expressions;
        }
		public void setExpressions(List<Expression> sequence) {
			changeValue(this, this.expressions, sequence);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitExpressionSequence(this);
		}
		@Override
		public Element getNextChild(Element child) {
			return getNextSibling(getExpressions(), child);
		}
		@Override
		public Element getPreviousChild(Element child) {
			return getPreviousSibling(getExpressions(), child);
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			return replaceChild(getExpressions(), Expression.class, this, child, by);
		}
	}
	public static class OpaqueExpression extends Expression {
		String opaqueString;
		public void setOpaqueString(String opaqueString) {
			this.opaqueString = opaqueString;
		}
		public String getOpaqueString() {
			return opaqueString;
		}
		public OpaqueExpression() {
		}
		public OpaqueExpression(String opaqueString) {
			setOpaqueString(opaqueString);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitOpaqueExpression(this);
		}

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			return false;
		}

	}
	boolean parenthesis;
	public Expression setParenthesis(boolean parenthesis) {
		this.parenthesis = parenthesis;
		return this;
	}
    public Expression setParenthesisIfNeeded() {
        setParenthesis(!(
            this instanceof VariableRef ||
            this instanceof FunctionCall ||
            this instanceof MemberRef ||
            this instanceof ArrayAccess
        ));
        return this; 
    }
	public boolean getParenthesis() {
		return parenthesis;
	}
	public boolean isParenthesis() {
		return parenthesis;
	}
	
	@Override
	public Expression clone() {
		return (Expression)super.clone();
	}
	
	public enum MemberRefStyle {
		Dot, SquareBrackets, Arrow, Colons
	}
	
	public static MemberRefStyle parseMemberRefStyle(String s) {
		if (s.equals("->"))
			return MemberRefStyle.Arrow;
		if (s.equals("."))
			return MemberRefStyle.Dot;
		if (s.equals("::"))
			return MemberRefStyle.Colons;
		return null;
	}
	
	public static class TypeRefExpression extends Expression {
		TypeRef type;
		
		public TypeRefExpression(TypeRef type) {
			setType(type);
		}
		public TypeRefExpression() {}
		
		public TypeRef getType() {
			return type;
		}
		public void setType(TypeRef type) {
			this.type = changeValue(this, this.type, type);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitTypeRefExpression(this);
		}
		@Override
		public Element getNextChild(Element child) {
			return null;
		}
		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getType()) {
				setType((TypeRef) by);
				return true;
			}
			return false;
		}
	}

	public static class EmptyArraySize extends Expression {

		@Override
		public void accept(Visitor visitor) {
			visitor.visitEmptyArraySize(this);
		}

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			return false;
		}
	}
	
	public static class MemberRef extends Expression {
		MemberRefStyle memberRefStyle;
		Expression target;
		
		Identifier name; 
		
		public MemberRef(SimpleIdentifier name) {
			setName(name);
		}
		public MemberRef(Expression target, MemberRefStyle memberRefStyle, Identifier name) {
			setTarget(target);
			setName(name);
			setMemberRefStyle(memberRefStyle);
		}

		public MemberRef() {
		}

		public void setName(Identifier name) {
			this.name = changeValue(this, this.name, name);
		}
		public Identifier getName() {
			return name;
		}
		public void setTarget(Expression target) {
			this.target = changeValue(this, this.target, target);
		}
		public Expression getTarget() {
			return target;
		}
		
		public void setMemberRefStyle(MemberRefStyle memberRefStyle) {
			this.memberRefStyle = memberRefStyle;
		}
		public MemberRefStyle getMemberRefStyle() {
			return memberRefStyle;
		}
		
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTarget())
				setTarget((Expression)by);
			if (child == getName())
				setName((Identifier)by);
			return false;
		}
		
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitMemberRef(this);
		}
		@Override
		public Element getNextChild(Element child) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Element getPreviousChild(Element child) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	public static class NewArray extends Expression {
		TypeRef type;
		List<Expression> dimensions = new ArrayList<Expression>();
		List<Expression> initialValues = new ArrayList<Expression>();
		
		public NewArray() {}
		
		public NewArray(TypeRef type, Expression[] dimensions, Expression[] initialValues) {
			setType(type);
			setDimensions(Arrays.asList(dimensions));
            setInitialValues(Arrays.asList(initialValues));
		}
		public List<Expression> getDimensions() {
			return unmodifiableList(dimensions);
		}
        public void addDimension(Expression dimension) {
			if (dimension == null)
				return;
			
			dimension.setParentElement(this);
			dimensions.add(dimension);
		}
		
		public void setDimensions(List<Expression> dimensions) {
			changeValue(this, this.dimensions, dimensions);
		}
		public List<Expression> getInitialValues() {
			return unmodifiableList(initialValues);
		}
		public void setInitialValues(List<Expression> initialValues) {
			changeValue(this, this.initialValues, initialValues);
		}
		public TypeRef getType() {
			return type;
		}
		public void setType(TypeRef type) {
			this.type = changeValue(this, this.type, type);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitNewArray(this);
		}

		@Override
		public Element getNextChild(Element child) {
            Element e = getNextSibling(dimensions, child);
            if (e == null)
                e = getNextSibling(initialValues, child);
			return e;
		}

		@Override
		public Element getPreviousChild(Element child) {
            Element e = getPreviousSibling(dimensions, child);
            if (e == null)
                e = getPreviousSibling(initialValues, child);
			return e;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getType()) {
				setType((TypeRef)by);
				return true;
			}
            return 
                replaceChild(initialValues, Expression.class, this, child, by) ||
                replaceChild(dimensions, Expression.class, this, child, by);
		}

	}

	public static class New extends Expression {
		TypeRef type;
		FunctionCall construction;
		public New(TypeRef type) {
			setType(type);
		}
		public New(TypeRef type, FunctionCall construction) {
			setType(type);
			setConstruction(construction);
		}
		public New(TypeRef type, Expression... arguments) {
			setType(type);
			setConstruction(new FunctionCall(null, arguments));
		}
		public New(TypeRef type, List<Expression> arguments) {
            this(type, arguments.toArray(new Expression[arguments.size()]));
		}
		public New() {}
		public void setType(TypeRef type) {
			this.type = changeValue(this, this.type, type);
		}
		public TypeRef getType() {
			return type;
		}
		public void setConstruction(FunctionCall construction) {
			this.construction = changeValue(this, this.construction, construction);
		}
		public FunctionCall getConstruction() {
			return construction;
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitNew(this);
		}
		@Override
		public Element getNextChild(Element child) {
			return null;
		}
		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getType()) {
				setType((TypeRef) by);
				return true;
			}
			if (child == getConstruction()) {
				setConstruction((FunctionCall) by);
				return true;
			}
			return false;
		}
	}
	public static class FunctionCall extends MemberRef {
		Expression function;
		//String functionName;
		List<Pair<String, Expression>> arguments = new ArrayList<Pair<String, Expression>>(); 
		
		public List<Pair<String, Expression>> getArguments() {
			return unmodifiableList(arguments);
		}
		public void setArguments(List<Pair<String, Expression>> arguments) {
			for (Pair<String, Expression> p : this.arguments)
				p.getSecond().setParentElement(null);
			this.arguments.clear();
			for (Pair<String, Expression> p : arguments)
				addArgument(p.getFirst(), p.getSecond());
		}

		public FunctionCall(Expression function) {
			setFunction(function);
		}
		public FunctionCall(Expression function, Expression... unnamedArgs) {
			setFunction(function);
			for (Expression x : unnamedArgs)
				if (x != null)
					addArgument(x);
		}
		
		public FunctionCall(Expression target, Expression function, MemberRefStyle memberRefStyle, Expression... unnamedArgs) {
			setTarget(target);
			setFunction(function);
			setMemberRefStyle(memberRefStyle);
			for (Expression x : unnamedArgs)
				addArgument(x);
		}
		public FunctionCall() {
		}

		public void addArgument(Expression ex) {
			addArgument(null, ex);
		}
		public void addArguments(List<Expression> ex) {
			for (Expression x : ex)
				addArgument(null, x);
		}
		public void addArgument(String argumentSelector, Expression ex) {
			if (ex == null)
				return;
			
			ex.setParentElement(this);
			arguments.add(new Pair<String, Expression>(argumentSelector, ex));
		}
		public Expression getFunction() {
			return function;
		}
		public void setFunction(Expression function) {
			this.function = changeValue(this, this.function, function);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitFunctionCall(this);
		}

		protected int indexOf(Element x, List<Pair<String, Expression>> list) {
			int i = 0;
			for (Pair<String, Expression> p : list) {
				if (p.getValue() == x)
					return i;
				i++;
			}
			return -1;
		}
		
		@Override
		public Element getNextChild(Element child) {
			int i = indexOf(child, arguments);
			if (i >= 0) {
				return i < arguments.size() - 1 ? arguments.get(i + 1).getValue() : null;
			}
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			int i = indexOf(child, arguments);
			if (i >= 0) {
				return i > 0 ? arguments.get(i - 1).getValue() : null;
			}
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTarget()) {
				setTarget((Expression) by);
				return true;
			}
			if (child == getFunction()) {
				setFunction((Expression) by);
				return true;
			}
			//return replaceChild(arguments, Expression.class, this, child, by);
			int i = indexOf(child, arguments);
			if (i >= 0) {
				Expression old;
				if (by == null)
					old = arguments.remove(i).getValue();
				else
					old = arguments.get(i).setValue((Expression)by);

				if (old != by) {
					if (old != null)
						old.setParentElement(null);
					
					if (by != null)
						by.setParentElement(this);
				}
				return true;
			}
			
			return super.replaceChild(child, by);
		}
	}
	public static class ArrayAccess extends Expression {
		Expression target, index;
		public ArrayAccess() {}
		public ArrayAccess(Expression target, Expression index) {
			setTarget(target);
			setIndex(index);
		}
		public Expression getTarget() {
			return target;
		}
		public void setTarget(Expression target) {
			this.target = changeValue(this, this.target, target);
		}
		public void setIndex(Expression index) {
			this.index = changeValue(this, this.index, index);
		}
		public Expression getIndex() {
			return index;
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitArrayAccess(this);
		}
		@Override
		public Element getNextChild(Element child) {
			return null;
		}
		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTarget()) {
				setTarget((Expression)by);
				return true;
			}
			if (child == getIndex()) {
				setIndex((Expression)by);
				return true;
			}
			return false;
		}
	}
	
	public static class VariableRef extends Expression {
		Identifier name;
		
		
		public VariableRef(Identifier name) {
			setName(name);
		}
		
		public VariableRef() {
		}

		public Identifier getName() {
			return name;
		}
		
		public void setName(Identifier name) {
			this.name = changeValue(this, this.name, name);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitVariableRef(this);
		}

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getName()) {
				setName((Identifier) by);
				return true;
			}
			return false;
		}
	}

	public enum BinaryOperator  implements Operator {
		Assign("="),
		Arrow("->"),
		ArrowStar("->*"),
		SquareBrackets("[]"),
		Comma(","),
		Plus("+"), Minus("-"), Divide("/"), Multiply("*"), Modulo("%"), LeftShift("<<"), RightShift(">>"), SignedRightShift(">>>"), XOR("^"), 
		LessEqual("<=", true), GreaterEqual(">=", true), Less("<", true), Greater(">", true), IsEqual("==", true), IsDifferent("!=", true),
        BitOr("|"), Or("||", true), BitAnd("&"), And("&&", true);
		
		final String s;
        public final boolean givesBool;
        BinaryOperator(String s) {
            this(s, false);
        }
		BinaryOperator(String s, boolean givesBool) {
			this.s = s;
            this.givesBool = givesBool;
		}
		@Override
		public String toString() {
			return s;
		}
	};
	public interface Operator {
		
	}
	public enum AssignmentOperator implements Operator {
		Equal("=", null),
		MultiplyEqual("*=", BinaryOperator.Multiply),
		DivideEqual("/=", BinaryOperator.Divide),
		ModuloEqual("%=", BinaryOperator.Modulo),
		PlusEqual("+=", BinaryOperator.Plus),
		MinusEqual("-=", BinaryOperator.Minus),
		LeftShiftEqual("<<=", BinaryOperator.LeftShift),
		RightShiftEqual(">>=", BinaryOperator.RightShift),
		SignedRightShiftEqual(">>>=", BinaryOperator.SignedRightShift),
		BitAndEqual("&=", BinaryOperator.BitAnd),
		XOREqual("^=", BinaryOperator.XOR),
		//ComplementEqual("~=", BinaryOperator.C),
		BitOrEqual("|=", BinaryOperator.BitOr);
		
		String s;
		AssignmentOperator(String s, BinaryOperator correspondingBinaryOp) {
			this.s = s;
            this.correspondingBinaryOp = correspondingBinaryOp;
		}
		@Override
		public String toString() {
			return s;
		}
        BinaryOperator correspondingBinaryOp;
        public BinaryOperator getCorrespondingBinaryOp() {
            return correspondingBinaryOp;
        }
	}
	public enum UnaryOperator  implements Operator {
		Not("!"), 
		Minus("-"), 
		Parenthesis("()"),
		Complement("~"),
		Reference("&"),
		Dereference("*"),
		PreIncr("++"), 
		PreDecr("--"),
		PostIncr("++"), 
		PostDecr("--");
		
		String s;
		UnaryOperator(String s) {
			this.s = s;
		}
		@Override
		public String toString() {
			return s;
		}
	};
	
	static final Map<String, AssignmentOperator> assignOps = new HashMap<String, AssignmentOperator>();
	static final Map<String, BinaryOperator> binOps = new HashMap<String, BinaryOperator>();
	static final Map<String, UnaryOperator> unOps = new HashMap<String, UnaryOperator>();
	static final Map<AssignmentOperator, String> assignOpsRev = new HashMap<AssignmentOperator, String>();
	static final Map<BinaryOperator, String> binOpsRev = new HashMap<BinaryOperator, String>();
	static final Map<UnaryOperator, String> unOpsRev = new HashMap<UnaryOperator, String>();
	//public static final Expression EMPTY_EXPRESSION = new Constant(null, null, "");
	static {
		for (AssignmentOperator op : AssignmentOperator.values())
			map(assignOps, assignOpsRev, op.toString(), op);
		for (UnaryOperator op : UnaryOperator.values())
			map(unOps, unOpsRev, op.toString(), op);
		for (BinaryOperator op : BinaryOperator.values())
			map(binOps, binOpsRev, op.toString(), op);
	}
	static <K, V> void map(Map<K, V> m, Map<V, K> r, K k, V v) {
		m.put(k, v);
		r.put(v, k);
	}
	public static BinaryOperator getBinaryOperator(String s) {
		return binOps.get(s);
	}
	

	public static AssignmentOperator getAssignmentOperator(String s) {
		AssignmentOperator op = assignOps.get(s);
        if (op == null)
            throw new RuntimeException("Failed to parse op " + s);
        
        return op;
	}
	
	public static UnaryOperator getUnaryOperator(String s) {
		UnaryOperator op = unOps.get(s);
        if (op == null)
            throw new RuntimeException("Failed to parse op " + s);
        
        return op;
	}
	public static java.lang.Enum<?> getAnyOperator(String s) {

		java.lang.Enum<?> e = Expression.getBinaryOperator(s);
		if (e != null)
			return e;
		e = Expression.getUnaryOperator(s);
		if (e != null)
			return e;
		return Expression.getAssignmentOperator(s);
	}
	
	public static class NullExpression extends Expression {

		@Override
		public void accept(Visitor visitor) {
			visitor.visitNullExpression(this);
		}

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			return false;
		}
	}
	public static class ConditionalExpression extends Expression {
		Expression test, thenValue, elseValue;

		public Expression getTest() {
			return test;
		}

		public void setTest(Expression test) {
			this.test = changeValue(this, this.test, test);
		}

		public Expression getThenValue() {
			return thenValue;
		}

		public void setThenValue(Expression thenValue) {
			this.thenValue = changeValue(this, this.thenValue, thenValue);
		}

		public Expression getElseValue() {
			return elseValue;
		}

		public void setElseValue(Expression elseValue) {
			this.elseValue = changeValue(this, this.elseValue, elseValue);
		}


		@Override
		public void accept(Visitor visitor) {
			visitor.visitConditionalExpression(this);
		}

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTest()) {
				setTest((Expression)by);
				return true;
			}
			if (child == getThenValue()) {
				setThenValue((Expression)by);
				return true;
			}
			if (child == getElseValue()) {
				setElseValue((Expression)by);
				return true;
			}
			return false;
		}
		
	}
	public static class Cast extends Expression {
		TypeRef type;
		Expression target;
		
		public Cast(TypeRef type, Expression target) {
			setTarget(target);
			setType(type);
		}
		
		public Cast() {
		}

		public void setTarget(Expression target) {
			this.target = changeValue(this, this.target, target);
		}
		public void setType(TypeRef type) {
			this.type = changeValue(this, this.type, type);
		}
		public Expression getTarget() {
			return target;
		}
		public TypeRef getType() {
			return type;
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitCast(this);
		}
		
		@Override
		public Element getNextChild(Element child) {
			return null;
		}
		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTarget()) {
				setTarget((Expression)by);
				return true;
			}
			if (child == getType()) {
				setType((TypeRef)by);
				return true;
			}
			return false;
		}
	}
	public static class AssignmentOp extends Expression {
		Expression target, value;
		AssignmentOperator operator;
		
		public AssignmentOp() {
		}
		public AssignmentOp(Expression target, AssignmentOperator operator, Expression value) {
			if (operator == null)
				throw new NullPointerException();
			
			setValue(value);
			setTarget(target);
			setOperator(operator);
		}

		public AssignmentOperator getOperator() {
			return operator;
		}
		public void setOperator(AssignmentOperator operator) {
			this.operator = operator;
		}
		public Expression getValue() {
			return value;
		}
		public Expression getTarget() {
			return target;
		}
		public void setValue(Expression value) {
			this.value = changeValue(this, this.value, value);
		}
		public void setTarget(Expression target) {
			this.target = changeValue(this, this.target, target);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAssignmentOp(this);
		}

		@Override
		public Element getNextChild(Element child) {
			if (child == getTarget())
				return getValue();

			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			if (child == getValue())
				return getTarget();

			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTarget()) {
				setTarget((Expression) by);
				return true;
			}
			if (child == getValue()) {
				setValue((Expression) by);
				return true;
			}
			return false;
		}


	}
	
	public static class BinaryOp extends Expression {
		BinaryOperator operator;
		Expression firstOperand, secondOperand;
		
		public BinaryOp(Expression firstOperand, BinaryOperator operator, Expression secondOperand) {
			if (operator == null)
				throw new NullPointerException();
			
			setOperator(operator);
			setFirstOperand(firstOperand);
			setSecondOperand(secondOperand);
		}

		public BinaryOp() {
		}
		public void setOperator(BinaryOperator operator) {
			this.operator = operator;
		}
		public Expression getSecondOperand() {
			return secondOperand;
		}
		public Expression getFirstOperand() {
			return firstOperand;
		}
		public void setSecondOperand(Expression secondOperand) {
			this.secondOperand = changeValue(this, this.secondOperand, secondOperand);
		}
		public void setFirstOperand(Expression firstOperand) {
			this.firstOperand = changeValue(this, this.firstOperand, firstOperand);
		}
		
		public BinaryOperator getOperator() {
			return operator;
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitBinaryOp(this);
		}

		@Override
		public Element getNextChild(Element child) {
			if (child == getFirstOperand())
				return getSecondOperand();

			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			if (child == getSecondOperand())
				return getFirstOperand();

			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getFirstOperand()) {
				setFirstOperand((Expression) by);
				return true;
			}
			if (child == getSecondOperand()) {
				setSecondOperand((Expression) by);
				return true;
			}
			return false;
		}

	}

	public static class UnaryOp extends Expression {
		UnaryOperator operator;
		Expression operand;

		public UnaryOp(Expression operand, UnaryOperator operator) {
			setOperand(operand);
			setOperator(operator);
		}

		public UnaryOp() {
		}

		public Expression getOperand() {
			return operand;
		}
		
		public void setOperator(UnaryOperator operator) {
			this.operator = operator;
		}
		public void setOperand(Expression operand) {
			this.operand = changeValue(this, this.operand, operand);
		}
		public UnaryOperator getOperator() {
			return operator;
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitUnaryOp(this);
		}

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getOperand()) {
				setOperand((Expression) by);
				return true;
			}
			return false;
		}

	}

	public static class Constant extends Expression {
		public enum Type {
			Int, String, Char, IntegerString, Float, Short, Byte, Long, UInt, Double, LongString, ULong, Bool, Null
		}
		public enum IntForm {
			Hex, Octal, String, Decimal
		}
		Type type;
		IntForm intForm;
		Object value;
        String originalTextualRepresentation;

		
		public Constant(Type type, IntForm intForm, Object value, String originalTextualRepresentation) {
			if (value == null)
				throw new NullPointerException();
			setType(type);
			setIntForm(intForm);
			setValue(value);
            setOriginalTextualRepresentation(originalTextualRepresentation);
			
			checkType();
		}

        public void setOriginalTextualRepresentation(String originalTextualRepresentation) {
            this.originalTextualRepresentation = originalTextualRepresentation;
        }

        public String getOriginalTextualRepresentation() {
            return originalTextualRepresentation;
        }
        
		void checkType() {
			if (type == null)
				return;
			
			Object value = getValue();
			switch (type) {
			case Int:
			case UInt:
			case IntegerString:
				value = (Integer)value;
				break;
			case ULong:
			case Long:
			case LongString:
				value = (Long)value;
				break;
			case Char:
				value = (Character)value;
				break;
			case Double:
				value = (Double)value;
				break;
			case Float:
				value = (Float)value;
				break;
			case String:
				value = (String)value;
				break;
			case Bool:
				value = (Boolean)value;
			}
		}
		public static Constant newNull() {
            return new Constant(Constant.Type.Null, null, null);
        }
		public Constant(Type type, Object value, String originalTextualRepresentation) {
			setType(type);
			setValue(value);
            setOriginalTextualRepresentation(originalTextualRepresentation);
			
			checkType();
		}
		
		public void setIntForm(IntForm intForm) {
			this.intForm = intForm;
		}
		public IntForm getIntForm() {
			return intForm;
		}
		public Type getType() {
			return type;
		}
		public void setType(Type type) {
			this.type = type;
		}

		public Constant() {
		}
		/*
		public Constant fromString(String s) {
			String v = s.toUpperCase();
			Type type = null;
			if (v.startsWith("\""))
				type = Type.String;
			else if (v.startsWith("'"))
				type = v.length()Type.String;
			else if (v.contains(".")) {
				if (v.endsWith("L"))
					c = Float.TYPE;
				else
					c = Double.TYPE;
			} else if (v.endsWith("L"))
				c = Long.TYPE;
			else if (v.endsWith("F"))
				c = Float.TYPE;
			else if (v.endsWith("D"))
				c = Double.TYPE;
			else {
				//TODO try to parse as long and if it fails as integer, use Long.TYPE
				c = Integer.TYPE;
			}
		}*/
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		
		static String intStr(int intVal) {
			return String.valueOf((char)(0xff & (intVal >> 24))) +
				((char)(0xff & (intVal >> 16)))+
				((char)(0xff & (intVal >> 8)))+
				((char)(0xff & (intVal)))
				;
		}
		
		public void accept(Visitor visitor) {
			visitor.visitConstant(this);
		}
		

		@Override
		public Element getNextChild(Element child) {
			return null;
		}

		@Override
		public Element getPreviousChild(Element child) {
			return null;
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			return false;
		}

		public Integer asInteger() {
			switch (getType()) {
			case Int:
			case IntegerString:
				return (Integer)value;
			}
			throw new ClassCastException("Constant " + getValue() + " is not an integer, but a " + getType());
		}


		public static Constant parseCharOrStringInteger(final String orig) {
            String string = orig;
			int len = string.length();
			if (len <= 2 || string.charAt(0) != '\'' || string.charAt(len - 1) != '\'')
				throw new IllegalArgumentException("Expecting char or integer string, got " + string);
			
			string = string.substring(1, len - 1);
			len -= 2;
			if (len == 4) {
				boolean isIntegerString = true;
				for (int i = len; i-- != 0;) {
					if (string.charAt(i) == '\\') {
						isIntegerString = false;
						break;
					}
				}
				if (isIntegerString) {
					long result = 0;
					for (int i = 0; i < len; i++) {
						result = result << 8 | (int)string.charAt(i);
					}
					if (len == 4)
						return new Constant(Type.IntegerString, IntForm.String, (int)result, orig);
					else
						return new Constant(Type.LongString, IntForm.String, result, orig);
				}
			}
			return new Constant(Type.Char, parseNakedString(string).charAt(0), orig);
			
		}
		
		public static Constant parseChar(final String orig) {
            String string = orig;
			int len = string.length();
			if (len <= 2 || string.charAt(0) != '\'' || string.charAt(len - 1) != '\'')
				throw new IllegalArgumentException("Expecting char, got " + string);
			
			string = string.substring(1, len - 1);
			return new Constant(Type.Char, parseNakedString(string).charAt(0), orig);
		}

		private static String parseNakedString(String string) {
			//return StringUtils.javaUnEscape(string)
			int len = string.length();
			StringBuffer b = new StringBuffer(len);
			for (int i = 0; i < len;) {
				char c = string.charAt(i++);
				if (c == '\\') {
					c = string.charAt(i++);
					switch (c) {
                    case 't':
						b.append('\t');
						break;
					case 'n':
						b.append('\n');
						break;
					case 'r':
						b.append('\r');
						break;
					case 'f':
						b.append('\f');
						break;
					case 'b':
						b.append('\b');
						break;
					case '\\':
						b.append('\\');
						break;
					case '"':
						b.append('"');
						break;
					case '\'':
						b.append('\'');
						break;
					case 'u':
						if (i < (len - 3)) {
							b.append((char)Integer.parseInt(string.substring(i, i + 4), 16));
							i += 4;
						}
						break;
					case '0':
                        b.append('\0');
                        break;
					default:
						if (Character.isDigit(c)) {
							int start = i - 1;
							int end = i;
							while (Character.isDigit(string.charAt(end))) {
								end++;
							}
							b.append((char)Integer.parseInt(string.substring(start, end), 8));
							i = end;
						}
					}
				} else {
					b.append(c);
				}
			}
			return b.toString();
		}

		public static Constant parseStringInteger(final String orig) {
            String string = orig;
			int len = string.length();
			if (len <= 2 || string.charAt(0) != '\'' || string.charAt(len - 1) != '\'' || ((len -= 2) != 4 && len != 8))
				throw new IllegalArgumentException("Expecting 'xxxx' or 'xxxxxxxx', got " + string);
			
			string = string.substring(1, len - 1);
			long result = 0;
			for (int i = len; i-- != 0;) {
				result = result << 8 | (int)string.charAt(i);
			}
			if (len == 4)
				return new Constant(Type.Int, IntForm.String, (int)result, orig);
			else
				return new Constant(Type.Long, IntForm.String, result, orig);
		}
		
		public static Constant parseString(final String orig) {
            String string = orig;
			int len = string.length();
			if (len <= 2 || string.charAt(0) != '"' || string.charAt(len - 1) != '"')
				throw new IllegalArgumentException("Expecting string, got " + string);

			string = string.substring(1, len - 1);
			return new Constant(Type.String, parseNakedString(string), orig);
		}

		public static Constant parseDecimal(String string) {
			return parseDecimal(string, 10, IntForm.Decimal, false);
		}
		
        public static Constant parseDecimal(final String string, int radix, IntForm form, boolean negate) {
            return parseDecimal(string, radix, form, negate, string);
        }
		public static Constant parseDecimal(String string, int radix, IntForm form, boolean negate, final String orig) {
            string = string.trim().toLowerCase();
			int len = string.length();
			boolean unsigned = false;
			if (string.endsWith("ll") || string.endsWith("li"))
				return new Constant(Type.Long, Long.parseLong(string.substring(0, len - 2), radix), orig);
			else if (string.endsWith("l"))
				string = string.substring(0, len - 1);
			else if (string.endsWith("s"))
				return new Constant(Type.Short, Long.parseLong(string.substring(0, len - 1), radix), orig);
			//else if (string.endsWith("b"))
			//	return new Constant(Type.Byte, Long.parseLong(string.substring(0, len - 1), radix));
			else if (string.endsWith("u")) {
				string = string.substring(0, len - 1);
				unsigned = true;
			}
			
			long val;
			if (string.equals("ffffffffffffffff"))
				val = 0xffffffffffffffffL;
			else {
				if (string.startsWith("+"))
					string = string.substring(1);
				
				/*int len2 = string.length();
				if (len2 > 0 && !Character.isDigit(string.charAt(len2 - 1))) {
					string = string.substring(0, len2 - 1);
					len2--;
				}*/
				try {
					if (string.length() == 16) // only for large longs (otherwise would need to prepend "0" to avoid sign issues)
						val = new java.math.BigInteger(string, radix).longValue();
					else
						val = Long.parseLong(string, radix);
				} catch (NumberFormatException ex) {
					unsigned = true;
					val = Long.parseLong(string.substring(0, string.length() - 1), radix);
					val = val * radix + Short.parseShort(string.substring(string.length() - 1));
				}
			}
			
			if (negate) {
				val = -val;
//				form = IntForm.Decimal;
			}
			
			//TODO handle unsigned properly !
			if ((form == IntForm.Hex && len <= 8) || val > Integer.MIN_VALUE && val < Integer.MAX_VALUE)
				return new Constant(unsigned ? Type.UInt : Type.Int, form, (int)val, orig);
			else if (val >= 0 && val < MAX_UINT_VALUE)
				return new Constant(Type.UInt, form, (int)val, orig);
			else
				return new Constant(unsigned ? Type.ULong : Type.Long, form, val, orig);
			
		}

		public static Constant parseHex(final String orig, boolean negate) {
            String string = orig;
			string = string.trim().toLowerCase();
			if (!string.startsWith("0x"))
				throw new IllegalArgumentException("Expected hex literal, got " + string);
			
			try {
				return parseDecimal(string.substring(2), 16, IntForm.Hex, negate, orig);
			} catch (NumberFormatException ex) {
				throw new NumberFormatException("Parsing hex : \"" + string +"\"");
			}
		}

		public static Constant parseOctal(String string, boolean negate) {
			string = string.trim().toLowerCase();
			if (!string.startsWith("0"))
				throw new IllegalArgumentException("Expected octal literal, got " + string);
			
			return parseDecimal(string.substring(1), 8, IntForm.Octal, negate);
		}

		public static Constant parseFloat(final String orig) {
            String string = orig;
			string = string.trim().toLowerCase();
			if (string.length() > 0) {
				int lm1 = string.length() - 1;
				char c = string.charAt(lm1);
				if (Character.isLetter(c)) {
					String beg = string.substring(0, lm1);
					if (c == 'f')
						return new Constant(Type.Float, Float.parseFloat(beg), orig);
					
				}
			}
			return new Constant(Type.Double, Double.parseDouble(string), orig);
		}
        static final String[] trailingTypeInfos = new String[] { "ll", "li", "l", "s", "u" };
        static String trimTrailingTypeInfo(String s) {
            if (s == null)
                return null;
            
            String low = s.toLowerCase();
            for (String end : trailingTypeInfos) {
                if (low.endsWith(end))
                    return s.substring(0, s.length() - end.length());
            }
            return s;
        }
		public Constant asJava() {
			Type type = getType();
            String txt = originalTextualRepresentation;
            switch (type) {
                case Byte:
                case Int:
                case IntegerString:
                case Long:
                case LongString:
                case Short:
                    txt = trimTrailingTypeInfo(txt);
                    break;
                case UInt:
                case ULong:
                    txt = null;
                    break;
            }
            
			switch (type) {
            case Long:
			case ULong:
                if (txt != null)
                    txt += "L";
			case LongString:
				type = Type.Long;
				break;
			case UInt:
                if ((getValue() instanceof Long) && ((Long)getValue()) > Integer.MAX_VALUE)
                    txt = null;
            case IntegerString:
			    type = Type.Int;
				break;
			}
            
            return new Constant(type, getValue(), txt);
		}

		
	}
}
