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

import java.util.Collection;
import java.util.EnumSet;
import static java.util.EnumSet.of;
import java.util.HashMap;
import java.util.Map;

import static com.ochafik.lang.jnaerator.parser.ModifierKind.*;

/**
 * @see http://msdn.microsoft.com/en-us/library/dabb5z75.aspx
 */
public enum Modifier {
	__cdecl(of(CallingConvention)),
		_cdecl(__cdecl),
	
	__stdcall(of(CallingConvention)),
		_stdcall(__stdcall),
	
	__fastcall(of(CallingConvention)),
		_fastcall(__fastcall),
	
	__thiscall(of(CallingConvention)),
		_thiscall(__thiscall),
	
	__pascal(of(CallingConvention)),
		_pascal(__pascal),
	
	/// VC++ annotations 
	/// @see http://msdn.microsoft.com/en-us/library/cc264104.aspx
	
	__pre(of(VCAnnotationNoArg)),
	__valid(of(VCAnnotationNoArg)),
	__reserved(of(VCAnnotationNoArg)),
	__checkReturn(of(VCAnnotationNoArg)),
	__fallthrough(of(VCAnnotationNoArg)),
	__readonly(of(VCAnnotationNoArg)),
	__null(of(VCAnnotationNoArg)),
	__in(of(VCAnnotationNoArg)),
	__out(of(VCAnnotationNoArg)),
	__inout(of(VCAnnotationNoArg)),
	__refparam(of(VCAnnotationNoArg)),
	__exceptthat(of(VCAnnotationNoArg)),
	
	_opt(of(VCAnnotationNoArg)),
	_deref(of(VCAnnotationNoArg)),
	_deref_opt(of(VCAnnotationNoArg)),
	_ecount(of(VCAnnotation1Arg)),
	_bcount(of(VCAnnotation1Arg)),
	_full(of(VCAnnotation1Arg)),
	_part(of(VCAnnotation2Args)),
	
	__ptr64(of(TypeQualifier)), // TODO find better kind 
	__maybenull(of(TypeQualifier)),
	__nullterminated(of(TypeQualifier, StringAnnotation)),
	__nullnullterminated(of(TypeQualifier, StringAnnotation)),
	__possibly_notnullterminated(of(TypeQualifier, StringAnnotation)),
	//__success,
	
	Auto(of(StorageClassSpecifier)),
	Register(of(StorageClassSpecifier)),
	Static(of(StorageClassSpecifier)), 
	Virtual(of(StorageClassSpecifier)), 
	Extern(of(StorageClassSpecifier)),
	Pascal(of(StorageClassSpecifier)),
	//TypeDef(of(StorageClassSpecifier)), // TODO propagate this to everywhere : need to remove TypeDef class
	
	__const(of(TypeQualifier)), 
	Const(__const),
	Volatile(of(TypeQualifier)), 
	Mutable(of(TypeQualifier)),
	
	__unsigned(of(NumericTypeQualifier, SignModifier)),
	__signed(of(NumericTypeQualifier, SignModifier)),
	Unsigned(__unsigned),
	Signed(__signed),
	Long(of(NumericTypeQualifier, SizeModifier)),
	Short(of(NumericTypeQualifier, SizeModifier)),
	
	Typename(of(ReferenceQualifier)),
	Struct(of(ReferenceQualifier)),
	Class(of(ReferenceQualifier)),

	/*primSignModifier
		:	'signed' | 'unsigned' | '__signed' | '__unsigned';
		*/	
	
	//Transient(of(TypeQualifier, Java)), 
	
	Public(of(Publicity)),
	Abstract(of(Publicity)),
	Final(of(Publicity)),
	Private(of(Publicity)), 
	Protected(of(Publicity)),
	
	Inline(of(C, StorageClassSpecifier)),
	__inline(Inline),
	__inline__(Inline),

	In(of(ObjectiveC, OnlyInArgDef)),
	Out(of(ObjectiveC, OnlyInArgDef)),
	InOut(of(ObjectiveC, OnlyInArgDef)),
	OneWay(of(ObjectiveC, OnlyInArgDef)),
	ByCopy(of(ObjectiveC, OnlyInArgDef)),
	ByRef(of(ObjectiveC, OnlyInArgDef)),

	Align(of(Declspec, HasArguments)),
	Allocate(of(Declspec, HasArguments)),
	AppDomain(of(Declspec)),
	Deprecated(of(Declspec, Attribute)),
	DllExport(of(Declspec, StorageClassSpecifier)),
	DllImport(of(Declspec, StorageClassSpecifier)),
	JITIntrinsic(of(Declspec)),
	Naked(of(Declspec, StorageClassSpecifier, Attribute)),
	NoAlias(of(Declspec, StorageClassSpecifier)),
	NoInline(of(Declspec)),
	NoReturn(of(Declspec)),
	NoThrow(of(Declspec, StorageClassSpecifier)),
	NoVTable(of(Declspec)),
	Process(of(Declspec)),
	Property(of(Declspec, HasArguments, StorageClassSpecifier, COMSpecific)), //TODO handle args
	Restrict(of(Declspec, StorageClassSpecifier)),
	SelectAny(of(Declspec, StorageClassSpecifier, COMSpecific)),
	Thread(of(Declspec)),
	UUID(of(Declspec, HasArguments, StorageClassSpecifier, COMSpecific)),
	
	Alias(of(Attribute)),
	Always_inline(of(Attribute)),
	Cdecl(of(Attribute)),
	//Const(of(Attribute)),
	Constructor(of(Attribute)),
	Destructor(of(Attribute)),
	Dllexport(of(Attribute)),
	Weak_import(of(Attribute)),
	Dllimport(of(Attribute)),
	Eightbit_data(of(Attribute)),
	Exception(of(Attribute)),
	Far(of(Attribute)),
	Fastcall(of(Attribute)),
	Format(of(Attribute)),
	Format_arg(of(Attribute)),
	Function_vector(of(Attribute)),
	Interrupt(of(Attribute)),
	Interrupt_handler(of(Attribute)),
	Long_call(of(Attribute)),
	Short_call(of(Attribute)),
	Longcall(of(Attribute)),
	Shortcall(of(Attribute)),
	Malloc(of(Attribute)),
	Model(of(Attribute)),
	Near(of(Attribute)),
	No_check_memory_usage(of(Attribute)),
	No_instrument_function(of(Attribute)),
	Noinline(of(Attribute)),
	Nonnull(of(Attribute)),
	Noreturn(of(Attribute)),
	Nothrow(of(Attribute)),
	Pure(of(Attribute)),
	Regparm(of(Attribute)),
	Saveall(of(Attribute)),
	Section(of(Attribute)),
	Signal(of(Attribute)),
	Sp_switch(of(Attribute)),
	Stdcall(of(Attribute)),
	Tiny_data(of(Attribute)),
	Trap_exit(of(Attribute)),
	Unused(of(Attribute)),
	Used(of(Attribute)),
	Visibility(of(Attribute)),
	Warn_unused_result(of(Attribute)),
	Weak(of(Attribute)), 
	
    //Complex(of(OpenCL)),
    __kernel(of(OpenCL)),
    __global(of(OpenCL)),
    __read_only(of(OpenCL)),
    __write_only(of(OpenCL)),
    __local(of(OpenCL)),
    __constant(of(OpenCL)),
    __private(of(OpenCL)),

    Synchronized(of(Java)),
	Native(of(Java));
	
	EnumSet<ModifierKind> kinds;
	Modifier alias;

	/*
	Modifier(Kind... kinds) {
		this.kinds = EnumSet.noneOf(Kind.class);
		for (Kind kind : kinds)
			this.kinds.add(kind);
	}
	Modifier(Modifier alias, Kind... kinds) {
		this(kinds);
		this.alias = alias;
	}
	Modifier(EnumSet<Kind> kinds) {
		this.alias = alias;
	}*/
	Modifier(Modifier alias) {
		this.kinds = alias.kinds;
		this.alias = alias;
	}
	Modifier(EnumSet<ModifierKind> kinds) {
		this.kinds = kinds;
	}

	public Modifier resolveAlias() {
		if (alias == null)
			return this;
		return alias.resolveAlias();
	}
	
	public boolean isContainedBy(Collection<Modifier> modifiers) {
		Modifier alias = resolveAlias();
		for (Modifier modifier : modifiers)
			if (modifier.resolveAlias().equals(alias))
				return true;
		return false;
	}
	public int countIn(Collection<Modifier> modifiers) {
		Modifier alias = resolveAlias();
		int c = 0;
		for (Modifier modifier : modifiers)
			if (modifier.resolveAlias().equals(alias))
				c++;
		return c;
	}
	public Modifier getAlias() {
		return alias;
	}

	static Map<String, Modifier> mods = new 
	HashMap<String, Modifier>();
	static {
		for (Modifier m : values()) {
			mods.put(m.toString(), m);
		}
	}
	/**
	 * @param name modifier name to parse
	 * @param kinds if not empty, returns only a modifier that matches all of the kinds
	 * @return Modifier that matches any of the kinds constraints
	 */
	public static Modifier parseModifier(String name, ModifierKind... kinds) {
		try {
			//Modifier modifier = Modifier.valueOf(name);
			Modifier modifier = mods.get(name);
			if (kinds.length == 0 || modifier == null)
				return modifier;
			for (ModifierKind kind : kinds)
				if (modifier.isA(kind))
					return modifier;
			return kinds.length > 0 ? null : modifier;
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	/**
	 * Try to be smart about Kind inheritance (C => C++ or Objective-C)
	 */
	public boolean isA(ModifierKind k) {
		if (k == Plain && !(kinds.contains(Attribute) || kinds.contains(Declspec)))
			return true;
		if (k == Extended && (kinds.contains(Attribute) || kinds.contains(Declspec)))
			return true;
		if ((k == CPlusPlus || k == CPlusPlusCLI || k == ObjectiveC) && kinds.contains(C))
			return true;
		
		if (kinds.contains(k))
			return true;
		
		//if (alias != null && alias.isA(k))
		//	return true;
		
		return false;
	}
	
	public boolean isAnyOf(ModifierKind...kinds) {
		for (ModifierKind kind : kinds)
			if (isA(kind))
				return true;
		
		return false;
	}
	
	public boolean isAllOf(ModifierKind...kinds) {
		for (ModifierKind kind : kinds)
			if (!isA(kind))
				return false;
		
		return true;
	}
	

	public enum Compiler {
		GCC, MSVC, Intel
	}

	@Override
	public String toString() {
		return toString(null);
	}
	public String toString(Modifier.Compiler compiler) {
		String low = super.toString().toLowerCase();
		if (kinds.contains(Declspec) && (compiler == null || compiler == Compiler.MSVC))
			return "__declspec(" + low + ")";
		
		if (kinds.contains(Attribute) && (compiler == null || compiler == Compiler.GCC))
			return "__attribute__((" + low + "))";
		
		return low;
	}
}