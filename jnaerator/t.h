

namespace std  {

  typedef int*			__c_locale;

  // Convert numeric value of type double and long double to string and
  // return length of string.  If vsnprintf is available use it, otherwise
  // fall back to the unsafe vsprintf which, in general, can be dangerous
  // and should be avoided.
  inline int
  __convert_from_v(const __c_locale&, char* __out, 
		   const int __size ,
		   const char* __fmt, ...)
  {
    char* __old = std::setlocale(4, 0);
    char* __sav = 0;
    if (std::strcmp(__old, "C"))
      {
	__sav = new char[std::strlen(__old) + 1];
	std::strcpy(__sav, __old);
	std::setlocale(4, "C");
      }

    va_list __args;
    va_start(__args, __fmt);


    const int __ret = std::vsnprintf(__out, __size, __fmt, __args);

    


    va_end (__args);
      
    if (__sav)
      {
	std::setlocale(4, __sav);
	delete [] __sav;
      }
    return __ret;
  }

}


