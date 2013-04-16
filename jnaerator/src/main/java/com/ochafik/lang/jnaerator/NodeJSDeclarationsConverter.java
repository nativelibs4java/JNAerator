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
import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.Define;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.ElementsHelper;
import com.ochafik.lang.jnaerator.parser.Expression.BinaryOperator;
import com.ochafik.lang.jnaerator.parser.Expression.UnaryOperator;
import com.ochafik.lang.jnaerator.parser.Statement;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.Statement.If;
import com.ochafik.lang.jnaerator.parser.Statement.Return;
import java.io.File;

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
    public void convertConstants(String library, List<Define> defines, Element sourcesRoot, final Signatures signatures, final DeclarationsHolder out, final Identifier libraryClassName) {
        
    }
    
    Block getInitMethodBody(DeclarationsHolder decls) {
        for (Declaration decl : decls.getDeclarations()) {
            if (decl instanceof Function) {
                return ((Function)decl).getBody();
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
    @Override
    protected void convertFunction(Function function, Signatures signatures, boolean callback, DeclarationsHolder objOut, Identifier libraryClassName, String sig, Identifier functionName, String library, int iConstructor) {
        String methodName = library + "_" + (function.getParentElement() instanceof Struct ? ((Struct)function.getParentElement()).getTag() + "_" : "") + function.getName();
        Function method = new Function(Function.Type.CppMethod, ident(methodName), typeRef(v8Ident("Handle", v8Ident("Value"))));
        method.addArg(new Arg("args", new TypeRef.Pointer(typeRef(v8Ident("Arguments")), Declarator.PointerStyle.Reference).addModifiers(ModifierType.Const)));
        Block body = new Block();
        
        //if (args.Length() != 4) {
        //return THROW_ERROR_EXCEPTION("ffi_call() requires 4 arguments!");
        List<Arg> args = function.getArgs();
        int argCount = args.size();
        body.addStatement(
            new If(
                expr(methodCall(varRef("args"), "Length"), BinaryOperator.IsDifferent, expr(argCount)),
                new Return(
                    methodCall(v8Ident("ThrowException"), 
                        newV8String(functionName + "() requires " + argCount + " arguments!")))
            )
        );
        
        List<Expression> params = new ArrayList<Expression>();
        for (int iArg = 0; iArg < argCount; iArg++) {
            Arg arg = args.get(iArg);
            TypeRef tr = result.typeConverter.resolveTypeDef(arg.getValueType(), null, false, false);
            Expression typeTest = null;
            String typeErrorMessage = null;
            Expression argExpr = new Expression.ArrayAccess(varRef("args"), expr(iArg));
            Expression argDeclExpr = null;
            TypeRef argType = null;
            Expression argUsageExpr = null;
            if (tr instanceof TypeRef.TargettedTypeRef) {
                TypeRef.TargettedTypeRef ttr = (TypeRef.TargettedTypeRef) tr;
                
                if (ttr.getTarget().toString().matches("(const\\s+)?char")) {
                    typeTest = arrowMethodCall(argExpr, "IsString");
                    typeErrorMessage = "expected a String";
                    argDeclExpr = arrowMethodCall(argExpr.clone(), "ToString");
                    argType = typeRef(v8Ident("Handle", v8Ident("String")));
                    argUsageExpr = cast(arg.getValueType().clone(), 
                        expr(UnaryOperator.Dereference, 
                            methodCall(ident("v8", "String", "AsciiValue"),
                                varRef(arg.getName()))));
                } else {
                    // TODO: use Buffer + ref module
                }
            } else if (tr instanceof TypeRef.Primitive) {
                String t;
                if (tr.toString().matches("bool|BOOL")) {
                    t = "Boolean";
                } else {
                    t = "Number";
                }
                typeTest = arrowMethodCall(argExpr, "Is" + t);
                typeErrorMessage = "expected a " + t;
                argDeclExpr = arrowMethodCall(argExpr.clone(), "To" + t);
                argType = typeRef(v8Ident("Handle", v8Ident(t)));
                argUsageExpr = cast(arg.getValueType().clone(), arrowMethodCall(expr(UnaryOperator.Dereference, varRef(arg.getName())), "Value"));
            } else if (tr instanceof TypeRef.SimpleTypeRef) {
                // TODO
            }
            
            if (typeTest != null) {
                body.addStatement(
                    new If(
                        expr(UnaryOperator.Not, typeTest),
                        new Return(
                            methodCall(v8Ident("ThrowException"),
                                methodCall(ident("v8", "Exception", "TypeError"),
                                    newV8String("Invalid value for argument '" + arg.getName() + "'" + (typeErrorMessage == null ? "" : ": " + typeErrorMessage)))))));
            }
            if (argUsageExpr == null)
                throw new UnsupportedConversionException(arg, "Cannot convert arguments of type " + tr);
            
            if (argDeclExpr != null)
                body.addStatement(stat(new VariablesDeclaration(argType, new Declarator.DirectDeclarator(arg.getName(), argDeclExpr))));
            
            if (argUsageExpr != null)
                params.add(argUsageExpr);
        }
        Expression call = methodCall(function.getName(), params.toArray(new Expression[params.size()]));
        body.addStatement(stat(call));
        method.setBody(body);
        
        objOut.addDeclaration(method);
        
        Block initBlock = getInitMethodBody(objOut);
        initBlock.addStatement(stat(methodCall("NODE_SET_METHOD", varRef("target"), expr(functionName.toString()), varRef(methodName))));
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
        for (int i = 0; i < args.length; i++)
            exprArgs[i] = expr(typeRef(args[i]));
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
            sourceFile.addDeclaration(new Include(Include.Type.CInclude, "v8.h"));
            sourceFile.addDeclaration(new Include(Include.Type.CInclude, "node.h"));
            if (isFramework) {
                sourceFile.addDeclaration(new Include(Include.Type.ObjCImport, library + "/" + library + ".h"));
            } else {
                List<File> librarySourceFiles = config.sourceFilesByLibrary.get(library);
                if (librarySourceFiles != null) {
                    for (File file : librarySourceFiles) {
                        sourceFile.addDeclaration(new Include(Include.Type.CInclude, file.toString()));
                    }
                }
            }
            
            String initFunctionName = library + "_init";
            Function initMethod = new Function(Function.Type.CppMethod, ident(initFunctionName), typeRef(void.class));
            initMethod.addArg(new Arg("target", typeRef(v8Ident("Handle", v8Ident("Object")))));
            initMethod.setBody(new Block());
            // Hack: add init method here so that convertFunction finds it, then (re)move it to the end.
            sourceFile.addDeclaration(initMethod);
            
            fillLibraryMapping(result, sourceFiles, sourceFile, library, null, null, null);
            initMethod.replaceBy(null);
            sourceFile.addDeclaration(initMethod);
            sourceFile.addDeclaration(decl(stat(methodCall("NODE_MODULE", varRef(ident(library)), varRef(ident(initFunctionName))))));
            writeLibraryInterface(result, sourceFiles, sourceFile, library, null, null);
            
            Map<String, Object> gypTarget = map();
            gypTarget.put("target_name", library);
            gypTarget.put("sources", sources);
            gypTarget.put("include_dirs", config.preprocessorConfig.explicitIncludes);
            gypTarget.put("dependencies", dependencies);
            Map<String, Object> linkSettings = map();
            linkSettings.put("libraries",
                isFramework ?
                    Arrays.asList("-framework", library) :
                    Arrays.asList("-l" + library));
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
