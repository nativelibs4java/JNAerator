#	
#	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
#	
#	This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
#	
#	JNAerator is free software: you can redistribute it and/or modify
#	it under the terms of the GNU Lesser General Public License as published by
#	the Free Software Foundation, either version 3 of the License, or
#	(at your option) any later version.
#	
#	JNAerator is distributed in the hope that it will be useful,
#	but WITHOUT ANY WARRANTY; without even the implied warranty of
#	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#	GNU Lesser General Public License for more details.
#	
#	You should have received a copy of the GNU Lesser General Public License
#	along with JNAerator.s  If not, see <http://www.gnu.org/licenses/>.
#	
#pragma reversible
--
void f() {
	delete[] ptr;
	delete ptr;
}
--
void f(int __in);
--
void meth(double*) throw(int, SomeEx*);
--
void f(void (*g)());
--
void (*fptr)();
--
int val = (1 << 16) - 2;
--
typedef void *__ptr64 PVOID64;
--
typedef char CHAR;
typedef CHAR *PCHAR, *LPCH, *PCH;
--
typedef char CHAR;
typedef const CHAR *LPCCH, *PCCH;
--
long value = (int)(__u.__u >> 31);
--
long long l = 0x8000000000000000;
long long ll = 10LL;
unsigned long long ull = 10ULL;
--
void f(struct s i);
--
const char WINDOWS_ENDL_CH_CONST[3] = { 0x0d, 0x0a, '\0' };
--
extern long long int i;
--
extern long int llrint(double);
--
extern long long int llrint(double);
--
long * f(long v, unsigned long* pv);
--
long long * fpll();
--
typedef int I;
register I i;
--
char* initstate(unsigned, char*, size_t); /* no  __DARWIN_ALIAS needed */
--
register unsigned int g;
--
typedef struct myStruct
{
  char Struct[2];
  char typeName[2];
} Mine;
--
void srand(unsigned);
--
void f(int* pA, int* pB) {
	int a = *pA, b = *pB;
}
--
void f(int* pA, int* pB) {
	int a = *pA;
	int b = *pB;
	int c;
	bool b;
	c = a + b;
	c = a - b;
	c = a * b;
	c = a / b;
	c = a % b;
	c = a ^ b;
	c = a | b;
	c = a & b;
	c = a << b;
	c = a >> b;
	b = a > b;
	b = a >= b;
	b = a < b;
	b = a <= b;
	b = a == b;
	b = a != b;
	b = a && b;
	b = a || b;
	*pA = a + 1;
}
void test() {
	int a = 10;
	int *pA = &a;
	f(pA);
}
--
/**
 * Need to see public final ByteByReference[] data = new ByteByReference[3]; in results<br>
 * https://jna.dev.java.net/servlets/ReadMsg?list=users&msgNo=2335
 */
typedef struct {
	int width;
	int height;
	int rowBytes;
	int columnBytes;
	unsigned char *data[3];
} ImageTransfer;
--
struct BitField {
	float a;
	int f1:1;
	int f2:7;
	unsigned f5:16;
	double z;
};
--
class string;
string name = "ok";
string empty = "";
--
class CPPClass {
	static void f();
};
--
struct S {
	friend long f();
};
--
extern void **const GC_objfreelist_ptr;
--
typedef struct _NSSize {
	CGFloat width; /* width should never be negative */
	CGFloat height; /* height should never be negative */
} NSSize;
--
void f() {}
--
free(void* _Memory);
malloc(int _Size);
int align(int);
int dllexport(int);
--
struct __declspec(uuid("9B")) __declspec(novtable) ID3D10DeviceChild : IUnknown
{
	void f(); 
};
--
template <class _A >
struct S
{
	_A field;
	S() : field() {}
	S(const _A& __f) : field(__f) {}
	template <class _B >
	S(const S<_B >& __s) : field(__s.field) {}
  	template <class _B >
  	void f(S<_B >&);
};
--
template <class U, class V >
struct X {
	void f(X<U, V >&);
};
template <class S, class T >
inline bool operator==(const X<S, T >&);
--
struct __attribute__((__unused__)) U {
};
--
template <typename _CharT, typename _Traits = char_traits<_CharT >, typename _Alloc = allocator<_CharT > >
class basic_string;
template < > struct char_traits<char >;
--
/** Struct comment */
struct ParamBlockRec {
	int ok; // comment after ok
};
--
class C {
	C() : a(1), b() {}
};
--
static inline int isascii(int _c) {
	return (_c & ~0x7F) == 0;
}
--
/** these are comments... */
void* CreateHandle(int size);
--
void** ResizeHandle(void** h, int size);
--
/** typedef bool BOOL; */
class NSMapTable;
BOOL (*isEqual)(NSMapTable* table, const void*, const void*);
--
void operator()(signed&);
--
void operator+=(long long*, const signed&);
--
void A<T* >::~A();
--
const void *x;
--
typedef void *GC_thread;
extern volatile GC_thread GC_threads[THREAD_TABLE_SZ];
--
typedef bool GC_bool;
extern GC_bool GC_thr_initialized;
--
const void *x;
--
const void *const x;
--
int *const a = (int*)10;
--
int *const *aa = (int**)10;
--
const void x[];
--
const void *const &volatile *register x[];
--
#// Only comments here !
--
char *(*(**foo[][8])())[];
--
typedef struct {
	unsigned char *data[3];
} S;
--
int x[4];
--
bool (*x)(const void* buffer, unsigned int length);
--
unsigned long long int d;
--
unsigned *pa[4];
--
unsigned int a = a, *pa[4];
--
unsigned short x, *px, **ppx, **&rppx = a, *pa[4];
--
class pair;
class map;
class string;
pair<int, map<long*, string > > x;
--
class pair;
class map;
class string;
pair<int, map<long*, string >* > x;
--
enum {
	a = 1,
	b = 2 + 4,
	c = 55,
	rAliasType = 'alis' /* Aliases are stored as resources of this type */
};
--
@class NSAppleEventDescriptor;
--
typedef struct const __NSAppleEventManagerSuspension *NSAppleEventManagerSuspensionID;
--
extern const double NSAppleEventTimeOutNone;
--
class NSString;
extern NSString *NSAppleEventManagerWillProcessFirstEventNotification;
--
int i = 10;
--
int i = [NSObject createWithTest:"ok" encoding:[NSEncoding ascii] length:10];
--
# see http://tigcc.ticalc.org/doc/keywords.html
typedef unsigned char byte;
--
typedef char str40[41];
--
union short_or_long_NOVAR {
	short i;
	long l2;
};
--
union short_or_long {
	short i;
	long l;
} a_number, *p_anumber = NULL;
--
struct IOParam;
struct FileParam;
struct VolumeParam;
struct CntrlParam;
struct SlotDevParam;
struct MultiDevParam;
/** Test of comments before */
union ParamBlockRec {
	IOParam ioParam;
	FileParam fileParam;
	VolumeParam volumeParam;
	CntrlParam cntrlParam;
	SlotDevParam slotDevParam;
	MultiDevParam multiDevParam;
};
--
typedef void *AVLTreePtr;
typedef void *AVLNodeType;
typedef SInt32 (__cdecl *AVLCompareItemsProcPtr)(AVLTreePtr tree, const void* i1, const void* i2, AVLNodeType nd_typ);
--
void ComplexArgFunction(struct {
	union {
		enum {
			A,
			B
		} e;
		float f;
	} u;
	long v;
	void (*fptr)();
}* pArg);
--
typedef struct {
	float *re;
	float im[4];
} complex;
--
typedef char *byteptr;
--
int f(int* out, char byref, double in);
--
typedef int (*fncptr)(int);
--
__inline long f();
--
struct GC_thread;
extern volatile GC_thread GC_threads[];
--
#pragma parse

struct Test {__
	int i, j, k;
};
--
libvlc_instance_t* libvlc_new(char *const *);
--
__extension__ typedef long long __time64_t;
--
void f(int __restrict__ a);
--
extern __inline__ __attribute__((__always_inline__,__gnu_inline__)) void __cdecl __debugbreak(void)
{
  __asm__ __volatile__("int $3");
}
--
void f(struct x * const);
--
typedef __success(return >= 0) long HRESULT;
--
static __inline__  int __inline_signbit( long double __x ){ 
	union {
		long double __ld;
		struct { 
			unsigned long long __m;
			short __sexp;
		} __p;
	} __u = {__x}; 
	return (int) ((unsigned int) __u.__p.__sexp >> 15);
}
--
static __inline__  int __inline_isnormalf( float __x ) {
	float fabsf = __builtin_fabsf(__x); 
	if( __x != __x ) 
		return 0; 
	return fabsf < __builtin_inff() && fabsf >= __FLT_MIN__;
}
--
@interface NSLock : NSObject <NSLocking> {
@private
    void *_priv;
}

- (BOOL)tryLock;
@optional
- (BOOL)lockBeforeDate:(NSDate *)limit;

//- (void)setName:(NSString *)n ;
@required
- (void)setName:(NSString in*)n;
//- (NSString *)name ;
- (NSString *)name;

@end
--
typedef struct {
    NSUInteger	(*hash)(NSMapTable *table, const void *);
    BOOL	(*isEqual)(NSMapTable *table, const void *, const void *);
    void	(*retain)(NSMapTable *table, const void *);
    void	(*release)(NSMapTable *table, void *);
    NSString 	*(*describe)(NSMapTable *table, const void *);
    const void	*notAKeyMarker;
} NSMapTableKeyCallBacks;
--
@interface NSObject <NSObject> {
    Class	isa;
}

+ (void)load;

+ (void)initialize;
- (id)init;

+ (id)new;
+ (id)allocWithZone:(NSZone *)zone;
+ (id)alloc;
- (void)dealloc;

- (void)finalize ;

- (id)copy;
- (id)mutableCopy;

+ (id)copyWithZone:(NSZone *)zone;
+ (id)mutableCopyWithZone:(NSZone *)zone;

+ (Class)superclass;
+ (Class)class;
+ (BOOL)instancesRespondToSelector:(SEL)aSelector;
+ (BOOL)conformsToProtocol:(Protocol *)protocol;
- (IMP)methodForSelector:(SEL)aSelector;
+ (IMP)instanceMethodForSelector:(SEL)aSelector;
- (void)doesNotRecognizeSelector:(SEL)aSelector;

- (id)forwardingTargetForSelector:(SEL)aSelector;
- (void)forwardInvocation:(NSInvocation *)anInvocation;
- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector;

+ (NSMethodSignature *)instanceMethodSignatureForSelector:(SEL)aSelector;

+ (NSString *)description;

+ (BOOL)isSubclassOfClass:(Class)aClass ;

+ (BOOL)resolveClassMethod:(SEL)sel ;
+ (BOOL)resolveInstanceMethod:(SEL)sel ;

@end
--
@interface NSObject (NSCoderMethods) {
}
enum {
    NSMachPortDeallocateNone = 0,
    NSMachPortDeallocateSendRight = (1UL << 0),
    NSMachPortDeallocateReceiveRight = (1UL << 1)
};

+ (NSInteger)version;
+ (void)setVersion:(NSInteger)aVersion;
- (Class)classForCoder;
- (id)replacementObjectForCoder:(NSCoder *)aCoder;
- (id)awakeAfterUsingCoder:(NSCoder *)aDecoder;

@end
--
extern "C"  void   cvReleaseMat( CvMat** mat );
--
/* Decrements CvMat data reference counter and deallocates the data if
   it reaches 0 */
inline  void  cvDecRefData( CvArr* arr )
{
    if( (((arr) != 0 && (((const CvMat*)(arr))->type & 0xFFFF0000) == 0x42420000 && ((const CvMat*)(arr))->cols > 0 && ((const CvMat*)(arr))->rows > 0) && ((const CvMat*)(arr))->data.ptr != 0))
    {
        CvMat* mat = (CvMat*)arr;
        mat->data.ptr = 0;
        if( mat->refcount != 0 && --*mat->refcount == 0 )
            (cvFree_(*(&mat->refcount)), *(&mat->refcount)=0);
        mat->refcount = 0;
    }
    else if( (((arr) != 0 && (((const CvMatND*)(arr))->type & 0xFFFF0000) == 0x42430000) && ((const CvMatND*)(arr))->data.ptr != 0))
    {
        CvMatND* mat = (CvMatND*)arr;
        mat->data.ptr = 0;
        if( mat->refcount != 0 && --*mat->refcount == 0 )
            (cvFree_(*(&mat->refcount)), *(&mat->refcount)=0);
        mat->refcount = 0;
    }
}
--
@class Categ;
@interface Base (Categ)
	- (Base*)merge:(Base*)f;
@end
--
struct {
	int i:13;
	unsigned j:1, jj:2;
	signed k:2;
	long l:3;
	short m:2;
	float f;
} a;
--
namespace std  {
  using ::mbstate_t;
}
--
@interface Foo {
  __strong __weak __autoreleasing __unsafe_unretained __block __bridge int _bar;
}

@property(readonly, readwrite, strong, retain, unsafe_unretained, copy, assign, nonatomic, atomic, getter=barre, weak) int bar;

@end
--
extern TestMe();
--
#pragma fail
int[4] x;
--
bool (*test)(const void *buffer, unsigned int length) x;
--
class fpos;
class fpos
{
	fpos() {}
};
--
typedef enum : Foo { Quux } Baz;
