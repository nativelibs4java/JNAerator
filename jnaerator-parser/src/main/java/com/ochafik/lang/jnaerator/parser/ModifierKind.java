package com.ochafik.lang.jnaerator.parser;

public enum ModifierKind {

	StorageClassSpecifier, 
	TypeQualifier,
	Declspec, /// VC++ __declspec
	Attribute, /// GCC __attribute__
	Publicity,
	CallingConvention,
	HasArguments,
	COMSpecific,
	MSSpecific,

	OnlyInArgDef,
			
	C,
    OpenCL,
	CPlusPlus,
	ObjectiveC,
	Java,
	CSharp,
	CPlusPlusCLI,
	
	Plain,
	Extended,
	NumericTypeQualifier,
	ReferenceQualifier,
	SizeModifier,
	SignModifier, ///http://msdn.microsoft.com/en-us/library/cc264105.aspx
	StringAnnotation,
	VCAnnotationNoArg,
	VCAnnotation2Args,
	VCAnnotation1Arg
}
