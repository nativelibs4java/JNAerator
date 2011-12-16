//#include <iostream>

/*
#define __asm(x)
void fff() {
	const int xx = someMacro(10);
}
*/
time_t time2posix(time_t);
time_t timelocal(struct tm * const);
time_t timegm(struct tm * const);

//typedef __darwin_pthread_attr_t		pthread_attr_t;

void f(int a __attribute__((__unused__)));
