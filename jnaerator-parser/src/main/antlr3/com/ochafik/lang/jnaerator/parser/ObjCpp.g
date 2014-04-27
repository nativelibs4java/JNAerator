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
*/


grammar ObjCpp;
options {
  backtrack = true;
  memoize = true;
  k = 2;
}

scope Symbols {
  Set<String> typeIdentifiers;
}
scope CurrentClass {
  Identifier name;
}
scope ModifierKinds {
  EnumSet<ModifierKind> allowedKinds;
  EnumSet<ModifierKind> forbiddenKinds;
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
  public void setupScopes() {
    if (hasModifierKinds())
      return;
      
        Symbols_scope ss = new Symbols_scope();
        ss.typeIdentifiers = new HashSet();
        Symbols_stack.push(ss);
        
        ModifierKinds_scope mk = new ModifierKinds_scope();
        
        //mk.allowedKinds = EnumSet.allOf(ModifierKind.class);
        
        mk.forbiddenKinds = EnumSet.noneOf(ModifierKind.class);
        mk.forbiddenKinds.addAll(Arrays.asList(
          ModifierKind.VCParameterAnnotation, 
          ModifierKind.Attribute,
          ModifierKind.Declspec,
          ModifierKind.Java,
          //ModifierKind.ObjectiveC, // TODO find a way to disable this...
          ModifierKind.OpenCL,
          ModifierKind.StringAnnotation
      ));
        //mk.forbiddenKinds.add(ModifierKind.ObjectiveCRemoting);
        
        ModifierKinds_stack.push(mk);
  }
  void setCurrentClassName(Identifier name) {
    ((CurrentClass_scope)CurrentClass_stack.peek()).name = name;
  }
  Identifier getCurrentClassName() {
    if (CurrentClass_stack.isEmpty())
      return null;
      
    return ((CurrentClass_scope)CurrentClass_stack.peek()).name;
  }
  boolean hasModifierKinds() {
    if (ModifierKinds_stack.isEmpty())
      return false;
    if (ModifierKinds_stack.size() > 1)
      return true;
    ModifierKinds_scope kinds = (ModifierKinds_scope)ModifierKinds_stack.get(0);
    return kinds.allowedKinds != null || kinds.forbiddenKinds != null;
  }
  ModifierKinds_scope getModifierKinds() {
    if (!hasModifierKinds())
      setupScopes();
//      return null;
    return (ModifierKinds_scope)ModifierKinds_stack.get(ModifierKinds_stack.size() - 1);
  }
  public void forbidKinds(ModifierKind... kinds) {
    ModifierKinds_scope scope = getModifierKinds();
    if (scope == null)
      return;
    if (scope.forbiddenKinds == null)
      scope.forbiddenKinds = EnumSet.copyOf(Arrays.asList(kinds));
    else
      scope.forbiddenKinds.addAll(Arrays.asList(kinds));
      
    if (scope.allowedKinds != null)
      scope.allowedKinds.removeAll(Arrays.asList(kinds));
  }
  public void allowKinds(ModifierKind... kinds) {
    ModifierKinds_scope scope = getModifierKinds();
    if (scope == null)
      return;
    if (scope.allowedKinds == null)
      scope.allowedKinds = EnumSet.copyOf(Arrays.asList(kinds));
    else
      scope.allowedKinds.addAll(Arrays.asList(kinds));
      
    if (scope.forbiddenKinds != null)
      scope.forbiddenKinds.removeAll(Arrays.asList(kinds));
  }
  public boolean isAllowed(Modifier mod) {
    if (!hasModifierKinds())
      setupScopes();
    
    int nScopes = ModifierKinds_stack.size();
    for (int i = nScopes; i-- != 0;) {
      ModifierKinds_scope scope = (ModifierKinds_scope)ModifierKinds_stack.get(i);
      boolean allowed = false;
      for (ModifierKind kind : mod.getKinds()) {
        if (scope.forbiddenKinds != null && scope.forbiddenKinds.contains(kind))
          return false;
        if (scope.allowedKinds != null && scope.allowedKinds.contains(kind))
          allowed = true;
      }
      if (allowed)
        return true;
    }
    return true;
    //if (true) return true;
    /*ModifierKinds_scope scope = getModifierKinds();
    if (scope == null)
      return true;
    return scope.allowedKinds.containsAll(mod.getKinds());*/
  }
  public void addTypeIdent(String ident) {
    try {
      $Symbols::typeIdentifiers.add(ident);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
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
    while (index > 0) {
      Token token = getTokenStream().get(--index);
      switch (token.getType()) {
        case COMMENT:
        case LINE_COMMENT:
          String comment = token.getText();
          while (index > 0) {
            Token prevToken = getTokenStream().get(--index);
            switch (prevToken.getType()) {
              case COMMENT:
              case LINE_COMMENT:
                if (prevToken.getText().indexOf("\n") >= 0) {
                  return comment;
                }
                break;
              default:
                if (getLine(prevToken) < getLine(token)) {
                  return comment;
                } else {
                  return null;
                }
            }
          }
          if (index == 0) {
            return comment;
          }
          break;
        case SEMICOLON:
        case LBRACE:
        case RBRACE:
        case LPAREN:
        case RPAREN:
        case COMMA:
          return null;
        default:
          break;
      }
    }
    return null;
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
    Modifier mod = ModifierType.parseModifier(next(), anyModKind);
    if (mod == null)
      return null;
    if (mod.isAllOf(ModifierKind.ObjectiveC, ModifierKind.OnlyInArgDef) && !isObjCArgDef())
      return null;
    if (mod.isA(ModifierKind.Java))
      return null;
      
    //if (mod.isAnyOf(ModifierKind.Declspec, ModifierKind.Attribute) && !isInExtMod())
    //  return null;
      
    if (!mod.isAnyOf(anyModKind) && !isAllowed(mod))
      return null;
    
    return mod;
  }
  protected boolean next(ModifierKind... anyModKind) {
    return parseModifier(next(), anyModKind) != null;
  } 
  protected boolean next(String... ss) {
    return next(1, ss);
  }
  protected boolean next(Identifier id) {
    if (id == null)
      return false;
    return next(id.toString());
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
      checkInterrupt();
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
  protected boolean checkInterrupt() {
    if (Thread.interrupted())
      throw new RuntimeException(new InterruptedException());
      
    return true;
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
  :  ln='#line' line=DECIMAL_NUMBER {
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
    ) 
    depth=DECIMAL_NUMBER
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
      } |
      { next("extern") }?=> IDENTIFIER STRING LBRACE |
      RBRACE
    )* 
     EOF
   ;

externDeclarations returns [ExternDeclarations declaration]
  :  { next("extern") }?=> IDENTIFIER
    STRING {
      $declaration = mark(new ExternDeclarations(), getLine($STRING));
      $declaration.setLanguage($STRING.text);
    }
    (
      LBRACE 
        (
          ed=declaration { 
            $declaration.addDeclaration($ed.declaration); 
          } |
          lineDirective
        )* 
      RBRACE |
      dd=declaration { 
        $declaration.addDeclaration($dd.declaration); 
        }
    )
  ;

declaration returns [Declaration declaration, List<Modifier> modifiers, String preComment, int startTokenIndex, Template template]
scope ModContext;
@before {
  checkInterrupt();
}
@after {
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
  if ($template != null) {
    $template.setDeclaration($declaration);
    if ($declaration instanceof TaggedTypeRefDeclaration) {
      TaggedTypeRefDeclaration ttrd = (TaggedTypeRefDeclaration)$declaration;
      TaggedTypeRef ttr = (TaggedTypeRef)ttrd.getTaggedTypeRef();
      if (ttr != ttr)
        defineTypeIdentifierInParentScope(ttr.getTag());
    }
    $declaration = $template;
  }
}
  :  
      // GCC extension.
    ( { next("__extension__") }? IDENTIFIER )?
    ( tp=templatePrefix { $template = $tp.template; } )?
    {
      $modifiers = new ArrayList<Modifier>();
      $startTokenIndex = getTokenStream().index();
      $preComment = getCommentBefore($startTokenIndex);
    }
    (
      (
        { next("__pragma") }?=> pragmaContent |
        functionDeclaration {
          $declaration = $functionDeclaration.function;
        } |
        { next("extern") }?=> externDeclarations {
          $declaration = $externDeclarations.declaration; 
        } |
        { next("using") }?=> IDENTIFIER qualifiedIdentifier SEMICOLON {
          // TODO
        } |
        varDecl SEMICOLON { 
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
        //nonMutableTypeRef SEMICOLON | // TODO
        namespaceDecl {
          $declaration = $namespaceDecl.namespace;
        }// | 
        //SEMICOLON */// allow isolated semi-colons
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
    'namespace' ns=IDENTIFIER LBRACE {
      $namespace = new Namespace();
      $namespace.setName(new SimpleIdentifier($ns.text));
    }
    (
      subD=declaration {
        $namespace.addDeclaration($subD.declaration);
      } |
      lineDirective
    )*
    RBRACE
  ;
  
forwardClassDecl returns [List<Declaration> declarations]
  :   { $declarations = new ArrayList<Declaration>(); }
    '@class' 
    n1=IDENTIFIER { 
      $declarations.add(decl(Struct.forwardDecl(new SimpleIdentifier($n1.text), Struct.Type.ObjCClass))); 
      defineTypeIdentifierInParentScope($n1.text);
    }
    (COMMA 
    nx=IDENTIFIER { 
      $declarations.add(decl(Struct.forwardDecl(new SimpleIdentifier($nx.text), Struct.Type.ObjCClass))); 
      defineTypeIdentifierInParentScope($nx.text);
    }
    )*
    SEMICOLON 
  ;
  
functionPointerVarDecl  returns [Declaration decl]
  :  tr=mutableTypeRef {
      ($tr.type instanceof FunctionSignature) && 
      ((FunctionSignature)$tr.type).getFunction().getName() != null
    }? {
      $decl = new FunctionPointerDeclaration(((FunctionSignature)$tr.type));
    }
    SEMICOLON
  ;
  
enumItem returns [Enum.EnumItem item]
  :  n=IDENTIFIER ('=' v=topLevelExpr)? {
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
    LBRACE 
      (  
        i1=enumItem { 
          if ($i1.text != null)
            $e.addItem($i1.item); 
        }
        (
          COMMA 
          (ix=enumItem { 
            if ($ix.text != null)
              $e.addItem($ix.item); 
          })?
        )*
      )?
    RBRACE
  ;
enumCore returns [Enum e]
@init {
  List<Modifier> modifiers = new ArrayList<Modifier>();
}
  :
    t='enum'
    (
      ( m1=modifiers { modifiers.addAll($m1.modifiers); } )?
      (
      	( ':' IDENTIFIER )?
        ab=enumBody {
          $e = $ab.e;
          $e.setForwardDeclaration(false);
        } |
        tag=qualifiedIdentifier
        (
          ( m2=modifiers { modifiers.addAll($m2.modifiers); } )?
          ( ':' IDENTIFIER )?
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
        LPAREN categoryName=IDENTIFIER RPAREN {
          $struct.setCategoryName($categoryName.text);
        }
      ) |
    )
    (  
      '<' (
        p1=IDENTIFIER { $struct.addProtocol(new SimpleIdentifier($p1.text)); }
        (
          COMMA 
          px=IDENTIFIER { $struct.addProtocol(new SimpleIdentifier($px.text)); }
        )*
      )? '>'
    )?
    (
      LBRACE
      (
        '@package' | // TODO keep in AST 
        '@public' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Public); } | 
        '@private' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Private); } | 
        '@protected' { $struct.setNextMemberVisibility(Struct.MemberVisibility.Protected); } |
        (
          functionPointerOrSimpleVarDecl SEMICOLON {
            $struct.addDeclaration($functionPointerOrSimpleVarDecl.decl);
          }
        ) |
        lineDirective
      )* 
      RBRACE
    )?
    { $struct.setNextMemberVisibility(Struct.MemberVisibility.Public); }
    (
        '@required' | // TODO keep in AST 
        '@optional' | // TODO keep in AST
      objCMethodDecl { 
        $struct.addDeclaration($objCMethodDecl.function);
      } |
      objCPropertyDecl {
        $struct.addDeclaration($objCPropertyDecl.property); 
      } |
      typeDef {
        $struct.addDeclaration($typeDef.typeDef); 
      } |
      vd=varDecl SEMICOLON {
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

objCPropertyAttribute returns [Modifier modifier]
    :
        { next(ModifierKind.ObjCPropertyModifier) }? m=IDENTIFIER {
          $modifier = ModifierType.parseModifier($m.text);
        }
        (
          '=' v=IDENTIFIER {
            $modifier = new ValuedModifier($modifier, Constant.string($v.text));
          }
        )?
    ;

objCPropertyDecl returns [Property property]
@init {
  List<Modifier> modifiers = new ArrayList<Modifier>();
}
@after { 
  $property.addModifiers(modifiers);
}
  :
    '@property' 
    (
      LPAREN
          a1=objCPropertyAttribute {
            modifiers.add($a1.modifier);
          }
          (
            COMMA ax=objCPropertyAttribute {
              modifiers.add($ax.modifier);
            }
          )*
      RPAREN
    )?
    functionPointerOrSimpleVarDecl SEMICOLON {
      $property = new Property($functionPointerOrSimpleVarDecl.decl);
    }
  ;
  
objCMethodDecl returns [Function function]
scope ModContext;
@init {
  $ModContext::isObjCArgDef = true;
}
  :  {   
      $function = new Function(); 
      $function.setType(Function.Type.ObjCMethod);
    }
    tk=(
      tp='+' { 
        $function.addModifiers(ModifierType.Static); 
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
      LPAREN
        returnTypeRef=mutableTypeRef? { 
          $function.setValueType($returnTypeRef.type); 
        }
      RPAREN
    )?
    methodName=(IDENTIFIER | 'class') { 
      $function.setName(new SimpleIdentifier($methodName.text)); 
      $function.setCommentAfter(getCommentAfterOnSameLine($methodName.getTokenIndex(), null));
    } 
    (
      ':' LPAREN argType1=mutableTypeRef RPAREN argName1=IDENTIFIER {
        Arg arg = new Arg($argName1.text, $argType1.type);
        arg.setSelector($methodName.text);
        $function.addArg(arg);
      }
      (
        sel=IDENTIFIER ':' 
        LPAREN argType=mutableTypeRef RPAREN 
        argName=IDENTIFIER {
          Arg arg = new Arg($argName.text, $argType.type);
          arg.setSelector($sel.text);
          $function.addArg(arg);
        }
      )*
      (
        COMMA '...' {
          $function.addArg(Arg.createVarArgs());
        }
      )?
    )?
    SEMICOLON
  ;

structBody returns [Struct struct]
scope ModContext;
scope Symbols; 
@init {
  $Symbols::typeIdentifiers = new HashSet<String>();
}
  :
    { 
      $struct = new Struct();
      $struct.setForwardDeclaration(false); 
    }
    LBRACE
      (
        (
          { next("public") }? IDENTIFIER { $struct.setNextMemberVisibility(Struct.MemberVisibility.Public); } | 
          { next("private") }? IDENTIFIER { $struct.setNextMemberVisibility(Struct.MemberVisibility.Private); } | 
          { next("protected") }? IDENTIFIER { $struct.setNextMemberVisibility(Struct.MemberVisibility.Protected); } 
        ) ':' |
        { next("friend") }? IDENTIFIER (
          friendDecl=declaration {
            $struct.addDeclaration(new FriendDeclaration(friendDecl.declaration));
          } |
          friendVar=varDecl SEMICOLON {
            $struct.addDeclaration(new FriendDeclaration(decl.declaration));
          }
        ) |
        tp=templatePrefix?
        { next(getCurrentClassName()) }? id=IDENTIFIER s=functionDeclarationSuffix {
          Function f = new Function();
          f.setName(getCurrentClassName());
          f.setType(Function.Type.CppMethod);
          f.setArgs($s.args);
          f.addModifiers($s.postModifiers);
          f.setInitializers($s.initializers);
          f.setBody($s.body);
          
          if ($tp.template != null) {
            $tp.template.setDeclaration(f);
            $struct.addDeclaration($tp.template);
          } else {
            $struct.addDeclaration(f);
          }
        } |
        decl=declaration {
          $struct.addDeclaration($decl.declaration);
        } |
        fv=varDecl SEMICOLON {
          $struct.addDeclaration($fv.decl);
        } |
        lineDirective
      )*
    RBRACE
  ;

structCore returns [Struct struct]
scope Symbols;
scope ModContext;
scope CurrentClass;
@init {
  $Symbols::typeIdentifiers = new HashSet<String>();
  List<Modifier> modifiers = new ArrayList<Modifier>();
  Identifier parentIdentifier = null;
}
@after {
  $struct = mark($struct, getLine($typeToken)); 
  $struct.setType(
    $typeToken.text.equals("struct") ?  Struct.Type.CStruct :
    $typeToken.text.equals("union") ?  Struct.Type.CUnion :
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
  $struct.addModifiers(modifiers);
}
  :  
    //{ next("struct", "class", "union") }?=> typeToken=IDENTIFIER
    //typeToken=('struct' | 'union' | { next("class") }?=> IDENTIFIER)
    typeToken=('struct' | 'union' | 'class')
    (
      ( m1=modifiers { modifiers.addAll($m1.modifiers); } )?
      (
        ab=structBody {
          $struct = $ab.struct;
          $struct.setForwardDeclaration(false);
        } |
        tag=qualifiedIdentifier {
          defineTypeIdentifierInParentScope($tag.identifier);
          setCurrentClassName($tag.identifier);
        }
        (
          (
            ( m2=modifiers { modifiers.addAll($m2.modifiers); } )?
            (
              ':'
              ( { next("public", "private", "virtual") }? p=IDENTIFIER )?//m3=modifiers
              parent=qualifiedIdentifier {
                parentIdentifier = $parent.identifier;
              }
            )? 
            nb=structBody {
              $struct = $nb.struct;
              $struct.setForwardDeclaration(false);
              if (parentIdentifier != null)
                $struct.addParent(parentIdentifier);
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
  :  binaryOp { $op = $binaryOp.op; } | 
    unaryOp { $op = $unaryOp.op; } | 
    assignmentOp { $op = $assignmentOp.op; } 
  ;

//structInsides returns [List<Declaration> declarations, Struct.MemberVisibility

functionDeclaration returns [Function function]
scope Symbols;
@init {
  $Symbols::typeIdentifiers = new HashSet<String>();
}
  :  {   
      $function = mark(new Function(), -1);
      $function.setType(Function.Type.CFunction);
    }
    (
      // C++ cast operator
      { next("operator") }?=> IDENTIFIER {
        $function.setName(ident("operator")); 
        mark($function, getLine($start));
      } 
      ( castPreMods=modifiers { $function.addModifiers($castPreMods.modifiers); } )?
      castTypeRef=mutableTypeRef { 
        $function.setValueType($castTypeRef.type); 
      } |
      ( preMods1=modifiers { $function.addModifiers($preMods1.modifiers); } )?
      returnTypeRef=mutableTypeRef { 
        $function.setValueType($returnTypeRef.type); 
      }
      ( preMods2=modifiers { $function.addModifiers($preMods2.modifiers); } )?
      name=qualifiedCppFunctionName {
        $function.setName($name.identifier); 
        mark($function, getLine($start));
      }
    )
    s=functionDeclarationSuffix {
      $function.setArgs($s.args);
      $function.addModifiers($s.postModifiers);
      if (!$s.initializers.isEmpty()) {
        $function.setInitializers($s.initializers);
        $function.setType(Function.Type.CppMethod);
      }
      $function.setThrows($s.thrown != null);
      $function.setThrown($s.thrown);
      $function.setBody($s.body);
    }
  ;

functionDeclarationSuffix returns [List<Arg> args, List<Modifier> postModifiers, List<FunctionCall> initializers, Block body, List<TypeRef> thrown]
  :   
    {
      $initializers = new ArrayList<FunctionCall>();
    }
    argList {
      $args = $argList.args;
    }
    ( postMods=modifiers { $postModifiers = $postMods.modifiers; } )?
    (
      { next("throw") }? IDENTIFIER {
        $thrown = new ArrayList<TypeRef>();
      }
      LPAREN
        (
          t1=mutableTypeRef {
            $thrown.add($t1.type);
          }
          (
            COMMA
            tx=mutableTypeRef {
              $thrown.add($tx.type);
            }
          )*
        )?
      RPAREN
    )?          
    (
      ':'
      i1=constructorInitializer { $initializers.add($i1.init); }
      (
        COMMA ix=constructorInitializer { $initializers.add($ix.init); }
      )*
    )?
    (  
      ( 
        { next(2, "0") }? '=' DECIMAL_NUMBER // TODO mark in DOM
      )?
      SEMICOLON |
      statementsBlock {
        $body = $statementsBlock.statement;
      }
    )
  ;
constructorInitializer returns [FunctionCall init]
  :  qn=qualifiedCppFunctionName {
      $init = new FunctionCall(new TypeRefExpression(new SimpleTypeRef($qn.identifier)));
    }  
    LPAREN (
      el=topLevelExprList { $init.addArguments($el.exprs); }
    )? RPAREN
  ;
  
modifiers returns [List<Modifier> modifiers]
@init { $modifiers = new ArrayList<Modifier>(); }
  :   
    m=modifier {
      $modifiers.addAll($m.modifiers); 
    }
    (  
      x=modifier { 
        $modifiers.addAll($x.modifiers); 
      } 
    )*
  ;

pragmaContent  :  
    //{ next("__pragma") }?=> pragmaContent
      // MSVC-specific : parse as token soup for now
      IDENTIFIER LPAREN
        (IDENTIFIER | constant | COMMA | ':' | LPAREN (IDENTIFIER | constant | COMMA | ':')* RPAREN)*
      RPAREN
      SEMICOLON?
    //) 
  ;

modifier returns [List<Modifier> modifiers, String asmName]
@init { $modifiers = new ArrayList<Modifier>(); }
  :
    
    { next("__pragma") }?=> pragmaContent | 
    { next("extern") }?=> IDENTIFIER ex=STRING {
      $modifiers.add(ModifierType.Extern); // TODO
    } |
    { parseModifier(next()) != null }? m=IDENTIFIER {
      $modifiers.add(ModifierType.parseModifier($m.text));
    } |
    //{ next("__declspec", "__attribute__") }?=> IDENTIFIER
    ( '__declspec' | '__attribute__' )
    LPAREN+ (  
      { next(ModifierKind.Extended) }? m=IDENTIFIER 
      (
        LPAREN arg=constant RPAREN {
          $modifiers.add(new ValuedModifier(ModifierType.parseModifier($m.text), $arg.constant));
        } |
        {
          $modifiers.add(ModifierType.parseModifier($m.text));
        }
      )
      //extendedModifiers { $modifiers.addAll($extendedModifiers.modifiers); }
    
    )* RPAREN+ |
    { next("__asm") }?=> IDENTIFIER LPAREN ( 
      an=STRING { 
        String s = String.valueOf(Constant.parseString($an.text).getValue());
        if ($asmName == null) 
          $asmName = s; 
        else 
          $asmName += s; 
      } 
    )+ RPAREN |
    { next("__success") }?=>
    IDENTIFIER LPAREN 'return' binaryOp expression  RPAREN |
    
    // TODO handle it properly @see http://blogs.msdn.com/staticdrivertools/archive/2008/11/06/annotating-for-success.aspx
    { next(ModifierKind.VCAnnotation1Arg, ModifierKind.VCAnnotation2Args) }?=>
    m=IDENTIFIER LPAREN x=constant RPAREN {
      $modifiers.add(new ValuedModifier(ModifierType.parseModifier($m.text), $x.constant));
    }
  ;

//http://msdn.microsoft.com/en-us/library/dabb5z75.aspx
extendedModifiers returns [List<Modifier> modifiers]
scope ModContext;
@init {
  $ModContext::isInExtMod = true;
}
  :  { $modifiers = new ArrayList<Modifier>(); }
    (
      { next(ModifierKind.Extended) }? m=IDENTIFIER 
      (
        LPAREN arg=constant RPAREN {
          $modifiers.add(new ValuedModifier(ModifierType.parseModifier($m.text), $arg.constant));
        } |
        {
          $modifiers.add(ModifierType.parseModifier($m.text));
        }
      )
    )*
  ;
argDef  returns [Arg arg]
  :  (
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
      declarator { 
        if ($arg != null) {
          $arg.setDeclarator($declarator.decl); 
        }
      }
    )?
    /*modifiers {
      $arg.addModifiers($modifiers.modifiers);
    }*/
    ( '=' dv=topLevelExpr {
      if ($arg != null)
        $arg.setDefaultValue($dv.expr);
    } )? 
    | 
    '...' { 
      $arg = Arg.createVarArgs(); 
    }
  ;

typeMutator returns [TypeMutator mutator]
  :  
    { next("const", "__const") }?=> IDENTIFIER { 
      $mutator = TypeMutator.CONST; 
    } |
    '*' { 
      $mutator = TypeMutator.STAR;
    } |
    '&' { 
      $mutator = TypeMutator.AMPERSTAND; 
    } |
    '^' { 
      $mutator = TypeMutator.HAT; 
    } |
    '[' 
      (
      	  expression {
	        $mutator = TypeMutator.array($expression.expr); 
	      } | 
	      {
	      	$mutator = TypeMutator.BRACKETS;
	      } 
      )      
    ']' 
  ;

templatePrefix returns [Template template]
@init {
  $template = new Template();
}
  :  
    { next("template") }? IDENTIFIER 
    '<' 
    (
      t1=templateArgDecl {
        $template.addArg($t1.arg);
      }
      (
        COMMA tx=templateArgDecl {
          $template.addArg($tx.arg);
        }
      )* 
    )? 
    '>'
  ;
  
templateArgDecl returns [Arg arg]
  :  t=('class' | 'typename') n=IDENTIFIER {
      $arg = new Arg($n.text, new SimpleTypeRef($t.text));
      addTypeIdent($n.text);
    } 
    ( 
      '=' tr=mutableTypeRef {
        $arg.setDefaultValue(expr($tr.type));
      }
    )? |
    argDef {
      $arg = $argDef.arg;
    } //mutableTypeRef ('=' constant)?
    ( 
      '=' v=expression  {
        $arg.setDefaultValue($v.expr);
      }
    )? 
  ;  
  
functionSignatureSuffix returns [FunctionSignature signature, PointerStyle pointerStyle]
  :  tk=LPAREN {
      $signature = mark(new FunctionSignature(new Function(Function.Type.CFunction, null, null)), getLine($tk));
      $signature.getFunction().setType(Function.Type.CFunction);
    }
    (
      m1=modifiers {
        $signature.getFunction().addModifiers($m1.modifiers);
      }
    )?
    (
      pt=('*' | '^') {
        $pointerStyle = PointerStyle.parsePointerStyle($pt.text);
        if ($pt.text != null && $pt.text.equals("^"))
          $signature.setType(FunctionSignature.Type.ObjCBlock);
      }
      (
        m2=modifiers {
          $signature.getFunction().addModifiers($m2.modifiers);
        }
      )?
    )?
    (
      ii=IDENTIFIER {
        $signature.getFunction().setName(new SimpleIdentifier($IDENTIFIER.text));
      }
    )?
    RPAREN
    LPAREN (
      a1=argDef { 
        if (!$a1.text.equals("void"))
          $signature.getFunction().addArg($a1.arg); 
      }
      (
        COMMA 
        ax=argDef { 
          $signature.getFunction().addArg($ax.arg); 
        }
      )*
    )? RPAREN
  ;

//functionSignatureSuffixNoName returns [FunctionSignature signature]
//  :  tk=LPAREN m=modifiers? pt=('*' | '^') RPAREN { 
//      $signature = mark(new FunctionSignature(new Function(Function.Type.CFunction, null, null)), getLine($tk));
//      if ($pt.text.equals("^"))
//        $signature.setType(FunctionSignature.Type.ObjCBlock);
//      $signature.getFunction().setType(Function.Type.CFunction);
//      if ($m.modifiers != null)
//        $signature.getFunction().addModifiers($m.modifiers);
//    }
//    LPAREN (
//      a1=argDef { 
//        if (!$a1.text.equals("void"))
//          ((FunctionSignature)$signature).getFunction().addArg($a1.arg); 
//      }
//      (
//        COMMA 
//        ax=argDef { 
//          ((FunctionSignature)$signature).getFunction().addArg($ax.arg); 
//        }
//      )*
//    )? RPAREN
//  ;

mutableTypeRef returns [TypeRef type]
@before {
  checkInterrupt();
}
  :  
    nonMutableTypeRef {
      $type = $nonMutableTypeRef.type;
    }
    (
      (
        m1=typeMutator {
          $type = $m1.mutator.mutateType($type);
        }
      ) |
      ( { next(ModifierKind.StorageClassSpecifier) }? m=IDENTIFIER {
          $type.addModifiers(ModifierType.parseModifier($m.text));
      }) |
      (
        f1=functionSignatureSuffix { 
          assert $f1.signature != null && $f1.signature.getFunction() != null;
          if ($f1.signature != null && $f1.signature.getFunction() != null) {
            $f1.signature.getFunction().setValueType($type); 
            $type = $f1.signature;
            if ($f1.pointerStyle != null) {
              $type = new Pointer($type, $f1.pointerStyle);
            }
          }
        }
      )
    )*
  ;

declarator  returns [Declarator decl]
  :
    m=modifiers?
    (
      (
        (
          ( 
            directDeclarator { 
              $decl = $directDeclarator.decl; 
            } 
          ) |
          ( 
            pt=('*' | '&' | '^')
            inner=declarator {
              // TODO EMPTY DECLARATOR... maybe not the brightest idea one can have...
              $decl = new PointerDeclarator($inner.decl == null ? new DirectDeclarator(null) : $inner.decl, PointerStyle.parsePointerStyle($pt.text));
            } 
          )
        )
        (
          ':' bits=DECIMAL_NUMBER {
            if ($decl != null)
              $decl.setBits(Integer.parseInt($bits.text));
          }
        )?
      ) |
      ':' bits=DECIMAL_NUMBER {
        $decl = mark(new DirectDeclarator(null), getLine($bits));
        $decl.setBits(Integer.parseInt($bits.text));
      }
    )
    {
      if ($decl != null && $m.modifiers != null)
        $decl.setModifiers($m.modifiers);
    }
  ;

typeDef returns [TypeDef typeDef]
@after {
  for (Declarator d : $typeDef.getDeclarators()) {
    String n = d.resolveName();
    if (n != null) {
      addTypeIdent(n);
    }
  }
}
  :  'typedef'
     varDecl SEMICOLON {
       VariablesDeclaration vd = $varDecl.decl;
      $typeDef = new TypeDef(vd.getValueType(), vd.getDeclarators());
    }
  ;
  
varDeclEOF returns [Declaration decl]
  : varDecl SEMICOLON EOF { $decl = $varDecl.decl; }
  ;

declarationEOF returns [Declaration declaration]
  :   d=declaration EOF { $declaration = $d.declaration; }
  ;

varDecl returns [VariablesDeclaration decl]
  :  
    tr=nonMutableTypeRef { 
      $decl = new VariablesDeclaration($tr.type);
    }
    (
      d1=declaratorsList {
        $decl.setDeclarators($d1.declarators);
      }
    )?
  ;
  
objCProtocolRefList
  :  '<' 
    IDENTIFIER 
    (
      COMMA 
      IDENTIFIER
    )* 
    '>'
  ;

declaratorWithValue returns [Declarator decl]
  :
    declarator {
      $decl = $declarator.decl;
    }
    (
      '=' 
      dv=topLevelExpr {
        $decl.setDefaultValue($dv.expr);
      }
    )?
  ;

declaratorsList returns [List<Declarator> declarators]
  :  { $declarators = new ArrayList<Declarator>(); }
    d=declaratorWithValue { 
      $declarators.add($d.decl); 
    }
    (
      COMMA 
      x=declaratorWithValue { 
        $declarators.add($x.decl); 
      }
    )*
  ;

directDeclarator returns [Declarator decl]
  :  
    (
      { parseModifier(next()) == null }?=> IDENTIFIER {
        $decl = mark(new DirectDeclarator($IDENTIFIER.text), getLine($IDENTIFIER));
      } | 
      LPAREN inner=declarator RPAREN {
        $decl = $inner.decl;
        if ($decl != null)
          $decl.setParenthesized(true);
      } 
    )
    (
      '[' 
      (
        (
          expression {
            if ($decl instanceof ArrayDeclarator)
              ((ArrayDeclarator)$decl).addDimension($expression.expr);
            else
              $decl = new ArrayDeclarator($decl, $expression.expr);
          } 
        ) |
        (
          {
            $decl = new ArrayDeclarator($decl, new Expression.EmptyArraySize());
          }
        )
      )
      ']' | 
      argList {
        $decl = new FunctionDeclarator($decl, $argList.args);
      }
    )*
  ;

argList returns [List<Arg> args, boolean isObjC]
  :  { 
      $isObjC = false; 
      $args = new ArrayList<Arg>();
    }
    
    op=LPAREN 
    (
      a1=argDef {
        if (!$a1.text.equals("void")) 
          $args.add($a1.arg);
      }
      (
        COMMA 
        ax=argDef {
          $args.add($ax.arg);
        }
      )*
      ( 
        COMMA '...' {
          $isObjC = true;
          $args.add(Arg.createVarArgs());
        }
      )?
    )?
    cp=RPAREN
  ;

nonMutableTypeRef returns [TypeRef type]
scope ModifierKinds;
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
    ( preMods=modifiers { 
      modifiers.addAll($preMods.modifiers);
      try {
        if (ModifierType.UUID.isContainedBy(modifiers))
          allowKinds(ModifierKind.VCParameterAnnotation);
      } catch (Throwable th) {
        th.printStackTrace();
      }
      
    } )?
    (
      { next("va_list", "__gnuc_va_list", "__builtin_va_list") }?=> i=IDENTIFIER {
        $type = new SimpleTypeRef($i.text); 
      } |
      'typename' pn=typeName { $type = $pn.type; } |
      { 
        next(2, "<") ||
        isTypeIdentifier(next()) || 
        (
          parseModifier(next(1)) == null &&
          //(isTypeDef() || !next(2, "=", ",", ";", ":", "[", "(", ")"))
          !next(2, "=", ",", ";", ":", "[", "(", ")")
        ) 
      }?=> an=typeName { $type = $an.type; } |
      structCore { $type = $structCore.struct; } |
      enumCore { $type = $enumCore.e; }
    )?
    ( postMods=modifiers { modifiers.addAll($postMods.modifiers); } )?
    /*
    (
      ( preMods=modifiers { modifiers.addAll($preMods.modifiers); } )?
      (
        //{ parseModifier(next()) == null }?=> 
        tr1=typeRefInsides {
          $type = $tr1.type;
        }
        ( postMods1=modifiers { modifiers.addAll($postMods1.modifiers); } )? 
      )?
    ) |
    //{ parseModifier(next()) == null }? 
    (
      tr2=typeRefInsides {
        $type = $tr2.type;
      }
      ( postMods2=modifiers { modifiers.addAll($postMods2.modifiers); } )?
    )*/
  ;
  

typeRefInsides returns [TypeRef type]
  :
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
  ;
  
typeName returns [TypeRef type]
  :
    i=qualifiedIdentifier {
      if ($i.identifier.isPlain() && isPrimitiveType($i.identifier.toString()))
        $type = new Primitive($i.identifier.toString());
      else
        $type = new SimpleTypeRef($i.identifier);
      if ($i.identifier.isPlain())
        addTypeIdent($i.identifier.toString());
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
  :   t=(
    '+' | '-' | '*' | '/' | '%' | 
    '<<' | '>>>' | '>>' | 
    '^' | '||' | '|' | '&&' | '&' |
    '<=' | '>=' | '<' | '>' | '==' | '!='
    ) {
      $op = Expression.getBinaryOperator($t.text);
      if ($op == null)
        throw new RuntimeException("Failed to parse op " + $t.text);
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
  :  tr=mutableTypeRef {
      $expr = new Expression.TypeRefExpression($tr.type);
    } | 
    e=topLevelExpr {
      $expr = $e.expr;
    }
  ;

simpleIdentifier returns [SimpleIdentifier identifier]
  :  i=IDENTIFIER { $identifier = new SimpleIdentifier($i.text); }
    (
      '<' (
        a1=typeRefOrExpression { $identifier.addTemplateArgument($a1.expr); }
        (
          COMMA 
          ax=typeRefOrExpression  { $identifier.addTemplateArgument($ax.expr); }
        )* 
      )? '>'
    )?
  ;

qualifiedIdentifier returns [Identifier identifier]
  :  
    '::'?
    i1=simpleIdentifier { $identifier = $i1.identifier; }
    (
      '::' ix=simpleIdentifier { $identifier = $identifier.derive(QualificationSeparator.Colons, $ix.identifier); }
    )*
  ;
  
qualifiedCppFunctionName returns [Identifier identifier]
  :  i1=simpleCppFunctionName { $identifier = $i1.identifier; }
    (
      '::' ix=simpleCppFunctionName { 
          $identifier = $identifier.derive(QualificationSeparator.Colons, $ix.identifier); 
            }
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
    LPAREN RPAREN { $op = UnaryOperator.Parenthesis; } |
    COMMA { $op = BinaryOperator.Comma; } |
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
  
expressionsBlock returns [ExpressionsBlock expr]
  :
    LBRACE { 
      $expr = new ExpressionsBlock();
    }
    (
      e1=expression {
        $expr.addExpression($e1.expr);
      }
      (
        COMMA
        ex=expression {
          $expr.addExpression($ex.expr);
        }
      )*
    )?
    RBRACE
  ;
        
        
baseExpression returns [Expression expr]
  :
    i=simpleIdentifier { $expr = new VariableRef($i.identifier); }  |
    constant { $expr = $constant.constant; } |
    LPAREN expression RPAREN { 
      $expr = $expression.expr; 
      if ($expr != null)
        $expr.setParenthesis(true);
    } |
    objCMethodCall { $expr = $objCMethodCall.expr; } |
    expressionsBlock { $expr = $expressionsBlock.expr; } |
    selectorExpr |
    protocolExpr |
    encodingExpr//|
  ;
  
selectorExpr returns [Expression expr]
  :  '@selector' 
    LPAREN 
    selectorName 
    RPAREN
  ;

selectorName
  :  IDENTIFIER (IDENTIFIER ':')*
  ;

protocolExpr
  :  '@protocol'
    LPAREN
    IDENTIFIER
    RPAREN
  ;

encodingExpr
  :  '@encode' 
    LPAREN
    IDENTIFIER 
    RPAREN
  ;

assignmentExpr returns [Expression expr]
  :  e=inlineCondExpr  { $expr = $e.expr; } 
    ( 
      op=assignmentOp f=assignmentExpr { 
        $expr = new AssignmentOp($expr, getAssignmentOperator($op.text), $f.expr); 
      }
    )?
  ;
  
assignmentOp returns [Expression.AssignmentOperator op]
  :   t=('=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|=' | '~=') {
      $op = getAssignmentOperator($t.text);
    }
  ;

inlineCondExpr returns [Expression expr]
  :  e=logOrExpr { $expr = $e.expr; } 
    (
      '?'
      logOrExpr 
      ':'
      logOrExpr
    )*
  ;

addExpr returns [Expression expr]
  :  e=multExpr { $expr = $e.expr; }
    (
      op=('+' | '-')
      f=multExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

multExpr returns [Expression expr]
  :  e=castExpr  { $expr = $e.expr; }
    (
      op=('%' | '*' | '/') 
      f=castExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

bitOrExpr returns [Expression expr]
  :  e=xorExpr  { $expr = $e.expr; }
    (
      op='|'
      f=xorExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

bitAndExpr returns [Expression expr]
  :  e=equalExpr { $expr = $e.expr; }
    (
      op='&'
      f=equalExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;


shiftExpr returns [Expression expr]
  :  e=addExpr { $expr = $e.expr; }
    (
      op=('>>' | '<<')
      f=addExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

xorExpr returns [Expression expr]
  :  e=bitAndExpr { $expr = $e.expr; }
    (
      op='^'
      f=bitAndExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

logOrExpr returns [Expression expr]
  :  e=logAndExpr { $expr = $e.expr; }
    (
      op='||'
      f=logAndExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

logAndExpr returns [Expression expr]
  :  e=bitOrExpr { $expr = $e.expr; }
    (
      op='&&'
      f=bitOrExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

equalExpr returns [Expression expr]
  :  e=compareExpr { $expr = $e.expr; }
    (
      op=('!=' | '==')
      f=compareExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

compareExpr returns [Expression expr]
  :  e=shiftExpr { $expr = $e.expr; }
    (
      op=('<' | '<=' | '>' | '>=') 
      f=shiftExpr { $expr = new BinaryOp($expr, getBinaryOperator($op.text), $f.expr); }
    )*
  ;

castExpr returns [Expression expr]
  :  LPAREN tr=mutableTypeRef RPAREN (
      inner=castExpr { $expr = new Cast($tr.type, $inner.expr); } |
      LPAREN expression ( COMMA expression ) + RPAREN // TODO OpenCL tuple cast 
    ) | 
    e=unaryExpr { $expr = $e.expr; }
  ;

unaryExpr returns [Expression expr] 
  :
    { next("sizeof") }? IDENTIFIER (
      LPAREN tr=mutableTypeRef RPAREN {
        $expr = new FunctionCall(varRef("sizeof"), new TypeRefExpression($tr.type));
      }
      //| unaryExpr // TODO check this !!!
    ) |
    p=postfixExpr { $expr = $p.expr; } |
    uo=unaryOp castExpr { $expr = new UnaryOp($castExpr.expr, $uo.op); }
  ;

unaryOp returns [Expression.UnaryOperator op]
  :   t=('++' | '--' | '&' | '*' | '-' | '~' | '!') {
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
      LPAREN topLevelExprList? RPAREN {
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
  :  e=assignmentExpr { $expr = $e.expr; }
  ;
topLevelExprList returns [List<Expression> exprs]
  :  
    { $exprs = new ArrayList<Expression>(); }
    e=topLevelExpr { $exprs.add($e.expr); }
    (
      COMMA
      f=topLevelExpr { $exprs.add($f.expr); }
    )*
  ;

expression returns [Expression expr]
@before {
  checkInterrupt();
}
  :  l=topLevelExprList {
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
  :  { $statement = new Block(); }
    LBRACE
    (
      statement {
        $statement.addStatement($statement.stat);
      } |
      lineDirective
    )* 
    RBRACE 
  ;
  
gccAsmInOut
  :
    STRING LPAREN IDENTIFIER RPAREN
  ;
gccAsmInOuts
  :
    gccAsmInOut ( COMMA gccAsmInOut )*
  ;
  
statement returns [Statement stat]
  :
    b=statementsBlock { 
      $stat = $b.statement; 
    } |
    // GCC inline asm (see http://ibiblio.org/gferg/ldp/GCC-Inline-Assembly-HOWTO.html)
     
    { next("__asm__", "asm") }?=> IDENTIFIER
    ( { next("__volatile__", "volatile") }?=> IDENTIFIER )? LPAREN
      STRING* ( ':' gccAsmInOuts? )*
    RPAREN SEMICOLON ? |
    // MSVC inline asm soup
    { next("__asm") }?=> IDENTIFIER LBRACE
      ( expression | COMMA )*
    RBRACE|
    { next("foreach") }?=> IDENTIFIER LPAREN varDecl { next("in") }? IDENTIFIER expression RPAREN statement { 
      // TODO
    } |
    { next("delete") }?=> IDENTIFIER  (
      '[' ']' delArr=expression { 
        $stat = new Delete($delArr.expr, true); 
      } |
      del=expression { 
        $stat = new Delete($del.expr, false); 
      }
    ) SEMICOLON |
    pe=postfixExpr SEMICOLON {
      // Hack to make sure that f(x); is not parsed as a declaration !
      // Must stay before declaration.
      $stat = new ExpressionStatement($pe.expr); 
    } |
    declaration { 
      $stat = stat($declaration.declaration); 
    } |
    es=expression SEMICOLON { 
      $stat = new ExpressionStatement($es.expr); 
    } |
    rt='return' rex=expression? SEMICOLON { 
      $stat = mark(new Return($rex.expr), getLine($rt));
    } |
    IDENTIFIER ':' | // label
    'break' SEMICOLON |
    'if' LPAREN ifTest=topLevelExpr RPAREN thn=statement ('else' els=statement)? { 
      $stat = new Statement.If(ifTest.expr, thn, els);
    } | 
    'while' LPAREN whileTest=topLevelExpr RPAREN wh=statement { 
      $stat = new Statement.While(whileTest.expr, wh);
    } | 
    'do' doStat=statement 'while' LPAREN doWhileTest=topLevelExpr RPAREN SEMICOLON { 
      $stat = new Statement.DoWhile(doWhileTest.expr, doStat);
    } | 
    'for' LPAREN
      (
        (varDecl | expression) ? SEMICOLON expression? SEMICOLON expression?  |
        varDecl ':' expression // Java foreach
      )
        RPAREN forStat=statement { 
      // TODO
    } | 
    'switch' LPAREN expression RPAREN LBRACE // TODO
      (  
        'case' topLevelExpr ':' |
        statement |
        lineDirective
      )*
    RBRACE |
    SEMICOLON
  ;
  
constant returns [Constant constant]
  :  s=('-' | '+')? (
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
  :  '$' |
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
          COMMA
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
    LPAREN
    first=javaArg {
      $function.addArg($first.arg);
    }
    (
      COMMA
      other=javaArg {
        $function.addArg($other.arg);
      }
    )
    RPAREN
    SEMICOLON
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
  :  ('e' | 'E')
    ('+' | '-')?
    ('0'..'9')+
  ;

fragment
FloatingPointConstantSuffix
  :  'f' |
    'F' |
    'd' |
    'D'
  ;

fragment
CharEscape
  :  '\\'
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
  :  '\\' (
      ('0'..'3') ('0'..'7') ('0'..'7') |
      ('0'..'7') ('0'..'7') |
      ('0'..'7')
    )
  ;

fragment
UnicodeEscape
  :  '\\'
    'u'
    HexDigit HexDigit HexDigit HexDigit
  ;


CHARACTER
  :  '\'' 
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
  :  '"'
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
  :  '0'..'9' |
    'a'..'f' |
    'A'..'F'
  ;

fragment
IntegerConstantSuffix
  :  UnsignedConstantSuffix LongConstantSuffix? |
      LongConstantSuffix
  ;

fragment
UnsignedConstantSuffix
  :  ('u' | 'U')
  ;

fragment
LongConstantSuffix
  :  ('l' | 'L') ('l' | 'L')?
  ;

HEXADECIMAL_NUMBER
  :  '0' ('x'|'X') 
    HexDigit+
    IntegerConstantSuffix?
  ;

DECIMAL_NUMBER
  :  //('-' | '+')?
    ('0' | '1'..'9' '0'..'9'*)
    IntegerConstantSuffix?
  ;

OCTAL_NUMBER
  :   '0'
    ('0'..'7')+
    IntegerConstantSuffix?
  ;

FLOAT_NUMBER
  :  //('-' | '+')?
    ('0'..'9')+
    (
      '.'
      ('0'..'9')*
    )?
    FloatingPointExponentSuffix?
    FloatingPointConstantSuffix?
  ;

WS
  :  (
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
  :  (
      '/*' 
      ( options {greedy=false;} : . )* 
      '*/'
    )  { 
      $channel=HIDDEN; 
    }
  ;

COMMA: ',';
LBRACE: '{';
RBRACE: '}';
LPAREN: '(';
RPAREN: ')';
SEMICOLON: ';';

LINE_COMMENT
  :  (
      '//'
      ~('\n'|'\r')*
      ('\r'? '\n' | { input.LT(1) == EOF }? )
    ) { 
      $channel=HIDDEN;
    }
    ;

