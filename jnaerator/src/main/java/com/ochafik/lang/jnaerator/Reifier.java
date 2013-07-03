/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.*;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.TypeConversion.*;
import com.ochafik.lang.jnaerator.parser.Function.SignatureType;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bridj.ann.Ptr;

/**
 *
 * @author ochafik
 */
public class Reifier {

    final Result result;

    public Reifier(Result result) {
        this.result = result;
    }

    void toDirectFakePointer(Result result, Declaration decl) {
        decl.setValueType(typeRef(long.class));
        decl.addAnnotation(new Annotation(typeRef(Ptr.class)));
    }

    boolean isFakePointerRef(Result result, TypeRef tr) {
        if (tr instanceof TypeRef.SimpleTypeRef) {
            Identifier id = ((TypeRef.SimpleTypeRef) tr).getName();
            if (result.isFakePointer(id)) //if (id.equals(ident(result.config.runtime.pointerClass)))
            {
                return true;
            }
        }
        return false;
    }

    private void privatize(Declaration d) {
        List<Modifier> modifiers = new ArrayList<Modifier>(d.getModifiers());
        modifiers.remove(ModifierType.Public);
        modifiers.remove(ModifierType.Protected);
        modifiers.add(0, ModifierType.Private);
        d.setModifiers(modifiers);
    }

    static String trimAny(String s, String[] prefixes, String[] suffixes) {
        String l = s.toLowerCase();
        if (prefixes != null) {
            for (String prefix : prefixes) {
                if (l.startsWith(prefix.toLowerCase())) {
                    s = s.substring(prefix.length());
                    break;
                }
            }
        }
        if (suffixes != null) {
            for (String suffix : suffixes) {
                if (l.endsWith(suffix.toLowerCase())) {
                    s = s.substring(0, s.length() - suffix.length());
                    break;
                }
            }
        }
        return s;
    }

    public String reifyFunctionName(Result result, String fakePointerName, String functionName) {
        String simplifiedPointerName = StringUtils.trimUnderscores(trimAny(fakePointerName, result.config.libraryNamingPrefixes, null));
        List<String> prefs = new ArrayList<String>();
        if (result.config.libraryNamingPrefixes != null) {
            prefs.addAll(Arrays.asList(result.config.libraryNamingPrefixes));
        }
        prefs.add(fakePointerName);
        prefs.add(simplifiedPointerName);
        String s = StringUtils.uncapitalize(StringUtils.trimUnderscores(trimAny(functionName, prefs.toArray(new String[prefs.size()]), new String[]{
            simplifiedPointerName,
            simplifiedPointerName.replaceAll("_", "")
        })));
        if (s.length() == 0 || result.typeConverter.isJavaKeyword(s)) {
            return functionName;
        }
        return s;
    }

    public void reifyFakePointer(Struct ptClass, Identifier fullLibraryClassName, String fakePointerName, Signatures signatures) {
        Identifier resolvedFakePointer = result.getFakePointer(fullLibraryClassName, ident(fakePointerName));
        List<Pair<Identifier, Function>> functionsReifiableInFakePointers = result.getFunctionsReifiableInFakePointer(resolvedFakePointer);
        String thisFakePtrRefStr = typeRef(resolvedFakePointer).toString();

        if (functionsReifiableInFakePointers == null) {
            return;
        }

        for (Pair<Identifier, Function> p : functionsReifiableInFakePointers) {
            Function original = p.getSecond();
            Function fDirect = original.clone();

            int thisLocation = -1;
            List<Integer> fakePointersLocations = new ArrayList<Integer>();
            int iArg = 0;
            for (Arg arg : fDirect.getArgs()) {
                if (isFakePointerRef(result, arg.getValueType())) {
                    if (iArg == 0 && arg.getValueType().toString().equals(thisFakePtrRefStr)) {
                        thisLocation = iArg;
                    }
                    fakePointersLocations.add(iArg);
                    toDirectFakePointer(result, arg);
                }
                iArg++;
            }


            String indirectRetVarName = "$";
            boolean returnsFakePointer = isFakePointerRef(result, fDirect.getValueType());
            boolean needsDirect = !fakePointersLocations.isEmpty() || returnsFakePointer;

            Expression finalCall = null;
            //boolean needsDirect = fDirect.toString().equals(original.toString());
            String directFunctionName = null;
            if (needsDirect) {
                if (returnsFakePointer) {
                    finalCall = new Expression.New(fDirect.getValueType(), varRef(indirectRetVarName));
                    toDirectFakePointer(result, fDirect);
                }
                privatize(fDirect);
                if (signatures.addMethod(fDirect)) {
                    ((DeclarationsHolder) original.getParentElement()).addDeclaration(fDirect);
                }
                if (original.computeSignature(SignatureType.JavaStyle).equals(fDirect.computeSignature(SignatureType.JavaStyle))) {
                    fDirect.setName(ident(original.getName() + "$direct"));
                }
                directFunctionName = fDirect.getName().toString();
            }


            // TODO private
            // TODO -reify:ptrname
            // TODO -reify:function=name
            //

            Function f = original.clone();
            List<Arg> args = new ArrayList<Arg>(f.getArgs());
            f.setModifiers(Collections.EMPTY_LIST);
            f.addModifiers(ModifierType.Public);
            if (thisLocation < 0) {
                f.addModifiers(ModifierType.Static);
            }
            String functionName = f.getName().toString();
            f.setName(ident(reifyFunctionName(result, fakePointerName, functionName)));
            Identifier id = p.getFirst();
            List<Expression> followedArgs = new ArrayList<Expression>();
            //followedArgs.add(thisRef());

            iArg = 0;
            for (Arg arg : args) {
                if (iArg == thisLocation) {
                    followedArgs.add(methodCall(thisRef(), "getPeer"));
                } else if (fakePointersLocations.contains(iArg)) {
                    followedArgs.add(methodCall(varRef(arg.getName()), "getPeer"));
                } else {
                    followedArgs.add(varRef(arg.getName()));
                }
                iArg++;
            }
            if (thisLocation >= 0) {
                args.remove(thisLocation);
            }
            f.setArgs(args);

            Expression nlib = expr(typeRef(p.getFirst().clone()));//nativeLibFieldExpr.clone(); // expr(typeRef(id.clone()))
            Expression x = methodCall(nlib, needsDirect ? directFunctionName : functionName, followedArgs.toArray(new Expression[followedArgs.size()]));
            boolean retVoid = "void".equals(String.valueOf(f.getValueType()));
            if (retVoid) {
                f.setBody(block(stat(x)));
            } else if (needsDirect && finalCall != null) {
                VariablesDeclaration vd = new VariablesDeclaration(typeRef(long.class), new Declarator.DirectDeclarator(indirectRetVarName, x));
                Expression.ConditionalExpression ce = new Expression.ConditionalExpression();
                ce.setTest(expr(varRef(indirectRetVarName), Expression.BinaryOperator.IsEqual, expr(0)));
                ce.setThenValue(nullExpr());
                ce.setElseValue(finalCall);
                f.setBody(block(stat(vd), new Statement.Return(ce)));
            } else {
                f.setBody(block(new Statement.Return(x)));
            }
            ptClass.addDeclaration(f);
        }
    }
}
