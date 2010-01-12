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

import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.FunctionDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.PointerDeclarator;
import com.ochafik.lang.jnaerator.parser.Expression.BinaryOp;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.Statement.Catch;
import com.ochafik.lang.jnaerator.parser.Statement.DeclarationStatement;
import com.ochafik.lang.jnaerator.parser.Statement.If;
import com.ochafik.lang.jnaerator.parser.Statement.Return;
import com.ochafik.lang.jnaerator.parser.Statement.Throw;
import com.ochafik.lang.jnaerator.parser.Expression.ArrayAccess;
import com.ochafik.lang.jnaerator.parser.Expression.AssignmentOp;
import com.ochafik.lang.jnaerator.parser.Expression.Cast;
import com.ochafik.lang.jnaerator.parser.Expression.ConditionalExpression;
import com.ochafik.lang.jnaerator.parser.Expression.Constant;
import com.ochafik.lang.jnaerator.parser.Expression.EmptyArraySize;
import com.ochafik.lang.jnaerator.parser.Expression.ExpressionSequence;
import com.ochafik.lang.jnaerator.parser.Expression.FunctionCall;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRef;
import com.ochafik.lang.jnaerator.parser.Expression.New;
import com.ochafik.lang.jnaerator.parser.Expression.NewArray;
import com.ochafik.lang.jnaerator.parser.Expression.NullExpression;
import com.ochafik.lang.jnaerator.parser.Expression.OpaqueExpression;
import com.ochafik.lang.jnaerator.parser.Expression.TypeRefExpression;
import com.ochafik.lang.jnaerator.parser.Expression.UnaryOp;
import com.ochafik.lang.jnaerator.parser.Expression.VariableRef;
import com.ochafik.lang.jnaerator.parser.Identifier.QualifiedIdentifier;
import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.lang.jnaerator.parser.Statement.ExpressionStatement;
import com.ochafik.lang.jnaerator.parser.Statement.Try;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.ArrayRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.Pointer;
import com.ochafik.lang.jnaerator.parser.TypeRef.Primitive;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;

public interface Visitor {

	void visitConstant(Constant constant);

	void visitArg(Arg arg);

	void visitEnum(Enum enum1);

	void visitFunction(Function function);

	void visitFunctionPointerDeclaration(
			FunctionPointerDeclaration functionPointerDeclaration);

	void visitStruct(Struct struct);

	void visitTypeDef(TypeDef typeDef);

	void visitSimpleTypeRef(SimpleTypeRef simpleTypeRef);

	void visitFunctionSignature(FunctionSignature functionSignature);

	void visitPrimitive(Primitive primitive);

	void visitPointer(Pointer pointer);

	void visitArray(ArrayRef array);

	void visitSourceFile(SourceFile sourceFile);

	void visitEnumItem(Enum.EnumItem enumItem);

	void visitUnaryOp(UnaryOp unaryOp);

	void visitVariableRef(VariableRef variableRef);

	void visitBinaryOp(BinaryOp binaryOp);

	void visitFunctionCall(FunctionCall functionCall);

	void visitCast(Cast cast);

	void visitDeclarator(Declarator variableStorage);

	void visitVariablesDeclaration(VariablesDeclaration variablesDeclaration);

	void visitTaggedTypeRefDeclaration(TaggedTypeRefDeclaration taggedTypeRefDeclaration);
	
	void visitTaggedTypeRef(TaggedTypeRef taggedTypeRef);

	void visitEmptyArraySize(EmptyArraySize emptyArraySize);

	void visitDefine(Define define);

	void visitTypeRefExpression(TypeRefExpression typeRefExpression);

	void visitNew(New new1);

	void visitAnnotation(Annotation annotation);

	void visitEmptyDeclaration(EmptyDeclaration emptyDeclaration);

	void visitNewArray(NewArray newArray);

	void visitPointerDeclarator(PointerDeclarator pointerDeclarator);

	void visitArrayDeclarator(com.ochafik.lang.jnaerator.parser.Declarator.PointerDeclarator.ArrayDeclarator arrayDeclarator);

	void visitDirectDeclarator(DirectDeclarator directDeclarator);

	void visitFunctionDeclarator(FunctionDeclarator functionDeclarator);

	void visitModifiableElement(ModifiableElement modifiableElement);

	void visitBlock(Block block);

	void visitExpressionStatement(ExpressionStatement expressionStatement);

	void visitIf(If if1);

	void visitNullExpression(NullExpression nullExpression);

	void visitReturn(Return return1);

	void visitExternDeclarations(ExternDeclarations externDeclarations);

	void visitOpaqueExpression(OpaqueExpression opaqueExpression);

	void visitArrayAccess(ArrayAccess arrayIncr);

	void visitMemberRef(MemberRef memberRef);

	void visitAssignmentOp(AssignmentOp assignment);

	void visitConditionalExpression(ConditionalExpression conditionalExpression);

	void visitExpressionSequence(ExpressionSequence expressionSequence);

	void visitSimpleIdentifier(SimpleIdentifier simpleIdentifier);

	void visitQualifiedIdentifier(QualifiedIdentifier qualifiedIdentifier);

	void visitDeclarationStatement(DeclarationStatement declarationStatement);

	void visitThrow(Throw t);

	void visitProperty(Property property);

	void visitFriendDeclaration(FriendDeclaration friendDeclaration);

    public void visitTry(Try aThis);

    public void visitCatch(Catch aThis);
	
}
