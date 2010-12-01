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
	GNU Lesser General Public Lticense for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
	This grammar is by no mean complete.
	It is able to parse preprocessed C & Objective-C files and can tolerate some amount of C++. 
	It lacks serious expression support, which is being worked on.
	Complex variable declarations may not be supported, such as complex signatures of functions that return function pointers...
*/

grammar ObjCpp;
options {
	backtrack = true;
	//output = AST;
	memoize = true;
//	k = 2;
}

scope Symbols {
	Set<String> typeIdentifiers;
}
scope ModifierKinds {
	EnumSet<ModifierKind> allowedKinds;
}
scope IsTypeDef {
	boolean isTypeDef;
}
scope ModContext {
	boolean isObjCArgDef;
	boolean isInExtMod;
}

@header { 
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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.HashSet;
import static com.ochafik.lang.jnaerator.parser.TypeRef.*;
import static com.ochafik.lang.jnaerator.parser.Expression.*;
import static com.ochafik.lang.jnaerator.parser.Declaration.*;
import static com.ochafik.lang.jnaerator.parser.Identifier.*;
import static com.ochafik.lang.jnaerator.parser.Statement.*;
import static com.ochafik.lang.jnaerator.parser.Declarator.*;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
}

@members {
	public enum Language {
		C, CPlusPlus, ObjectiveC
	}
	public EnumSet<Language> possibleLanguages = EnumSet.allOf(Language.class);
	
	String lastComment;
	String file;
	int sourceLineDelta = 0; // line(token) = token.line - lastLineToken.line + lastLine; sourceLineDelta = lastLine - lastLineToken.line
	//String pack;
	
	public Set<String> topLevelTypeIdentifiers;// = new HashSet<String>();//java.util.Arrays.asList("CHAR"));
	public interface ObjCParserHelper {
		boolean isObjCppPrimitive(String identifier);
	}
	public ObjCParserHelper objCParserHelper;
	boolean isPrimitiveType(String identifier) {
		return objCParserHelper != null && objCParserHelper.isObjCppPrimitive(identifier);
	}
	public EnumSet<ModifierKind> newKinds(ModifierKind first, ModifierKind... rest) {
		return EnumSet.of(first, rest);
	}
	public void setupSymbolsStack() {
    		Symbols_scope ss = new Symbols_scope();
    		ss.typeIdentifiers = new HashSet();
    		Symbols_stack.push(ss);
	}
	public boolean isAllowed(ModifierKind kind) {
		if (ModifierKinds_stack.isEmpty())
			return false;
		ModifierKinds_scope scope = (ModifierKinds_scope)ModifierKinds_stack.get(ModifierKinds_stack.size() - 1);
		return scope.allowedKinds.contains(kind);
	}
	
	boolean isObjCArgDef() {
		if (ModContext_stack.isEmpty())
			return false;
		ModContext_scope scope = (ModContext_scope)ModContext_stack.get(ModContext_stack.size() - 1);
		return scope.isObjCArgDef;
	}
	boolean isInExtMod() {
		if (ModContext_stack.isEmpty())
			return false;
		ModContext_scope scope = (ModContext_scope)ModContext_stack.get(ModContext_stack.size() - 1);
		return scope.isInExtMod;
	}
	boolean isTypeDef() {
		if (IsTypeDef_stack.isEmpty())
			return false;
		IsTypeDef_scope scope = (IsTypeDef_scope)IsTypeDef_stack.get(IsTypeDef_stack.size() - 1);
		return scope.isTypeDef;
	}
	void defineTypeIdentifierInParentScope(Identifier i) {
		if (i != null && i.isPlain())
			defineTypeIdentifierInParentScope(i.toString());
	}
	void defineTypeIdentifierInParentScope(String name) {
		if (name == null || Symbols_stack.isEmpty())
			return;
		int s = Symbols_stack.size();
		Symbols_scope sp = (Symbols_scope)Symbols_stack.get(s - 2 >= 0 ? s - 2 : s - 1);
		sp.typeIdentifiers.add(name);
	}
	boolean isTypeIdentifier(String identifier) {
		if (possibleLanguages.contains(Language.ObjectiveC)) {
			if ("SEL".equals(identifier) ||
				"id".equals(identifier) ||
				"IMP".equals(identifier) ||
				"Class".equals(identifier) ||
				"Protocol".equals(identifier) ||
				"BOOL".equals(identifier) ||
				"NSObject".equals(identifier) ||
				"NSClass".equals(identifier))
				return true;
		}
		for (Object oscope : Symbols_stack) {
			Symbols_scope scope = (Symbols_scope)oscope;
			if (scope.typeIdentifiers.contains(identifier))
				return true;
		}
		if (isPrimitiveType(identifier)) {
			//System.err.println("Found primitive : " + identifier);
			return true;
		}
		//System.err.println("Not a primitive : " + identifier);
		return topLevelTypeIdentifiers == null ? false : topLevelTypeIdentifiers.contains(identifier);
	}
	public void setFile(String file) {
		this.file = file;
		possibleLanguages = guessPossibleLanguages(file);
	}
	
	public EnumSet<Language> guessPossibleLanguages(String file) {
		int i = file.lastIndexOf(".");
		if (i > 0) {
			String ext = file.substring(i + 1).toLowerCase();
		
			if (ext.equals("h"))
				return EnumSet.allOf(Language.class);
			else if (ext.equals("c"))
				return EnumSet.of(Language.C);
			else if (ext.startsWith("c") || ext.startsWith("h")) // cxx, hxx
				return EnumSet.of(Language.C, Language.CPlusPlus);
			else if (ext.equals("m"))
				return EnumSet.of(Language.C, Language.ObjectiveC);
			else if (ext.equals("mm"))
				return EnumSet.allOf(Language.class);
		}
		return EnumSet.allOf(Language.class);
	}
	public String getFile() { 
		return file; 
	}
	
	public int getLine() {
		return getLine(getTokenStream().get(getTokenStream().index()));
	}
	
	public int getLine(Token token) {
		return token.getLine(); //+ sourceLineDelta;
	}
	
	protected <T extends Element> T mark(T element, int tokenLine) {
		element.setElementFile(getFile());
		if (tokenLine >= 0)
			element.setElementLine(tokenLine + sourceLineDelta);
		return element;
	}
	protected String getCommentBefore() {
		return getCommentBefore(getTokenStream().index());
	}
	protected String getCommentBefore(int index) {
		String comment = null;
		boolean toleratedNewLine = false;
		while (index > 0) {
			Token token = getTokenStream().get(--index);
			if (token.getType() == COMMENT || token.getType() == LINE_COMMENT) {
				//if (comment != null)
				//return comment;
				if (comment != null && comment.endsWith("\n") && toleratedNewLine)
    					return null;
				if (comment != null)
					return comment;
				comment = token.getText();
	  			if (comment != null && comment.endsWith("\n") && toleratedNewLine)
    					return null;
			} else if (token.getType() == WS) {
				if (token.getText().indexOf("\n") >= 0) {
					if (comment != null)
						return comment;
					else if (toleratedNewLine)
						return null;
					else
						toleratedNewLine = true;
				}
			} else
				return null;
		}
		return comment;
	}
	protected String getCommentAfterOnSameLine(int index, String forbiddenChars) {
		int size = getTokenStream().size();
		while (index < size) {
			Token token = getTokenStream().get(index++);
			String tt = token.getText();
			if (token.getType() == COMMENT || token.getType() == LINE_COMMENT)
				return tt;
			else if (tt.indexOf("\n") >= 0 || forbiddenChars != null && tt.matches(".*?[" + forbiddenChars + "].*"))
				break;
		}
		return null;
	}
	protected Declaration decl(TaggedTypeRef type) {
		return mark(new TaggedTypeRefDeclaration(type), type.getElementLine());
	} 
	
	protected String next() {
		return next(1);
	}
	protected String next(int i) {
		return input.LT(i).getText();
	}
	protected Modifier parseModifier(String s, ModifierKind... anyModKind) {
		Modifier mod = Modifier.parseModifier(next(), anyModKind);
		if (mod == null)
			return null;
		if (mod.isAllOf(ModifierKind.ObjectiveC, ModifierKind.OnlyInArgDef) && !isObjCArgDef())
			return null;
		if (mod.isA(ModifierKind.Java))
			return null;
			
		if (mod.isAnyOf(ModifierKind.Declspec, ModifierKind.Attribute) && !isInExtMod())
			return null;
		return mod;
	}
	protected boolean next(ModifierKind... anyModKind) {
		return parseModifier(next(), anyModKind) != null;
	} 
	protected boolean next(String... ss) {
		return next(1, ss);
	}
	protected boolean next(int i, String... ss) {
		String n = next(i);
		for (String s : ss)
			if (s.equals(n))
				return true;
				
		return false;
	}
	
	String getSurroundings(Token t, int width) {
		if (t == null)
			return null;
		int x = t.getTokenIndex();
		List<String> strs = new ArrayList<String>();
		int size = getTokenStream().size();
		for (int i = x - width; i < x + width + 1; i++) {
			if (i < 0 || i >= size)
				continue;

			strs.add(getTokenStream().get(i).getText());
		}
		return com.ochafik.util.string.StringUtils.implode(strs, " ");
	}
	@Override
	public String getErrorMessage(RecognitionException e, String[] tokenNames) {
		if (e instanceof NoViableAltException) {
			NoViableAltException ne = (NoViableAltException)e;
			return "Failed to match any alternative with token " + ne.token + "\n\t" +
				" File: " + getFile() + ":" + (ne.line + sourceLineDelta) + "\n\t" +
				"Input: " + getSurroundings(ne.token, 5).replace('\n', ' ') + "\n\t" +
				" Rule: " + ne.grammarDecisionDescription + "\n\t" +
				"Stack: " + getRuleInvocationStack(e, getClass().getName()) + "\n";
		} else
			return super.getErrorMessage(e, tokenNames);
	}
	@Override
	public String getTokenErrorDisplay(Token t) {
		return t.toString();	
	}
}

@lexer::header { 
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
import static com.ochafik.lang.jnaerator.parser.Expression.*;
}

lineDirective
	:	ln='#line' line=DECIMAL_NUMBER {
			try {
				sourceLineDelta = Integer.parseInt($line.text) - $ln.getLine() - 1;
			} catch (Exception ex) {
				System.err.println("ERROR: unparsable line in #line directive : " + $line.text);
				sourceLineDelta = 0;
			}
		}
		(
			unescapedString=STRING {
				String fileStr = $unescapedString.text.trim();
				if (fileStr.startsWith("\"")) {
					fileStr = fileStr.substring(1);
					if (fileStr.endsWith("\""))
						fileStr = fileStr.substring(0, fileStr.length() - 1);
				}				
				setFile(fileStr);
			} 
		)? 
		depth=DECIMAL_NUMBER?
	;
	
sourceFile returns [SourceFile sourceFile]
scope Symbols; 
@init {
	$Symbols::typeIdentifiers = new HashSet<String>();
}
	:	
		{ $sourceFile = new SourceFile(); }//mark(new SourceFile(), getLine()); }
		(
			declaration { 
				$sourceFile.addDeclaration($declaration.declaration); 
			} |
			lineDirective {
				if ($sourceFile.getElementFile() == null)
					$sourceFile.setElementFile(getFile());
			}
		)* 
	 	EOF
	 ;

externDeclarations returns [ExternDeclarations declaration]
	:	{ next("extern") }?=> IDENTIFIER
		STRING {
			$declaration = mark(new ExternDeclarations(), getLine($STRING));
			$declaration.setLanguage($STRING.text);
		}
		'{' 
			(
				ed=declaration { 
					$declaration.addDeclaration($ed.declaration); 
				} |
				lineDirective
			)* 
		'}'
	;

declaration returns [Declaration declaration, List<Modifier> modifiers, String preComment, int startTokenIndex]
scope IsTypeDef;
scope ModContext;
/*@after {
	if ($declaration == null)
	try {
		int i = $start.getTokenIndex();
		if (i > 0) {
			String s1 = getTokenStream().get(i - 1).getText(), s2 = i > 1 ? getTokenStream().get(i - 2).getText() : null;
           		String s = (s2 == null ? "" : s2) + s1;
           		if (s.matches(".*\n\\s*\n"))
				$declaration = new EmptyDeclaration();
		}
	} catch (Exception ex) {
		ex.printStackTrace();
	}
}*/
	:	
		{
		  $modifiers = new ArrayList<Modifier>();
		  $startTokenIndex = getTokenStream().index();
		  $preComment = getCommentBefore($startTokenIndex);
		}
		(
			(
				{ next("__pragma") }?=> pragmaContent |
				templateDef {
					$declaration = $templateDef.template;
				} |
				functionDeclaration {
					$declaration = $functionDeclaration.function;
				} |
				{ next("extern") }?=> externDeclarations {
					$declaration = $externDeclarations.declaration; 
				} |
				varDecl ';' { 
					$declaration = $varDecl.decl; 
				} |
				objCClassDef { 
					$declaration = decl($objCClassDef.struct); 
				} |
				typeDef {
					$declaration = $typeDef.typeDef; 
				} |
				forwardClassDecl {
					$declaration = new Declarations($forwardClassDecl.declarations); 
				} |
				namespaceDecl {
					$declaration = $namespaceDecl.namespace;
				}// | 
				//';' */// allow isolated semi-colons
			)
			{
				String commentAfter = getCommentAfterOnSameLine($startTokenIndex, null);
				if ($declaration != null) {
					$declaration.setCommentBefore($preComment);
					$declaration.setCommentAfter(commentAfter);
					$declaration.addModifiers($modifiers);
				}
			}
		)
	;
	
namespaceDecl returns [Namespace namespace]
	:
		'namespace' ns=IDENTIFIER '{' {
			$namespace = new Namespace();
			$namespace.setName(new SimpleIdentifier($ns.text));
		}
		(
			subD=declaration {
				$namespace.addDeclaration($subD.declaration);
			} |
			lineDirective
		)*
		'}'
	;
	
forwardClassDecl returns [List<Declaration> declarations]
	: 	{ $declarations = new ArrayList<Declaration>(); }
		'@class' 
		n1=IDENTIFIER { 
			$declarations.add(decl(Struct.forwardDecl(new SimpleIdentifier($n1.text), Struct.Type.ObjCClass))); 
			defineTypeIdentifierInParentScope($n1.text);
		}
		(',' 
		nx=IDENTIFIER { 
			$declarations.add(decl(Struct.forwardDecl(new SimpleIdentifier($nx.text), Struct.Type.ObjCClass))); 
			defineTypeIdentifierInParentScope($nx.text);
		}
		)*
		';' 
	;
	
functionPointerVarDecl  returns [Declaration decl]
	:	tr=mutableTypeRef {
			($tr.type instanceof FunctionSignature) && 
			((FunctionSignature)$tr.type).getFunction().getName() != null
		}? {
			$decl = new FunctionPointerDeclaration(((FunctionSignature)$tr.type));
		}
		';'
	;
	
enumItem returns [Enum.EnumItem item]
	:	n=IDENTIFIER ('=' v=topLevelExpr)? {
			$item = mark(new Enum.EnumItem($n.text, $v.text == null ? null : $v.expr), getLine($n));
			$item.setCommentBefore(getCommentBefore($n.getTokenIndex()));
			$item.setCommentAfter(getCommentAfterOnSameLine($n.getTokenIndex() - 1, "}"));
		}
	;
	
enumBody returns [Enum e]
scope ModContext;
	:
		{ 
			$e = new Enum();
            $e.setType(Enum.Type.C);
			$e.setForwardDeclaration(false); 
		}
		'{' 
			(  
				i1=enumItem { 
					if ($i1.text != null)
						$e.addItem($i1.item); 
				}
				(
					',' 
					(ix=enumItem { 
						if ($ix.text != null)
							$e.addItem($ix.item); 
					})?
				)*
			)?
		'}'
	;
enumCore returns [Enum e]
@init {
	List<Modifier> modifiers = new ArrayList<Modifier>();
}
	:
		t='enum'
		(
			m1=modifiers { modifiers.addAll($m1.modifiers); }
			(
				ab=enumBody {
					$e = $ab.e;
					$e.setForwardDeclaration(false);
				} |
				tag=qualifiedIdentifier
				(
					m2=modifiers { modifiers.addAll($m2.modifiers); }
					nb=enumBody {
						$e = $nb.e;
						$e.setForwardDeclaration(false);
					} | {
						$e = new Enum();
                        $e.setType(Enum.Type.C);
						$e.setForwardDeclaration(true);
					}
				) {
					$e.setTag($tag.identifier);
				}
			)
		) {
			//$e.setCommentBefore(getCommentBefore($t.getTokenIndex()));
			$e = mark($e, getLine($t));
			$e.addModifiers(modifiers);
			defineTypeIdentifierInParentScope($e.getTag());
		}
	;
	
		
objCClassDef returns [Struct struct]
	:	
		octype=('@protocol'|'@interface') 
		className=IDENTIFIER {
			defineTypeIdentifierInParentScope($className.text);
			$struct = mark(new Struct(), getLine($octype));
			//$struct.setForwardDeclaration(true);
			//$struct.setCommentBefore(getCommentBefore($octype.getTokenIndex()));
			$struct.setType($octype.text.equals("@interface") ?
				Struct.Type.ObjCClass :
				Struct.Type.ObjCProtocol
			);
			$struct.setTag(new SimpleIdentifier($className.text));
		}
		(
			(	
				':' parentClass=IDENTIFIER {
				if ($parentClass.text != null)
					$struct.addParent(new SimpleIdentifier($parentClass.text));
				}
			) |
			(
				'(' categoryName=IDENTIFIER ')' {
					$struct.setCategoryName($categoryName.text);
				}
			) |
		)
		(	
			'<' (
				p1=IDENTIFIER { $struct.addProtocol(new SimpleIdentifier($p1.text)); }
				(
					',' 
					px=IDENTIFIER { $struct.addProtocol(new SimpleIdentifier($px.text)); }
				)*
			)? '>'
		)?
		(
			'{'
			(
				'@package' | // TODO keep in AST 
				'@required' | // TODO keep in AST 
				'@optional' | // TODO keep in AST 
				'@public' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Public); } | 
				'@private' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Private); } | 
				'@protected' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Protected); } |
				(
					functionPointerOrSimpleVarDecl ';' {
						$struct.addDeclaration($functionPointerOrSimpleVarDecl.decl);
					}
				) |
				lineDirective
			)* 
			'}'
		)?
		{ $struct.setNextMemberVisibility(Struct.MemberVisibility.Public); }
		(
			objCMethodDecl { 
				$struct.addDeclaration($objCMethodDecl.function);
			} |
			objCPropertyDecl {
				$struct.addDeclaration($objCPropertyDecl.property); 
			} |
			typeDef {
				$struct.addDeclaration($typeDef.typeDef); 
			} |
			vd=varDecl ';' { !($vd.decl instanceof VariablesDeclaration) }? {
				$struct.addDeclaration($vd.decl);
			} |
			lineDirective
		)*
		'@end'
	;						

functionPointerOrSimpleVarDecl returns [Declaration decl]
	:
		fv=varDecl {
			$decl = $fv.decl;
		} |
		functionPointerVarDecl { 
			$decl = $functionPointerVarDecl.decl; 
		}
	;
					
objCPropertyDecl returns [Property property]
	:
		'@property' 
		( '(' IDENTIFIER ( ',' IDENTIFIER ) * ) ? // TODO parse modifiers : assign, nonatomic, readonly, copy...
		functionPointerOrSimpleVarDecl ';' {
			$property = new Property($functionPointerOrSimpleVarDecl.decl);
		}
	;
	
objCMethodDecl returns [Function function]
scope ModContext;
@init {
	$ModContext::isObjCArgDef = true;
}
	:	{ 	
			$function = new Function(); 
			$function.setType(Function.Type.ObjCMethod);
		}
		tk=(
			tp='+' { 
				$function.addModifiers(Modifier.Static); 
				$function = mark($function, getLine($tp)); 
				$function.setCommentBefore(getCommentBefore($tp.getTokenIndex()));
			} | 
			tm='-' {
				$function = mark($function, getLine($tm)); 
				$function.setCommentBefore(getCommentBefore($tm.getTokenIndex()));
			}
		)
		(
			// Optional return type
			'('
				returnTypeRef=mutableTypeRef? { 
					$function.setValueType($returnTypeRef.type); 
				}
			')'
		)?
		methodName=(IDENTIFIER | 'class') { 
			$function.setName(new SimpleIdentifier($methodName.text)); 
			$function.setCommentAfter(getCommentAfterOnSameLine($methodName.getTokenIndex(), null));
		} 
		(
			':' '(' argType1=mutableTypeRef ')' argName1=IDENTIFIER {
				Arg arg = new Arg($argName1.text, $argType1.type);
				arg.setSelector($methodName.text);
				$function.addArg(arg);
			}
			(
				sel=IDENTIFIER ':' 
				'(' argType=mutableTypeRef ')' 
				argName=IDENTIFIER {
					Arg arg = new Arg($argName.text, $argType.type);
					arg.setSelector($sel.text);
					$function.addArg(arg);
				}
			)*
			(
				',' '...' {
					$function.addArg(Arg.createVarArgs());
				}
			)?
		)?
		';'
	;

structBody returns [Struct struct]
scope ModContext;
	:
		{ 
			$struct = new Struct();
			$struct.setForwardDeclaration(false); 
		}
		'{'
			(
				(
					'public' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Public); } | 
					'private' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Private); } | 
					'protected' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Protected); } 
				) ':' |
				{ next("friend") }? IDENTIFIER friend=declaration ';' {
					$struct.addDeclaration(new FriendDeclaration(friend.declaration));
				} |
				decl=declaration {
					$struct.addDeclaration($decl.declaration);
				} |
				fv=varDecl ';' {
					$struct.addDeclaration($fv.decl);
				} |
				lineDirective
			)*
		'}'
	;

structCore returns [Struct struct]
scope Symbols; 
@init {
	$Symbols::typeIdentifiers = new HashSet<String>();
	List<Modifier> modifiers = new ArrayList<Modifier>();
}
@after {
	$struct = mark($struct, getLine($typeToken)); 
	$struct.setType(
		$typeToken.text.equals("struct") ?	Struct.Type.CStruct :
		$typeToken.text.equals("union") ?	Struct.Type.CUnion :
						Struct.Type.CPPClass
	);
	
	Function.Type forcedType = null;
	if ($struct.getType() == Struct.Type.CPPClass)
		forcedType = Function.Type.CppMethod;
	
	if (forcedType != null)
	for (Declaration d : $struct.getDeclarations()) {
		if (d instanceof Function)
			((Function)d).setType(forcedType);
	}
}
	:	
		//{ next("struct", "class", "union") }?=> typeToken=IDENTIFIER
		//typeToken=('struct' | 'union' | { next("class") }?=> IDENTIFIER)
		typeToken=('struct' | 'union' | 'class')
		(
			m1=modifiers { modifiers.addAll($m1.modifiers); }
			(
				ab=structBody {
					$struct = $ab.struct;
					$struct.setForwardDeclaration(false);
				} |
				tag=qualifiedIdentifier {
					defineTypeIdentifierInParentScope($tag.identifier);
				}
				(
					(
						m2=modifiers { modifiers.addAll($m2.modifiers); }
						(
							':'
							'public'?//m3=modifiers
							parent=qualifiedIdentifier
						)? 
						nb=structBody {
							$struct = $nb.struct;
							$struct.setForwardDeclaration(false);
							if ($parent.text != null)
								$struct.addParent($parent.identifier);
						} 
					) | {
						$struct = new Struct();
						$struct.setForwardDeclaration(true);
					}
				) {
					$struct.setTag($tag.identifier);
				}
			)
		)  
	;

anyOp returns [java.lang.Enum<?> op]
	:	binaryOp { $op = $binaryOp.op; } | 
		unaryOp { $op = $unaryOp.op; } | 
		assignmentOp { $op = $assignmentOp.op; } 
	;

//structInsides returns [List<Declaration> declarations, Struct.MemberVisibility

functionDeclaration returns [Function function]
scope Symbols;
@init {
	$Symbols::typeIdentifiers = new HashSet<String>();
}
	:	{ 	
			$function = mark(new Function(), -1);
			$function.setType(Function.Type.CFunction);
		}
		(
			// C++ cast operator
			{ next("operator") }?=> IDENTIFIER {
				$function.setName(ident("operator")); 
				mark($function, getLine($start));
			} 
			castPreMods=modifiers { $function.addModifiers($castPreMods.modifiers); }
			castTypeRef=mutableTypeRef { 
				$function.setValueType($castTypeRef.type); 
			} |
			preMods1=modifiers { $function.addModifiers($preMods1.modifiers); }
			returnTypeRef=mutableTypeRef? { 
				$function.setValueType($returnTypeRef.type); 
			}
			preMods2=modifiers { $function.addModifiers($preMods2.modifiers); }
			name=qualifiedCppFunctionName {
				$function.setName($name.identifier); 
				mark($function, getLine($start));
			}
		)	
		argList {
			$function.setArgs($argList.args);
		}
		postMods=modifiers { $function.addModifiers($postMods.modifiers); }
		(
			':'
			i1=constructorInitializer { $function.addInitializer($i1.init); }
			(
				',' ix=constructorInitializer { $function.addInitializer($ix.init); }
			)*
		)?
		(	
			( 
				{ next(2, "0") }? '=' DECIMAL_NUMBER // TODO mark in DOM
			)?
			';' |
			statementsBlock {
				$function.setBody($statementsBlock.statement);
			}
		)
	;

constructorInitializer returns [FunctionCall init]
	:	qn=qualifiedCppFunctionName {
			$init = new FunctionCall(new TypeRefExpression(new SimpleTypeRef($qn.identifier)));
		}  
		'(' (
			el=topLevelExprList { $init.addArguments($el.exprs); }
		)? ')'
	;
	
modifiers returns [List<Modifier> modifiers]
@init { $modifiers = new ArrayList<Modifier>(); }
	: 	( modifier { $modifiers.addAll($modifier.modifiers); } )*
	;

pragmaContent	:	
		//{ next("__pragma") }?=> pragmaContent
			// MSVC-specific : parse as token soup for now
			IDENTIFIER '('
				(IDENTIFIER | constant | ',' | ':' | '(' (IDENTIFIER | constant | ',' | ':')* ')')*
			')'
			';'?
		//) 
	;

modifier returns [List<Modifier> modifiers, String asmName]
@init { $modifiers = new ArrayList<Modifier>(); }
	:
		
		{ next("__pragma") }?=> pragmaContent | 
		{ next("extern") }?=> IDENTIFIER ex=STRING {
			$modifiers.add(Modifier.Extern); // TODO
		} |
		{ parseModifier(next()) != null }? m=IDENTIFIER {
			$modifiers.add(Modifier.parseModifier($m.text));
		} |
		{ next("__success") }?=>
		IDENTIFIER '(' 'return' binaryOp expression  ')' |
		
		// TODO handle it properly @see http://blogs.msdn.com/staticdrivertools/archive/2008/11/06/annotating-for-success.aspx
		{ next(ModifierKind.VCAnnotation1Arg, ModifierKind.VCAnnotation2Args) }?=>
		IDENTIFIER '(' expression ')' |
		
		{ next("__declspec", "__attribute__", "__asm") }?=>
		IDENTIFIER
		'(' (
			( an=STRING { 
				String s = String.valueOf(Constant.parseString($an.text).getValue());
				if ($asmName == null) 
					$asmName = s; 
				else 
					$asmName += s; 
			} )* |
			extendedModifiers {
				$modifiers.addAll($extendedModifiers.modifiers);
			}
		) ')'
	;

//http://msdn.microsoft.com/en-us/library/dabb5z75.aspx
extendedModifiers returns [List<Modifier> modifiers]
scope ModContext;
@init {
	$ModContext::isInExtMod = true;
}
	:	{ $modifiers = new ArrayList<Modifier>(); }
		(
			{ next(ModifierKind.Extended) }? m=IDENTIFIER
			(
				{
					$modifiers.add(Modifier.parseModifier($m.text));
				}/* |
				{ $IDENTIFIER.text.equals("align") }? DECIMAL_NUMBER |
				{ $IDENTIFIER.text.equals("allocate") }?  '(' STRING ')' |
//				{ $IDENTIFIER.text.equals("property") }?  '(' getSet=IDENTIFIER { $getSet.text.equals("get") || $getSet.text.equals("set") }? '=' func_name=IDENTIFIER ')' |
				{ $IDENTIFIER.text.equals("uuid") }?  '(' ComObjectGUID=STRING ')'*/
			)
		)*
	;
argDef	returns [Arg arg]
	:	(
			tr=mutableTypeRef { 
				if ($tr.type != null) {
					$arg = new Arg(); 
					$arg.setValueType($tr.type); 
					int i = getTokenStream().index() + 1;
					$arg.setCommentBefore(getCommentBefore(i));
					$arg.setCommentAfter(getCommentAfterOnSameLine(i, ")"));
				}
			}
		)
		(
			declarator? { 
				if ($arg != null) {
					if ($declarator.declarator != null)
						$arg.setDeclarator($declarator.declarator); 
					/*else if ($arg.getValueType() instanceof FunctionSignature) {
						FunctionSignature fs = (FunctionSignature)$arg.getValueType();
						if (fs != null && fs.getFunction() != null) {
							//$arg.setName(fs.getFunction().getName());
							//fs.getFunction().setName(null);
						}
					}*/
				}
			}
		)
		('=' dv=topLevelExpr {
			if ($arg != null)
				$arg.setDefaultValue($dv.expr);
		})? 
		| 
		'...' { 
			$arg = Arg.createVarArgs(); 
		}
	;

typeMutator returns [TypeMutator mutator]
	:	{ next("const", "__const") }?=> IDENTIFIER '*' { $mutator = TypeMutator.CONST_STAR; } |
		'*' { 
			$mutator = TypeMutator.STAR;
		} |
		'&' { 
			$mutator = TypeMutator.AMPERSTAND; 
		} |
		'^' { 
			$mutator = TypeMutator.HAT; 
		} |
		'[' ']'  { $mutator = TypeMutator.BRACKETS; }
	;

arrayTypeMutator returns [TypeMutator mutator]
	:	'[' 
			expression {
				$mutator = TypeMutator.array($expression.expr); 
			}			
		']' 
	;

templateDef returns [Template template]
scope Symbols; 
scope IsTypeDef;
@init {
	$IsTypeDef::isTypeDef = true;
	$Symbols::typeIdentifiers = new HashSet<String>();
	$template = new Template();
}
	:	'template' '<' (
			t1=templateArgDecl {
				$template.addArg($t1.arg);
			}
			(
				',' tx=templateArgDecl {
					$template.addArg($tx.arg);
				}
			)* 
		)? '>'
		declaration {
			if ($declaration.declaration != null) {
				Declaration decl = $declaration.declaration;
				$template.setDeclaration(decl);
				if (decl instanceof TaggedTypeRefDeclaration) {
					TaggedTypeRefDeclaration ttrd = (TaggedTypeRefDeclaration)decl;
					TaggedTypeRef ttr = (TaggedTypeRef)ttrd.getTaggedTypeRef();
					if (ttr instanceof Struct)
						defineTypeIdentifierInParentScope(((Struct)ttr).getTag());
				}	
			}
		}
		//(structCore ';' | functionDeclaration)
	;
	
templateArgDecl returns [Arg arg]
	:	t=('class' | 'typename') n=IDENTIFIER {
			$arg = new Arg($n.text, new SimpleTypeRef($t.text));
			$Symbols::typeIdentifiers.add($t.text);
		} |
		argDef ('=' expression)? {
			$arg = $argDef.arg;
		} //mutableTypeRef ('=' constant)?
	;	
	
functionSignatureSuffix returns [FunctionSignature signature]
	:	tk='(' m1=modifiers pt=('*' | '^') m2=modifiers IDENTIFIER? ')' { 
			$signature = mark(new FunctionSignature(new Function(Function.Type.CFunction, $IDENTIFIER.text == null ? null : new SimpleIdentifier($IDENTIFIER.text), null)), getLine($tk));
			if ($pt.text.equals("^"))
				$signature.setType(FunctionSignature.Type.ObjCBlock);
			$signature.getFunction().setType(Function.Type.CFunction);
			$signature.getFunction().addModifiers($m1.modifiers);
			$signature.getFunction().addModifiers($m2.modifiers);
			if ($IDENTIFIER.text != null && isTypeDef()) {
				$Symbols::typeIdentifiers.add($IDENTIFIER.text);
			}
		}
		'(' (
			a1=argDef { 
				if (!$a1.text.equals("void"))
					((FunctionSignature)$signature).getFunction().addArg($a1.arg); 
			}
			(
				',' 
				ax=argDef { 
					((FunctionSignature)$signature).getFunction().addArg($ax.arg); 
				}
			)*
		)? ')'
	;

functionSignatureSuffixNoName returns [FunctionSignature signature]
	:	tk='(' modifiers pt=('*' | '^') ')' { 
			$signature = mark(new FunctionSignature(new Function(Function.Type.CFunction, null, null)), getLine($tk));
			if ($pt.text.equals("^"))
				$signature.setType(FunctionSignature.Type.ObjCBlock);
			$signature.getFunction().setType(Function.Type.CFunction);
			$signature.getFunction().addModifiers($modifiers.modifiers);
		}
		'(' (
			a1=argDef { 
				if (!$a1.text.equals("void"))
					((FunctionSignature)$signature).getFunction().addArg($a1.arg); 
			}
			(
				',' 
				ax=argDef { 
					((FunctionSignature)$signature).getFunction().addArg($ax.arg); 
				}
			)*
		)? ')'
	;

mutableTypeRef returns [TypeRef type]
	:	
		( typeRefCore { 
			$type = $typeRefCore.type; 
		} )
		(
			(
				m1=typeMutator {
					$type = $m1.mutator.mutateType($type);
				}
			) |
			(
				f1=functionSignatureSuffix { 
					assert $f1.signature != null && $f1.signature.getFunction() != null;
					if ($f1.signature != null && $f1.signature.getFunction() != null) {
						$f1.signature.getFunction().setValueType($type); 
						$type = $f1.signature;
					}
				}
			)
		)*
	;

nonMutableTypeRef returns [TypeRef type]
	:
		typeRefCore { 
			$type = $typeRefCore.type; 
		}
		(
			(
				typeMutator {
					$type = $typeMutator.mutator.mutateType($type);
				}
			)*
			(
				fs=functionSignatureSuffix { 
					assert $fs.signature != null && $fs.signature.getFunction() != null;
					if ($fs.signature != null && $fs.signature.getFunction() != null) {
						$fs.signature.getFunction().setValueType($type); 
						$type = $functionSignatureSuffix.signature;
					}
				}
			)
		)*
	;

declarator  returns [Declarator declarator]
	:
		modifiers
		(
			(
				(
					( 
						directDeclarator { 
							$declarator = $directDeclarator.declarator; 
						} 
					) |
					( 
						pt=('*' | '&' | '^')
						inner=declarator {
							$declarator = new PointerDeclarator($inner.declarator, PointerStyle.parsePointerStyle($pt.text));
						} 
					)
				)
				(
					':' bits=DECIMAL_NUMBER {
						if ($declarator != null)
							$declarator.setBits(Integer.parseInt($bits.text));
					}
				)?
				(
					'=' 
					dv=topLevelExpr {
						if ($declarator != null)
							$declarator.setDefaultValue($dv.expr);
					}
				)?
			) |
			':' bits=DECIMAL_NUMBER {
				$declarator = mark(new DirectDeclarator(null), getLine($bits));
				$declarator.setBits(Integer.parseInt($bits.text));
			}
		)
		{
			if ($declarator != null)
				$declarator.setModifiers($modifiers.modifiers);
		}
	;

typeDef returns [TypeDef typeDef]
scope IsTypeDef;
@init {
	$IsTypeDef::isTypeDef = true;
}
	:	'typedef'
	 	varDecl ';' {
		 	VariablesDeclaration vd = $varDecl.decl;
			$typeDef = new TypeDef(vd.getValueType(), vd.getDeclarators());
		}
	;
	
varDeclEOF returns [Declaration decl]
	: varDecl ';' EOF { $decl = $varDecl.decl; }
	;

declarationEOF returns [Declaration declaration]
	: 	d=declaration EOF { $declaration = $d.declaration; }
	;

varDecl returns [VariablesDeclaration decl]
	:	
		tr=nonMutableTypeRef { 
			$decl = new VariablesDeclaration($tr.type); 
			//$decl.addModifiers($modifiers.modifiers);
		}
		d1=declaratorsList {
			$decl.setDeclarators($d1.declarators);
		}
	;
	
objCProtocolRefList
	:	'<' 
		IDENTIFIER 
		(
			',' 
			IDENTIFIER
		)* 
		'>'
	;

declaratorsList returns [List<Declarator> declarators]
	:	{ $declarators = new ArrayList<Declarator>(); }
		d=declarator { $declarators.add($d.declarator); }
		(
			',' 
			x=declarator { $declarators.add($x.declarator); }
		)*
	;

directDeclarator returns [Declarator declarator]
	:	
		(
			{ parseModifier(next()) == null }?=> IDENTIFIER {
				$declarator = mark(new DirectDeclarator($IDENTIFIER.text), getLine($IDENTIFIER));
				if (isTypeDef()) {
					$Symbols::typeIdentifiers.add($IDENTIFIER.text);
				}
			} | 
			'(' inner=declarator ')' {
				$declarator = $inner.declarator;
				if ($declarator != null)
					$declarator.setParenthesized(true);
			} 
		)
		(
			'[' 
			(
				expression? {
					if ($expression.text != null) {
						if ($declarator instanceof ArrayDeclarator)
							((ArrayDeclarator)$declarator).addDimension($expression.expr);
						else
							$declarator = new ArrayDeclarator($declarator, $expression.expr);
					} else
						$declarator = new ArrayDeclarator($declarator, new Expression.EmptyArraySize());
				}
			)
			']' | 
			argList {
				$declarator = new FunctionDeclarator($declarator, $argList.args);
			}
		)*
	;

argList returns [List<Arg> args, boolean isObjC]
	:	{ 
			$isObjC = false; 
			$args = new ArrayList<Arg>();
		}
		op='(' 
		(
			a1=argDef {
				if (!$a1.text.equals("void")) 
					$args.add($a1.arg);
			}
			(
				',' 
				ax=argDef {
					$args.add($ax.arg);
				}
			)*
			( 
				',' '...' {
					$isObjC = true;
					$args.add(Arg.createVarArgs());
				}
			)?
		)?
		cp=')'
	;

typeRefCore returns [TypeRef type]
@init {
	List<Modifier> modifiers = new ArrayList<Modifier>();
	//TypeRef ref = null;
	int line = -1;
}
@after {
	if ($type == null && !modifiers.isEmpty()) {
		$type = new Primitive(null);
	}
	if ($type != null) {
		$type.addModifiers(modifiers);
		mark($type, line);
	}
}
	:	
		preMods=modifiers { modifiers.addAll($preMods.modifiers); }
		(
			'typename' pn=typeName { $type = $pn.type; } |
			{ 
				isTypeIdentifier(next()) || 
				(
					parseModifier(next(1)) == null && 
					!next(2, "=", ",", ";", ":", "[", "(", ")")
				) 
			}?=> an=typeName { $type = $an.type; } |
			structCore { $type = $structCore.struct; } |
			enumCore { $type = $enumCore.e; }
		)?
		postMods=modifiers { modifiers.addAll($postMods.modifiers); }
	;
	
typeName returns [TypeRef type]
	:
		i=qualifiedIdentifier {
			if ($i.identifier.isPlain() && isPrimitiveType($i.identifier.toString()))
				$type = new Primitive($i.identifier.toString());
			else
				$type = new SimpleTypeRef($i.identifier);
			if ($i.identifier.isPlain())
				$Symbols::typeIdentifiers.add($i.identifier.toString());
		}
	;
	
objCMethodCall returns [FunctionCall expr]
	:
		'[' target=expression methodName=IDENTIFIER {
			$expr = new FunctionCall();
			$expr.setFunction(new VariableRef(new SimpleIdentifier($methodName.text)));
			$expr.setTarget($target.expr);
			$expr.setMemberRefStyle(MemberRefStyle.SquareBrackets);
		}
		(
			':' arg1=expression {
				$expr.addArgument(null, $arg1.expr);
			}
			(
				selx=IDENTIFIER ':' argx=expression {
					$expr.addArgument($selx.text, $argx.expr);
				}
			)*
		)?
		']'
	;

binaryOp returns [Expression.BinaryOperator op]
	: 	t=(
		'+' | '-' | '*' | '/' | '%' | 
		'<<' | '>>>' | '>>' | 
		'^' | '||' | '|' | '&&' | '&' |
		'<=' | '>=' | '<' | '>' | '==' | '!='
		) {
			$op = Expression.getBinaryOperator($t.text);
		}
	;

postfixOp returns [Expression.UnaryOperator op]
	:
		'++' { 
			$op = UnaryOperator.PostIncr; 
		} |
		'--' { 
			$op = UnaryOperator.PostDecr; 
		}
	; 
	
typeRefOrExpression returns [Expression expr]
	:	tr=mutableTypeRef {
			$expr = new Expression.TypeRefExpression($tr.type);
		} | 
		e=topLevelExpr {
			$expr = $e.expr;
		}
	;

simpleIdentifier returns [SimpleIdentifier identifier]
	:	i=IDENTIFIER { $identifier = new SimpleIdentifier($i.text); }
		(
			'<' (
				a1=typeRefOrExpression { $identifier.addTemplateArgument($a1.expr); }
				(
					',' 
					ax=typeRefOrExpression  { $identifier.addTemplateArgument($ax.expr); }
				)* 
			)? '>'
		)?
	;

qualifiedIdentifier returns [Identifier identifier]
	:	i1=simpleIdentifier { $identifier = $i1.identifier; }
		(
			'::' ix=simpleIdentifier { $identifier = $identifier.derive(QualificationSeparator.Colons, $ix.identifier); }
		)*
	;
	
qualifiedCppFunctionName returns [Identifier identifier]
	:	i1=simpleCppFunctionName { $identifier = $i1.identifier; }
		(
			'::' ix=simpleCppFunctionName { $identifier = $identifier.derive(QualificationSeparator.Colons, $ix.identifier); }
		)*
	;
	
operator returns [Expression.Operator op]
	:
		unaryOp { $op = $unaryOp.op; } | 
		postfixOp { $op = $postfixOp.op; } | 
		binaryOp { $op = $binaryOp.op; } | 
		assignmentOp { $op = $assignmentOp.op; } |
		'=' { $op = BinaryOperator.Assign; } |
		'->' '*' { $op = BinaryOperator.ArrowStar; } |
		'[' ']' { $op = BinaryOperator.SquareBrackets; } |
		'(' ')' { $op = UnaryOperator.Parenthesis; } |
		',' { $op = BinaryOperator.Comma; } |
		'->' { $op = BinaryOperator.Arrow; }
	;
	
simpleCppFunctionName returns [SimpleIdentifier identifier]
	:
		{ next("operator") }? IDENTIFIER operator {
			$identifier = new SimpleIdentifier("operator" + $operator.op.toString());
		} |
		pre='~'? //n=IDENTIFIER anyOp? { 
		i=simpleIdentifier {
			if ($pre.text != null)
				$i.identifier.setName($pre.text + $i.identifier.getName());
			$identifier = $i.identifier;
		}
	;
	
baseExpression returns [Expression expr]
	:
		i=simpleIdentifier { $expr = new VariableRef($i.identifier); }  |
		constant { $expr = $constant.constant; } |
		'(' expression ')' { 
			$expr = $expression.expr; 
			if ($expr != null)
				$expr.setParenthesis(true);
		} |
		objCMethodCall { $expr = $objCMethodCall.expr; } |
        '{' ( expression ( ',' expression ) * ) ? '}' |
		selectorExpr |
		protocolExpr |
		encodingExpr//|
	;
	
selectorExpr returns [Expression expr]
	:	'@selector' 
		'(' 
		selectorName 
		')'
	;

selectorName
	:	IDENTIFIER (IDENTIFIER ':')*
	;

protocolExpr
	:	'@protocol'
		'('
		IDENTIFIER
		')'
	;

encodingExpr
	:	'@encode' 
		'('
		IDENTIFIER 
		')'
	;

assignmentExpr returns [Expression expr]
	:	e=inlineCondExpr  { $expr = $e.expr; } 
		( 
			op=assignmentOp f=assignmentExpr { $expr = new AssignmentOp($expr, getAssignmentOperator($op.text), $f.expr); }
		)?
	;
	
assignmentOp returns [Expression.AssignmentOperator op]
	: 	t=('=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|=' | '~=') {
			$op = getAssignmentOperator($t.text);
		}
	;

inlineCondExpr returns [Expression expr]
	:	e=logOrExpr { $expr = $e.expr; } 
		(
			'?'
			logOrExpr 
			':'
			logOrExpr
		)*
	;

addExpr returns [Expression expr]
	:	e=multExpr { $expr = $e.expr; }
		(
			op=('+' | '-')
			f=multExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

multExpr returns [Expression expr]
	:	e=castExpr  { $expr = $e.expr; }
		(
			op=('%' | '*' | '/') 
			f=castExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

bitOrExpr returns [Expression expr]
	:	e=xorExpr  { $expr = $e.expr; }
		(
			op='|'
			f=xorExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

bitAndExpr returns [Expression expr]
	:	e=equalExpr { $expr = $e.expr; }
		(
			op='&'
			f=equalExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;


shiftExpr returns [Expression expr]
	:	e=addExpr { $expr = $e.expr; }
		(
			op=('>>' | '<<')
			f=addExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

xorExpr returns [Expression expr]
	:	e=bitAndExpr { $expr = $e.expr; }
		(
			op='^'
			f=bitAndExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

logOrExpr returns [Expression expr]
	:	e=logAndExpr { $expr = $e.expr; }
		(
			op='||'
			f=logAndExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

logAndExpr returns [Expression expr]
	:	e=bitOrExpr { $expr = $e.expr; }
		(
			op='&&'
			f=bitOrExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

equalExpr returns [Expression expr]
	:	e=compareExpr { $expr = $e.expr; }
		(
			op=('!=' | '==')
			f=compareExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

compareExpr returns [Expression expr]
	:	e=shiftExpr { $expr = $e.expr; }
		(
			op=('<' | '<=' | '>' | '>=') 
			f=shiftExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
		)*
	;

castExpr returns [Expression expr]
	:	'(' tr=mutableTypeRef ')' (
			inner=castExpr { $expr = new Cast($tr.type, $inner.expr); } |
			'(' expression ( ',' expression ) + ')' // TODO OpenCL tuple cast 
		) | 
		e=unaryExpr { $expr = $e.expr; }
	;

unaryExpr returns [Expression expr] 
	:
		{ next("sizeof") }? IDENTIFIER (
			'(' tr=mutableTypeRef ')' {
				$expr = new FunctionCall(varRef("sizeof"), new TypeRefExpression($tr.type));
			}
			//| unaryExpr // TODO check this !!!
		) |
		p=postfixExpr { $expr = $p.expr; } |
		uo=unaryOp castExpr { $expr = new UnaryOp($castExpr.expr, $uo.op); }
	;

unaryOp returns [Expression.UnaryOperator op]
	: 	t=('++' | '--' | '&' | '*' | '-' | '~' | '!') {
			$op = Expression.getUnaryOperator($t.text);
		}
	;

postfixExpr returns [Expression expr] 
@init {
	List<Expression> multiArrayDims = new ArrayList<Expression>();
}
	: 
		{ next("new") }? IDENTIFIER tr=nonMutableTypeRef
		(
			(
				topLevelExprList? {
					if ($topLevelExprList.exprs == null)
						$expr = new New(tr);
					else
						$expr = new New(tr, $topLevelExprList.exprs);
				} 
			) |
			(
				(
					'[' dim=expression ']' {
						multiArrayDims.add($expression.expr);
					}
				)+
				{
					NewArray na = new NewArray();
					na.setType(tr);
					na.setDimensions(multiArrayDims);
					$expr = na;
				}
			)
		) |
		baseExpression { $expr = $baseExpression.expr; }
		(
			'[' expression ']' { 
				$expr = new ArrayAccess($expr, $expression.expr); 
			} |
			'(' topLevelExprList? ')' {
				FunctionCall fc = new FunctionCall($expr);
				if ($topLevelExprList.exprs != null)
					for (Expression x : $topLevelExprList.exprs)
						fc.addArgument(x);
				$expr = fc;
			} |
			'::' ao=simpleIdentifier {
				$expr = new MemberRef($expr, MemberRefStyle.Colons, $ao.identifier); 
			} |
			'.' di=simpleIdentifier { 
				$expr = new MemberRef($expr, MemberRefStyle.Dot, $di.identifier); 
			} |
			'->' ai=simpleIdentifier { 
				$expr = new MemberRef($expr, MemberRefStyle.Arrow, $ai.identifier); 
			} |
			postfixOp { 
				$expr = new UnaryOp($expr, $postfixOp.op); 
			}
		)*
	;

topLevelExpr returns [Expression expr]
	:	e=assignmentExpr { $expr = $e.expr; }
	;
topLevelExprList returns [List<Expression> exprs]
	:	
		{ $exprs = new ArrayList<Expression>(); }
		e=topLevelExpr { $exprs.add($e.expr); }
		(
			','
			f=topLevelExpr { $exprs.add($f.expr); }
		)*
	;

expression returns [Expression expr]
	:	l=topLevelExprList {
			if ($l.exprs != null) {
				if ($l.exprs.size() == 1)
					$expr = $l.exprs.get(0);
				else
					$expr = new ExpressionSequence($l.exprs);
			}
		}
	;

	
statementsBlock returns [Block statement]
scope Symbols; 
@init {
	$Symbols::typeIdentifiers = new HashSet<String>();
}
	:	{ $statement = new Block(); }
		'{'
		(
			statement {
				$statement.addStatement($statement.statement);
			} |
			lineDirective
		)* 
		'}' 
	;
	
gccAsmInOut
	:
		STRING '(' IDENTIFIER ')'
	;
gccAsmInOuts
	:
		gccAsmInOut ( ',' gccAsmInOut )*
	;
	
statement returns [Statement statement]
	:
		b=statementsBlock { 
			$statement = $b.statement; 
		} |
		// see http://ibiblio.org/gferg/ldp/GCC-Inline-Assembly-HOWTO.html
		{ next("__asm__") }? IDENTIFIER '('
			STRING* ( ':' gccAsmInOuts )*
		')' ';' ? |
		{ next("foreach") }? IDENTIFIER '(' varDecl { next("in") }? IDENTIFIER expression ')' statement { 
			// TODO
		} |
		declaration { 
			$statement = stat($declaration.declaration); 
		} |
		es=expression ';' { 
			$statement = new ExpressionStatement($es.expr); 
		} |
		rt='return' rex=expression? ';' { 
			$statement = mark(new Return($rex.expr), getLine($rt));
		} |
		IDENTIFIER ':' | // label
		'break' ';' |
		'if' '(' ifTest=topLevelExpr ')' thn=statement ('else' els=statement)? { 
			$statement = new Statement.If(ifTest.expr, thn, els);
		} | 
		'while' '(' whileTest=topLevelExpr ')' wh=statement { 
			$statement = new Statement.While(whileTest.expr, wh);
		} | 
		'do' doStat=statement 'while' '(' doWhileTest=topLevelExpr ')' ';' { 
			$statement = new Statement.DoWhile(doWhileTest.expr, doStat);
		} | 
		'for' '('
			(
				(varDecl | expression) ? ';' expression? ';' expression?  |
				varDecl ':' expression // Java foreach
			)
				')' forStat=statement { 
			// TODO
		} | 
		'switch' '(' expression ')' '{' // TODO
			(	
				'case' topLevelExpr ':' |
				statement |
				lineDirective
			)*
		'}' |
		';'
	;
	
constant returns [Constant constant]
	:	s=('-' | '+')? (
			DECIMAL_NUMBER { $constant =  Constant.parseDecimal(($s.text == null ? "" : $s.text) + $DECIMAL_NUMBER.text); } |
			HEXADECIMAL_NUMBER { $constant = Constant.parseHex($HEXADECIMAL_NUMBER.text, "-".equals($s.text)); } |
			OCTAL_NUMBER { $constant = Constant.parseOctal($OCTAL_NUMBER.text, "-".equals($s.text)); }
		) |
		CHARACTER { $constant =  Constant.parseCharOrStringInteger($CHARACTER.text); } |
		s2=('-' | '+')? FLOAT_NUMBER { $constant = Constant.parseFloat(($s2.text == null ? "" : $s2.text) + $FLOAT_NUMBER.text); } |
		//CHARACTER { $constant =  Constant.parseChar($CHARACTER.text); } |
		STRING { $constant =  Constant.parseString($STRING.text); }
	;
	
	
fragment
Letter
	:	'$' |
		'_' |
		'A'..'Z' |
		'a'..'z'
	;

javaTemplateArg returns [Expression expr]
	:
		t=javaType {
			$expr = expr($t.typeRef);
		} | 
		tk='?' {
			$expr = expr($tk.text);
		}
	;
		
javaTypeIdent returns [Identifier ident]
	:
		f=IDENTIFIER { 
			$ident = ident($f.text); 
		}
		( 
			'.' n=IDENTIFIER {
				$ident = $ident.derive(Identifier.QualificationSeparator.Dot, $n.text);	
			} 
		)+
		(
			'<'
				first=javaTemplateArg {
					$ident.resolveLastSimpleIdentifier().addTemplateArgument($first.expr);
				}
				(
					','
					other=javaTemplateArg  {
						$ident.resolveLastSimpleIdentifier().addTemplateArgument($other.expr);
					}
				)*
			'>'
		)?
		
	;
	
javaType returns [TypeRef typeRef]
	:
		i=javaTypeIdent {
			$typeRef = new TypeRef.SimpleTypeRef($i.ident);
		}
		(
			'[' ']' {
				$typeRef = new TypeRef.ArrayRef($typeRef);
			}
		)
	;

javaArg returns [Arg arg]
	:
		t=javaType n=IDENTIFIER {
			$arg = new Arg($n.text, $t.typeRef);
		}
	;
	
javaMethodDeclaration returns [Function function]
	:
		ret=javaType
		name=IDENTIFIER {
			$function = new Function(); 
			$function.setType(Function.Type.JavaMethod);
			$function.setName(ident($name.text));
			$function.setValueType($ret.typeRef);
		}
		'('
		first=javaArg {
			$function.addArg($first.arg);
		}
		(
			','
			other=javaArg {
				$function.addArg($other.arg);
			}
		)
		')'
		';'
	;
		
IDENTIFIER
	:	
		(
			//(
				Letter 
				(
					Letter |
					'0'..'9'
				)*
			//) |
			/*(
				'operator'
				(
					'+' '+'? '='? |
					'-' '-'? '='? |
					'*' '='? | 
					'/' '='? | 
					'%' '='? | 
					'<' '<'? '='? | 
					'>' ('>' '>'?)? '='? | 
					'^' '='? | 
					'|' '|'? '='? | 
					'&' '&'? '='? | 
					'=' '='? |
					'!' '='? |
					'~' '='?
				)
			)*/
		)
	;

fragment
FloatingPointExponentSuffix
	:	('e' | 'E')
		('+' | '-')?
		('0'..'9')+
	;

fragment
FloatingPointConstantSuffix
	:	'f' |
		'F' |
		'd' |
		'D'
	;

fragment
CharEscape
	:	'\\'
		(
			'b' | 
			't' | 
			'n' | 
			'f' | 
			'r' | 
			'\"' | 
			'\'' | 
			'\\' 
		) |
		OctalEscape 
	;

fragment
OctalEscape
	:	'\\' (
			('0'..'3') ('0'..'7') ('0'..'7') |
			('0'..'7') ('0'..'7') |
			('0'..'7')
		)
	;

fragment
UnicodeEscape
	:	'\\'
		'u'
		HexDigit HexDigit HexDigit HexDigit
	;


CHARACTER
	:	'\'' 
		(
			CharEscape |
			~(
				'\\' |
				'\''
			)
		)+
		'\''
	;

STRING
	:	'"'
		(
			CharEscape |
			~(
				'\\' |
				'"'
			)
		)*
		'"'
	;


fragment
HexDigit
	:	'0'..'9' |
		'a'..'f' |
		'A'..'F'
	;

fragment
IntegerConstantSuffix
	:	('u' | 'U') |
		(
			('l' | 'L')
			('l' | 'L')?
		)
	;

HEXADECIMAL_NUMBER
	:	'0' ('x'|'X') 
		HexDigit+
		IntegerConstantSuffix?
	;

DECIMAL_NUMBER
	:	//('-' | '+')?
		('0' | '1'..'9' '0'..'9'*)
		IntegerConstantSuffix?
	;

OCTAL_NUMBER
	: 	'0'
		('0'..'7')+
		IntegerConstantSuffix?
	;

FLOAT_NUMBER
	:	//('-' | '+')?
		('0'..'9')+
		(
			'.'
			('0'..'9')*
		)?
		FloatingPointExponentSuffix?
		FloatingPointConstantSuffix?
	;

WS
	:	(
			' ' |
			'\r' |
			'\t' |
			'\u000C' |
			'\n'
		) {
			$channel=HIDDEN;
		}
    ;

COMMENT
	:	(
			'/*' 
			( options {greedy=false;} : . )* 
			'*/'
		)  { 
			$channel=HIDDEN; 
		}
	;

LINE_COMMENT
	:	(
			'//'
			~('\n'|'\r')*
			('\r'? '\n' | { input.LT(1) == EOF }? )
		) { 
			$channel=HIDDEN;
		}
    ;

