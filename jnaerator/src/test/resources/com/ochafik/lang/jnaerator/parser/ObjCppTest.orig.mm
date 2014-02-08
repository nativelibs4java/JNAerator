#	
#	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
#	
#	This file is part of JNAerator (http://jnaerator.googlecode.com/).
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
