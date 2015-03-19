## Current development version (0.13-SNAPSHOT)

...

## Version 0.12 (20150308)

- Added `-library foo -dependencies bar,baz` to generate `@Library("foo", dependencies = {"bar", "baz"})` ([issue nativelibs4java#535]
  (https://github.com/nativelibs4java/nativelibs4java/issues/535))
- Added JNA-only -forceStringSignatures option to force String and String[] params even with char* and char** (as opposed to const char* and const char**) ([issue nativelibs4java#476]
  (https://github.com/nativelibs4java/nativelibs4java/issues/476))
- Added -skipDeprecated option for JNA output ([issue nativelibs4java#480]
  (https://github.com/nativelibs4java/nativelibs4java/issues/480))
- Added -forceNames option to issue Name annotations for all supported entities ([issue nativelibs4java#443]
  (https://github.com/nativelibs4java/nativelibs4java/issues/443))
- Added `@Name` support to all entities ([issue nativelibs4java#443]
  (https://github.com/nativelibs4java/nativelibs4java/issues/443))
- Added partial support for C++ namespaces with GCC-compiled libraries ([issue nativelibs4java#446]
  (https://github.com/nativelibs4java/nativelibs4java/issues/446))
- Added a new "peer pointer" constructor to JNA structures ([issue nativelibs4java#442]
  (https://github.com/nativelibs4java/nativelibs4java/issues/442), thanks to topikachu@ for the patch!)
- Added -publicRawBindings and -noRawBindings options (since -genRawBindings is now the default)
- Added support for JNA 4.0.0 (implement getFieldOrder in all structs, see [issue nativelibs4java#384]
  (https://github.com/nativelibs4java/nativelibs4java/issues/384))
- Added support for node.js target runtime (see example of bindings: https://github.com/ochafik/node-opencl)
- Added -optionalFunctions option to tag functions with `@org.bridj.ann.Optional`
- Added mechanism to skip generation of entities (-skipStructs, -skipEnums, -skipFunctions)
- Added -noStaticInit option for BridJ
- Added -extractDeclarations option to create an interface with all the methods and types of a library (BridJ-specific)
- Improved harvesting of header comments: go further away before the declaration, and remove leading * in oxygen comment lines ([issue nativelibs4java#481]
  (https://github.com/nativelibs4java/nativelibs4java/issues/481))
- Improved raw mode for BridJ callbacks: in raw mode, generate two crossed callbacks, one of which must be overridden.
- Reverted to by-default chunks parsing, and added -parseInOnePiece instead of -parseChunks (makes parsing more robust to syntax errors)
- Moved to ECJ 4.2.2
- Dropped support for bit fields in JNA structs (users should implement bit manipulation logic manually or use BridJ: JNAerator's fork of JNA that added this feature incurred too much maintenance work).
- Dropped old shared library (DLL, Mach-O) scanning code, superseded by dyncall/dynload and BridJ's demanglers.
- Fixed missing actual name in raw native methods ([issue nativelibs4java#499]
  (https://github.com/nativelibs4java/nativelibs4java/issues/499), thanks to @washley for the patch!)
- Fixed handling of `const char * const *` params for JNA runtime ([issue nativelibs4java#476]
  (https://github.com/nativelibs4java/nativelibs4java/issues/476))
- Fixed duplicate bindings with -reification, caused by interaction with interface extraction feature ([issue nativelibs4java#500]
  (https://github.com/nativelibs4java/nativelibs4java/issues/500))
- Fixed duplicate &amp; mismatching `@Name` annotations with -beautifyNames ([issue nativelibs4java#460]
  (https://github.com/nativelibs4java/nativelibs4java/issues/460))
- Fixed routing of simple callback with ptr-ptr arg (e.g. `typedef void* (*F3)(int** pptr)`)
- Fixed pointer casts in constants ([issue nativelibs4java#132]
  (https://github.com/nativelibs4java/nativelibs4java/issues/132))
- Fixed some GLib parsing issues: type literals of null TypeRefs, support signatures with va_list / __gnuc_va_list and __builtin_va_list, and fixed NPE ([issue nativelibs4java#132]
  (https://github.com/nativelibs4java/nativelibs4java/issues/132))
- Fixed parsing of exotic enums ([issue nativelibs4java#459]
  (https://github.com/nativelibs4java/nativelibs4java/issues/459))
- Fixed parsing of many Objective-C (property) attributes
- Fixed case-sensitivity of C/C++ modifiers, which prevented some variables from being parsed ([issue nativelibs4java#449]
  (https://github.com/nativelibs4java/nativelibs4java/issues/449))
- Fixed parsing of unsigned long constants ([issue nativelibs4java#362]
  (https://github.com/nativelibs4java/nativelibs4java/issues/362), [issue nativelibs4java#374]
  (https://github.com/nativelibs4java/nativelibs4java/issues/374))
- Fixed generation of raw bindings for callback return types ([issue nativelibs4java#386]
  (https://github.com/nativelibs4java/nativelibs4java/issues/386))
- Fixed generation of raw bindings with varargs ([issue nativelibs4java#386]
  (https://github.com/nativelibs4java/nativelibs4java/issues/386), patch contributed by twitwi!)
- Fixed generation of pointer to fake pointer for JNA target ([issue nativelibs4java#403]
  (https://github.com/nativelibs4java/nativelibs4java/issues/403))
- Fixed handling of library names with hyphens ([issue nativelibs4java#260]
  (https://github.com/nativelibs4java/nativelibs4java/issues/260))
- Fixed support of identifiers with Object method names ([issue nativelibs4java#375]
  (https://github.com/nativelibs4java/nativelibs4java/issues/375))
- Fixed support of enums inside objc classes ([issue nativelibs4java#370]
  (https://github.com/nativelibs4java/nativelibs4java/issues/370))
- Fixed parsing of `@optional` and `@required` objective-c annotations ([issue nativelibs4java#369]
  (https://github.com/nativelibs4java/nativelibs4java/issues/369))
- Fixed UTF32Char, added VOID, CHAR and byte as predefined types ([issue nativelibs4java#363]
  (https://github.com/nativelibs4java/nativelibs4java/issues/363))
- Fixed silent disappearance of struct field conversion errors ([issue nativelibs4java#390]
  (https://github.com/nativelibs4java/nativelibs4java/issues/390))
- Fixed pom files for Maven output modes (removed rococoa dependency by default, added commented repo needed by it)
- Fixed resolution of typedef'd structs when the typedef name was chosen as "better" than the original struct tag ([issue nativelibs4java#367]
  (https://github.com/nativelibs4java/nativelibs4java/issues/367))
- Fixed restitution of hex constants ([issue nativelibs4java#362]
  (https://github.com/nativelibs4java/nativelibs4java/issues/362))
- Fixed parsing of __restrict__ args
- Fixed parsing of MinGW system headers ([issue nativelibs4java#373]
  (https://github.com/nativelibs4java/nativelibs4java/issues/373))
- Fixed wrong use of stdcall mapper outside Windows in JNAerator Runtime
- Fixed recursive typedefs
- Fixed typedefs of forward-declared enums and structs ([issue nativelibs4java#430]
  (https://github.com/nativelibs4java/nativelibs4java/issues/430))
- Fixed handling of large unicode values ([issue nativelibs4java#401]
  (https://github.com/nativelibs4java/nativelibs4java/issues/401))
- Fixed warnings for getFieldOrder ([issue nativelibs4java#408]
  (https://github.com/nativelibs4java/nativelibs4java/issues/408))
- Fixed handling of constant array sizes that depend on enums for BridJ ([issue nativelibs4java#354]
  (https://github.com/nativelibs4java/nativelibs4java/issues/354))
- Fixed handling of hypens in package names ([issue nativelibs4java#260]
  (https://github.com/nativelibs4java/nativelibs4java/issues/260))
- Fixed OSGi imports ([issue nativelibs4java#429]
  (https://github.com/nativelibs4java/nativelibs4java/issues/429), thanks to Michael Werle for the pull request!)
...

## Version 0.11 (20130107)

- Fixed infinite loops in simple typedefs ([issue nativelibs4java#288]
  (https://github.com/nativelibs4java/nativelibs4java/issues/288))
- Fixed some -beautifyNames cases ([issue nativelibs4java#315]
  (https://github.com/nativelibs4java/nativelibs4java/issues/315))
- Fixed parsing of some C++ templates (including template constructors)
- Fixed "long long" regression
- Fixed JNAeratorMojo.config documentation ([issue nativelibs4java#330]
  (https://github.com/nativelibs4java/nativelibs4java/issues/330))
- Fixed long / long long / short pointer function return types
- Fixed generation of BridJ C++ constructors
- Fixed enum names that collide with Java identifiers ([issue nativelibs4java#334]
  (https://github.com/nativelibs4java/nativelibs4java/issues/334))
- Added a type definition override switch, useful force mismatching 32/64bits types to some predefined types (for instance, -TmyVal=intptr_t) 
- Added raw bindings generation for BridJ
- Added parsing of 'using' C++ statements
- Added TypeRef.resolvedJavaIdentifier
- Added parser support for `complex double` (cf. complex.h)
- Added test for BridJ raw signatures
- Moved to ECJ 3.7.2
- Moved to JNA 3.4.0
- Refactored type resolution and conversion
- Rationalized CompilerUtils classpath + bootclasspath

## Version 0.10 (20120415, commit 6bc061dfce06b941086a29f696195e82fbaffbdc)

- Release artifacts are available in Maven Central
- Fixed generation of large long values
- Fixed conditional parsing of __in modifier (and COM modifiers in general)
- Fixed generation of globals and variables included more than once
- Fixed parsing of unary '-' operator
- Fixed parsing of C++ constructors and class inheritance
- Fixed parsing of default values for type name template arguments
- Fixed parsing of const type mutator (fixes `void f(struct x * const);`) ([issue nativelibs4java#205]
  (https://github.com/nativelibs4java/nativelibs4java/issues/205))
- Fixed parsing of null char escape '\0' ([issue nativelibs4java#214]
  (https://github.com/nativelibs4java/nativelibs4java/issues/214))
- Fixed conversion of `int a; f(&amp;a);`
- Fixed handling of "long int" and "short int" ([issue nativelibs4java#267]
  (https://github.com/nativelibs4java/nativelibs4java/issues/267))
- Fixed parsing of __declspec, __attribute__ and some modifiers-related regressions
- Fixed conversion of __inline functions when -convertBodies is on
- Fixed NPE in JNAeratorUtils.findBestPlainStorageName ([issue nativelibs4java#258]
  (https://github.com/nativelibs4java/nativelibs4java/issues/258))
- Fixed parsing of empty strings (spotted by @ENargit in [issue nativelibs4java#255]
  (https://github.com/nativelibs4java/nativelibs4java/issues/255))
- Fixed generation of typedefs ([issue nativelibs4java#273]
  (https://github.com/nativelibs4java/nativelibs4java/issues/273))
0 Fixed generation of casted constants ([issue nativelibs4java#96]
  (https://github.com/nativelibs4java/nativelibs4java/issues/96))
- Fixed generation of unnamed structs and unions ([issue nativelibs4java#94]
  (https://github.com/nativelibs4java/nativelibs4java/issues/94))
- Fixed multidimensional array sizes for JNA target ([issue nativelibs4java#165]
  (https://github.com/nativelibs4java/nativelibs4java/issues/165))
- Fixed handling of hexadecimal constants ([issue nativelibs4java#296]
  (https://github.com/nativelibs4java/nativelibs4java/issues/296))
- Fixed conversion of comments for BridJ target runtime
- Fixed generation of BridJ calling conventions ([issue nativelibs4java#282]
  (https://github.com/nativelibs4java/nativelibs4java/issues/282))
- Fixed handling of __stdcall function pointers and functions ([issue nativelibs4java#282]
  (https://github.com/nativelibs4java/nativelibs4java/issues/282))
- Fixed mapping of bool for JNA(erator) target runtime ([issue nativelibs4java#289]
  (https://github.com/nativelibs4java/nativelibs4java/issues/289))
- Fixed parsing of malloc, free and many potential modifiers ([issue nativelibs4java#278]
  (https://github.com/nativelibs4java/nativelibs4java/issues/278) and issue #280)
- Fixed handling of unicode library paths ([issue nativelibs4java#276]
  (https://github.com/nativelibs4java/nativelibs4java/issues/276))
- Fixed parsing of friend members in C++ classes, and of assignment operators (operator+=, ...)
- Fixed generation of very simple edge cases "long f();", "short f();", "f();" ([issue nativelibs4java#270]
  (https://github.com/nativelibs4java/nativelibs4java/issues/270))
- Changed naming of anonymous function pointer types : `void f(void (*arg)());` now yields callback `f_arg_callback`
- Enhanced handling of parsing failures : faster failover to "sliced" parsing, reduced verbosity of errors
- Added Maven output modes and -mode switch to choose between Jar, StandaloneJar, Directory, Maven, AutoGeneratedMaven (deprecated -noJar and -noComp)
- Added support for MSVC asm soups + added -removeInlineAsm hack switch that tries to regex-remove __asm soups that still cannot be parsed out
- Added support for BridJ's bundled libraries mechanism and paths (+ enforce them using an enum param)
- Added parsing of expression initializer blocks `v = { 1, 2, 3};`
- Added preservation of original textual representation of constants
- Added tr1 to default C++ includes
- Generate symbols of all files in directories where any file was explicitly listed
- Added support for command-line definition of macros with arguments : -Df(x)=whatever
- Added conversion of `malloc(x * y * sizeof(whatever))`
- Removed C++ name mangling feature for JNA target runtime (was simplistic anyway)

## Version 0.9.9 (20111107, 1cad7b05da2e89ce53246f30b181159ef8fce0c3)

- Default runtime target is now BridJ: use -runtime JNAerator to retain old behaviour 
- Added option -emptyStructsAsForwardDecls to force generation of opaque pointers for empty structs (even if they inherit from other empty structs)
- Fixed Maven plugin : 
	- add generated files to compilation sources
	- now use "generate" goal ([issue nativelibs4java#152]
  (https://github.com/nativelibs4java/nativelibs4java/issues/152))
- Fixed handling of the empty package
- Fixed verbose mode (logs used to self-overwrite between different parsed files)
- Fixed rare package/className clash in pretty-printer ([issue nativelibs4java#182]
  (https://github.com/nativelibs4java/nativelibs4java/issues/182))
- Fixed C++ / Object.class method name clashes
- Fixed regression on fake pointers with JNA-based runtimes
- Fixed parsing of large unsigned longs
- Fixed generation of multiple anonymous enums ([issue nativelibs4java#96]
  (https://github.com/nativelibs4java/nativelibs4java/issues/96))
- Fixed BridJ enum items that refer to other items
- Fixed generation of string constant expressions cast as Pointers (only for JNAerator and BridJ runtimes)
- Fixed -runtime JNA target bug that used JNAerator target for generated library loading code ([issue nativelibs4java#95]
  (https://github.com/nativelibs4java/nativelibs4java/issues/95) @ jnaerator)
- Fixed varargs &amp; param name generation for BridJ (thanks to Eric Emonet for the fix : https://github.com/ochafik/nativelibs4java/commit/74b59d5152333bf2d7f9d9613662f5fc3c1ecaf2)
- Enhanced BridJ runtime : only use CRuntime when there's no C++ contents
- Updated to ANTLR 3.4
- Updated runtime to JNA 3.3.0

## Version 0.9.8 (r2128, 20110621)

- Better COM JNAeration for BridJ (now supports and converts __declspec(uuid("xxx")) attributes !)
- Accept some ill-formed constructs (mismatched extern "C" { ... }) for better cross-files parsing
- Fixed JNAeration of nested dependent constants defines
- Fixed automatic macro definitions : don't override already defined macros
- Fixed parsing of singleton extern "C" definitions (without curly braces)
- Fixed issue 86 ([http://code.google.com/p/jnaerator/issues/detail?id=86)](http://code.google.com/p/jnaerator/issues/detail?id=86)) : generate proper array creation code for empty structs with the JNA runtime.
- Fixed issue 85 ([http://code.google.com/p/jnaerator/issues/detail?id=85)](http://code.google.com/p/jnaerator/issues/detail?id=85)) : generate proper constant pointer casting code for BridJ : `#define PTR ((void*)-1)` generates `Pointer&lt;?&gt; PTR = Pointer.pointerToAddress(-1);`
- Fixed generation of enums
- Fixed typing of (constant) boolean expressions

## Version 0.9.7 (r1817, 20110329)

- Fixed generation of typed pointers (+ introduced undefined types for BridJ runtime)
- Added generation of globals for BridJ
- Added -parseInOneChunk option to control parsing more finely (forces regular, non isolated-mode parsing, which is more correct but also more fragile)
- Fixed enum renaming (issue 82: [http://code.google.com/p/jnaerator/issues/detail?id=82)
](http://code.google.com/p/jnaerator/issues/detail?id=82)
)- Fixed unescaped quotes in string defines (issue 79: [http://code.google.com/p/jnaerator/issues/detail?id=79)
](http://code.google.com/p/jnaerator/issues/detail?id=79)
)- Fixed some exotic function pointer syntax (issue 84: [http://code.google.com/p/jnaerator/issues/detail?id=84)
](http://code.google.com/p/jnaerator/issues/detail?id=84)
)- Fixed binding of Windows' BOOL type for BridJ: it's int, not boolean !!!

## Version 0.9.6 (r1638, 20110204)

- Added explicit call to setFieldOrder in JNAerated JNA structures (no need for BridJ, which has its @Field annotations to guarantee proper order)
- Enhanced parsing and AST generation of C/C++ expressions
- Fixed BridJ inherited struct fields indexes computation

## Version 0.9.5 (r1330, 20101011):

- Moved to JNA 3.2.7
- Support anonymous bit fields : struct S { int :7; int b; }
- Better beautification of underscored typedefs : typedef struct _Toto___ { ... } Toto; now handled properly
- Extended -I command line syntax to include files directly (which does the same as an #include statement) : files are parsed but their content is not targetted for code generation
- Added -beautifyNames switch to transform some_func() into someFunc()
([http://code.google.com/p/jnaerator/issues/detail?id=59)
](http://code.google.com/p/jnaerator/issues/detail?id=59)
)- Fixed names with dollars (and Scala struct field setters in particular !)
- Added -scalaStructSetters option
- Fixed callbacks fields
- Fixed studio JVM hanging issue ([http://code.google.com/p/jnaerator/issues/detail?id=66)
](http://code.google.com/p/jnaerator/issues/detail?id=66)
)- JNAerator Studio now launched when there's no command line args (JAR is double-clickable, at last !)
- Fixed struct** fields (PointerByReference instead of faulty Struct.ByReference[] !)
- Changed name of callbacks's single method from "invoke" to "apply" (to make them Scala-friendly)
- Fixed parsing of large files (reverted EASILY_DEBUGGABLE_BUT_FRAGILE_PARSING_MODE to false)
- Added __restrict keyword
- Added -synchronized switch to mark generated methods as synchronized (only in direct-mode JNA and BridJ outputs)
- Fixed generation of BridJ enums, constants and arrays
- Parse(skip) GCC inline assembler (__asm__)
- Parse some ObjectiveC 2 constructs : @package, @required and @optional member modifiers + new @property syntax
- Fixed parser regressions (0x7f, +10)
- Added experimental reification feature for BridJ output (-reification switch) : transforms functions that have a first "this-like" pointer argument into methods of the
corresponding pointer type. 
	Instead of getting :
	{{{
	public native static TestLibrary.GXEntity gxCreateEntity(int someParam);
	public native static void gxEntitySetValue(TestLibrary.GXEntity entity, int key,
	int value);
	public native static int gxEntityGetValue(TestLibrary.GXEntity entity, int key);
	/// Pointer to unknown (opaque) type
	public static class GXEntity extends TypedPointer {
			public GXEntity(long address) {
					super(address);
			}
			public GXEntity(Pointer address) {
					super(address);
			}
	};
	}}}
	You'd get the assembly-optimizable BridJ binding :
	{{{
	native static @Ptr long gxCreateEntity(int someParam);
	native static void gxEntitySetValue(@Ptr long entity, int key, int value);
	native static int gxEntityGetValue(@Ptr long entity, int key);
	public static class GXEntity extends TypedPointer {
			public GXEntity(long address) {
					super(address);
			}
			public GXEntity(Pointer address) {
					super(address);
			}
			public static GXEntity create(int someParam) {
					long ret = gxCreateEntity(someParam);
					return ret == null ? null : new GXEntity(ret);
			}
			public void setValue(int key, int value) {
					gxEntitySetValue(this.peer, key, value);
			}
			public int getValue(int key) {
					return gxEntityGetValue(this.peer, key);
			}
	}
	}}}

## Version 0.9.4 (r1067@nl4j, 20100717)

- better C/C++ parsing (cast operators, templates, anonymous structs...)
- fixed JNAerator issues #63 ("JNA runtime does not
support bit fields"), #64 ("on Windows, use
%ProgramFiles%" to infer Visual Studio install directory.")
- enhanced BridJ runtime generation support
- enhanced debug output in verbose mode
- Added -beanStructs command line option to generate getters and setters for JNA (issue 62 @ jnaerator)

## Version 0.9.3 (r760@nl4j, 20100121)

- Fixed parsing of some common C expressions (better tolerates inline functions)
- Fixed parsing of OpenCL source codes (used by JavaCL Generator project on NativeLibs4Java/JavaCL project)
- Introduced experimental lightweight structures support (use new "-runtime JNAeratorNL4JStructs" switch)
- Added automatic import statements generation to make source code smaller and easier to read (use new -noAutoImports switch to disable).

## Version 0.9.2 (r886, 20081030):

- Fixed [issue nativelibs4java#31]
  (https://github.com/nativelibs4java/nativelibs4java/issues/31) : parse unnamed "char *const *" function arguments
- Fixed [issue nativelibs4java#35]
  (https://github.com/nativelibs4java/nativelibs4java/issues/35) : byref, byval, in, out and bycopy can now be used in most contexts (general case of issue #22 is still not fixed, though)
- Fixed regression on structs with callbacks
- Added "oneway" Objective C modifier
- Allow java-only modifiers, declspec and gcc attributes as identifiers (native, transient, dllimport...)
- Fixed [issue nativelibs4java#38]
  (https://github.com/nativelibs4java/nativelibs4java/issues/38) : Function pointer pointers were completely broken
- Fixed regression since maven move : missed objectivec static forwards exclusion list
- Deprecated Structure.toArray(), renamed to Structure.castToArray().
- Added static Structure.newArray(Class&lt;? extends Structure&gt;, int length)
- JNAerating a typed static newArray(int length) for each structure
- Added "C type" comment to struct fields with non-primitive types
- Deprecated StringPointer
- Added basic output customization capabilities with -choicesOut &amp; -choice switches : syntax is one line of the C function declaration, then n lines of corresponding Java declarations, each function block being separated by an empty line. 
- Fixed [issue nativelibs4java#39]
  (https://github.com/nativelibs4java/nativelibs4java/issues/39) : Failure to parse define with expression containing a cast
- Fixed long constants being generated as NativeLong (now long)
- Fixed regression with function pre-comments
- Fixed generation of references to constants defined in other libraries
- size_t is now mapped to NativeSize. Classes NativeSizeByReference &amp; GlobalNativeSize have also been added, along with a backwards compatibility switch -sizeAsLong (and a switch -gccLong to treat 'long' as 'size_t')
- Fixed primitive type promotion of constants (NativeLong -&gt; long)
- Added -ifRegexMatch switch 
- Define __STDC__ in auto configuration
- Fixed [issue nativelibs4java#44]
  (https://github.com/nativelibs4java/nativelibs4java/issues/44): Hypens in header names translate to hyphens in class names
- Byref &amp; globals mapping of NSInteger now uses NativeSizeByReference and GlobalNativeSize, instead of NativeLong derivates


