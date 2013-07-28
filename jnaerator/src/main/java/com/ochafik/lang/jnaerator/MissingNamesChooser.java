/*	
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
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

import static com.ochafik.lang.SyntaxUtils.as;
import com.ochafik.lang.jnaerator.parser.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Scanner;
import java.util.*;

public class MissingNamesChooser extends Scanner {

    final boolean renameFunctionSignatures;

    public static boolean isNamedFunctionType(TypeRef tr) {
        if (!(tr instanceof FunctionSignature)) {
            return false;
        }

        FunctionSignature fs = (FunctionSignature) tr;
        Function f = fs.getFunction();
        return f != null && f.getName() != null;
    }

    public enum NameGenerationStyle {

        Java, PreserveCaseAndSeparateByUnderscores
    }
    NameGenerationStyle nameGenerationStyle = NameGenerationStyle.PreserveCaseAndSeparateByUnderscores;
    Result result;
    

    public MissingNamesChooser(Result result, boolean renameFunctionSignatures) {
        this.result = result;
        this.renameFunctionSignatures = renameFunctionSignatures;
    }
    
    protected boolean treatFunctionSignatureAsPointers() {
        return result.config.runtime.hasJNA;
    }

    public void setNameGenerationStyle(NameGenerationStyle nameGenerationStyle) {
        this.nameGenerationStyle = nameGenerationStyle;
    }

    public String chooseArgNameFromType(TypeRef tr) throws UnsupportedConversionException {
        if (tr instanceof TypeRef.SimpleTypeRef) {
            Identifier name = ((TypeRef.SimpleTypeRef) tr).getName();
            String out;
            if (isNull(name)) {
                out = StringUtils.implode(tr.getModifiers(), "");
                out = out.length() > 0 ? out.substring(0, 1) : out;
            } else {
                out = name.toString();
            }
            return out;
        } else if (tr instanceof TypeRef.Pointer) {
            return chooseArgNameFromType(((TypeRef.Pointer) tr).getTarget()) + "Ptr";
        } else if (tr instanceof TypeRef.ArrayRef) {
            return chooseArgNameFromType(((TypeRef.ArrayRef) tr).getTarget()) + "Arr";
        }
        throw new UnsupportedConversionException(tr, String.valueOf(tr));
    }

    @Override
    public void visitFunction(Function function) {

        switch (function.getType()) {
            case CFunction:
            case CppMethod:
                Set<String> names = new TreeSet<String>();
                List<Pair<Arg, Integer>> missing = new ArrayList<Pair<Arg, Integer>>();
                int i = 0;//, n = function.getArgs().size();

                for (Arg arg : function.getArgs()) {
                    String name = arg.getName();
                    if (name == null && !isNamedFunctionType(arg.getValueType())) {
                        missing.add(new Pair<Arg, Integer>(arg, i));
                    } else if (name != null) {
                        names.add(name);
                    }
                    i++;
                }
                for (Pair<Arg, Integer> p : missing) {
                    i = 1;
                    String base;
                    if (p.getFirst().isVarArg()) {
                        base = "varArgs";
                    } else {
                        try {
                            base = chooseArgNameFromType(p.getFirst().getValueType());
                        } catch (UnsupportedConversionException ex) {
                            base = "arg";
                        }
                    }
//					if (p.getFirst().getValueType() instanceof TypeRef.SimpleTypeRef)
//						base = ((TypeRef.SimpleTypeRef)p.getFirst().getValueType()).getName();
//					else
//						base = "arg";

                    //(n == 1 ? "" : p.getValue())
                    String name;
                    while (names.contains(name = base + i)) {
                        i++;
                    }
                    names.add(name);
                    p.getFirst().setName(name);
                }
                break;
        }

        super.visitFunction(function);
    }

    static boolean isNull(Identifier i) {
        return i == null || i.resolveLastSimpleIdentifier() == null || i.resolveLastSimpleIdentifier().getName() == null;
    }

    @Override
    public void visitFunctionSignature(FunctionSignature functionSignature) {
        Identifier origName = functionSignature.getFunction() == null ? null : functionSignature.getFunction().getName();
        Element parent = functionSignature.getParentElement();

        if (!renameFunctionSignatures) {
            super.visitFunctionSignature(functionSignature);
            if (functionSignature.getParentElement() instanceof Arg) {
                Arg arg = (Arg) functionSignature.getParentElement();
                if (arg.getName() == null) {
                    arg.setName(origName.toString());
                    functionSignature.getFunction().setName(null);
                }
            }
            return;
        }

        if (!chooseNameIfMissing(functionSignature)) {
            super.visitFunctionSignature(functionSignature);
        }
        
        if ((parent instanceof TypeDef) ||
            (parent instanceof TypeRef.Pointer) && (parent.getParentElement() instanceof TypeDef)) {
            return;
        }

        DeclarationsHolder holder = functionSignature.findParentOfType(DeclarationsHolder.class);
        Function f = functionSignature.getFunction();
        if (holder != null && f != null && !isNull(f.getName())) {
            Identifier fnameClone = f.getName().clone();
            StoredDeclarations d = as(parent, StoredDeclarations.class);
            
            if (d != null && d.getDeclarators().isEmpty()) {
                if (d instanceof VariablesDeclaration) {
                    VariablesDeclaration pvd = (VariablesDeclaration) d;
                    pvd.addDeclarator(new DirectDeclarator((origName == null ? fnameClone : origName).toString()));
                    functionSignature.replaceBy(functionSignatureRef(fnameClone));
                } else {
                    d.replaceBy(null); // special case of C++-like struct sub-type definition 
                }
            } else {
                functionSignature.replaceBy(functionSignatureRef(fnameClone));
            }
            TypeDef td = new TypeDef();
            td.importDetails(functionSignature, true);
            td.setValueType(functionSignature);
            td.addDeclarator(new DirectDeclarator(fnameClone.toString()));
            holder.addDeclaration(td);
            td.accept(this);
        }
    }
    
    TypeRef functionSignatureRef(Identifier funSigName) {
        TypeRef.SimpleTypeRef tr = new TypeRef.SimpleTypeRef(funSigName.clone());
        return tr;//treatFunctionSignatureAsPointers() ? tr : new TypeRef.Pointer(tr, Declarator.PointerStyle.Pointer);
        //result.typeConverter.pointerTypeRef(tr);
    }

    static boolean isUnnamed(TaggedTypeRefDeclaration d) {
        return d != null && d.getTaggedTypeRef() != null && isNull(d.getTaggedTypeRef().getTag());
    }

    Set<Identifier> listChildIdentifiers(DeclarationsHolder h) {
        List<Identifier> list = new ArrayList<Identifier>();
        for (Declaration d : h.getDeclarations()) {
            if (d instanceof Function) {
                list.add(((Function) d).getName());
            } else if (d instanceof TaggedTypeRefDeclaration) {
                TaggedTypeRefDeclaration td = (TaggedTypeRefDeclaration) d;
                TaggedTypeRef tr = td.getTaggedTypeRef();
                if (tr != null) {
                    list.add(tr.getTag());
                }
            } else if (d instanceof VariablesDeclaration) {
                for (Declarator dc : ((VariablesDeclaration) d).getDeclarators()) {
                    list.add(ident(dc.resolveName()));
                }
            }
        }
        Set<Identifier> ret = new HashSet<Identifier>();
        for (Identifier i : list) {
            if (i != null) {
                ret.add(i);
            }
        }
        return ret;
    }

    List<TaggedTypeRefDeclaration> getUnnamedTaggedTypeRefs(List<Declaration> ds) {
        List<TaggedTypeRefDeclaration> ret = new ArrayList<TaggedTypeRefDeclaration>();
        for (Declaration d : ds) {
            if (d instanceof TaggedTypeRefDeclaration) {
                TaggedTypeRefDeclaration td = (TaggedTypeRefDeclaration) d;
                TaggedTypeRef tr = td.getTaggedTypeRef();
                if (tr != null && tr.getTag() == null) {
                    ret.add(td);
                }
            }
        }
        return ret;
    }

    @Override
    public void visitStruct(Struct struct) {
        fixUnNamedChildren(struct);
        super.visitStruct(struct);
    }

    //http://stackoverflow.com/questions/2503183/jnaerator-unnamed-union-missing-in-structure
    private void fixUnNamedChildren(Struct struct) {
        List<TaggedTypeRefDeclaration> trs = getUnnamedTaggedTypeRefs(struct.getDeclarations());
        if (trs.isEmpty()) {
            return;
        }

        Set<Identifier> ids = listChildIdentifiers(struct);
        for (TaggedTypeRefDeclaration td : trs) {
            TaggedTypeRef tr = td.getTaggedTypeRef();
            if (!(tr instanceof Struct)) {
                continue;
            }

            Struct s = (Struct) tr;
            switch (s.getType()) {
                case CStruct:
                case CUnion:
                    String n = chooseNameSuffix(tr);
                    int i = 1;
                    Identifier fieldName;
                    while (!ids.add(fieldName = ident("field" + i))) {
                        i++;
                    }

                    //tr.setTag(idTag);
                    td.replaceBy(new VariablesDeclaration(tr, new Declarator.DirectDeclarator(fieldName.toString())));
                    break;
            }
        }
    }

    @Override
    public void visitTaggedTypeRef(TaggedTypeRef taggedTypeRef) {
        super.visitTaggedTypeRef(taggedTypeRef);

        chooseNameIfMissing(taggedTypeRef);
        //	return;

        Element parent = taggedTypeRef.getParentElement();

        if (!(parent instanceof TaggedTypeRefDeclaration) && !(parent instanceof TypeDef)) {
            DeclarationsHolder holder = taggedTypeRef.findParentOfType(DeclarationsHolder.class);
            if (holder != null && holder != taggedTypeRef.getParentElement() && !(parent instanceof DeclarationsHolder)) {
                TaggedTypeRefDeclaration td = new TaggedTypeRefDeclaration();
                if (parent instanceof VariablesDeclaration && ((VariablesDeclaration) parent).getDeclarators().isEmpty()) {
                    taggedTypeRef.importDetails(parent, false);
                    parent.replaceBy(null);
                } else {
                    TypeRef tr = new TypeRef.SimpleTypeRef(taggedTypeRef.getTag().clone());
                    for (Modifier mod : taggedTypeRef.getModifiers()) {
                        if (mod.isA(ModifierKind.StorageClassSpecifier)) {
                            tr.addModifiers(mod);
                        }
                    }
                    taggedTypeRef.replaceBy(tr);
                    if (taggedTypeRef instanceof Struct) {
                        tr.setMarkedAsResolved(true);
                    }
                }

                td.setTaggedTypeRef(taggedTypeRef);
                holder.addDeclaration(td);
                //td.accept(this);
            }
        }
//		
//		super.visitTaggedTypeRef(taggedTypeRef);
//
//		Element parent = taggedTypeRef.getParentElement();
//		boolean unnamed = 
//			(
//				parent instanceof TaggedTypeRefDeclaration && isUnnamed((TaggedTypeRefDeclaration)parent) ||
//				parent instanceof VariablesDeclaration && ((VariablesDeclaration)parent).getDeclarators().isEmpty()
//			) && 
//			parent.getParentElement() instanceof Struct
//		;
//		// Support (non-standard) unnamed structs and unions : http://www.redhat.com/docs/manuals/enterprise/RHEL-4-Manual/gcc/unnamed-fields.html
//		if (unnamed) {
//			String type = null;
//			if (taggedTypeRef instanceof Struct) {
//				switch (((Struct)taggedTypeRef).getType()) {
//				case CStruct:
//					type = "Struct";
//					break;
//				case CUnion:
//					type = "Union";
//					break;
//				case CPPClass:
//					type = "Class";
//					break;
//				}
//			}
//			if (type != null) {
////				taggedTypeRef.setParentElement(null);
//				
//				VariablesDeclaration vd = new VariablesDeclaration();
//				vd.setValueType(taggedTypeRef);
//				String pref;
//				if (!isNull(taggedTypeRef.getTag())) {
//					pref = taggedTypeRef.getTag() + "Field";
//					pref = pref.substring(0, 1).toLowerCase() + pref.substring(1);
//				} else {
//					pref = "unnamed" + type;
//					int unnamedId = getNextUnnamedId(pref);
//					if (taggedTypeRef.getTag() == null)
//						taggedTypeRef.setTag(ident("Unnamed" + type + unnamedId));
//				}
//				vd.addDeclarator(new DirectDeclarator(pref + getNextUnnamedId(pref)));
////				taggedTypeRef.accept(this);
//				parent.replaceBy(vd);
////				vd.accept(this);
////				return;
//			}
//		}
//		chooseNameIfMissing(taggedTypeRef);
//		
////		Element parent = taggedTypeRef.getParentElement(); 
//		
//		if (!(parent instanceof TaggedTypeRefDeclaration) && !(parent instanceof TypeDef)) 
//		{
//			DeclarationsHolder holder = taggedTypeRef.findParentOfType(DeclarationsHolder.class);
//			if (holder != null && holder != taggedTypeRef.getParentElement() && !(parent instanceof DeclarationsHolder)) {
//				TaggedTypeRefDeclaration td = new TaggedTypeRefDeclaration();
//				if (unnamed) {
//					String type = null;
//					if (taggedTypeRef instanceof Struct) {
//						switch (((Struct)taggedTypeRef).getType()) {
//						case CStruct:
//							type = "Struct";
//							break;
//						case CUnion:
//							type = "Union";
//							break;
//						case CPPClass:
//							type = "Class";
//							break;
//						}
//					}
//					if (type == null) {
//						taggedTypeRef.importDetails(parent, false);
//						parent.replaceBy(null);
//					} else {
////				} else if (unnamed && parent instanceof VariablesDeclaration && ((VariablesDeclaration)parent).getDeclarators().isEmpty()) {
////					
////					taggedTypeRef.importDetails(parent, false);
////					parent.replaceBy(null);
//				
//						VariablesDeclaration vd = new VariablesDeclaration();
//						vd.setValueType(taggedTypeRef);
//						String pref;
//						if (!isNull(taggedTypeRef.getTag())) {
//							pref = taggedTypeRef.getTag() + "Field";
//							pref = pref.substring(0, 1).toLowerCase() + pref.substring(1);
//						} else {
//							pref = "unnamed" + type;
//							int unnamedId = getNextUnnamedId(pref);
//							if (taggedTypeRef.getTag() == null)
//								taggedTypeRef.setTag(ident("Unnamed" + type + unnamedId));
//						}
//						vd.addDeclarator(new DirectDeclarator(pref + getNextUnnamedId(pref)));
////						taggedTypeRef.accept(this);
//						parent.replaceBy(vd);
//						vd.accept(this);
//						return;
//					}
//					
//				} else {
//					TypeRef tr = new TypeRef.SimpleTypeRef(taggedTypeRef.getTag().clone());
//					for (Modifier mod : taggedTypeRef.getModifiers()) {
//						if (mod.isA(ModifierKind.StorageClassSpecifier))
//							tr.addModifiers(mod);
//					}
//					taggedTypeRef.replaceBy(tr);
//					if (taggedTypeRef instanceof Struct)
//						tr.setMarkedAsResolved(true);
//				}
//				
//		 		td.setTaggedTypeRef(taggedTypeRef);
//		 		holder.addDeclaration(td);
//				td.accept(this);
//			}
//		}
    }

    /**
     * @return true if the functionSignature changed and triggerered
     * revisitation
     */
    private boolean chooseNameIfMissing(FunctionSignature functionSignature) {
        Function function = functionSignature.getFunction();
        Element parent = functionSignature.getParentElement();
        if (function != null && (isNull(function.getName()) || parent instanceof VariablesDeclaration || parent instanceof Arg)) {
            String name = null;
            String exact = JNAeratorUtils.getExactTypeDefName(functionSignature);
            if (exact != null) {
                name = exact;
            } else {
                List<String> ownerNames = JNAeratorUtils.guessOwnerName(function);
                if (function.getName() != null) {
                    ownerNames.add(function.getName().toString());
                }
                name = chooseName(functionSignature, ownerNames, true);
            }
            if (name != null) {
                function.setName(ident(name));
                function.accept(this);
                return true;
            }
        }
        return false;
    }
    Map<String, Integer> nextUnnamedId = new HashMap<String, Integer>();

    /**
     * @return true if changed and revisited on change results (caller can give
     * up)
     */
    private boolean chooseNameIfMissing(TaggedTypeRef taggedTypeRef) {
//		String tag = taggedTypeRef.getTag(); 
//		taggedTypeRef.setTag(result.declarationsConverter.getActualTaggedTypeName(taggedTypeRef));
//		if (!SyntaxUtils.equal(tag, taggedTypeRef.getTag())) {
//			taggedTypeRef.accept(this);
//			return true;
//		}
        //String betterTag = result.declarationsConverter.getActualTaggedTypeName(taggedTypeRef);
        if (isNull(taggedTypeRef.getTag()) && !(taggedTypeRef.getParentElement() instanceof TaggedTypeRefDeclaration)) {
            Identifier tag = result.declarationsConverter.getActualTaggedTypeName(taggedTypeRef);
            if (isNull(tag)) {
                List<String> ownerNames = JNAeratorUtils.guessOwnerName(taggedTypeRef);//.getParentElement() instanceof StructTypeRef ? struct.getParentElement() : struct);
                tag = ident(chooseName(taggedTypeRef, ownerNames, true));
            }

            if (!isNull(tag)) {
                taggedTypeRef.setTag(tag.clone());
//				taggedTypeRef.accept(this);
                return true;
            }
        }
        return false;
    }

    private int getNextUnnamedId(String type) {

        Integer i = nextUnnamedId.get(type);
        int unnamedId;
        if (i == null) {
            unnamedId = 1;
        } else {
            unnamedId = i;
        }

        nextUnnamedId.put(type, unnamedId + 1);
        return unnamedId;
    }
    int nextAnonymous = 1;

    public String chooseName(Element e, List<String> ownerNames, boolean isType) {
        String s = chooseNameSuffix(e);
        if (s == null) {
            return null;
        }

        String n;

        List<String> names = new ArrayList<String>();
        if (ownerNames != null) {
            names.addAll(ownerNames);
        }
        if (ownerNames.isEmpty()) {
            n = s + (nextAnonymous++);
        } else {
            names.add(s);
            switch (nameGenerationStyle) {
                case Java:
                    n = StringUtils.capitalize(ownerNames, "");
                    break;
                case PreserveCaseAndSeparateByUnderscores:
                    n = StringUtils.implode(names, "_");
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown name generation style " + nameGenerationStyle);
            }
        }

        if (result.config.beautifyNames) {
            n = result.typeConverter.beautify(n, isType);
        }
        return n;
    }

    public String chooseNameSuffix(Element e) {
        if (e instanceof Struct) {
            Struct struct = (Struct) e;
            if (struct.getType() == Struct.Type.CStruct) {
                return "struct";
            } else if (struct.getType() == Struct.Type.CUnion) {
                return "union";
            }
        } else if (e instanceof Enum) {
            return "enum";
        } else if (e instanceof FunctionSignature) {
            return "callback";
        }
        return null;
    }

    static <T extends Element> T importDetails(T t, Element e, boolean move) {
        t.importDetails(e, move);
        return t;
    }
}