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
package com.ochafik.lang.jnaerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

//import org.rococoa.cocoa.foundation.FoundationLibrary;
//import org.rococoa.cocoa.foundation.NSClass;
//import org.rococoa.cocoa.foundation.NSInvocation;
//import org.rococoa.cocoa.foundation.NSMethodSignature;
import org.rococoa.cocoa.foundation.NSObject;
//import org.rococoa.cocoa.foundation.NSString;

import com.ochafik.lang.jnaerator.JNAerator.Feedback;
import com.ochafik.lang.jnaerator.TypeConversion.JavaPrim;
import com.ochafik.lang.jnaerator.parser.Define;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.SourceFile;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.Enum.EnumItem;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Printer;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
import com.ochafik.util.SystemUtils;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import java.util.LinkedHashMap;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.TaggedTypeRefDeclaration;

public class Result extends Scanner {

	public final JNAeratorConfig config;
	public final Feedback feedback;
    public final ClassOutputter classOutputter;
    
	public TypeConversion typeConverter;
	public DeclarationsConverter declarationsConverter;
	public GlobalsGenerator globalsGenerator;
	public ObjectiveCGenerator objectiveCGenerator;
	public UniversalReconciliator universalReconciliator;
    public Reifier reifier;
    public BridJer bridjer;
    public Symbols symbols;
    public boolean hasCPlusPlus;
	
	public final Set<Identifier> 
		structsFullNames = new HashSet<Identifier>(),
		enumsFullNames = new HashSet<Identifier>(),
		unionsFullNames = new HashSet<Identifier>(),
		callbacksFullNames = new HashSet<Identifier>();

	/**
	 * @param config
	 * @param classOutputter
	 * @param feedback
	 */
	public Result(JNAeratorConfig config, ClassOutputter classOutputter, Feedback feedback) {
		if (config == null)
			throw new IllegalArgumentException("No config in result !");
		this.config = config;
		this.classOutputter = classOutputter;
        this.feedback = feedback;

        init();
    }
    public void init() {
        if (config.runtime == JNAeratorConfig.Runtime.BridJ) {
            declarationsConverter =  new BridJDeclarationsConverter(this);
            globalsGenerator = new BridJGlobalsGenerator(this);
            typeConverter = new BridJTypeConversion(this);
		
        } else {
            declarationsConverter = new JNADeclarationsConverter(this);
            globalsGenerator = new JNAGlobalsGenerator(this);
            typeConverter = new JNATypeConversion(this);
		
        }
        reifier = new Reifier(this);
		objectiveCGenerator = new ObjectiveCGenerator(this);
		universalReconciliator = new UniversalReconciliator();
        bridjer = new BridJer(this);
	}

	Set<Identifier> javaPackages = new TreeSet<Identifier>();
	
	//Map<Identifier, ObjCClass> objCClasses = new HashMap<Identifier, ObjCClass>();
	Map<Struct.Type, Map<Identifier, Struct>> classes = new TreeMap<Struct.Type, Map<Identifier, Struct>>();
	Map<Identifier, Map<String, Struct>> objCCategoriesByTargetType = new HashMap<Identifier, Map<String,Struct>>();
	Map<String, Struct> objCCategoriesByName = new HashMap<String, Struct>();
	
	Map<String, Map<String, String>> stringConstants = new HashMap<String, Map<String,String>>();
	Map<String, Map<String, Boolean>> retainedRetValFunctions = new HashMap<String, Map<String,Boolean>>();
	
	//Set<String> 
		//cStructNames = new HashSet<String>(), 
		//enumNames = new HashSet<String>();
	
	public Map<String, List<String>> missingPointersByUsingLibrary = new HashMap<String, List<String>>();
 	public Map<String, List<Struct>> structsByLibrary = new HashMap<String, List<Struct>>();
	public Map<String, List<FunctionSignature>> callbacksByLibrary = new HashMap<String, List<FunctionSignature>>();
	
	public Map<String, List<VariablesDeclaration>> globalsByLibrary = new HashMap<String, List<VariablesDeclaration>>();
	public Map<Identifier, VariablesDeclaration> globalVariablesByName = new HashMap<Identifier, VariablesDeclaration>();
	
	public Map<Identifier, Struct> structsByName = new HashMap<Identifier, Struct>();
	public Map<Identifier, FunctionSignature> callbacksByName = new HashMap<Identifier, FunctionSignature>();
	
	public Map<String, List<Enum>> enumsByLibrary = new HashMap<String, List<Enum>>();
	public Map<Identifier, Enum> enumsByName = new HashMap<Identifier, Enum>();
	public Map<String, EnumItem> enumItems = new HashMap<String, EnumItem>();
	public Map<String, Define> defines = new LinkedHashMap<String, Define>();
	public Map<String, List<Define>> definesByLibrary = new HashMap<String, List<Define>>();
	public Map<String, Set<String>> fakePointersByLibrary = new HashMap<String, Set<String>>();
    public Map<String, Set<String>> undefinedTypesByLibrary = new HashMap<String, Set<String>>();
	
	public Map<String, List<Function>> functionsByLibrary = new HashMap<String, List<Function>>();
	//Map<String, Expression> defines = new LinkedHashMap<String, Expression>();
	public Map<Identifier, Signatures> signaturesByOutputClass = new HashMap<Identifier, Signatures>();
	
	public Map<String, Pair<TypeDef, Declarator>> typeDefs = new HashMap<String, Pair<TypeDef, Declarator>>();
	
	static <T> List<T> getList(Map<String, List<T>> m, String key) {
		List<T> list = m.get(key);
		if (list == null)
			m.put(key, list = new ArrayList<T>());
		return list;
	}
	static <T, U, V> Map<U, V> getMap(Map<T, Map<U, V>> m, T key) {
		Map<U, V> map = m.get(key);
		if (map == null)
			m.put(key, map = new LinkedHashMap<U, V>());
		return map;
	}
	
	public Identifier findFakePointer(Identifier name) {
		name = getFakePointerName(name);
		if (name == null)
			return null;
		String s = name.toString();
		for (Map.Entry<String, Set<String>> e : fakePointersByLibrary.entrySet()) {
			if (e.getValue().contains(s))
				return ident(ident(e.getKey()), name);
		}
		return null;
	}
    public Identifier findUndefinedType(Identifier name) {
		name = getUndefinedTypeName(name);
		if (name == null)
			return null;
		String s = name.toString();
		for (Map.Entry<String, Set<String>> e : undefinedTypesByLibrary.entrySet()) {
			if (e.getValue().contains(s))
				return ident(ident(e.getKey()), name);
		}
		return null;
	}
    public boolean isFakePointer(Identifier id) {
        return resolvedFakePointers.contains(id);
    }
    public boolean isUndefinedType(Identifier id) {
        return resolvedUndefinedTypes.contains(id);
    }
    public boolean isUndefinedType(TypeRef tpe) {
        return tpe instanceof SimpleTypeRef && isUndefinedType(((SimpleTypeRef)tpe).getName());
    }

    Map<Identifier, List<Pair<Identifier, Function>>> functionsReifiableInFakePointers = new HashMap<Identifier, List<Pair<Identifier, Function>>>();
    Set<Identifier> resolvedFakePointers = new HashSet<Identifier>();
    Set<Identifier> resolvedUndefinedTypes = new HashSet<Identifier>();
	public Identifier getFakePointer(Identifier libraryToUseIfNotDefinedYet, Identifier name) {
		Identifier lib = findFakePointer(name);
		if (lib != null)
			return lib;
		name = getFakePointerName(name);
		Set<String> set = fakePointersByLibrary.get(libraryToUseIfNotDefinedYet);
		if (set == null && libraryToUseIfNotDefinedYet != null)
			fakePointersByLibrary.put(libraryToUseIfNotDefinedYet.toString(), set = new HashSet<String>());
		set.add(name.toString());
		Identifier id = libraryToUseIfNotDefinedYet == null ? ident(name) : ident(libraryToUseIfNotDefinedYet, name);
        resolvedFakePointers.add(id);
        return id;
	}
	public Identifier getUndefinedType(Identifier libraryToUseIfNotDefinedYet, Identifier name) {
		Identifier lib = findUndefinedType(name);
		if (lib != null)
			return lib;
		name = getUndefinedTypeName(name);
		Set<String> set = undefinedTypesByLibrary.get(libraryToUseIfNotDefinedYet);
		if (set == null)
			undefinedTypesByLibrary.put(libraryToUseIfNotDefinedYet.toString(), set = new HashSet<String>());
		set.add(name.toString());
		Identifier id = ident(libraryToUseIfNotDefinedYet, name);
        resolvedUndefinedTypes.add(id);
        return id;
	}
	private Identifier getFakePointerName(Identifier name) {

		String nameStr = name == null ? null : name.toString();
		String trimmed = StringUtils.trimUnderscores(nameStr);
		if (trimmed != null && !nameStr.equals(trimmed)) {
			String nicerName = trimmed;
			Pair<TypeDef, Declarator> pair = typeDefs.get(nicerName);
			if (pair != null) {
				TypeRef targetType = pair.getFirst().getValueType();
				String target = targetType.toString();
				if (target.equals(nameStr+"*") || target.equals(nameStr))
					name = ident(nameStr = nicerName);
			}
		}
		return name;
	}
	private Identifier getUndefinedTypeName(Identifier name) {

		String nameStr = name == null ? null : name.toString();
		String trimmed = StringUtils.trimUnderscores(nameStr);
		if (trimmed != null && !nameStr.equals(trimmed)) {
			String nicerName = trimmed;
			Pair<TypeDef, Declarator> pair = typeDefs.get(nicerName);
			if (pair != null) {
				String target = pair.getFirst().getValueType().toString();
				if (target.equals(nameStr))// || target.equals(nameStr+"*"))
					name = ident(nameStr = nicerName);
			}
		}
		return name;
	}
	public Signatures getSignaturesForOutputClass(Identifier name) {
		Signatures s = signaturesByOutputClass.get(name);
		if (s == null)
			signaturesByOutputClass.put(name, s = new Signatures());
		return s;
	}
	/*
	public void addDefine(String name, Expression ex) {
		defines.put(name, ex);
		String library = getLibrary(ex);
		if (library.equals(""))
			library = library.toString();

		getList(definesByLibrary, getLibrary(ex)).add(new Pair<String, Expression>(name, ex));
	}*/
	
	@Override
	public void visitDefine(Define define) {
		super.visitDefine(define);
		defines.put(define.getName(), define);
		getList(definesByLibrary, getLibrary(define)).add(define);
	}
	
	
	@Override
	public void visitEnum(Enum e) {
		super.visitEnum(e);
		if (e.getTag() == null) {
			// Hack to infer the enum name from the next typedef NSUInteger NSSomethingThatLooksLikeTheEnumsIdentifiers
			Element nextDeclaration = e.getNextSibling();
			if (nextDeclaration != null && (nextDeclaration instanceof TypeDef)) {
				TypeDef typeDef = (TypeDef) nextDeclaration;
				TypeRef type = typeDef.getValueType();
				if (type instanceof TypeRef.SimpleTypeRef) {
					String simpleTypeStr = ((TypeRef.SimpleTypeRef)type).getName().toString();
					if (simpleTypeStr.equals("NSUInteger") || 
							simpleTypeStr.equals("NSInteger") ||
							simpleTypeStr.equals("CFIndex")) 
					{
						Declarator bestPlainStorage = null;
						for (Declarator st : typeDef.getDeclarators()) {
							if (st instanceof DirectDeclarator) {
								String name = st.resolveName();
								boolean niceName = StringUtils.trimUnderscores(name).equals(name);;
								if (bestPlainStorage == null || niceName) {
									bestPlainStorage = st;
									if (niceName)
										break;
								}
							}
						}
						if (bestPlainStorage != null) {
							String name = bestPlainStorage.resolveName();
							System.err.println("Automatic struct name matching : " + name);
							e.setTag(ident(name));
						}
					}
				}
			}
		}

        Identifier name = e.getTag();
        String lib = getLibrary(e);
		if (name == null)
        		getList(enumsByLibrary, lib).add(e);
        	else {
			Enum oldEnum = enumsByName.get(name);
	
			if (declarativePower(e) > declarativePower(oldEnum)) {
				enumsByName.put(name, e);
                e.setResolvedJavaIdentifier(typeConverter.computeTaggedTypeIdentifierInJava(e));
	
				//if (e.getTag() != null) {
				//	enumsByName.put(e.getTag(), e);
				//}
				getList(enumsByLibrary, lib).add(e);
	
				Identifier identifier = typeConverter.getTaggedTypeIdentifierInJava(e);
				if (identifier != null) {
					enumsFullNames.add(identifier);
				}
			}
		}
	}
    
    static int declarativePower(TaggedTypeRef e) {
        if (e == null)
            return 0;
        Element p = e.getParentElement();
        if (p instanceof TypeDef)
            return 4;
        if (p instanceof TaggedTypeRefDeclaration)
            return 3;
        if (e.isForwardDeclaration())
            return 2;
        return 1;
    }
	
	@Override
	public void visitEnumItem(EnumItem enumItem) {
		super.visitEnumItem(enumItem);
		enumItems.put(enumItem.getName(), enumItem);
		
		String library = getLibrary(enumItem);
		if (library == null)
			return;
		
		Element parent = enumItem.getParentElement();
		if (parent == null || !(parent instanceof Enum))
			return;
		
		Enum e = (Enum)parent;
		Identifier ident = ident(getLibraryClassFullName(library), declarationsConverter.getActualTaggedTypeName(e), ident(enumItem.getName()));
		enumItemsFullName.add(ident);
	}
	

	@Override
	public void visitVariablesDeclaration(VariablesDeclaration v) {
		super.visitVariablesDeclaration(v);
		if (v.findParentOfTypes(Struct.class, Function.class, Enum.class) != null)
			return;

		for (Declarator d : v.getDeclarators())
			globalVariablesByName.put(ident(d.resolveName()), v);
		getList(globalsByLibrary, getLibrary(v)).add(v);
	}
	@Override
	public void visitTypeDef(TypeDef typeDef) {
		super.visitTypeDef(typeDef);
		for (Declarator vs : typeDef.getDeclarators())
			typeDefs.put(vs.resolveName(), new Pair<TypeDef, Declarator>(typeDef, vs));
	}
	
    private static Pattern frameworkPathPattern = Pattern.compile(".*/(\\w+)\\.framework/(?:.*/)?Headers/(?:.*/)?([^/]+)\\.[^/.]+$");
	private static Pattern bridgesupportFrameworkPattern = Pattern.compile("(?:^|/)(\\w+?)(?:Full)?\\.bridgesupport$");
	
    String guessFramework(String file) {
        Matcher matcher = frameworkPathPattern.matcher(file);
        if (matcher.find() || (matcher = bridgesupportFrameworkPattern.matcher(file)).find()) {
            return matcher.group(1);
        }
        return null;
    }
	String getLibrary(Element decl) {
		String file = resolveFile(decl);
        String library = config.getLibrary(file);
        if (library == null) {
            SourceFile f = decl.findParentOfType(SourceFile.class);
            if (f != null) {
                //library = config.getLibrary(f.getElementFile());
                //if (library == null) {
                    library = guessFramework(file);
                    if (library == null)
                        library = f.getLibrary();
                //}
            }
        }
		return library;
	}
	
	public String resolveFile(Element e) {
		String file = null;
		while (e != null && (file = e.getElementFile()) == null) {
			e = e.getParentElement();
		}
		return file;
	}
	@Override
	public void visitFunction(Function function) {
		super.visitFunction(function);
		Element parent = function.getParentElement();
		if (parent != null) {
			if (parent instanceof FunctionSignature)
				return;
			if (parent instanceof Struct) {
				Struct parentStruct = (Struct)parent;
				switch (parentStruct.getType()) {
					case CPPClass:
//						if (config.genCPlusPlus)
//							break;
					case JavaClass:
					case JavaInterface:
					case ObjCClass:
					case ObjCProtocol:
					case CStruct:
						return;
				}
			}
		}
		
		getList(functionsByLibrary, getLibrary(function)).add(function);
	}
	public Expression getLibraryInstanceReferenceExpression(String libraryName) {
		Identifier hub = getHubFullClassName();
		Identifier classIdent;
		String fieldName;
		if (hub != null) {
			classIdent = hub;
			fieldName = libraryName;
		} else {
			classIdent = getLibraryClassFullName(libraryName);
			fieldName = "INSTANCE";
		}
		return memberRef(expr(typeRef(classIdent)), MemberRefStyle.Dot, fieldName);
	}
	public Identifier getHubFullClassName() {
		return config.entryName == null ? null : ident(config.entryName.toLowerCase(), config.entryName);
	}
    
	

	@Override
	public void visitStruct(Struct struct) {
		Identifier name = struct.getTag();
		if (name != null) {
			switch (struct.getType()) {
			case CPPClass:
				if (!config.runtime.equals(JNAeratorConfig.Runtime.BridJ) && !config.genCPlusPlus)
					break;
			case CStruct:
			case CUnion:
                
                boolean isFwd = struct.isForwardDeclaration();
                if (!isFwd && struct.getDeclarations().isEmpty() && config.treatEmptyStructsAsForwardDecls) {
                    List<SimpleTypeRef> parents = struct.getParents();
                    Struct p;
                    if (parents.isEmpty() || parents.size() == 1 && ((p = structsByName.get(parents.get(0))) == null || p.isForwardDeclaration()))
                        isFwd = true;
                }
                
				if (isFwd)
					break;
				
				if (config.skipIncludedFrameworks) {
					String lib = getLibrary(struct);
					if (lib != null) {
						if (!config.frameworks.contains(lib))
							break;
					}
				}
				Struct oldStruct = structsByName.get(name);
				
				if (declarativePower(struct) > declarativePower(oldStruct)) {
					structsByName.put(name, struct);

                    Identifier resolvedJavaIdentifier = typeConverter.computeTaggedTypeIdentifierInJava(struct);
                    struct.setResolvedJavaIdentifier(resolvedJavaIdentifier);
                    
                    if (struct.findParentOfType(Struct.class) == null)
                        getList(structsByLibrary, getLibrary(struct)).add(struct);
                    Identifier identifier = typeConverter.getTaggedTypeIdentifierInJava(struct);
                    if (identifier != null) {
                        if (struct.getType() == Struct.Type.CUnion)
                            unionsFullNames.add(identifier);
                        structsFullNames.add(identifier);
                    }
                }
				
				break;
			case ObjCClass:
			case ObjCProtocol:
//				if (name.equals("NSObject"))
//					name = name.clone();
				
				if (struct.isForwardDeclaration())
					break;
				
				if (struct.getCategoryName() != null) {
					getMap(objCCategoriesByTargetType, struct.getTag()).put(struct.getCategoryName(), struct);
					objCCategoriesByName.put(struct.getCategoryName(), struct);
				} else
					getMap(classes, struct.getType()).put(struct.getTag(), struct);
				
				break;
			default:
				struct = null;
			}
		}
        super.visitStruct(struct);
	}
	
	@Override
	public void visitFunctionSignature(FunctionSignature functionSignature) {
		super.visitFunctionSignature(functionSignature);
		Function function = functionSignature.getFunction();
		Identifier name = typeConverter.inferCallBackName(functionSignature, false, false, null);
        Identifier identifier = typeConverter.computeCallbackIdentifierInJava(functionSignature);
        functionSignature.setResolvedJavaIdentifier(identifier);
        if (function != null) {
            if (functionSignature.findParentOfType(Struct.class) == null) {
                getList(callbacksByLibrary, getLibrary(functionSignature)).add(functionSignature);
            }
			if (name != null) {
				callbacksByName.put(name, functionSignature);
				if (identifier != null)
					callbacksFullNames.add(identifier);
				
			}
		}
	}
    
    
	static String camelCase(String name) {
		StringBuilder out = new StringBuilder();
		for (String s : name.split("[^\\w]+")) {
			String t = s.trim();
			if (t.length() > 0)
				out.append(StringUtils.capitalize(t));
		}
		return out.toString();
	}
	public Identifier getLibraryClassSimpleName(String library) {
		return ident(camelCase(library) + "Library");
	}
	public Identifier getLibraryClassFullName(String library) {
		return ident(getLibraryPackage(library), getLibraryClassSimpleName(library));
	}
	public String getLibraryFileExpression(String library) {
        Class<?> platformClass = config.runtime.hasJNA ? com.sun.jna.Platform.class : SystemUtils.class;
		if (library.equals("c"))
			return "(" + platformClass.getName() + ".isWindows() ? \"msvcrt\" : \"c\")";
		
		return "\"" + library + "\"";
	}

	Set<String> libraries = new HashSet<String>();
	Map<String, Identifier> javaPackageByLibrary = new HashMap<String, Identifier>();
	public Set<Identifier> enumItemsFullName = new HashSet<Identifier>();
	
	public Identifier getLibraryPackage(String library) {
		if (library == null)
			return null;
		return config.packageName == null ? 
				ident(ident(config.rootPackageName), library.toLowerCase()) : 
				ident(config.packageName);
	}
	public void chooseLibraryClasses(String packageName, String rootPackageName) {
		libraries.clear();
		javaPackages.clear();
		javaPackageByLibrary.clear();
		
		libraries.addAll(structsByLibrary.keySet());
		libraries.addAll(callbacksByLibrary.keySet());
		libraries.addAll(functionsByLibrary.keySet());
		libraries.addAll(enumsByLibrary.keySet());
		libraries.addAll(globalsByLibrary.keySet());
		libraries.addAll(definesByLibrary.keySet());
		libraries.addAll(stringConstants.keySet());
		
		for (String library : libraries) {
			Identifier javaPackage = getLibraryPackage(library);
			if (javaPackage != null)
				javaPackageByLibrary.put(library, javaPackage);
		}
		javaPackages.addAll(javaPackageByLibrary.values());
		
	}
	public Struct resolveObjCClass(Identifier name) {
        if (name == null)
            return null;
		Struct s = getMap(classes, Struct.Type.ObjCClass).get(name);
		if (s == null)
			s = getMap(classes, Struct.Type.ObjCProtocol).get(name);
		if (s == null)
			s = objCCategoriesByName.get(name.toString());
		return s;
	}

	Class<?>[] overwrittenClassesThatNeedToKeepAllTheirMethods = new Class[] {
		NSObject.class, 
		//NSClass.class,
		//NSMethodSignature.class,
		//NSInvocation.class,
		//FoundationLibrary.class
	};

    public void addFunctionReifiableInFakePointer(Identifier resolvedFakePointer, Identifier libraryClassName, Function f) {
        List<Pair<Identifier, Function>> list = functionsReifiableInFakePointers.get(resolvedFakePointer);
        if (list == null)
            functionsReifiableInFakePointers.put(resolvedFakePointer, list = new ArrayList<Pair<Identifier, Function>>());
        list.add(new Pair<Identifier, Function>(libraryClassName, f));
    }
	public List<Pair<Identifier, Function>> getFunctionsReifiableInFakePointer(Identifier resolvedFakePointer) {
        return functionsReifiableInFakePointers.get(resolvedFakePointer);
    }

    public JavaPrim resolvePrimitive(String name) {
        return name == null ? null : javaPrims.get(name);
    }

    private boolean isPrimitive(TypeRef tr) {
        // TODO un-hack me
        return tr != null && javaPrims.containsKey(tr.toString());
    }

    public TaggedTypeRef resolveFullTaggedTypeRef(TaggedTypeRef s) {
        Identifier tag = s.getTag();
        if (tag != null && s.getResolvedJavaIdentifier() == null) {
            TaggedTypeRef rep = null;
            if (s instanceof Struct) {
                rep = structsByName.get(tag);
            } else if (s instanceof Enum) {
                rep = enumsByName.get(tag);
            }
            if (rep != null && rep != s)
                s = rep;
        }
        return s;
    }

    public interface ClassWritingNotifiable {
		Struct writingClass(Identifier fullClassName, Struct interf, Signatures signatures, String currentLibrary);
	}
	public Struct notifyBeforeWritingClass(Identifier fullClassName, Struct interf, Signatures signatures, String currentLibrary) {
		for (Class<?> c : overwrittenClassesThatNeedToKeepAllTheirMethods) {
			if (fullClassName.equals(c.getName())) {
				declarationsConverter.addMissingMethods(c, signatures, interf);
				break;
			}
		}
		
		if (fullClassName.resolveLastSimpleIdentifier().equals("char"))
			return null;
		
		String runtimeSpecificHelp = config.runtime == JNAeratorConfig.Runtime.BridJ ?
			"or <a href=\"http://bridj.googlecode.com/\">BridJ</a> " :
			", <a href=\"http://rococoa.dev.java.net/\">Rococoa</a>, " +
			"or <a href=\"http://jna.dev.java.net/\">JNA</a>"
		;
		interf.addToCommentBefore(
			"This file was autogenerated by <a href=\"http://jnaerator.googlecode.com/\">JNAerator</a>, ",
			"a tool written by <a href=\"http://ochafik.com/\">Olivier Chafik</a> that <a href=\"http://code.google.com/p/jnaerator/wiki/CreditsAndLicense\">uses a few opensource projects.</a>.",
			"For help, please visit <a href=\"http://nativelibs4java.googlecode.com/\">NativeLibs4Java</a> " +
			runtimeSpecificHelp + "."
		); 
		
		for (ClassWritingNotifiable n : classWritingNotifiables) {
			interf = n.writingClass(fullClassName, interf, signatures, currentLibrary);
			if (interf == null)
				return null;
		}
		return interf;
	}
	public List<ClassWritingNotifiable> classWritingNotifiables = new ArrayList<ClassWritingNotifiable>();
	
	public void printJavaClass(Identifier javaPackage, Struct javaClass, PrintWriter out) {

		if (javaPackage != null)
			out.println("package " + javaPackage + ";");

        if (config.noAutoImports)
            out.println(javaClass);
        else
            Printer.printJava(javaPackage, ident(javaPackage == null ? null : javaPackage.clone(), javaClass.getTag().clone()), javaClass, out);
//		out.println("@SuppressWarnings(\"unused\")");
	}
	public boolean hasObjectiveC() {
		if (!objCCategoriesByName.isEmpty())
			return true;
		
		Map<Identifier, Struct> m;

		if ((m = classes.get(Struct.Type.ObjCClass)) != null && !m.isEmpty())
			return true;

		if ((m = classes.get(Struct.Type.ObjCProtocol)) != null && !m.isEmpty())
			return true;
		return false;
	}
	public final Map<String, TypeRef> weakTypeDefs = new HashMap<String, TypeRef>();
	public void addWeakTypeDef(TypeRef clone, String sn) {
		weakTypeDefs.put(sn, clone);
	}
	public void rehabilitateWeakTypeDefs() {
		for (Map.Entry<String, TypeRef> e : weakTypeDefs.entrySet()) {
			if (typeDefs.get(e.getKey()) == null) {
				DirectDeclarator dd = new DirectDeclarator(e.getKey());
				TypeDef td = new TypeDef(e.getValue(), dd);
				typeDefs.put(e.getKey(), new Pair<TypeDef, Declarator>(td, dd));
			}
		}
	}
    
    
    Map<String, TypeRef> manualTypeDefs = new HashMap<String, TypeRef>();

    public TypeRef getTypeDef(Identifier name) {
        if (name == null) {
            return null;
        }

        Pair<TypeDef, Declarator> p = typeDefs.get(name);
        if (p == null) {
            return manualTypeDefs.get(name.toString());
        }

        Declarator value = p.getValue();
        String rname = value == null ? null : value.resolveName();
        if (rname != null) {
            if (name.equals("id")) {
                return null;
            }

            if (name.equals("SEL")) {
                return null;
            }

            if (name.equals("IMP")) {
                return null;
            }

            if (name.equals("Class")) {
                return null;
            }

            if (name.equals("BOOL")) {
                if (rname.equals("byte")) {
                    return null;
                }
            }
        }
        
        Declarator.MutableByDeclarator mt = p.getValue().mutateType(p.getFirst().getValueType());
        return mt instanceof TypeRef ? (TypeRef)mt : null;
    }
    public void addManualTypeDef(String name, TypeRef tr) {
        manualTypeDefs.put(name, tr);
	}

    public TypeRef resolveType(TypeRef tr, boolean keepUnresolvedIdentifiers) {
        return resolveType(tr, keepUnresolvedIdentifiers, new HashSet<Identifier>());
    }
    protected TypeRef resolveType(TypeRef tr, boolean keepUnresolvedIdentifiers, Set<Identifier> resolvedTypeDefs) {
        if (tr instanceof TypeRef.TargettedTypeRef) {
            TypeRef.TargettedTypeRef ttr = (TypeRef.TargettedTypeRef)tr;
            TypeRef originalTarget = ttr.getTarget();
            TypeRef resolvedTarget = resolveType(originalTarget, keepUnresolvedIdentifiers, resolvedTypeDefs);
            if (resolvedTarget == null)
                return null;
            
            ttr.setTarget(null);
            TypeRef.TargettedTypeRef clone = (TypeRef.TargettedTypeRef)ttr.clone();
            ttr.setTarget(originalTarget);
            clone.setTarget(resolvedTarget.clone());
            
            clone.setParentElement(ttr.findParentOfType(Struct.class));
            return clone;
        } 
        
        if (isPrimitive(tr))
            return tr;
        if (tr instanceof SimpleTypeRef) {
            TypeRef resolved = resolveType(((SimpleTypeRef)tr).getName(), keepUnresolvedIdentifiers, resolvedTypeDefs);
            if (resolved != null || !keepUnresolvedIdentifiers)
                return resolved;
        }
        
        return tr;
    }
    protected TypeRef resolveType(Identifier name, boolean keepUnresolvedIdentifiers, Set<Identifier> resolvedTypeDefs) {
        TypeRef res;
        res = resolveStruct(name);
        if (res != null)
            return res;

        res = resolveEnum(name);
        if (res != null)
            return res;
        
        res = resolveCallback(name);
        if (res != null)
            return res;
        
        res = resolveObjCClass(name);
        if (res != null)
            return res;
        
        if (resolvedTypeDefs.add(name)) {
            res = getTypeDef(name);
            if (res != null)
                return resolveType(res, keepUnresolvedIdentifiers, resolvedTypeDefs);
        }

        return null;
    }
    
    public FunctionSignature resolveCallback(Identifier name) {
        return callbacksByName.get(name);
    }

    public Enum resolveEnum(Identifier name) {
        return enumsByName.get(name);
    }

    public Struct resolveStruct(Identifier name) {
        return structsByName.get(name);
    }
    
 
    Map<String, TypeConversion.JavaPrim> javaPrims = new TreeMap<String, TypeConversion.JavaPrim>();

    public boolean isObjCppPrimitive(String s) {
        return javaPrims.containsKey(s);
    }

    protected void prim(String from, TypeConversion.JavaPrim to) {
        javaPrims.put(from, to);
    }
}