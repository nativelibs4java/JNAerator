// 
// This example file shows a few basic language 
// features that are handled by JNAerator.
//
// Click on "JNAerate !" to see the JNAerated output.
// 

extern "C" {

enum SimpleEnum {
	First, // first comments
	Second = 10,  // comments on second
	Last // not a real value
};

typedef int (__cdecl *DummyCallback)(void);

struct TestStruct {
	SimpleEnum enumValue;
	char		 charValue;
	short	 shortValue; // comment on shortValue
	wchar_t	 wcharValue;	
	int		 intValue;
	// comment on boolValue
	bool		 boolValue;
	long		 longValue;
	size_t	 sizeValue;
	long long	 longLongValue;
	float	 floatValue;
	double	 doubleValue;
	char*	 cstringValue;
	char		 charArrayValue[255];
	void*	 voidPointerValue;
	// first comment on functionValue
	int		 (*functionValue)(SimpleEnum e); // second comment on functionValue
	struct { int first, second; } structValue;
	TestStruct *structPointerValue;
};

void Test(const char* name, TestStruct& values, RandIntFunc func);
void TestByValue(TestStruct valuesByValue);

void ComplexAnonymous(struct { union { enum { A, B } e; float f; } u; long v; void (*fptr)(); }*);

}
