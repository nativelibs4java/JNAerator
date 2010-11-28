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

import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.StructObject;
import org.bridj.ValuedEnum;
import org.bridj.cpp.CPPObject;

import static com.ochafik.lang.SyntaxUtils.as;
//import org.bridj.structs.StructIO;
//import org.bridj.structs.Array;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import com.ochafik.lang.jnaerator.JNAeratorConfig.GenFeatures;
import com.ochafik.lang.jnaerator.TypeConversion.NL4JConversion;
import com.ochafik.lang.jnaerator.cplusplus.CPlusPlusMangler;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.Struct.MemberVisibility;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Function.Type;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder.ListWrapper;
import com.ochafik.lang.jnaerator.parser.Declarator.*;
import com.ochafik.lang.jnaerator.runtime.VirtualTablePointer;
import com.ochafik.util.CompoundCollection;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.TypeConversion.*;

public class BridJer {
    Result result;
    public BridJer(Result result) {
        this.result = result;
    }
	public Element convertToJava(Element element) {
        element = element.clone();
        final HashSet<Element> referencedElements = new HashSet<Element>();
        element.accept(new Scanner() {

            @Override
            public void visitUnaryOp(UnaryOp unaryOp) {
                super.visitUnaryOp(unaryOp);
                
                if (unaryOp.getOperator() == UnaryOperator.Reference)
                    referencedElements.add(unaryOp);
            }
            
        });
        
        element.accept(new Scanner() {
            
            @Override
            public void visitUnaryOp(UnaryOp unaryOp) {
                super.visitUnaryOp(unaryOp);
                
                switch (unaryOp.getOperator()) {
                    case Dereference:
                        unaryOp.replaceBy(methodCall(unaryOp.getOperand(), "get"));
                        break;
                    case Reference:
                        // TODO handle special case of referenced primitives, for instance
                        unaryOp.replaceBy(methodCall(unaryOp.getOperand(), "getReference"));
                        break;
                }
            }

            @Override
            public void visitCast(Cast cast) {
                super.visitCast(cast);
                if (cast.getType() instanceof TargettedTypeRef) {
                    cast.replaceBy(methodCall(cast.getTarget(), "asPointerTo", result.typeConverter.typeLiteral(cast.getType())));
                }
            }

            @Override
            public void visitArrayAccess(ArrayAccess arrayAccess) {
                super.visitArrayAccess(arrayAccess);
                arrayAccess.replaceBy(methodCall(arrayAccess.getTarget(), "get", arrayAccess.getIndex()));
            }

            @Override
            public void visitMemberRef(MemberRef memberRef) {
                super.visitMemberRef(memberRef);
                // TODO restrict to struct/class fields
                memberRef.replaceBy(methodCall(memberRef.getTarget(), memberRef.getName().toString()));
            }

            
            @Override
            public void visitVariablesDeclaration(VariablesDeclaration v) {
                if (v.getDeclarators().size() == 1) {
                    Declarator d = v.getDeclarators().get(0);
                    MutableByDeclarator t = d.mutateType(v.getValueType());
                    if (t instanceof TypeRef) {
                        v.setValueType((TypeRef)t);
                        v.setDeclarators(Arrays.asList((Declarator)new DirectDeclarator(d.resolveName(), d.getDefaultValue())));
                    }
                }
                super.visitVariablesDeclaration(v);
            }

            Class ptrClass() {
                return result.config.runtime.pointerClass;
            }

            @Override
            public void visitAssignmentOp(AssignmentOp assignment) {
                BinaryOperator binOp = assignment.getOperator().getCorrespondingBinaryOp();
                Expression value = assignment.getValue();
                value.setParenthesis(true);
                if (assignment.getTarget() instanceof UnaryOp) {
                    UnaryOp uop = (UnaryOp)assignment.getTarget();
                    if (uop.getOperator() == UnaryOperator.Dereference) {
                        visit(uop.getOperand());
                        visit(assignment.getValue());
                        Expression target = uop.getOperand();
                        if (binOp != null) {
                            value = expr(methodCall(target.clone(), "get"), binOp, value);
                        }
                        assignment.replaceBy(methodCall(target, "set", value));
                        return;
                    }
                }
                if (assignment.getTarget() instanceof ArrayAccess) {
                    ArrayAccess aa = (ArrayAccess)assignment.getTarget();
                    visit(aa.getTarget());
                    visit(aa.getIndex());
                    visit(assignment.getValue());
                    Expression target = aa.getTarget();
                    Expression index = aa.getIndex();
                    if (binOp != null) {
                        value = expr(methodCall(target.clone(), "get", index.clone()), binOp, value);
                    }
                    assignment.replaceBy(methodCall(target, "set", index, value));
                    return;
                }
                if (assignment.getTarget() instanceof MemberRef) {
                    MemberRef mr = (MemberRef)assignment.getTarget();
                    Expression target = mr.getTarget();
                    String name = mr.getName().toString();
                    if (binOp != null) {
                        value = expr(methodCall(target.clone(), name), binOp, value);
                    }
                    assignment.replaceBy(methodCall(target, name, value));
                    return;
                }
                super.visitAssignmentOp(assignment);
                
            }
            
            
            @Override
            public void visitNew(New new1) {
                super.visitNew(new1);
                if (new1.getConstruction() == null)
                    new1.replaceBy(methodCall(expr(typeRef(ptrClass())), "allocate" + StringUtils.capitalize(new1.getType().toString())));
            }

            public void notSup(Element x, String msg) throws UnsupportedConversionException {
                throw new UnsupportedConversionException(x, msg);
            }
            @Override
            public void visitNewArray(NewArray newArray) {
                super.visitNewArray(newArray);
                if (newArray.getType() instanceof Primitive) {
                    if (newArray.getDimensions().size() != 1)
                        notSup(newArray, "TODO only dimensions 1 to 3 are supported for primitive array creations !");
                
                    newArray.replaceBy(
                        methodCall(
                            expr(typeRef(ptrClass())), 
                            "allocate" + StringUtils.capitalize(newArray.getType().toString()) + "s", 
                            newArray.getDimensions().toArray(new Expression[newArray.getDimensions().size()])
                        )
                    );
                } else {
                    if (newArray.getDimensions().size() != 1)
                        notSup(newArray, "TODO only dimension 1 is supported for reference array creations !");
                
                    newArray.replaceBy(
                        methodCall(
                            expr(typeRef(ptrClass())), 
                            "allocateArray",  
                            result.typeConverter.typeLiteral(newArray.getType()), 
                            newArray.getDimensions().get(0)
                        )
                    );
                }
            }

            @Override
            public void visitPointer(Pointer pointer) {
                super.visitPointer(pointer);
            }
            
            @Override
            protected void visitTargettedTypeRef(TargettedTypeRef targettedTypeRef) {
                super.visitTargettedTypeRef(targettedTypeRef);
                Identifier id = ident(ptrClass());
                id.resolveLastSimpleIdentifier().addTemplateArgument(expr(targettedTypeRef.getTarget()));
                targettedTypeRef.replaceBy(typeRef(id));
            }
        });
        return element;
    }
}
