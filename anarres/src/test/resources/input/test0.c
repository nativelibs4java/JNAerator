line = __LINE__
file = __FILE__

#define A a	/* a defined */
#define B b	/* b defined */
#define C c	/* c defined */

#define EXPAND(x) x
EXPAND(a) -> a
EXPAND(A) -> a

#define _STRINGIFY(x) #x
_STRINGIFY(A) -> "A"

#define STRINGIFY(x) _STRINGIFY(x)
STRINGIFY(b) -> "b"
STRINGIFY(A) -> "a"

#define _CONCAT(x, y) x ## y
_CONCAT(A, B) -> AB

#define A_CONCAT done_a_concat
_CONCAT(A, _CONCAT(B, C)) -> done_a_concat(b, c)

#define CONCAT(x, y) _CONCAT(x, y)
CONCAT(A, CONCAT(B, C)) -> abc

#define _CONCAT3(x, y, z) x ## y ## z
_CONCAT3(a, b, c) -> abc
_CONCAT3(A, B, C) -> ABC
_CONCAT3(A, EXPAND(B), C) -> AEXPAND(b)C

Line is __LINE__
File is __FILE__

#define two three
one /* one */
#define one two
one /* three */
#undef two
#define two five
one /* five */
#undef two
one /* two */
#undef one
#define one four
one /* four */
#undef one
#define one one
one /* one */

/* warning line 57 column 0 */
#warning arse

#define foo(x) foo(x, b)
foo(1) -> _foo(1, b) without the _
foo(foo(2)) -> _foo(_foo(2, b), b) without the _
foo(y, z)

#define var(x...) a x b
var(e, f, g) -> a e, f, g b
