package com.ochafik.lang.jnaerator;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.typeRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.util.listenable.Pair;

public class UniversalReconciliator {

	
	static class DefSeq {
		List<SimpleTypeRef> simpleTypeRefs = new ArrayList<SimpleTypeRef>();
//		List<Identifier> identifiers = new ArrayList<Identifier>();
		List<Declarator> declarators = new ArrayList<Declarator>();
		boolean matches(DefSeq o) {
			int n = declarators.size();
			if (n != declarators.size())
				return false;
			if (simpleTypeRefs.size() != o.simpleTypeRefs.size())
				return false;
			for (int i = 0; i < n; i++) {
				Declarator id = declarators.get(i), oid = declarators.get(i);
				if (!id.toString().equals(oid.toString()))
					return false;
			}
			return true;
		}
	}
	DefSeq extractDefSeq(Element tr) {
		final DefSeq ret = new DefSeq();
		tr.accept(new Scanner() {
			@Override
			public void visitDeclarator(Declarator declarator) {
				super.visitDeclarator(declarator);
				ret.declarators.add(declarator);
			}
//			public void visitIdentifier(Identifier i) {
//				super.visitIdentifier(i);
//				ret.identifiers.add(i);
//			}
			@Override
			public void visitSimpleTypeRef(SimpleTypeRef simpleTypeRef) {
				super.visitSimpleTypeRef(simpleTypeRef);
				ret.simpleTypeRefs.add(simpleTypeRef);
			}
		});
		return ret;
	}
	public static class ReconciliationException extends Exception {
		private static final long serialVersionUID = -8197343041734256268L;
		public ReconciliationException(Element t1, Element t2, Element reason1, Element reason2) {
			this(t1, t2, "\"" + reason1 + "\" and \"" + reason2 + "\" cannot be matched");
		}
		public ReconciliationException(Element t1, Element t2, String reason) {
			super("Types \"" + t1 + "\" and \"" + t2 + "\" could not be reconciliated" + (reason == null ? "" : ". Reason = " + reason));
		}
	}
	public Element reconciliate32bitsAnd64bits(Element tr32, Element tr64) throws ReconciliationException {
		if (tr32 == null && tr64 == null)
			return null;
		if ((tr32 == null) != (tr64 == null)) {
			if (tr32 == null && tr64.toString().matches("id") ||
					tr64 == null && tr32.toString().equals("id"))
				return typeRef("id");
			throw new ReconciliationException(tr32, tr64, null);
		}
		
		tr32 = tr32.clone();
		DefSeq s32 = extractDefSeq(tr32), s64 = extractDefSeq(tr64);
		if (!s32.matches(s64))
			throw new ReconciliationException(tr32, tr64, null);
		
		int n = s32.simpleTypeRefs.size();
		for (int i = 0; i < n; i++) {
			TypeRef.SimpleTypeRef t32 = s32.simpleTypeRefs.get(i), t64 = s64.simpleTypeRefs.get(i);
			if (t32.toString().equals(t64.toString()))
				continue;
			TypeRef tr = reconciliateSimple32bitsAnd64bits(t32, t64);
			if (tr == null)
				throw new ReconciliationException(tr32, tr64, t32, t64);
			if (t32 == tr32)
				tr32 = tr;
			else
				t32.replaceBy(tr);
		}
		// TODO Auto-generated method stub
		return tr32;
	}

	static Map<Pair<String, String>, TypeRef> predefined32_64Reconciliations = new HashMap<Pair<String,String>, TypeRef>();
	static {
		defRecon("float", "double", typeRef("CGFloat"));
		defRecon("long", "long long", typeRef("long"));
		defRecon("int", "long long", typeRef("NSInteger"));
		
		defRecon("int", "unsigned long long", typeRef("NSInteger"));
		defRecon("int", "signed long long", typeRef("NSInteger"));
		defRecon("long", "unsigned long long", typeRef("NSInteger"));
		defRecon("long", "signed long long", typeRef("NSInteger"));
		defRecon("unsigned long", "unsigned long long", typeRef("long").addModifiers(Modifier.Unsigned));
		defRecon("unsigned int", "unsigned long long", typeRef("NSUInteger"));//int").addModifiers(Modifier.Unsigned));
		
		defRecon("unsigned long", "unsigned int", typeRef("int").addModifiers(Modifier.Unsigned));
		defRecon("signed long", "signed int", typeRef("int").addModifiers(Modifier.Signed));
		defRecon("long", "int", typeRef("int"));
		defRecon("int", "unsigned int", typeRef("int"));

		defRecon("signed long", "signed long long", typeRef("long").addModifiers(Modifier.Signed));
		defRecon("signed int", "signed long long", typeRef("NSInteger"));//int").addModifiers(Modifier.Signed));
	}
	static void defRecon(String s32, String s64, TypeRef sRecon) {
		predefined32_64Reconciliations.put(new Pair<String, String>(s32, s64), sRecon);
	}
	/*static TypeRef cleanClone(TypeRef t) {
		t = t.clone();
		List<Modifier> mods = new ArrayList<Modifier>(t.getModifiers());
		mods.remove(Modifier.Signed);
		mods.remove(Modifier.Unsigned);
		t.setModifiers(mods);
		return t;
	}*/
	public static TypeRef reconciliateSimple32bitsAnd64bits(TypeRef t32, TypeRef t64) {
		TypeRef recon = predefined32_64Reconciliations.get(new Pair<String, String>(t32.toString(), t64.toString()));
		/*if (recon == null) {
			TypeRef tt32 = cleanClone(t32), tt64 = cleanClone(t64);
			if (tt32.toString().equals(t64))
				return tt32;
			recon = predefined32_64Reconciliations.get(new Pair<String, String>(tt32.toString(), tt64.toString()));
		}*/
		
		return recon == null ? null : recon.clone();
	}

}
