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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Statement extends Element {
	
	public static class Throw extends Statement {
		Expression expression;
		public Expression getExpression() {
			return expression;
		}
		public void setExpression(Expression expression) {
			this.expression = changeValue(this, this.expression, expression);
		}
		public Throw() {}
		public Throw(Expression expression) {
			setExpression(expression);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitThrow(this);
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
			if (child == getExpression()) {
				setExpression((Expression)by);
				return true;
			}
			return false;
		}

	}
	public static class DeclarationStatement extends Statement {
		Declaration declaration;
		public void setDeclaration(Declaration declaration) {
			this.declaration = changeValue(this, this.declaration, declaration);
		}
		public Declaration getDeclaration() {
			return declaration;
		}
		public DeclarationStatement() {}
		public DeclarationStatement(Declaration declaration) {
			setDeclaration(declaration);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitDeclarationStatement(this);
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
			if (child == getDeclaration()) {
				setDeclaration((Declaration)by);
				return true;
			}
			return false;
		}
	}
	public static class Return extends Statement {
		Expression value;
		
		public Return() {}
		public Return(Expression value) {
			setValue(value);
		}

		public void setValue(Expression value) {
			this.value = changeValue(this, this.value, value);
		}
		public Expression getValue() {
			return value;
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitReturn(this);
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
			if (child == getValue()) {
				setValue((Expression)by);
				return true;
			}
			return false;
		}

	}
	public static class If extends Statement {
		public If() {}
		public If(Expression condition, Statement thenBranch, Statement elseBranch) {
			setCondition(condition);
			setThenBranch(thenBranch);
			setElseBranch(elseBranch);
		}
		Expression condition;
		Statement thenBranch, elseBranch;
		
		public void setElseBranch(Statement elseBranch) {
			this.elseBranch = changeValue(this, this.elseBranch, elseBranch);
		}
		public void setCondition(Expression condition) {
			this.condition = changeValue(this, this.condition, condition);
		}
		public void setThenBranch(Statement thenBranch) {
			this.thenBranch = changeValue(this, this.thenBranch, thenBranch);
		}
		public Statement getElseBranch() {
			return elseBranch;
		}
		public Statement getThenBranch() {
			return thenBranch;
		}
		public Expression getCondition() {
			return condition;
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitIf(this);
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
			if (child == getCondition()) {
				setCondition((Expression)by);
				return true;
			}
			if (child == getThenBranch()) {
				setThenBranch((Statement)by);
				return true;
			}
			if (child == getElseBranch()) {
				setElseBranch((Statement)by);
				return true;
			}
			return false;
		}
	}
	public static class ExpressionStatement extends Statement {

		public ExpressionStatement() {}
		public ExpressionStatement(Expression expression) {
			setExpression(expression);
		}

		Expression expression;
		
		public Expression getExpression() {
			return expression;
		}
		public void setExpression(Expression expression) {
			this.expression = changeValue(this, this.expression, expression);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitExpressionStatement(this);
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
			if (getExpression() == child) {
				setExpression((Expression)by);
				return true;
			}
			return false;
		}
	}
	
	public static class Block extends Statement {
		final List<Statement> statements = new ArrayList<Statement>();
		@Override
		public void accept(Visitor visitor) {
			visitor.visitBlock(this);
		}

		boolean compact;
		
		@Override
		public Block clone() {
			return (Block)super.clone();
		}
		
		public Block setCompact(boolean compact) {
			this.compact = compact;
			return this;
		}
		public boolean isCompact() {
			return compact;
		}
		public Block() {}
		public Block(Statement... statements) {
			addStatements(statements);
		}
		public Block(List<Statement> statements) {
			setStatements(statements);
		}
		public void addStatement(Statement statement) {
			if (statement != null) {
				statement.setParentElement(this);
				statements.add(statement);
			}
		}
		public void setStatements(List<Statement> statements) {
			changeValue(this, this.statements, statements);
		}
		public void addStatements(Statement... statements) {
			setStatements(Arrays.asList(statements));
		}
		public List<Statement> getStatements() {
			return unmodifiableList(statements);
		}
		@Override
		public Element getNextChild(Element child) {
			return getNextSibling(statements, child);
		}

		@Override
		public Element getPreviousChild(Element child) {
			return getPreviousSibling(statements, child);
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			return replaceChild(statements, Statement.class, this, child, by);
		}
	}

    public static class Catch extends Statement {
        VariablesDeclaration declaration;
        Statement body;

        public Catch() {}
        public Catch(VariablesDeclaration declaration, Statement body) {
            setDeclaration(declaration);
            setBody(body);
        }

        public VariablesDeclaration getDeclaration() {
            return declaration;
        }

        public void setDeclaration(VariablesDeclaration declaration) {
            this.declaration = changeValue(this, this.declaration, declaration);
        }

        public Statement getBody() {
            return body;
        }

        public void setBody(Statement body) {
            this.body = changeValue(this, this.body, body);
        }

        @Override
        public boolean replaceChild(Element child, Element by) {
            if (child == getDeclaration()) {
                setDeclaration((VariablesDeclaration)by);
                return true;
            }
            if (child == getBody()) {
                setBody((Statement)by);
                return true;
            }
            return false;
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
        public void accept(Visitor visitor) {
            visitor.visitCatch(this);
        }


    }
    public static class Try extends Statement {
        Statement tryStatement, finallyStatement;
        final List<Catch> catches = new ArrayList<Catch>();

        public Try() {}
        public Try(Statement tryStatement, Statement finallyStatement, Catch... catches) {
            setTryStatement(tryStatement);
            setFinallyStatement(finallyStatement);
            setCatches(Arrays.asList(catches));
        }

        public void setTryStatement(Statement tryStatement) {
            this.tryStatement = changeValue(this, this.tryStatement, tryStatement);
        }

        public Statement getTryStatement() {
            return tryStatement;
        }

        public void setFinallyStatement(Statement finallyStatement) {
            this.finallyStatement = changeValue(this, this.finallyStatement, finallyStatement);
        }

        public Statement getFinallyStatement() {
            return finallyStatement;
        }

        public List<Catch> getCatches() {
            return Collections.unmodifiableList(catches);
        }

        public void setCatches(List<Catch> catches) {
            changeValue(this, this.catches, catches);
        }

        @Override
        public boolean replaceChild(Element child, Element by) {
            if (child == getTryStatement()) {
				setTryStatement((Statement)by);
				return true;
			}
			if (child == getFinallyStatement()) {
				setFinallyStatement((Statement)by);
				return true;
			}
            return replaceChild(catches, Catch.class, this, child, by);
        }

        @Override
        public Element getNextChild(Element child) {
            return getNextSibling(catches, child);
        }

        @Override
        public Element getPreviousChild(Element child) {
            return getPreviousSibling(catches, child);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitTry(this);
        }

        

    }
}
