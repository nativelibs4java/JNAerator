
struct Pair {
    // Comment on both first and second
    int first, second;
};

#define PAIRS_COUNT 10

#define EXPRESSION (1 << 10) | (1 << 5)

enum Values {
First, // first comments
Second, // comments on second
Last // not a real value
};

struct BiggerStruct {
    Values enumValue;
    bool (*hook)(int val);
    long longValue;
    int intArray[sizeof(Pair) * PAIRS_COUNT];
};

// Wonderful comments on Test function
void Test(BiggerStruct& s);

// Be careful with that one : intArray and source are const, dest is not void
CopyBytes(char* dest, const char* source, size_t n, const int* intArray);