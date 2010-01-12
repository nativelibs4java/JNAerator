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
package com.ochafik.lang.jnaerator;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import com.ochafik.lang.jnaerator.TypeConversion.JavaPrim;
import com.ochafik.lang.jnaerator.TypeConversion.TypeConversionMode;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.Statement;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import com.ochafik.lang.jnaerator.parser.Declarator.PointerStyle;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Expression.VariableRef;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalCallback;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalPointer;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalPointerType;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalStruct;
import com.sun.jna.ptr.ByReference;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
public class GlobalsGenerator {
	public GlobalsGenerator(Result result) {
		this.result = result;
	}

	final Result result;
	
//	public static final class NAME {
//		private static NAME_holder NAME;
//		public static final class NAME_holder {
//			public String value;
//		}
//		public static synchronized NAME_holder get() {
//			if (NAME == null)
//				NAME = new NAME_holder((Pointer)null);
//			return NAME;
//		}
//		void test() {
//			GlobalsGenerator.NAME.get().value;
//		}
//	}
	
	@SuppressWarnings("unchecked")
	public void convertGlobals(VariablesDeclaration globals, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier callerLibraryName, String callerLibrary) throws UnsupportedConversionException {
		for (Declarator d : globals.getDeclarators()) {
			try {
				Identifier name = result.typeConverter.getValidJavaArgumentName(ident(d.resolveName()));
 				TypeRef type = (TypeRef)d.mutateType(globals.getValueType());
				if (type == null)
					continue;
				
				boolean isCallback = result.callbacksByName.containsKey(ident(type.toString()));//type instanceof FunctionSignature;
				List<Modifier> modifiers = new ArrayList<Modifier>(type.getModifiers());
				modifiers.addAll(globals.getModifiers());
				
				type.setModifiers(Collections.EMPTY_LIST);
				
				if (	!isCallback && 
						!(Modifier.Extern.isContainedBy(modifiers) || Modifier.Dllexport.isContainedBy(modifiers) || Modifier.Dllimport.isContainedBy(modifiers))
						//|| Modifier.Const.isContainedBy(modifiers) && d.getDefaultValue() != null
						) {
					//result.declarationsConverter.convertCon
					continue;
				}
				
				
				if (true) {//!result.config.useJNADirectCalls) {
					if (!signatures.variablesSignatures.add(name.toString()))
						continue;
					
					boolean isPointer = type instanceof com.ochafik.lang.jnaerator.parser.TypeRef.Pointer;
					JavaPrim prim = result.typeConverter.getPrimitive(isPointer ? ((com.ochafik.lang.jnaerator.parser.TypeRef.Pointer)type).getTarget() : type, callerLibraryName);
					type.setMarkedAsResolved(false);
					TypeRef convertedType = result.typeConverter.convertTypeToJNA(type, TypeConversionMode.NativeParameter, callerLibraryName);
					String convTypStr = convertedType.toString();
					if (convTypStr.endsWith(".ByValue"))
						convTypStr = convTypStr.substring(0, convTypStr.length() - ".ByValue".length());
					boolean isStruct = result.structsFullNames.contains(ident(convTypStr));
					boolean isUnion = result.unionsFullNames.contains(ident(convTypStr));
					
					//if (result. convertedType)
					if (prim != null || isCallback || isStruct || isUnion) {
						TypeRef globalType = null;
						Expression extraArg = null;
						//Class<? extends Global> optionA;
						if (isUnion || isStruct) {
							globalType = typeRef(ident(GlobalStruct.class, expr(convertedType.clone())));
							extraArg = memberRef(expr(convertedType.clone()), MemberRefStyle.Dot, "class");
						} else if (isCallback) {
							globalType = typeRef(ident(GlobalCallback.class, expr(type.clone())));
							extraArg = memberRef(expr(type.clone()), MemberRefStyle.Dot, "class");
						} else if (isPointer) {
							Class<? extends ByReference> brt = result.typeConverter.primToByReference.get(prim);
							if (brt != null) {
								globalType = typeRef(ident(GlobalPointerType.class, expr(typeRef(ident(brt)))));
								extraArg = classLiteral(brt);
							} else if (prim == JavaPrim.Void) {
								globalType = typeRef(GlobalPointer.class);
							}
						} else {
							Class<?> globalClass = result.typeConverter.primToGlobal.get(prim);
							if (globalClass != null)
								globalType = typeRef(globalClass);
						}
						if (globalType != null) {
							List<Expression> constructorArgs = new ArrayList<Expression>();
							constructorArgs.add(nativeLibFieldExpr.clone());
							if (extraArg != null) {
								constructorArgs.add(extraArg);
							}
							constructorArgs.add(expr(name.toString()));
							VariablesDeclaration vd = new VariablesDeclaration(
									globalType, 
								new Declarator.DirectDeclarator(
									name.toString(), 
									new Expression.New(
										globalType.clone(),
										constructorArgs.toArray(new Expression[constructorArgs.size()])
									)
								)
							);
	
							vd.addModifiers(Modifier.Public, Modifier.Static, Modifier.Final);
							vd.importDetails(globals, false);
							vd.moveAllCommentsBefore();
							
							out.addDeclaration(vd);
							continue;
						}
					}
				}
				
				if (!signatures.classSignatures.add(name))
					continue;
				
				
				/// We get a pointer to the global, not the global itself
				Struct struct = result.declarationsConverter.publicStaticClass(name, null, Struct.Type.JavaClass, null);
				struct.addModifiers(Modifier.Final);
				struct.importDetails(globals, false);
				struct.moveAllCommentsBefore();
				
				TypeRef pointerType = new TypeRef.Pointer(type, PointerStyle.Pointer);
				
				TypeRef convPointerType = result.typeConverter.convertTypeToJNA(pointerType, TypeConversionMode.FieldType, callerLibraryName);
				TypeRef instType;
				boolean hasOffset, isPtr = false, isByRef = false;
				String convPointerTypeStr = convPointerType.toString();
				if (convPointerTypeStr.equals(result.config.runtime.pointerClass.getName())) {
					isPtr = true;
					instType = convPointerType;
					hasOffset = false;
				} else if (result.typeConverter.byReferenceClassesNames.contains(convPointerTypeStr)) {
					isByRef = true;
					instType = convPointerType;
					hasOffset = false;
				} else if (convPointerTypeStr.endsWith(".ByReference") && result.structsByName.get(convPointerTypeStr.substring(0, convPointerTypeStr.length() - ".ByReference".length())) != null) {
					instType = result.typeConverter.convertTypeToJNA(type, TypeConversionMode.PointedValue, callerLibraryName);//convPointerType;
					hasOffset = true;
				} else {
					Identifier instTypeName = ident(name + "_holder");
					Struct holderStruct = result.declarationsConverter.publicStaticClass(instTypeName, ident(result.config.runtime.structClass), Struct.Type.JavaClass, null);
					holderStruct.addModifiers(Modifier.Final);
					VariablesDeclaration vd = result.declarationsConverter.convertVariablesDeclarationToJNA("value", type, new int[1], callerLibraryName);
					if (vd.getValueType().toString().equals(result.config.runtime.pointerClass.getName())) {
						isByRef = true;
						instType = convPointerType;
						hasOffset = false;	
					} else {
						holderStruct.addDeclaration(vd);
						Function pointerConstructor = new Function(Function.Type.JavaMethod, instTypeName, null, 
							new Arg("pointer", new TypeRef.SimpleTypeRef(result.config.runtime.pointerClass.getName()))
						);
						hasOffset = false;
						pointerConstructor.setBody(new Statement.Block(
								new Statement.ExpressionStatement(methodCall("super")),
								new Statement.ExpressionStatement(methodCall("useMemory", varRef("pointer"), expr(0))),
								new Statement.ExpressionStatement(methodCall("read"))
						));
						holderStruct.addDeclaration(pointerConstructor);
						
						//holderStruct.addDeclaration(new VariablesDeclaration(convType, new Declarator.DirectDeclarator("value")).addModifiers(Modifier.Public));
						instType = new TypeRef.SimpleTypeRef(instTypeName);
						struct.addDeclaration(decl(holderStruct));
					}
				}
				Identifier instName = name;//"_";
				struct.addDeclaration(new VariablesDeclaration(instType, new Declarator.DirectDeclarator(instName.toString())).addModifiers(Modifier.Private, Modifier.Static));
				VariableRef instRef = new VariableRef(instName);
				Expression ptrExpr = methodCall(
					nativeLibFieldExpr.clone(),
					MemberRefStyle.Dot,
					"getGlobalVariableAddress",
					expr(name.toString())
				);
				List<Statement> initStats = new ArrayList<Statement>();
				initStats.add(new Statement.ExpressionStatement(
					expr(
						instRef.clone(),
						Expression.AssignmentOperator.Equal,
						isPtr ? ptrExpr :
						isByRef ? new Expression.New(instType) :
						new Expression.New(instType, new Expression.FunctionCall(null, ptrExpr, hasOffset ? expr(0) : null))
					)
				));
				if (isByRef)
					initStats.add(new Statement.ExpressionStatement(methodCall(instRef, MemberRefStyle.Dot, "setPointer", ptrExpr)));
	
				struct.addDeclaration(new Function(Function.Type.JavaMethod, ident("get"), instType).setBody(new Statement.Block(
					new Statement.If(
						expr(instRef, Expression.BinaryOperator.IsEqual, nullExpr()),
						initStats.size() == 1 ? initStats.get(0) : new Statement.Block(initStats),
						null
					),
					new Statement.Return(instRef.clone())
				)).addModifiers(Modifier.Public, Modifier.Static, Modifier.Synchronized));
				out.addDeclaration(decl(struct));
			} catch (Throwable t) {
				out.addDeclaration(result.declarationsConverter.skipDeclaration(d, t.toString()));
			}
		}
	}

	public void convertGlobals(List<VariablesDeclaration> list, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier libraryNameExpression, String library) {		
		if (list == null)
			return;
		
		for (VariablesDeclaration v : list) {
			try {
				convertGlobals(v, signatures, out, nativeLibFieldExpr, libraryNameExpression, library);
			} catch (UnsupportedConversionException ex) {
				out.addDeclaration(result.declarationsConverter.skipDeclaration(v, ex.toString()));
			}
		}
	}
}
