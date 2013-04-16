/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator.parser;

/**
 * For macro calls that expand to declarations.
 * @author ochafik
 */
public class StatementDeclaration extends Declaration {
    Statement statement;

    public StatementDeclaration() {
    }

    public StatementDeclaration(Statement statement) {
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = changeValue(this, this.statement, statement);
    }

    @Override
    public boolean replaceChild(Element child, Element by) {
        if (child == getStatement()) {
            setStatement((Statement)by);
            return true;
        }
        return super.replaceChild(child, by);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitStatementDeclaration(this);
    }
    
}
