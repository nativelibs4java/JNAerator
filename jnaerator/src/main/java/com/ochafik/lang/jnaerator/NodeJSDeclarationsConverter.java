/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.io.IOUtils;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import java.io.IOException;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.Declarator;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Include;
import com.ochafik.lang.jnaerator.parser.ModifierType;
import com.ochafik.lang.jnaerator.parser.SourceFile;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static com.ochafik.lang.jnaerator.GYPUtils.*;
import com.ochafik.lang.jnaerator.TypeConversion.JavaPrim;
import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.Define;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.ElementsHelper;
import com.ochafik.lang.jnaerator.parser.Expression.BinaryOperator;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Expression.UnaryOperator;
import com.ochafik.lang.jnaerator.parser.Identifier.QualificationSeparator;
import com.ochafik.lang.jnaerator.parser.Statement;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.Statement.If;
import com.ochafik.lang.jnaerator.parser.Statement.Return;
import com.ochafik.util.listenable.Pair;
import java.io.File;
import org.bridj.demangling.Demangler;

/**
 *
 * @author ochafik
 */
public class NodeJSDeclarationsConverter extends DeclarationsConverter {

    public NodeJSDeclarationsConverter(Result result) {
        super(result);
    }

    @Override
    protected void configureCallbackStruct(Struct callbackStruct) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void convertCallback(TypeRef.FunctionSignature functionSignature, Signatures signatures, DeclarationsHolder out, Identifier callerLibraryName) {
    }
//    public void convertConstants(String library, List<Define> defines, Element sourcesRoot, final Signatures signatures, final DeclarationsHolder out, final Identifier libraryClassName) {
//    }

    @Override
    protected void outputNSString(String name, String value, DeclarationsHolder out, Signatures signatures, Element... elementsToTakeCommentsFrom) {
    }

    @Override
    protected void convertConstant(String name, JavaPrim prim, TypeRef mutatedType, Expression defaultValue, VariablesDeclaration v, Declarator decl, DeclarationsHolder out, Signatures signatures, Identifier libraryClassName) {
        convertConstant(name, defaultValue, out, signatures);
    }

    Expression.Constant.Type getValueConstantType(Expression value) {
        if (value instanceof Expression.Cast) {
            return getValueConstantType(((Expression.Cast) value).getTarget());
        }
        if (value instanceof Expression.BinaryOp) {
            return getValueConstantType(((Expression.BinaryOp) value).getFirstOperand());
        }
        if (value instanceof Expression.Constant) {
            return ((Expression.Constant) value).getType();
        }
        throw new UnsupportedConversionException(value, "Unknown value type");
    }

    @Override
    protected void convertDefine(Define define, DeclarationsHolder out, Signatures signatures, Identifier libraryClassName) {
        Expression value = define.getValue();
        convertConstant(define.getName(), value, out, signatures);
    }

    protected void convertConstant(String name, Expression value, DeclarationsHolder out, Signatures signatures) {
        Block initBlock = getInitMethodBody(out);
        Expression expr;
        Expression.Constant.Type valueType = getValueConstantType(value);
        switch (valueType) {
            case Int:
            case UInt:
            case IntegerString:
            case Long:
            case ULong:
            case LongString:
            case Short:
            case Byte:
                expr = methodCall(ident("v8", "Integer", "New"), value.clone());
                break;
            case Double:
            case Float:
                expr = methodCall(ident("v8", "Number", "New"), value.clone());
                break;
            case Bool:
                expr = methodCall(ident("v8", "Boolean", "New"), value.clone());
                break;
            case Char:
            case String:
                expr = methodCall(ident("v8", "String", "New"), new Expression.Constant(valueType, value + "", null));
                break;
            case Null:
                expr = methodCall(ident("v8", "Null"));
                break;
            default:
                throw new UnsupportedConversionException(value, "Constant type not supported yet");
        }
        initBlock.addStatement(stat(methodCall(varRef(initTarget), MemberRefStyle.Arrow, "Set",
                methodCall(ident("v8", "String", "NewSymbol"), expr(name)),
                expr)));
    }

    Block getInitMethodBody(DeclarationsHolder decls) {
        for (Declaration decl : decls.getDeclarations()) {
            if (decl instanceof Function) {
                return ((Function) decl).getBody();
            }
        }
        throw new RuntimeException("Init method not found in " + decls);

    }

    static Identifier ident(String... components) {
        return ElementsHelper.ident(Identifier.QualificationSeparator.Colons, components);
    }

    static Expression newV8String(String s) {
        return methodCall(ident("v8", "String", "New"), expr(s));
    }

    static Expression arrowMethodCall(Expression target, String name, Expression... args) {
        return methodCall(target, Expression.MemberRefStyle.Arrow, name, args);
    }
    static final String argsName = "_arguments_";
    static final String returnValueName = "_return_";
    static final String scopeName = "_scope_";
    static final String initTarget = "_target_";
    static final String dummyNodeBufferFreeCallbackName = "_dummy_node_buffer_free_callback_";

    private enum NodeType {

        Pointer, String, Number, Boolean, VAList, Unknown, Void
    };

    private TypeRef resolveTypeDef(TypeRef typeRef) {
        TypeRef tr = result.typeConverter.normalizeTypeRef(typeRef);
        return tr;
    }

    private NodeType getNodeType(TypeRef tr) {
        if (tr instanceof TypeRef.TargettedTypeRef) {
            TypeRef.TargettedTypeRef ttr = (TypeRef.TargettedTypeRef) tr;

            if (ttr.getTarget().toString().matches("(const\\s+)?char")) {
                return NodeType.String;
            } else {
                return NodeType.Pointer;
            }
        } else if (tr instanceof TypeRef.SimpleTypeRef) {
            TypeRef.SimpleTypeRef str = (TypeRef.SimpleTypeRef) tr;
            String n = str + "";//strstr.getName().toString();
            if (n.matches("bool|BOOL")) {
                return NodeType.Boolean;
            } else if (n.matches("void")) {
                return NodeType.Void;
            } else if (n.equals("va_list")) {
                return NodeType.VAList;
            } else {
                return NodeType.Number;
            }
        } else if (tr instanceof TypeRef.FunctionSignature) {
            return NodeType.Pointer;
        }
        return NodeType.Unknown;
    }

    @Override
    protected void convertFunction(Function function, Signatures signatures, boolean callback, 
            DeclarationsHolder declarations, DeclarationsHolder implementations, Identifier libraryClassName, 
            String sig, Identifier functionName, String library, int iConstructor) {
        assert implementations == declarations || declarations == null;
        String methodName = library + "_" + (function.getParentElement() instanceof Struct ? ((Struct) function.getParentElement()).getTag() + "_" : "") + function.getName();
        Function method = new Function(Function.Type.CppMethod, ident(methodName), typeRef(v8Ident("Handle", v8Ident("Value"))));
        method.addArg(new Arg(argsName, new TypeRef.Pointer(typeRef(v8Ident("Arguments")), Declarator.PointerStyle.Reference).addModifiers(ModifierType.Const)));
        Block body = new Block();

        //if (args.Length() != 4) {
        //return THROW_ERROR_EXCEPTION("ffi_call() requires 4 arguments!");
        List<Arg> args = function.getArgs();
        int argCount = args.size();
        body.addStatement(
                new If(
                expr(methodCall(varRef(argsName), "Length"), BinaryOperator.IsDifferent, expr(argCount)),
                new Return(
                methodCall(v8Ident("ThrowException"),
                newV8String(functionName + "() requires " + argCount + " arguments!")))));

        List<Expression> params = new ArrayList<Expression>();
        for (int iArg = 0; iArg < argCount; iArg++) {
            Arg arg = args.get(iArg);
            if (arg.isVarArg()) {
                throw new UnsupportedConversionException(function, "varargs not supported yet");
            }

            TypeRef tr = resolveTypeDef(arg.getValueType());
            NodeType argNodeType = getNodeType(tr);
            Expression typeTest = null;
            String typeErrorMessage = null;
            Expression argExpr = new Expression.ArrayAccess(varRef(argsName), expr(iArg));
            Expression argDeclExpr = null;
            TypeRef argType = null;
            Expression argUsageExpr = null;
            switch (argNodeType) {
                case String:
                    typeTest = arrowMethodCall(argExpr, "IsString");
                    typeErrorMessage = "expected a String";
                    argDeclExpr = arrowMethodCall(argExpr.clone(), "ToString");
                    argType = typeRef(v8Ident("Handle", v8Ident("String")));
                    argUsageExpr = cast(transformTypeForCast(arg.getValueType()),
                            expr(UnaryOperator.Dereference,
                            methodCall(ident("v8", "String", "AsciiValue"),
                            varRef(arg.getName()))));
                    break;
                case Pointer:
                    typeTest = expr(
                            methodCall(argExpr.clone(), MemberRefStyle.Arrow, "IsNull"),
                            BinaryOperator.Or,
                            methodCall(ident("node", "Buffer", "HasInstance"), argExpr));
                    typeErrorMessage = "expected a Buffer";
                    argDeclExpr =
                            new Expression.ConditionalExpression(
                            methodCall(argExpr.clone(), MemberRefStyle.Arrow, "IsNull"),
                            varRef("NULL"),
                            methodCall(ident("node", "Buffer", "Data"),
                            methodCall(argExpr.clone(), Expression.MemberRefStyle.Dot, templateIdent(ident("As"), varRef(v8Ident("Object"))))));
                    argType = new TypeRef.Pointer(typeRef(ident("char")), Declarator.PointerStyle.Pointer);
                    argUsageExpr = cast(transformTypeForCast(arg.getValueType()), varRef(arg.getName()));
                    break;
                case Boolean:
                case Number:
                    String t = argNodeType.name();
                    typeTest = arrowMethodCall(argExpr, "Is" + t);
                    typeErrorMessage = "expected a " + t;
                    argDeclExpr = arrowMethodCall(argExpr.clone(), "To" + t);
                    argType = typeRef(v8Ident("Handle", v8Ident(t)));
                    argUsageExpr = cast(arg.getValueType().clone(), arrowMethodCall(expr(UnaryOperator.Dereference, varRef(arg.getName())), "Value"));
                    break;
                case VAList:
                    throw new UnsupportedConversionException(function, "va_list not supported yet");
                case Unknown:
                case Void:
                    throw new UnsupportedConversionException(arg, "Cannot convert arguments of type " + tr);
            }

            if (typeTest != null) {
                body.addStatement(
                        new If(
                        expr(UnaryOperator.Not, typeTest),
                        new Return(
                        methodCall(v8Ident("ThrowException"),
                        methodCall(ident("v8", "Exception", "TypeError"),
                        newV8String("Invalid value for argument '" + arg.getName() + "' at index " + iArg + (typeErrorMessage == null ? "" : ": " + typeErrorMessage)))))));
            }

            if (argDeclExpr != null) {
                body.addStatement(stat(new VariablesDeclaration(argType, new Declarator.DirectDeclarator(arg.getName(), argDeclExpr))));
            }

            if (argUsageExpr != null) {
                params.add(argUsageExpr);
            }
        }

        TypeRef retTr = resolveTypeDef(function.getValueType());
        NodeType retNodeType = getNodeType(retTr);
        body.addStatement(new VariablesDeclaration(typeRef(v8Ident("HandleScope")), new Declarator.DirectDeclarator(scopeName)));
        Expression call = methodCall(function.getName(), params.toArray(new Expression[params.size()]));
        if (retNodeType == NodeType.Void) {
            body.addStatement(stat(call));
        } else {
            VariablesDeclaration retDecl = new VariablesDeclaration(function.getValueType().clone(), new Declarator.DirectDeclarator(returnValueName, call));
            body.addStatement(retDecl);

        }
        Expression retExpr;
        switch (retNodeType) {
            case Number:
                body.addStatement(new Return(scopeClose(methodCall(ident("v8", "Number", "New"), varRef(returnValueName)))));
                break;
            case Boolean:
                body.addStatement(new Return(scopeClose(methodCall(ident("v8", "Boolean", "New"), varRef(returnValueName)))));
                break;
            case Pointer:
                // TODO: add pointer validity info.
                body.addStatement(
                        new If(
                        varRef(returnValueName),
                        new Return(
                        scopeClose(
                        memberRef(
                        methodCall(ident("node", "Buffer", "New"),
                        cast(new TypeRef.Pointer(typeRef("char"), Declarator.PointerStyle.Pointer), varRef(returnValueName)),
                        expr(1),
                        varRef(dummyNodeBufferFreeCallbackName),
                        varRef("NULL")),
                        Expression.MemberRefStyle.Arrow,
                        "handle_"))),
                        new Return(scopeClose(methodCall(ident("v8", "Null"))))));
                break;
            case Void:
                body.addStatement(new Return(scopeClose(methodCall(v8Ident("Undefined")))));
                break;
            default:
                throw new UnsupportedConversionException(function, "Return type not handled: " + retTr + " (" + retNodeType + ")");
        }
        method.setBody(body);

        implementations.addDeclaration(method);

        Block initBlock = getInitMethodBody(implementations);
        initBlock.addStatement(stat(methodCall("NODE_SET_METHOD", varRef(initTarget), expr(functionName.toString()), varRef(methodName))));
    }

    private Expression scopeClose(Expression content) {
        return methodCall(varRef(scopeName), Expression.MemberRefStyle.Dot, "Close", content);
    }

    private TypeRef transformTypeForCast(TypeRef tr) {
        if (tr instanceof TypeRef.TargettedTypeRef) {
            TypeRef.TargettedTypeRef ttr = (TypeRef.TargettedTypeRef) tr;
            return new TypeRef.Pointer(transformTypeForCast(ttr.getTarget()), Declarator.PointerStyle.Pointer);
        }
        return tr.clone();
    }

    @Override
    protected Struct createFakePointerClass(Identifier fakePointer) {
        return null;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void convertEnum(com.ochafik.lang.jnaerator.parser.Enum e, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Struct convertStruct(Struct struct, Signatures signatures, Identifier callerLibraryClass, String callerLibrary, boolean onlyFields) throws IOException {
        return null;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean convertVariablesDeclaration(VariablesDeclaration v, Signatures signatures, DeclarationsHolder out, int[] iChild, boolean isGlobal, Identifier holderName, Identifier callerLibraryClass, String callerLibrary) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static Identifier v8Ident(String name, Identifier... args) {
        Expression[] exprArgs = new Expression[args.length];
        for (int i = 0; i < args.length; i++) {
            exprArgs[i] = expr(typeRef(args[i]));
        }
        return templateIdent(ident("v8", name), exprArgs);
    }

    @Override
    public void generateLibraryFiles(SourceFiles sourceFiles, Result result, JNAeratorConfig config) throws IOException {
        List<Object> gypTargets = new ArrayList();
        for (String library : result.libraries) {
            if (library == null) {
                continue; // to handle code defined in macro-expanded expressions
            }
            boolean isFramework = config.frameworks.contains(library);

            String moduleName = library;
            String sourcePath = "src/" + library + "_module.cc";
            final List<String> sources = new ArrayList<String>(),
                    dependencies = new ArrayList<String>();

            // TODO: list sources in fillLibraryMapping
            sources.add(sourcePath);

            //out.println("///\n/// This file was autogenerated by JNAerator (http://jnaerator.googlecode.com/), \n/// a tool written by Olivier Chafik (http://ochafik.com/).\n///");
            SourceFile sourceFile = new SourceFile();
            sourceFile.setElementFile(sourcePath);
            for (String inc : new String[]{"v8.h", "node.h", "node_buffer.h"}) {
                sourceFile.addDeclaration(new Include(Include.Type.CInclude, inc));
            }
            if (!isFramework) {
                List<File> librarySourceFiles = config.sourceFilesByLibrary.get(library);
                if (librarySourceFiles != null) {
                    for (File file : librarySourceFiles) {
                        sourceFile.addDeclaration(new Include(Include.Type.CInclude, file.toString()));
                    }
                }
            }
            for (String otherFramework : config.frameworks) {
                sourceFile.addDeclaration(new Include(Include.Type.ObjCImport, otherFramework + "/" + otherFramework + ".h"));
            }


            String initFunctionName = library + "_init";
            Function initMethod = new Function(Function.Type.CppMethod, ident(initFunctionName), typeRef(void.class));
            initMethod.addArg(new Arg(initTarget, typeRef(v8Ident("Handle", v8Ident("Object")))));
            initMethod.setBody(new Block());
            // Hack: add init method here so that convertFunction finds it, then (re)move it to the end.
            sourceFile.addDeclaration(initMethod);

            Function dummyNodeBufferFreeCallback =
                    new Function(
                    Function.Type.CFunction,
                    ident(dummyNodeBufferFreeCallbackName),
                    typeRef(void.class),
                    new Arg("data", new TypeRef.Pointer(typeRef("char"), Declarator.PointerStyle.Pointer)),
                    new Arg("hint", new TypeRef.Pointer(typeRef("void"), Declarator.PointerStyle.Pointer)));
            dummyNodeBufferFreeCallback.setBody(new Block());
            sourceFile.addDeclaration(dummyNodeBufferFreeCallback);

            fillLibraryMapping(result, sourceFiles, sourceFile, sourceFile, library, null, null);
            initMethod.replaceBy(null);
            sourceFile.addDeclaration(initMethod);
            sourceFile.addDeclaration(decl(stat(methodCall("NODE_MODULE", varRef(ident(library)), varRef(ident(initFunctionName))))));
            writeLibraryInterface(result, sourceFiles, sourceFile, library, null);

            Map<String, Object> gypTarget = map();
            gypTarget.put("target_name", library);
            gypTarget.put("sources", sources);
            gypTarget.put("include_dirs", config.preprocessorConfig.explicitIncludes);
            gypTarget.put("dependencies", dependencies);
            Map<String, Object> linkSettings = map();
            linkSettings.put("libraries",
                    isFramework
                    ? Arrays.asList("-framework", library)
                    : Arrays.asList("-l" + library));
            List<Object> defines = new ArrayList<Object>();
            for (Map.Entry<String, String> e : config.preprocessorConfig.explicitMacros.entrySet()) {
                String k = e.getKey(), v = e.getValue();
                defines.add(v == null || v.length() == 0 ? k : k + "=" + v);
            }
            gypTarget.put("defines", defines);
            gypTarget.put("link_settings", linkSettings);
            gypTargets.add(gypTarget);
        }
        Map<String, Object> gypContents = map();
        gypContents.put("targets", gypTargets);
        final PrintWriter gypOut = result.classOutputter.getSourceWriter("binding.gyp");
        gypOut.print(toGYP(gypContents));
        gypOut.close();
    }
}
