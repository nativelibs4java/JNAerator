
#line 1 "/Users/ochafik/github/nativelibs4java/libraries/jnaerator/jnaerator/simple.h" 1

#line 1 "/usr/include/c++/4.2.1/iostream" 1
// Standard iostream objects -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2001, 2002, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file iostream
 *  This is a Standard C++ Library header. 
 */

//
// ISO C++ 14882: 27.3  Standard iostream objects
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1
// Predefined symbols and macros -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005,
// 2006, 2007
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file c++config.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */




// Pick up any OS-specific definitions.

#line 1 "/usr/include/c++/4.2.1/bits/os_defines.h" 1
// Specific definitions for Darwin -*- C++ -*-

// Copyright (C) 2004 Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.





// System-specific #define, typedefs, corrections, etc, go here.  This
// file will come before all others.

/* Darwin has the pthread routines in libSystem, which every program
   links to, so there's no need for weak-ness for that.  */


// On Darwin, in order to enable overriding of operator new and delete,
// GCC makes the definition of these functions weak, relies on the
// loader to implement weak semantics properly, and uses
// -flat_namespace to work around the way that it doesn't.






/* APPLE LOCAL begin dtrace 6144676 */
/* Enable dtrace probes.  */
/* Inline copy of dtrace_cxa_runtime.h. */
/*
 * Generated by dtrace(1M).
 */





#line 1 "/usr/include/unistd.h" 1
/*
 * Copyright (c) 2000, 2002-2006, 2008-2010 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*-
 * Copyright (c) 1998-1999 Apple Computer, Inc. All Rights Reserved
 * Copyright (c) 1991, 1993, 1994
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)unistd.h	8.12 (Berkeley) 4/27/95
 *
 *  Copyright (c)  1998 Apple Compter, Inc.
 *  All Rights Reserved
 */

/* History:
        7/14/99 EKN at Apple fixed getdirentriesattr from getdirentryattr
        3/26/98 CHW at Apple added real interface to searchfs call
  	3/5/98  CHW at Apple added hfs semantic system calls headers
*/





#line 1 "/usr/include/_types.h" 1
/*
 * Copyright (c) 2004, 2008, 2009 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */





#line 1 "/usr/include/sys/_types.h" 1
/*
 * Copyright (c) 2003-2007 Apple Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */





#line 1 "/usr/include/sys/cdefs.h" 1
/*
 * Copyright (c) 2000-2009 Apple Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/* Copyright 1995 NeXT Computer, Inc. All rights reserved. */
/*
 * Copyright (c) 1991, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * This code is derived from software contributed to Berkeley by
 * Berkeley Software Design, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)cdefs.h	8.8 (Berkeley) 1/9/95
 */












/*
 * The __CONCAT macro is used to concatenate parts of symbol names, e.g.
 * with "#define OLD(foo) __CONCAT(old,foo)", OLD(foo) produces oldfoo.
 * The __CONCAT macro is a bit tricky -- make sure you don't put spaces
 * in between its arguments.  __CONCAT can also concatenate double-quoted
 * strings produced by the __STRING macro, but this only works with ANSI C.
 */












































/*
 * GCC1 and some versions of GCC2 declare dead (non-returning) and
 * pure (no side effects) functions using "volatile" and "const";
 * unfortunately, these then cause warnings under "-ansi -pedantic".
 * GCC2 uses a new, peculiar __attribute__((attrs)) style.  All of
 * these work for GNU C++ (modulo a slight glitch in the C++ grammar
 * in the distribution version of 2.5.5).
 */

	











/* __dead and __pure are depreciated.  Use __dead2 and __pure2 instead */





/* Delete pseudo-keywords wherever they are not available or needed. */
















/*
 * GCC 2.95 provides `__restrict' as an extension to C90 to support the
 * C99-specific `restrict' type qualifier.  We happen to use `__restrict' as
 * a way to define the `restrict' type qualifier without disturbing older
 * software that is unaware of C99 keywords.
 */








/*
 * Compiler-dependent macros to declare that functions take printf-like
 * or scanf-like arguments.  They are null except for versions of gcc
 * that are known to support the features properly.  Functions declared
 * with these attributes will cause compilation warnings if there is a
 * mismatch between the format string and subsequent function parameter
 * types.
 */




























/*
 * COMPILATION ENVIRONMENTS -- see compat(5) for additional detail
 *
 * DEFAULT	By default newly complied code will get POSIX APIs plus
 *		Apple API extensions in scope.
 *
 *		Most users will use this compilation environment to avoid
 *		behavioral differences between 32 and 64 bit code.
 *
 * LEGACY	Defining _NONSTD_SOURCE will get pre-POSIX APIs plus Apple
 *		API extensions in scope.
 *
 *		This is generally equivalent to the Tiger release compilation
 *		environment, except that it cannot be applied to 64 bit code;
 *		its use is discouraged.
 *
 *		We expect this environment to be deprecated in the future.
 *
 * STRICT	Defining _POSIX_C_SOURCE or _XOPEN_SOURCE restricts the
 *		available APIs to exactly the set of APIs defined by the
 *		corresponding standard, based on the value defined.
 *
 *		A correct, portable definition for _POSIX_C_SOURCE is 200112L.
 *		A correct, portable definition for _XOPEN_SOURCE is 600L.
 *
 *		Apple API extensions are not visible in this environment,
 *		which can cause Apple specific code to fail to compile,
 *		or behave incorrectly if prototypes are not in scope or
 *		warnings about missing prototypes are not enabled or ignored.
 *
 * In any compilation environment, for correct symbol resolution to occur,
 * function prototypes must be in scope.  It is recommended that all Apple
 * tools users add either the "-Wall" or "-Wimplicit-function-declaration"
 * compiler flags to their projects to be warned when a function is being
 * used without a prototype in scope.
 */

/* These settings are particular to each product. */
/* Platform: MacOSX */

/* #undef __DARWIN_ONLY_UNIX_CONFORMANCE (automatically set for 64-bit) */


/*
 * The __DARWIN_ALIAS macros are used to do symbol renaming; they allow
 * legacy code to use the old symbol, thus maintaining binary compatibility
 * while new code can use a standards compliant version of the same function.
 *
 * __DARWIN_ALIAS is used by itself if the function signature has not
 * changed, it is used along with a #ifdef check for __DARWIN_UNIX03
 * if the signature has changed.  Because the __LP64__ environment
 * only supports UNIX03 semantics it causes __DARWIN_UNIX03 to be
 * defined, but causes __DARWIN_ALIAS to do no symbol mangling.
 *
 * As a special case, when XCode is used to target a specific version of the
 * OS, the manifest constant __ENVIRONMENT_MAC_OS_X_VERSION_MIN_REQUIRED__
 * will be defined by the compiler, with the digits representing major version
 * time 100 + minor version times 10 (e.g. 10.5 := 1050).  If we are targeting
 * pre-10.5, and it is the default compilation environment, revert the
 * compilation environment to pre-__DARWIN_UNIX03.
 */




































































/*
 * symbol suffixes used for symbol versioning
 */










































/*
 * symbol versioning macros
 */














/*
 * symbol release macros
 */

#line 1 "/usr/include/sys/_symbol_aliasing.h" 1
/* Copyright (c) 2010 Apple Inc. All rights reserved.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */


















































































































#line 312 "/usr/include/sys/cdefs.h" 2










/*
 * POSIX.1 requires that the macros we test be defined before any standard
 * header file is included.  This permits us to convert values for feature
 * testing, as necessary, using only _POSIX_C_SOURCE.
 *
 * Here's a quick run-down of the versions:
 *  defined(_POSIX_SOURCE)		1003.1-1988
 *  _POSIX_C_SOURCE == 1L		1003.1-1990
 *  _POSIX_C_SOURCE == 2L		1003.2-1992 C Language Binding Option
 *  _POSIX_C_SOURCE == 199309L		1003.1b-1993
 *  _POSIX_C_SOURCE == 199506L		1003.1c-1995, 1003.1i-1995,
 *					and the omnibus ISO/IEC 9945-1: 1996
 *  _POSIX_C_SOURCE == 200112L		1003.1-2001
 *  _POSIX_C_SOURCE == 200809L		1003.1-2008
 *
 * In addition, the X/Open Portability Guide, which is now the Single UNIX
 * Specification, defines a feature-test macro which indicates the version of
 * that specification, and which subsumes _POSIX_C_SOURCE.
 */

/* Deal with IEEE Std. 1003.1-1990, in which _POSIX_C_SOURCE == 1L. */





/* Deal with IEEE Std. 1003.2-1992, in which _POSIX_C_SOURCE == 2L. */





/* Deal with various X/Open Portability Guides and Single UNIX Spec. */













/*
 * Deal with all versions of POSIX.  The ordering relative to the tests above is
 * important.
 */




/*
 * Deprecation macro
 */








/* POSIX C deprecation macros */

#line 1 "/usr/include/sys/_posix_availability.h" 1
/* Copyright (c) 2010 Apple Inc. All rights reserved.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
















































#line 366 "/usr/include/sys/cdefs.h" 2



/*
 * Set a single macro which will always be defined and can be used to determine
 * the appropriate namespace.  For POSIX, these values will correspond to
 * _POSIX_C_SOURCE value.  Currently there are two additional levels corresponding
 * to ANSI (_ANSI_SOURCE) and Darwin extensions (_DARWIN_C_SOURCE)
 */












/*
 * long long is not supported in c89 (__STRICT_ANSI__), but g++ -ansi and
 * c99 still want long longs.  While not perfect, we allow long longs for
 * g++.
 */




/*
 * Long double compatibility macro allow selecting variant symbols based
 * on the old (compatible) 64-bit long doubles, or the new 128-bit
 * long doubles.  This applies only to ppc; i386 already has long double
 * support, while ppc64 doesn't have any backwards history.
 */












/*****************************************
 *  Public darwin-specific feature macros
 *****************************************/

/*
 * _DARWIN_FEATURE_64_BIT_INODE indicates that the ino_t type is 64-bit, and
 * structures modified for 64-bit inodes (like struct stat) will be used.
 */




/*
 * _DARWIN_FEATURE_LONG_DOUBLE_IS_DOUBLE indicates when the long double type
 * is the same as the double type (ppc and arm only)
 */




/*
 * _DARWIN_FEATURE_64_ONLY_BIT_INODE indicates that the ino_t type may only
 * be 64-bit; there is no support for 32-bit ino_t when this macro is defined
 * (and non-zero).  There is no struct stat64 either, as the regular
 * struct stat will already be the 64-bit version.
 */




/*
 * _DARWIN_FEATURE_ONLY_VERS_1050 indicates that only those APIs updated
 * in 10.5 exists; no pre-10.5 variants are available.
 */




/*
 * _DARWIN_FEATURE_ONLY_UNIX_CONFORMANCE indicates only UNIX conforming API
 * are available (the legacy BSD APIs are not available)
 */




/*
 * _DARWIN_FEATURE_UNIX_CONFORMANCE indicates whether UNIX conformance is on,
 * and specifies the conformance level (3 is SUSv3)
 */




/* 
 * This macro casts away the qualifier from the variable
 *
 * Note: use at your own risk, removing qualifiers can result in
 * catastrophic run-time failures.
 */






#line 32 "/usr/include/sys/_types.h" 2

#line 1 "/usr/include/machine/_types.h" 1
/*
 * Copyright (c) 2003-2007 Apple Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */





#line 1 "/usr/include/i386/_types.h" 1
/*
 * Copyright (c) 2000-2003 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */



/*
 * This header file contains integer types.  It's intended to also contain
 * flotaing point and other arithmetic types, as needed, later.
 */


typedef signed char		__int8_t;



typedef unsigned char		__uint8_t;
typedef	short			__int16_t;
typedef	unsigned short		__uint16_t;
typedef int			__int32_t;
typedef unsigned int		__uint32_t;
typedef long long		__int64_t;
typedef unsigned long long	__uint64_t;

typedef long			__darwin_intptr_t;
typedef unsigned int		__darwin_natural_t;

/*
 * The rune type below is declared to be an ``int'' instead of the more natural
 * ``unsigned long'' or ``long''.  Two things are happening here.  It is not
 * unsigned so that EOF (-1) can be naturally assigned to it and used.  Also,
 * it looks like 10646 will be a 31 bit standard.  This means that if your
 * ints cannot hold 32 bits, you will be in trouble.  The reason an int was
 * chosen over a long is that the is*() and to*() routines take ints (says
 * ANSI C), but they use __darwin_ct_rune_t instead of int.  By changing it
 * here, you lose a bit of ANSI conformance, but your programs will still
 * work.
 *
 * NOTE: rune_t is not covered by ANSI nor other standards, and should not
 * be instantiated outside of lib/libc/locale.  Use wchar_t.  wchar_t and
 * rune_t must be the same type.  Also wint_t must be no narrower than
 * wchar_t, and should also be able to hold all members of the largest
 * character set plus one extra value (WEOF). wint_t must be at least 16 bits.
 */

typedef int			__darwin_ct_rune_t;	/* ct_rune_t */

/*
 * mbstate_t is an opaque object to keep conversion state, during multibyte
 * stream conversions.  The content must not be referenced by user programs.
 */
typedef union {
	char		__mbstate8[128];
	long long	_mbstateL;			/* for alignment */
} __mbstate_t;

typedef __mbstate_t		__darwin_mbstate_t;	/* mbstate_t */




typedef int			__darwin_ptrdiff_t;	/* ptr1 - ptr2 */





typedef unsigned long		__darwin_size_t;	/* sizeof() */





typedef void *			__darwin_va_list;	/* va_list */





typedef __darwin_ct_rune_t	__darwin_wchar_t;	/* wchar_t */


typedef __darwin_wchar_t	__darwin_rune_t;	/* rune_t */




typedef __darwin_ct_rune_t	__darwin_wint_t;	/* wint_t */


typedef unsigned long		__darwin_clock_t;	/* clock() */
typedef __uint32_t		__darwin_socklen_t;	/* socklen_t (duh) */
typedef long			__darwin_ssize_t;	/* byte count or error */
typedef long			__darwin_time_t;	/* time() */



#line 32 "/usr/include/machine/_types.h" 2








#line 33 "/usr/include/sys/_types.h" 2

/* pthread opaque structures */






















struct __darwin_pthread_handler_rec
{
	void           (*__routine)(void *);	/* Routine to call */
	void           *__arg;			/* Argument to pass */
	struct __darwin_pthread_handler_rec *__next;
};
struct _opaque_pthread_attr_t { long __sig; char __opaque[36]; };
struct _opaque_pthread_cond_t { long __sig; char __opaque[24]; };
struct _opaque_pthread_condattr_t { long __sig; char __opaque[4]; };
struct _opaque_pthread_mutex_t { long __sig; char __opaque[40]; };
struct _opaque_pthread_mutexattr_t { long __sig; char __opaque[8]; };
struct _opaque_pthread_once_t { long __sig; char __opaque[4]; };
struct _opaque_pthread_rwlock_t { long __sig; char __opaque[124]; };
struct _opaque_pthread_rwlockattr_t { long __sig; char __opaque[12]; };
struct _opaque_pthread_t { long __sig; struct __darwin_pthread_handler_rec  *__cleanup_stack; char __opaque[596]; };

/*
 * Type definitions; takes common type definitions that must be used
 * in multiple header files due to [XSI], removes them from the system
 * space, and puts them in the implementation space.
 */















typedef	__int64_t	__darwin_blkcnt_t;	/* total blocks */
typedef	__int32_t	__darwin_blksize_t;	/* preferred block size */
typedef __int32_t	__darwin_dev_t;		/* dev_t */
typedef unsigned int	__darwin_fsblkcnt_t;	/* Used by statvfs and fstatvfs */
typedef unsigned int	__darwin_fsfilcnt_t;	/* Used by statvfs and fstatvfs */
typedef __uint32_t	__darwin_gid_t;		/* [???] process and group IDs */
typedef __uint32_t	__darwin_id_t;		/* [XSI] pid_t, uid_t, or gid_t*/
typedef __uint64_t	__darwin_ino64_t;	/* [???] Used for 64 bit inodes */

typedef __darwin_ino64_t __darwin_ino_t;	/* [???] Used for inodes */



typedef __darwin_natural_t __darwin_mach_port_name_t; /* Used by mach */
typedef __darwin_mach_port_name_t __darwin_mach_port_t; /* Used by mach */
typedef __uint16_t	__darwin_mode_t;	/* [???] Some file attributes */
typedef __int64_t	__darwin_off_t;		/* [???] Used for file sizes */
typedef __int32_t	__darwin_pid_t;		/* [???] process and group IDs */
typedef struct _opaque_pthread_attr_t
			__darwin_pthread_attr_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_cond_t
			__darwin_pthread_cond_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_condattr_t
			__darwin_pthread_condattr_t; /* [???] Used for pthreads */
typedef unsigned long	__darwin_pthread_key_t;	/* [???] Used for pthreads */
typedef struct _opaque_pthread_mutex_t
			__darwin_pthread_mutex_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_mutexattr_t
			__darwin_pthread_mutexattr_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_once_t
			__darwin_pthread_once_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_rwlock_t
			__darwin_pthread_rwlock_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_rwlockattr_t
			__darwin_pthread_rwlockattr_t; /* [???] Used for pthreads */
typedef struct _opaque_pthread_t
			*__darwin_pthread_t;	/* [???] Used for pthreads */
typedef __uint32_t	__darwin_sigset_t;	/* [???] signal set */
typedef __int32_t	__darwin_suseconds_t;	/* [???] microseconds */
typedef __uint32_t	__darwin_uid_t;		/* [???] user IDs */
typedef __uint32_t	__darwin_useconds_t;	/* [???] microseconds */
typedef	unsigned char	__darwin_uuid_t[16];
typedef	char	__darwin_uuid_string_t[37];



#line 27 "/usr/include/_types.h" 2











typedef	int		__darwin_nl_item;
typedef	int		__darwin_wctrans_t;



typedef	unsigned long	__darwin_wctype_t;

























#line 71 "/usr/include/unistd.h" 2

#line 1 "/usr/include/sys/unistd.h" 1
/*
 * Copyright (c) 2000-2010 Apple Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/* Copyright (c) 1995 NeXT Computer, Inc. All Rights Reserved */
/*
 * Copyright (c) 1989, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)unistd.h	8.2 (Berkeley) 1/7/94
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 67 "/usr/include/sys/unistd.h" 2

/*
 * Although we have saved user/group IDs, we do not use them in setuid
 * as described in POSIX 1003.1, because the feature does not work for
 * root.  We use the saved IDs in seteuid/setegid, which are not currently
 * part of the POSIX 1003.1 specification.
 */







/* execution-time symbolic constants */
				/* may disable terminal special characters */






/* access function */






/*
 * Extended access functions.
 * Note that we depend on these matching the definitions in sys/kauth.h,
 * but with the bits shifted left by 8.
 */





















/* whence values for lseek(2) */







/* whence values for lseek(2); renamed by POSIX 1003.1 */






struct accessx_descriptor {
	unsigned int ad_name_offset;
	int ad_flags;
	int ad_pad[2];
};




/* configurable pathname variables */































/* configurable system strings */




#line 72 "/usr/include/unistd.h" 2

#line 1 "/usr/include/Availability.h" 1
/*
 * Copyright (c) 2007-2010 by Apple Inc.. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
 


 /*     
    These macros are for use in OS header files. They enable function prototypes
    and Objective-C methods to be tagged with the OS version in which they
    were first available; and, if applicable, the OS version in which they 
    became deprecated.  
     
    The desktop Mac OS X and the iPhone OS X each have different version numbers.
    The __OSX_AVAILABLE_STARTING() macro allows you to specify both the desktop
    and phone OS version numbers.  For instance:
        __OSX_AVAILABLE_STARTING(__MAC_10_2,__IPHONE_2_0)
    means the function/method was first available on Mac OS X 10.2 on the desktop
    and first available in OS X 2.0 on the iPhone.
    
    If a function is available on one platform, but not the other a _NA (not
    applicable) parameter is used.  For instance:
            __OSX_AVAILABLE_STARTING(__MAC_10_3,__IPHONE_NA)
    means that the function/method was first available on Mac OS X 10.3, and it
    currently not implemented on the iPhone.

    At some point, a function/method may be deprecated.  That means Apple
    recommends applications stop using the function, either because there is a 
    better replacement or the functionality is being phased out.  Deprecated
    functions/methods can be tagged with a __OSX_AVAILABLE_BUT_DEPRECATED()
    macro which specifies the OS version where the function became available
    as well as the OS version in which it became deprecated.  For instance:
        __OSX_AVAILABLE_BUT_DEPRECATED(__MAC_10_0,__MAC_10_5,__IPHONE_NA,__IPHONE_NA)
    means that the function/method was introduced in Mac OS X 10.0, then
    became deprecated beginning in Mac OS X 10.5.  On the iPhone the function 
    has never been available.  
    
    For these macros to function properly, a program must specify the OS version range 
    it is targeting.  The min OS version is specified as an option to the compiler:
    -mmacosx-version-min=10.x when building for Mac OS X, and -miphone-version-min=1.x.x
    when building for the iPhone.  The upper bound for the OS version is rarely needed,
    but it can be set on the command line via: -D__MAC_OS_X_VERSION_MAX_ALLOWED=10xx for
    Mac OS X and __IPHONE_OS_VERSION_MAX_ALLOWED = 1xxx for iPhone.  
    
    Examples:

        A function available in Mac OS X 10.5 and later, but not on the phone:
        
            extern void mymacfunc() __OSX_AVAILABLE_STARTING(__MAC_10_5,__IPHONE_NA);


        An Objective-C method in Mac OS X 10.5 and later, but not on the phone:
        
            @interface MyClass : NSObject
            -(void) mymacmethod __OSX_AVAILABLE_STARTING(__MAC_10_5,__IPHONE_NA);
            @end

        
        An enum available on the phone, but not available on Mac OS X:
        
            #if __IPHONE_OS_VERSION_MIN_REQUIRED
                enum { myEnum = 1 };
            #endif
           Note: this works when targeting the Mac OS X platform because 
           __IPHONE_OS_VERSION_MIN_REQUIRED is undefined which evaluates to zero. 
        

        An enum with values added in different iPhoneOS versions:
		
			enum {
			    myX  = 1,	// Usable on iPhoneOS 2.1 and later
			    myY  = 2,	// Usable on iPhoneOS 3.0 and later
			    myZ  = 3,	// Usable on iPhoneOS 3.0 and later
				...
		      Note: you do not want to use #if with enumeration values
			  when a client needs to see all values at compile time
			  and use runtime logic to only use the viable values.
			  

    It is also possible to use the *_VERSION_MIN_REQUIRED in source code to make one
    source base that can be compiled to target a range of OS versions.  It is best
    to not use the _MAC_* and __IPHONE_* macros for comparisons, but rather their values.
    That is because you might get compiled on an old OS that does not define a later
    OS version macro, and in the C preprocessor undefined values evaluate to zero
    in expresssions, which could cause the #if expression to evaluate in an unexpected
    way.
    
        #ifdef __MAC_OS_X_VERSION_MIN_REQUIRED
            // code only compiled when targeting Mac OS X and not iPhone
            // note use of 1050 instead of __MAC_10_5
            #if __MAC_OS_X_VERSION_MIN_REQUIRED < 1050
                // code in here might run on pre-Leopard OS
            #else
                // code here can assume Leopard or later
            #endif
        #endif


*/
























#line 1 "/usr/include/AvailabilityInternal.h" 1
/*
 * Copyright (c) 2007-2010 by Apple Inc.. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */

/*
    File:       AvailabilityInternal.h
 
    Contains:   implementation details of __OSX_AVAILABLE_* macros from <Availability.h>

*/






    
        
        
    



    
    
    
    
    

    
    
    
    



    
    
        
    
    
    
        
        
    

    
    
        
    
        
    
        
    
    
    
    
    
        
    
        
    
        
    
    
    
        
        
    
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
    
        
        
        
    
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
    
        
        
        
        
    
        
        
        
        
    
        
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
        
    
        
        
        
        
        
    
        
        
        
        
        
    
        
        
        
        
        
    
        
        
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
        
        
    
        
        
        
        
        
        
    
        
        
        
        
        
        
    
        
        
        
        
        
        
    
        
        
        
        
        
        
    
        
        
        
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
    
    
    
        
    
        
    
        
    
    
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
        
        
        
    
    
    
    


    
    
    
    
        
    
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
        
    
        
    
        
    
    
    
        
    
        
    
    
        
        
    
        
        
    
    
        
        
        
    
        
        
        
    
    
        
        
        
        
    
        
        
        
        
    
    
        
        
        
        
        
    
        
        
        
        
        
    
    
        
        
        
        
        
        
    
        
        
        
        
        
        
    
    
        
        
        
        
        
        
        
    
        
        
        
        
        
        
        
    
    
    
    
    
    
    
    
    
    




#line 122 "/usr/include/Availability.h" 2



    
    



    
    



    
    





#line 73 "/usr/include/unistd.h" 2



typedef __darwin_gid_t		gid_t;




typedef __darwin_intptr_t	intptr_t;




typedef __darwin_off_t		off_t;




typedef __darwin_pid_t		pid_t;




/* DO NOT REMOVE THIS COMMENT: fixincludes needs to see:
 * _GCC_SIZE_T */
typedef __darwin_size_t		size_t;




typedef	__darwin_ssize_t	ssize_t;




typedef __darwin_uid_t		uid_t;	/* user id 	*/




typedef __darwin_useconds_t	useconds_t;











/* Version test macros */
/* _POSIX_VERSION and _POSIX2_VERSION from sys/unistd.h */




/* Please keep this list in the same order as the applicable standard */









































































































/* Removed in Issue 7 */

















/* configurable system variables */




































































































































/* Removed in Issue 7 */















/* Removed in Issue 6 */


























/* Removed in Issue 7 */

































/* POSIX.1-1990 */

extern "C" {
void	 _exit(int) ;
int	 access(const char *, int);
unsigned int
	 alarm(unsigned int);
int	 chdir(const char *);
int	 chown(const char *, uid_t, gid_t);

int	 close(int) __asm("_" "close"  "$UNIX2003");

int	 dup(int);
int	 dup2(int, int);
int	 execl(const char *, const char *, ...);
int	 execle(const char *, const char *, ...);
int	 execlp(const char *, const char *, ...);
int	 execv(const char *, char * const *);
int	 execve(const char *, char * const *, char * const *);
int	 execvp(const char *, char * const *);
pid_t	 fork(void);
long	 fpathconf(int, int);
char	*getcwd(char *, size_t);
gid_t	 getegid(void);
uid_t	 geteuid(void);
gid_t	 getgid(void);



int	 getgroups(int, gid_t []);

char	*getlogin(void);
pid_t	 getpgrp(void);
pid_t	 getpid(void);
pid_t	 getppid(void);
uid_t	 getuid(void);
int	 isatty(int);
int	 link(const char *, const char *);
off_t	 lseek(int, off_t, int);
long	 pathconf(const char *, int);

int	 pause(void) __asm("_" "pause"  "$UNIX2003");

int	 pipe(int [2]);

ssize_t	 read(int, void *, size_t) __asm("_" "read"  "$UNIX2003");

int	 rmdir(const char *);
int	 setgid(gid_t);
int	 setpgid(pid_t, pid_t);
pid_t	 setsid(void);
int	 setuid(uid_t);

unsigned int
	 sleep(unsigned int) __asm("_" "sleep"  "$UNIX2003");

long	 sysconf(int);
pid_t	 tcgetpgrp(int);
int	 tcsetpgrp(int, pid_t);
char	*ttyname(int);


int	 ttyname_r(int, char *, size_t) __asm("_" "ttyname_r" "$UNIX2003");




int	 unlink(const char *);

ssize_t	 write(int, const void *, size_t) __asm("_" "write"  "$UNIX2003");
}



/* Additional functionality provided by:
 * POSIX.2-1992 C Language Binding Option
 */


extern "C" {
size_t	 confstr(int, char *, size_t) __asm("_" "confstr" "$UNIX2003");

int	 getopt(int, char * const [], const char *) __asm("_" "getopt" "$UNIX2003");

extern char *optarg;			/* getopt(3) external variables */
extern int optind, opterr, optopt;
}




/* Additional functionality provided by:
 * POSIX.1c-1995,
 * POSIX.1i-1995,
 * and the omnibus ISO/IEC 9945-1: 1996
 */


                               /* These F_* are really XSI or Issue 6 */





 extern "C" {

/* Begin XSI */
/* Removed in Issue 6 */

void	*brk(const void *);
int	 chroot(const char *) ;


char	*crypt(const char *, const char *);

/* Multiply defined in stdio.h and unistd.h by SUS */

char    *ctermid(char *);


void	 encrypt(char *, int) __asm("_" "encrypt" "$UNIX2003");



int	 fchdir(int);
long	 gethostid(void);
pid_t	 getpgid(pid_t);
pid_t	 getsid(pid_t);

/* Removed in Issue 6 */

int	 getdtablesize(void) ;
int	 getpagesize(void)  ;
char	*getpass(const char *) ;


/* Removed in Issue 7 */

char	*getwd(char *) ; /* obsoleted by getcwd() */


int	 lchown(const char *, uid_t, gid_t) __asm("_" "lchown" "$UNIX2003");

int	 lockf(int, int, off_t) __asm("_" "lockf"  "$UNIX2003");

int	 nice(int) __asm("_" "nice" "$UNIX2003");

ssize_t	 pread(int, void *, size_t, off_t) __asm("_" "pread"  "$UNIX2003");

ssize_t	 pwrite(int, const void *, size_t, off_t) __asm("_" "pwrite"  "$UNIX2003");

/* Removed in Issue 6 */

/* Note that Issue 5 changed the argument as intprt_t,
 * but we keep it as int for binary compatability. */
void	*sbrk(int);



pid_t	 setpgrp(void) __asm("_" "setpgrp" "$UNIX2003");




int	 setregid(gid_t, gid_t) __asm("_" "setregid" "$UNIX2003");

int	 setreuid(uid_t, uid_t) __asm("_" "setreuid" "$UNIX2003");

void     swab(const void * , void * , ssize_t);
void	 sync(void);
int	 truncate(const char *, off_t);
useconds_t	 ualarm(useconds_t, useconds_t);
int	 usleep(useconds_t) __asm("_" "usleep"  "$UNIX2003");
pid_t	 vfork(void);
/* End XSI */

int	 fsync(int) __asm("_" "fsync"  "$UNIX2003");

int	 ftruncate(int, off_t);
int	 getlogin_r(char *, size_t);
}




/* Additional functionality provided by:
 * POSIX.1-2001
 * ISO C99
 */


extern "C" {
int	 fchown(int, uid_t, gid_t);
int	 gethostname(char *, size_t);
ssize_t  readlink(const char * , char * , size_t);
int	 setegid(gid_t);
int	 seteuid(uid_t);
int	 symlink(const char *, const char *);
}




/* Darwin extensions */



#line 1 "/usr/include/sys/select.h" 1
/*
 * Copyright (c) 2000-2005 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/*
 * Copyright (c) 1992, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)select.h	8.2 (Berkeley) 1/4/94
 */





#line 1 "/usr/include/sys/appleapiopts.h" 1
/*
 * Copyright (c) 2002 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */




































#line 66 "/usr/include/sys/select.h" 2

#line 1 "/usr/include/sys/cdefs.h" 1

#line 67 "/usr/include/sys/select.h" 2

#line 1 "/usr/include/sys/_types.h" 1

#line 68 "/usr/include/sys/select.h" 2

/*
 * [XSI] The <sys/select.h> header shall define the fd_set type as a structure.
 * The timespec structure shall be defined as described in <time.h>
 * The <sys/select.h> header shall define the timeval structure.
 */




#line 1 "/usr/include/sys/_structs.h" 1
/*
 * Copyright (c) 2004-2006 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */


#line 1 "/usr/include/sys/cdefs.h" 1

#line 31 "/usr/include/sys/_structs.h" 2

#line 1 "/usr/include/sys/_types.h" 1

#line 32 "/usr/include/sys/_structs.h" 2














































	
	
	








struct timespec
{
	__darwin_time_t	tv_sec;
	long		tv_nsec;
};







struct timeval
{
	__darwin_time_t		tv_sec;		/* seconds */
	__darwin_suseconds_t	tv_usec;	/* and microseconds */
};









	
	














	
	
	
	
	
	

	















	
	
	
	
	
	









/*
 * Select uses bit masks of file descriptors in longs.  These macros
 * manipulate such bit fields (the filesystem macros use chars).  The
 * extra protection here is to permit application redefinition above
 * the default size.
 */









extern "C" {
typedef	struct fd_set {
	__int32_t	fds_bits[((((1024) % ((sizeof(__int32_t) * 8))) == 0) ? ((1024) / ((sizeof(__int32_t) * 8))) : (((1024) / ((sizeof(__int32_t) * 8))) + 1))];
} fd_set;
}

/* This inline avoids argument side-effect issues with FD_ISSET() */
static inline int
__darwin_fd_isset(int _n, const struct fd_set *_p)
{
	return (_p->fds_bits[_n/(sizeof(__int32_t) * 8)] & (1<<(_n % (sizeof(__int32_t) * 8))));
}












































#line 75 "/usr/include/sys/select.h" 2

/*
 * The time_t and suseconds_t types shall be defined as described in
 * <sys/types.h>
 * The sigset_t type shall be defined as described in <signal.h>
 */


typedef	__darwin_time_t		time_t;




typedef __darwin_suseconds_t	suseconds_t;




typedef __darwin_sigset_t	sigset_t;


/*
 * [XSI] FD_CLR, FD_ISSET, FD_SET, FD_ZERO may be declared as a function, or
 *	 defined as a macro, or both
 * [XSI] FD_SETSIZE shall be defined as a macro
 */

/*
 * Select uses bit masks of file descriptors in longs.  These macros
 * manipulate such bit fields (the filesystem macros use chars).  The
 * extra protection here is to permit application redefinition above
 * the default size.
 */






















extern "C" {


int	 pselect(int, fd_set * , fd_set * ,
		fd_set * , const struct timespec * ,
		const sigset_t * )

		


		

		__asm("_" "pselect"  "$UNIX2003")


		;



#line 1 "/usr/include/sys/_select.h" 1
/*
 * Copyright (c) 2005, 2007 Apple Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */

/*
 * This is called from sys/select.h and sys/time.h for the common prototype
 * of select().  Setting _DARWIN_C_SOURCE or _DARWIN_UNLIMITED_SELECT uses
 * the version of select() that does not place a limit on the first argument
 * (nfds).  In the UNIX conformance case, values of nfds greater than
 * FD_SETSIZE will return an error of EINVAL.
 */



int	 select(int, fd_set * , fd_set * ,
		fd_set * , struct timeval * )

		


		

		__asm("_" "select"  "$UNIX2003")


		;



#line 130 "/usr/include/sys/select.h" 2

}




#line 402 "/usr/include/unistd.h" 2



typedef __darwin_dev_t		dev_t;




typedef __darwin_mode_t		mode_t;




typedef __darwin_uuid_t		uuid_t;


extern "C" {
void	 _Exit(int) ;
int	 accessx_np(const struct accessx_descriptor *, size_t, int *, uid_t);
int	 acct(const char *);
int	 add_profil(char *, size_t, unsigned long, unsigned int);
void	 endusershell(void);
int	 execvP(const char *, const char *, char * const *);
char	*fflagstostr(unsigned long);
int	 getdomainname(char *, int);
int	 getgrouplist(const char *, int, int *, int *);
int	 gethostuuid(uuid_t, const struct timespec *) ;
mode_t	 getmode(const void *, mode_t);
int	 getpeereid(int, uid_t *, gid_t *);
int	 getsgroups_np(int *, uuid_t);
char	*getusershell(void);
int	 getwgroups_np(int *, uuid_t);
int	 initgroups(const char *, int);
int	 iruserok(unsigned long, int, const char *, const char *);
int	 iruserok_sa(const void *, int, int, const char *, const char *);
int	 issetugid(void);
char	*mkdtemp(char *);
int	 mknod(const char *, mode_t, dev_t);
int	 mkstemp(char *);
int	 mkstemps(char *, int);
char	*mktemp(char *);
int	 nfssvc(int, void *);
int	 profil(char *, size_t, unsigned long, unsigned int);
int	 pthread_setugid_np(uid_t, gid_t);
int	 pthread_getugid_np( uid_t *, gid_t *);
int	 rcmd(char **, int, const char *, const char *, const char *, int *);
int	 rcmd_af(char **, int, const char *, const char *, const char *, int *,
		int);
int	 reboot(int);
int	 revoke(const char *);
int	 rresvport(int *);
int	 rresvport_af(int *, int);
int	 ruserok(const char *, int, const char *, const char *);
int	 setdomainname(const char *, int);
int	 setgroups(int, const gid_t *);
void	 sethostid(long);
int	 sethostname(const char *, int);

void	 setkey(const char *) __asm("_" "setkey" "$UNIX2003");



int	 setlogin(const char *);
void	*setmode(const char *) ;
int	 setrgid(gid_t);
int	 setruid(uid_t);
int	 setsgroups_np(int, const uuid_t);
void	 setusershell(void);
int	 setwgroups_np(int, const uuid_t);
int	 strtofflags(char **, unsigned long *, unsigned long *);
int	 swapon(const char *);
int	 syscall(int, ...);
int	 ttyslot(void);
int	 undelete(const char *);
int	 unwhiteout(const char *);
void	*valloc(size_t);			

extern char *suboptarg;			/* getsubopt(3) external variable */
int	 getsubopt(char **, char * const *, char **);

/*  HFS & HFS Plus semantics system calls go here */









int	fgetattrlist(int,void*,void*,size_t,unsigned long) ;
int	fsetattrlist(int,void*,void*,size_t,unsigned long) ;
int	getattrlist(const char*,void*,void*,size_t,unsigned long) __asm("_" "getattrlist" "$UNIX2003");
int	setattrlist(const char*,void*,void*,size_t,unsigned long) __asm("_" "setattrlist" "$UNIX2003");
int exchangedata(const char*,const char*,unsigned long);
int	getdirentriesattr(int,void*,void*,size_t,unsigned long*,unsigned long*,unsigned long*,unsigned long);



struct fssearchblock;
struct searchstate;

int	 searchfs(const char *, struct fssearchblock *, unsigned long *, unsigned int, unsigned int, struct searchstate *);
int	 fsctl(const char *,unsigned long,void*,unsigned int);
int	 ffsctl(int,unsigned long,void*,unsigned int) ;

extern int optreset;

}




#line 25 "/usr/include/c++/4.2.1/bits/os_defines.h" 2


extern "C" {
























extern void __dtrace_probe$cxa_runtime$cxa_exception_rethrow$v1(void);
extern int __dtrace_isenabled$cxa_runtime$cxa_exception_rethrow$v1(void);
extern void __dtrace_probe$cxa_runtime$cxa_exception_throw$v1$766f6964202a(void *);
extern int __dtrace_isenabled$cxa_runtime$cxa_exception_throw$v1(void);


}



/* APPLE LOCAL end dtrace 6144676 */

/* APPLE LOCAL begin version */






/* APPLE LOCAL end version */

/* APPLE LOCAL begin keymgr */








































 

	
	
	
	
	



	

	
	
	
	
	















        
        
        
        


















































 











/* APPLE LOCAL end keymgr */


#line 14 "/usr/include/c++/4.2.1/bits/c++config.h" 2

// Pick up any CPU-specific definitions.

#line 1 "/usr/include/c++/4.2.1/bits/cpu_defines.h" 1
// Specific definitions for generic platforms  -*- C++ -*-

// Copyright (C) 2005 Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file cpu_defines.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */





	
	




#line 16 "/usr/include/c++/4.2.1/bits/c++config.h" 2

// The current version of the C++ library in compressed ISO date format.


// Macros for visibility.








// Macros for controlling various namespace association schemes and modes.










// Macros for namespace scope.
// _GLIBCXX_BEGIN_NAMESPACE
// _GLIBCXX_END_NAMESPACE
// _GLIBCXX_BEGIN_NESTED_NAMESPACE
// _GLIBCXX_END_NESTED_NAMESPACE

















// Namespace associations for versioning mode.



  
  





  
  




  
  
    
    
  



// Namespace associations for debug mode.



  
  
  




  
  
  


















/* Define if compatibility should be provided for -mlong-double-64. */


// XXX GLIBCXX_ABI Deprecated
// Namespace associations for long double 128 mode.
namespace std  {




  
  





}


// Allow use of "export template." This is currently not a feature
// that g++ supports.
// #define _GLIBCXX_EXPORT_TEMPLATE 1

// Allow use of the GNU syntax extension, "extern template." This
// extension is fully documented in the g++ manual, but in a nutshell,
// it inhibits all implicit instantiations and is used throughout the
// library to avoid multiple weak definitions for required types that
// are already explicitly instantiated in the library binary. This
// substantially reduces the binary size of resulting executables.





// Certain function definitions that are meant to be overridable from
// user code are decorated with this macro.  For some targets, this
// macro causes these definitions to be weak.




// The remainder of the prewritten config is automatic; all the
// user hooks are listed above.

// Create a boolean flag to be used to determine if --fast-math is set.






// This marks string literals in header files to be extracted for eventual
// translation.  It is primarily used for messages in thrown exceptions; see
// src/functexcept.cc.  We use __N because the more traditional _N is used
// for something else under certain OSes (see BADNAMES).


// For example, <windows.h> is known to #define min and max as macros...



// End of prewritten config; the discovered settings follow.
/* config.h.  Generated by configure.  */
/* config.h.in.  Generated from configure.ac by autoheader.  */

/* Define to 1 if you have the `acosf' function. */


/* Define to 1 if you have the `acosl' function. */


/* Define to 1 if you have the `asinf' function. */


/* Define to 1 if you have the `asinl' function. */


/* Define to 1 if you have the `atan2f' function. */


/* Define to 1 if you have the `atan2l' function. */


/* Define to 1 if you have the `atanf' function. */


/* Define to 1 if you have the `atanl' function. */


/* Define to 1 if you have the `ceilf' function. */


/* Define to 1 if you have the `ceill' function. */


/* Define to 1 if you have the <complex.h> header file. */


/* Define to 1 if you have the `copysign' function. */


/* Define to 1 if you have the `copysignf' function. */


/* Define to 1 if you have the `copysignl' function. */


/* Define to 1 if you have the `cosf' function. */


/* Define to 1 if you have the `coshf' function. */


/* Define to 1 if you have the `coshl' function. */


/* Define to 1 if you have the `cosl' function. */


/* Define to 1 if you have the <endian.h> header file. */
/* #undef _GLIBCXX_HAVE_ENDIAN_H */

/* Define to 1 if you have the `expf' function. */


/* Define to 1 if you have the `expl' function. */


/* Define to 1 if you have the `fabsf' function. */


/* Define to 1 if you have the `fabsl' function. */


/* Define to 1 if you have the <fenv.h> header file. */


/* Define to 1 if you have the `finite' function. */


/* Define to 1 if you have the `finitef' function. */
/* #undef _GLIBCXX_HAVE_FINITEF */

/* Define to 1 if you have the `finitel' function. */
/* #undef _GLIBCXX_HAVE_FINITEL */

/* Define to 1 if you have the <float.h> header file. */


/* Define to 1 if you have the `floorf' function. */


/* Define to 1 if you have the `floorl' function. */


/* Define to 1 if you have the `fmodf' function. */


/* Define to 1 if you have the `fmodl' function. */


/* Define to 1 if you have the `fpclass' function. */
/* #undef _GLIBCXX_HAVE_FPCLASS */

/* Define to 1 if you have the <fp.h> header file. */
/* #undef _GLIBCXX_HAVE_FP_H */

/* Define to 1 if you have the `frexpf' function. */


/* Define to 1 if you have the `frexpl' function. */


/* Define to 1 if you have the <gconv.h> header file. */
/* #undef _GLIBCXX_HAVE_GCONV_H */

/* Define if _Unwind_GetIPInfo is available. */


/* Define to 1 if you have the `getpagesize' function. */


/* Define if gthr-default.h exists (meaning that threading support is
   enabled). */


/* Define to 1 if you have the `hypot' function. */


/* Define to 1 if you have the `hypotf' function. */


/* Define to 1 if you have the `hypotl' function. */


/* Define to 1 if you have the `iconv' function. */


/* Define to 1 if you have the `iconv_close' function. */


/* Define to 1 if you have the `iconv_open' function. */


/* Define to 1 if you have the <ieeefp.h> header file. */
/* #undef _GLIBCXX_HAVE_IEEEFP_H */

/* Define if int64_t is available in <stdint.h>. */


/* Define to 1 if you have the <inttypes.h> header file. */


/* Define to 1 if you have the `isinf' function. */


/* Define to 1 if you have the `isinff' function. */
/* #undef _GLIBCXX_HAVE_ISINFF */

/* Define to 1 if you have the `isinfl' function. */
/* #undef _GLIBCXX_HAVE_ISINFL */

/* Define to 1 if you have the `isnan' function. */


/* Define to 1 if you have the `isnanf' function. */
/* #undef _GLIBCXX_HAVE_ISNANF */

/* Define to 1 if you have the `isnanl' function. */
/* #undef _GLIBCXX_HAVE_ISNANL */

/* Defined if iswblank exists. */


/* Define if LC_MESSAGES is available in <locale.h>. */


/* Define to 1 if you have the `ldexpf' function. */


/* Define to 1 if you have the `ldexpl' function. */


/* Define to 1 if you have the <libintl.h> header file. */
/* #undef _GLIBCXX_HAVE_LIBINTL_H */

/* Define to 1 if you have the `m' library (-lm). */


/* Only used in build directory testsuite_hooks.h. */


/* Only used in build directory testsuite_hooks.h. */


/* Only used in build directory testsuite_hooks.h. */


/* Only used in build directory testsuite_hooks.h. */


/* Only used in build directory testsuite_hooks.h. */


/* Define to 1 if you have the <locale.h> header file. */


/* Define to 1 if you have the `log10f' function. */


/* Define to 1 if you have the `log10l' function. */


/* Define to 1 if you have the `logf' function. */


/* Define to 1 if you have the `logl' function. */


/* Define to 1 if you have the <machine/endian.h> header file. */


/* Define to 1 if you have the <machine/param.h> header file. */


/* Define if mbstate_t exists in wchar.h. */


/* Define to 1 if you have the <memory.h> header file. */


/* Define to 1 if you have a working `mmap' system call. */


/* Define to 1 if you have the `modf' function. */


/* Define to 1 if you have the `modff' function. */


/* Define to 1 if you have the `modfl' function. */


/* Define to 1 if you have the <nan.h> header file. */
/* #undef _GLIBCXX_HAVE_NAN_H */

/* Define to 1 if you have the `nl_langinfo' function. */


/* Define if poll is available in <poll.h>. */


/* Define to 1 if you have the `powf' function. */


/* Define to 1 if you have the `powl' function. */


/* Define to 1 if you have the `qfpclass' function. */
/* #undef _GLIBCXX_HAVE_QFPCLASS */

/* Define to 1 if you have the `setenv' function. */


/* Define if sigsetjmp is available. */


/* Define to 1 if you have the `sincos' function. */
/* #undef _GLIBCXX_HAVE_SINCOS */

/* Define to 1 if you have the `sincosf' function. */
/* #undef _GLIBCXX_HAVE_SINCOSF */

/* Define to 1 if you have the `sincosl' function. */
/* #undef _GLIBCXX_HAVE_SINCOSL */

/* Define to 1 if you have the `sinf' function. */


/* Define to 1 if you have the `sinhf' function. */


/* Define to 1 if you have the `sinhl' function. */


/* Define to 1 if you have the `sinl' function. */


/* Define to 1 if you have the `sqrtf' function. */


/* Define to 1 if you have the `sqrtl' function. */


/* Define to 1 if you have the <stdbool.h> header file. */


/* Define to 1 if you have the <stdint.h> header file. */


/* Define to 1 if you have the <stdlib.h> header file. */


/* Define if strerror_l is available in <string.h>. */
/* #undef _GLIBCXX_HAVE_STRERROR_L */

/* Define if strerror_r is available in <string.h>. */


/* Define to 1 if you have the <strings.h> header file. */


/* Define to 1 if you have the <string.h> header file. */


/* Define to 1 if you have the `strtof' function. */


/* Define to 1 if you have the `strtold' function. */


/* Define if strxfrm_l is available in <string.h>. */
/* #undef _GLIBCXX_HAVE_STRXFRM_L */

/* Define to 1 if you have the <sys/filio.h> header file. */


/* Define to 1 if you have the <sys/ioctl.h> header file. */


/* Define to 1 if you have the <sys/ipc.h> header file. */


/* Define to 1 if you have the <sys/isa_defs.h> header file. */
/* #undef _GLIBCXX_HAVE_SYS_ISA_DEFS_H */

/* Define to 1 if you have the <sys/machine.h> header file. */
/* #undef _GLIBCXX_HAVE_SYS_MACHINE_H */

/* Define to 1 if you have the <sys/param.h> header file. */
/* #undef _GLIBCXX_HAVE_SYS_PARAM_H */

/* Define to 1 if you have the <sys/resource.h> header file. */


/* Define to 1 if you have the <sys/sem.h> header file. */


/* Define to 1 if you have the <sys/stat.h> header file. */


/* Define to 1 if you have the <sys/time.h> header file. */


/* Define to 1 if you have the <sys/types.h> header file. */


/* Define to 1 if you have the <sys/uio.h> header file. */


/* Define if S_IFREG is available in <sys/stat.h>. */
/* #undef _GLIBCXX_HAVE_S_IFREG */

/* Define if S_IFREG is available in <sys/stat.h>. */


/* Define to 1 if you have the `tanf' function. */


/* Define to 1 if you have the `tanhf' function. */


/* Define to 1 if you have the `tanhl' function. */


/* Define to 1 if you have the `tanl' function. */


/* Define to 1 if the target supports thread-local storage. */
/* #undef _GLIBCXX_HAVE_TLS */

/* Define to 1 if you have the <unistd.h> header file. */


/* Defined if vfwscanf exists. */


/* Defined if vswscanf exists. */


/* Defined if vwscanf exists. */


/* Define to 1 if you have the <wchar.h> header file. */


/* Defined if wcstof exists. */


/* Define to 1 if you have the <wctype.h> header file. */


/* Define if writev is available in <sys/uio.h>. */


/* Define to 1 if you have the `_acosf' function. */
/* #undef _GLIBCXX_HAVE__ACOSF */

/* Define to 1 if you have the `_acosl' function. */
/* #undef _GLIBCXX_HAVE__ACOSL */

/* Define to 1 if you have the `_asinf' function. */
/* #undef _GLIBCXX_HAVE__ASINF */

/* Define to 1 if you have the `_asinl' function. */
/* #undef _GLIBCXX_HAVE__ASINL */

/* Define to 1 if you have the `_atan2f' function. */
/* #undef _GLIBCXX_HAVE__ATAN2F */

/* Define to 1 if you have the `_atan2l' function. */
/* #undef _GLIBCXX_HAVE__ATAN2L */

/* Define to 1 if you have the `_atanf' function. */
/* #undef _GLIBCXX_HAVE__ATANF */

/* Define to 1 if you have the `_atanl' function. */
/* #undef _GLIBCXX_HAVE__ATANL */

/* Define to 1 if you have the `_ceilf' function. */
/* #undef _GLIBCXX_HAVE__CEILF */

/* Define to 1 if you have the `_ceill' function. */
/* #undef _GLIBCXX_HAVE__CEILL */

/* Define to 1 if you have the `_copysign' function. */
/* #undef _GLIBCXX_HAVE__COPYSIGN */

/* Define to 1 if you have the `_copysignl' function. */
/* #undef _GLIBCXX_HAVE__COPYSIGNL */

/* Define to 1 if you have the `_cosf' function. */
/* #undef _GLIBCXX_HAVE__COSF */

/* Define to 1 if you have the `_coshf' function. */
/* #undef _GLIBCXX_HAVE__COSHF */

/* Define to 1 if you have the `_coshl' function. */
/* #undef _GLIBCXX_HAVE__COSHL */

/* Define to 1 if you have the `_cosl' function. */
/* #undef _GLIBCXX_HAVE__COSL */

/* Define to 1 if you have the `_expf' function. */
/* #undef _GLIBCXX_HAVE__EXPF */

/* Define to 1 if you have the `_expl' function. */
/* #undef _GLIBCXX_HAVE__EXPL */

/* Define to 1 if you have the `_fabsf' function. */
/* #undef _GLIBCXX_HAVE__FABSF */

/* Define to 1 if you have the `_fabsl' function. */
/* #undef _GLIBCXX_HAVE__FABSL */

/* Define to 1 if you have the `_finite' function. */
/* #undef _GLIBCXX_HAVE__FINITE */

/* Define to 1 if you have the `_finitef' function. */
/* #undef _GLIBCXX_HAVE__FINITEF */

/* Define to 1 if you have the `_finitel' function. */
/* #undef _GLIBCXX_HAVE__FINITEL */

/* Define to 1 if you have the `_floorf' function. */
/* #undef _GLIBCXX_HAVE__FLOORF */

/* Define to 1 if you have the `_floorl' function. */
/* #undef _GLIBCXX_HAVE__FLOORL */

/* Define to 1 if you have the `_fmodf' function. */
/* #undef _GLIBCXX_HAVE__FMODF */

/* Define to 1 if you have the `_fmodl' function. */
/* #undef _GLIBCXX_HAVE__FMODL */

/* Define to 1 if you have the `_fpclass' function. */
/* #undef _GLIBCXX_HAVE__FPCLASS */

/* Define to 1 if you have the `_frexpf' function. */
/* #undef _GLIBCXX_HAVE__FREXPF */

/* Define to 1 if you have the `_frexpl' function. */
/* #undef _GLIBCXX_HAVE__FREXPL */

/* Define to 1 if you have the `_hypot' function. */
/* #undef _GLIBCXX_HAVE__HYPOT */

/* Define to 1 if you have the `_hypotf' function. */
/* #undef _GLIBCXX_HAVE__HYPOTF */

/* Define to 1 if you have the `_hypotl' function. */
/* #undef _GLIBCXX_HAVE__HYPOTL */

/* Define to 1 if you have the `_isinf' function. */
/* #undef _GLIBCXX_HAVE__ISINF */

/* Define to 1 if you have the `_isinff' function. */
/* #undef _GLIBCXX_HAVE__ISINFF */

/* Define to 1 if you have the `_isinfl' function. */
/* #undef _GLIBCXX_HAVE__ISINFL */

/* Define to 1 if you have the `_isnan' function. */
/* #undef _GLIBCXX_HAVE__ISNAN */

/* Define to 1 if you have the `_isnanf' function. */
/* #undef _GLIBCXX_HAVE__ISNANF */

/* Define to 1 if you have the `_isnanl' function. */
/* #undef _GLIBCXX_HAVE__ISNANL */

/* Define to 1 if you have the `_ldexpf' function. */
/* #undef _GLIBCXX_HAVE__LDEXPF */

/* Define to 1 if you have the `_ldexpl' function. */
/* #undef _GLIBCXX_HAVE__LDEXPL */

/* Define to 1 if you have the `_log10f' function. */
/* #undef _GLIBCXX_HAVE__LOG10F */

/* Define to 1 if you have the `_log10l' function. */
/* #undef _GLIBCXX_HAVE__LOG10L */

/* Define to 1 if you have the `_logf' function. */
/* #undef _GLIBCXX_HAVE__LOGF */

/* Define to 1 if you have the `_logl' function. */
/* #undef _GLIBCXX_HAVE__LOGL */

/* Define to 1 if you have the `_modf' function. */
/* #undef _GLIBCXX_HAVE__MODF */

/* Define to 1 if you have the `_modff' function. */
/* #undef _GLIBCXX_HAVE__MODFF */

/* Define to 1 if you have the `_modfl' function. */
/* #undef _GLIBCXX_HAVE__MODFL */

/* Define to 1 if you have the `_powf' function. */
/* #undef _GLIBCXX_HAVE__POWF */

/* Define to 1 if you have the `_powl' function. */
/* #undef _GLIBCXX_HAVE__POWL */

/* Define to 1 if you have the `_qfpclass' function. */
/* #undef _GLIBCXX_HAVE__QFPCLASS */

/* Define to 1 if you have the `_sincos' function. */
/* #undef _GLIBCXX_HAVE__SINCOS */

/* Define to 1 if you have the `_sincosf' function. */
/* #undef _GLIBCXX_HAVE__SINCOSF */

/* Define to 1 if you have the `_sincosl' function. */
/* #undef _GLIBCXX_HAVE__SINCOSL */

/* Define to 1 if you have the `_sinf' function. */
/* #undef _GLIBCXX_HAVE__SINF */

/* Define to 1 if you have the `_sinhf' function. */
/* #undef _GLIBCXX_HAVE__SINHF */

/* Define to 1 if you have the `_sinhl' function. */
/* #undef _GLIBCXX_HAVE__SINHL */

/* Define to 1 if you have the `_sinl' function. */
/* #undef _GLIBCXX_HAVE__SINL */

/* Define to 1 if you have the `_sqrtf' function. */
/* #undef _GLIBCXX_HAVE__SQRTF */

/* Define to 1 if you have the `_sqrtl' function. */
/* #undef _GLIBCXX_HAVE__SQRTL */

/* Define to 1 if you have the `_tanf' function. */
/* #undef _GLIBCXX_HAVE__TANF */

/* Define to 1 if you have the `_tanhf' function. */
/* #undef _GLIBCXX_HAVE__TANHF */

/* Define to 1 if you have the `_tanhl' function. */
/* #undef _GLIBCXX_HAVE__TANHL */

/* Define to 1 if you have the `_tanl' function. */
/* #undef _GLIBCXX_HAVE__TANL */

/* Define if the compiler/host combination has __builtin_abs. */


/* Define if the compiler/host combination has __builtin_cos. */


/* Define if the compiler/host combination has __builtin_cosf. */


/* Define if the compiler/host combination has __builtin_cosl. */


/* Define if the compiler/host combination has __builtin_fabs. */


/* Define if the compiler/host combination has __builtin_fabsf. */


/* Define if the compiler/host combination has __builtin_fabsl. */


/* Define if the compiler/host combination has __builtin_labs. */


/* Define if the compiler/host combination has __builtin_sin. */


/* Define if the compiler/host combination has __builtin_sinf. */


/* Define if the compiler/host combination has __builtin_sinl. */


/* Define if the compiler/host combination has __builtin_sqrt. */


/* Define if the compiler/host combination has __builtin_sqrtf. */


/* Define if the compiler/host combination has __builtin_sqrtl. */


/* Define to 1 if you have the `__signbit' function. */

	

/* #undef _GLIBCXX_HAVE___SIGNBIT */

/* Define to 1 if you have the `__signbitf' function. */


/* Define to 1 if you have the `__signbitl' function. */


/* Name of package */
/* #undef _GLIBCXX_PACKAGE */

/* Define to the address where bug reports for this package should be sent. */


/* Define to the full name of this package. */


/* Define to the full name and version of this package. */


/* Define to the one symbol short name of this package. */


/* Define to the version of this package. */


/* Define to 1 if you have the ANSI C header files. */


/* Version number of package */
/* #undef _GLIBCXX_VERSION */

/* Define if builtin atomic operations are supported on this host. */


/* Define to use concept checking code from the boost libraries. */
/* #undef _GLIBCXX_CONCEPT_CHECKS */

/* Define if a fully dynamic basic_string is wanted. */
/* #undef _GLIBCXX_FULLY_DYNAMIC_STRING */

/* Define to 1 if a full hosted library is built, or 0 if freestanding. */


/* Define if compatibility should be provided for -mlong-double-64. */

/* Define if ptrdiff_t is int. */

	


/* Define if using setrlimit to set resource limits during "make check" */


/* Define if size_t is unsigned int. */
/* #undef _GLIBCXX_SIZE_T_IS_UINT */

/* Define if the compiler is configured for setjmp/longjmp exceptions. */
/* #undef _GLIBCXX_SJLJ_EXCEPTIONS */

/* Define to use symbol versioning in the shared library. */


/* Define to use darwin versioning in the shared library. */


/* Define to use GNU versioning in the shared library. */
/* #undef _GLIBCXX_SYMVER_GNU */

/* Define to use GNU namespace versioning in the shared library. */
/* #undef _GLIBCXX_SYMVER_GNU_NAMESPACE */

/* Define if C99 functions or macros from <wchar.h>, <math.h>, <complex.h>,
   <stdio.h>, and <stdlib.h> can be used or exposed. */


/* Define if C99 functions in <complex.h> should be used in <complex>. Using
   compiler builtins for these functions requires corresponding C99 library
   functions to be present. */


/* Define if C99 functions in <complex.h> should be used in <tr1/complex>.
   Using compiler builtins for these functions requires corresponding C99
   library functions to be present. */


/* Define if C99 functions in <ctype.h> should be imported in <tr1/cctype> in
   namespace std::tr1. */


/* Define if C99 functions in <fenv.h> should be imported in <tr1/cfenv> in
   namespace std::tr1. */


/* Define if C99 functions in <inttypes.h> should be imported in
   <tr1/cinttypes> in namespace std::tr1. */


/* Define if C99 functions or macros in <math.h> should be imported in <cmath>
   in namespace std. */


/* Define if C99 functions or macros in <math.h> should be imported in
   <tr1/cmath> in namespace std::tr1. */


/* Define if C99 types in <stdint.h> should be imported in <tr1/cstdint> in
   namespace std::tr1. */


/* Define if iconv and related functions exist and are usable. */


/* Define if LFS support is available. */
/* #undef _GLIBCXX_USE_LFS */

/* Define if code specialized for long long should be used. */


/* Define if NLS translations are to be used. */
/* #undef _GLIBCXX_USE_NLS */

/* Define if dev/random and dev/urandom are available for the random_device of
   TR1 (Chapter 5.1). */

	


/* Define if code specialized for wchar_t should be used. */














































































































































































































































































































































#line 15 "/usr/include/c++/4.2.1/iostream" 2

#line 1 "/usr/include/c++/4.2.1/ostream" 1
// Output streams -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005,
// 2006, 2007
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file ostream
 *  This is a Standard C++ Library header.
 */

//
// ISO C++ 14882: 27.6.2  Output streams
//







#line 1 "/usr/include/c++/4.2.1/ios" 1
// Iostreams base classes -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2001, 2002 Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file ios
 *  This is a Standard C++ Library header.
 */

//
// ISO C++ 14882: 27.4  Iostreams base classes
//







#line 1 "/usr/include/c++/4.2.1/iosfwd" 1
// Forwarding declarations -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2001, 2002, 2003, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file iosfwd
 *  This is a Standard C++ Library header.
 */

//
// ISO C++ 14882: 27.2  Forward declarations
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 15 "/usr/include/c++/4.2.1/iosfwd" 2

#line 1 "/usr/include/c++/4.2.1/bits/c++locale.h" 1
// Wrapper for underlying C-language localization -*- C++ -*-

// Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file c++locale.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */

//
// ISO C++ 14882: 22.8  Standard locale categories.
//

// Written by Benjamin Kosnik <bkoz@redhat.com>







#line 1 "/usr/include/c++/4.2.1/clocale" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file clocale
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c locale.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 18.2.2  Implementation properties: C library
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/clocale" 2

#line 1 "/usr/include/locale.h" 1
/*
 * Copyright (c) 1991, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)locale.h	8.1 (Berkeley) 6/2/93
 * $FreeBSD: /repoman/r/ncvs/src/include/locale.h,v 1.7 2002/10/09 09:19:27 tjr Exp $
 */





#line 1 "/usr/include/_locale.h" 1
/*
 * Copyright (c) 1991, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)locale.h	8.1 (Berkeley) 6/2/93
 * $FreeBSD: /repoman/r/ncvs/src/include/locale.h,v 1.7 2002/10/09 09:19:27 tjr Exp $
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 40 "/usr/include/_locale.h" 2

#line 1 "/usr/include/_types.h" 1

#line 41 "/usr/include/_locale.h" 2

struct lconv {
	char	*decimal_point;
	char	*thousands_sep;
	char	*grouping;
	char	*int_curr_symbol;
	char	*currency_symbol;
	char	*mon_decimal_point;
	char	*mon_thousands_sep;
	char	*mon_grouping;
	char	*positive_sign;
	char	*negative_sign;
	char	int_frac_digits;
	char	frac_digits;
	char	p_cs_precedes;
	char	p_sep_by_space;
	char	n_cs_precedes;
	char	n_sep_by_space;
	char	p_sign_posn;
	char	n_sign_posn;
	char	int_p_cs_precedes;
	char	int_n_cs_precedes;
	char	int_p_sep_by_space;
	char	int_n_sep_by_space;
	char	int_p_sign_posn;
	char	int_n_sign_posn;
};





extern "C" {
struct lconv	*localeconv(void);
}



#line 40 "/usr/include/locale.h" 2











extern "C" {
char		*setlocale(int, const char *);
}



#line 22 "/usr/include/c++/4.2.1/clocale" 2

// Get rid of those macros defined in <locale.h> in lieu of real functions.



namespace std  {

  using ::lconv;
  using ::setlocale;
  using ::localeconv;

}



#line 17 "/usr/include/c++/4.2.1/bits/c++locale.h" 2

#line 1 "/usr/include/c++/4.2.1/cstring" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file cstring
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c string.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 20.4.6  C library
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cstring" 2

#line 1 "/usr/include/c++/4.2.1/cstddef" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file cstddef
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c stddef.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 18.1  Types
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cstddef" 2

#line 1 "/usr/include/stddef.h" 1
/*
 * Copyright (c) 2000 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*	$OpenBSD: stddef.h,v 1.2 1997/09/21 10:45:52 niklas Exp $	*/
/*	$NetBSD: stddef.h,v 1.4 1994/10/26 00:56:26 cgd Exp $	*/

/*-
 * Copyright (c) 1990 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)stddef.h	5.5 (Berkeley) 4/3/91
 */










#line 1 "/usr/include/_types.h" 1

#line 70 "/usr/include/stddef.h" 2




typedef	__darwin_ptrdiff_t	ptrdiff_t;

























typedef	__darwin_wint_t		wint_t;































#line 22 "/usr/include/c++/4.2.1/cstddef" 2

namespace std  {

  using ::ptrdiff_t;
  using ::size_t;

}



#line 22 "/usr/include/c++/4.2.1/cstring" 2

#line 1 "/usr/include/string.h" 1
/*
 * Copyright (c) 2000, 2007, 2010 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 *
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 *
 * @APPLE_LICENSE_HEADER_END@
 */
/*-
 * Copyright (c) 1990, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)string.h	8.1 (Berkeley) 6/2/93
 */





#line 1 "/usr/include/_types.h" 1

#line 61 "/usr/include/string.h" 2


#line 1 "/usr/include/sys/cdefs.h" 1

#line 63 "/usr/include/string.h" 2

#line 1 "/usr/include/Availability.h" 1

#line 64 "/usr/include/string.h" 2












/* ANSI-C */

extern "C" {
void	*memchr(const void *, int, size_t);
int	 memcmp(const void *, const void *, size_t);
void	*memcpy(void *, const void *, size_t);
void	*memmove(void *, const void *, size_t);
void	*memset(void *, int, size_t);
char	*strcat(char *, const char *);
char	*strchr(const char *, int);
int	 strcmp(const char *, const char *);
int	 strcoll(const char *, const char *);
char	*strcpy(char *, const char *);
size_t	 strcspn(const char *, const char *);
char	*strerror(int) __asm("_" "strerror" "$UNIX2003");
size_t	 strlen(const char *);
char	*strncat(char *, const char *, size_t);
int	 strncmp(const char *, const char *, size_t);
char	*strncpy(char *, const char *, size_t);
char	*strpbrk(const char *, const char *);
char	*strrchr(const char *, int);
size_t	 strspn(const char *, const char *);
char	*strstr(const char *, const char *);
char	*strtok(char *, const char *);
size_t	 strxfrm(char *, const char *, size_t);
}



/* Additional functionality provided by:
 * POSIX.1c-1995,
 * POSIX.1i-1995,
 * and the omnibus ISO/IEC 9945-1: 1996
 */


extern "C" {
char	*strtok_r(char *, const char *, char **);
}




/* Additional functionality provided by:
 * POSIX.1-2001
 */


extern "C" {
int	 strerror_r(int, char *, size_t);
char	*strdup(const char *);
void	*memccpy(void *, const void *, int, size_t);
}




/* Additional functionality provided by:
 * POSIX.1-2008
 */


extern "C" {
char	*stpcpy(char *, const char *);
char    *stpncpy(char *, const char *, size_t) ;
char	*strndup(const char *, size_t) ;
size_t   strnlen(const char *, size_t) ;
char	*strsignal(int sig);
}




/* Darwin extensions */







extern "C" {
void	*memmem(const void *, size_t, const void *, size_t) ;
void     memset_pattern4(void *, const void *, size_t) ;
void     memset_pattern8(void *, const void *, size_t) ;
void     memset_pattern16(void *, const void *, size_t) ;

char	*strcasestr(const char *, const char *);
char	*strnstr(const char *, const char *, size_t);
size_t	 strlcat(char *, const char *, size_t);
size_t	 strlcpy(char *, const char *, size_t);
void	 strmode(int, char *);
char	*strsep(char **, const char *);

/* SUS places swab() in unistd.h.  It is listed here for source compatibility */
void	 swab(const void * , void * , ssize_t);
}

/* Some functions historically defined in string.h were placed in strings.h
 * by SUS.  We are using "strings.h" instead of <strings.h> to avoid an issue
 * where /Developer/Headers/FlatCarbon/Strings.h could be included instead on
 * case-insensitive file systems.
 */

#line 1 "/usr/include/strings.h" 1
/*
 * Copyright (c) 2000, 2007, 2010 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*-
 * Copyright (c) 1990, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)strings.h	8.1 (Berkeley) 6/2/93
 */





#line 1 "/usr/include/_types.h" 1

#line 61 "/usr/include/strings.h" 2


#line 1 "/usr/include/sys/cdefs.h" 1

#line 63 "/usr/include/strings.h" 2

#line 1 "/usr/include/Availability.h" 1

#line 64 "/usr/include/strings.h" 2






extern "C" {
/* Removed in Issue 7 */

int	 bcmp(const void *, const void *, size_t) ;
void	 bcopy(const void *, void *, size_t) ;
void	 bzero(void *, size_t) ;
char	*index(const char *, int) ;
char	*rindex(const char *, int) ;


int	 ffs(int);
int	 strcasecmp(const char *, const char *);
int	 strncasecmp(const char *, const char *, size_t);
}

/* Darwin extensions */

extern "C" {
int	 ffsl(long) ;
int	 fls(int) ;
int	 flsl(long) ;
}


#line 1 "/usr/include/string.h" 1

#line 84 "/usr/include/strings.h" 2





#line 158 "/usr/include/string.h" 2














#line 23 "/usr/include/c++/4.2.1/cstring" 2

// Get rid of those macros defined in <string.h> in lieu of real functions.























namespace std  {

  using ::memcpy;
  using ::memmove;
  using ::strcpy;
  using ::strncpy;
  using ::strcat;
  using ::strncat;
  using ::memcmp;
  using ::strcmp;
  using ::strcoll;
  using ::strncmp;
  using ::strxfrm;
  using ::strcspn;
  using ::strspn;
  using ::strtok;
  using ::memset;
  using ::strerror;
  using ::strlen;

  using ::memchr;

  inline void*
  memchr(void* __p, int __c, size_t __n)
  { return memchr(const_cast<const void*>(__p), __c, __n); }

  using ::strchr;

  inline char*
  strchr(char* __s1, int __n)
  { return __builtin_strchr(const_cast<const char*>(__s1), __n); }

  using ::strpbrk;

  inline char*
  strpbrk(char* __s1, const char* __s2)
  { return __builtin_strpbrk(const_cast<const char*>(__s1), __s2); }

  using ::strrchr;

  inline char*
  strrchr(char* __s1, int __n)
  { return __builtin_strrchr(const_cast<const char*>(__s1), __n); }

  using ::strstr;

  inline char*
  strstr(char* __s1, const char* __s2)
  { return __builtin_strstr(const_cast<const char*>(__s1), __s2); }

}



#line 17 "/usr/include/c++/4.2.1/bits/c++locale.h" 2

#line 1 "/usr/include/c++/4.2.1/cstdio" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/cstdio
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c stdio.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 27.8.2  C Library files
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cstdio" 2

#line 1 "/usr/include/c++/4.2.1/cstddef" 1

#line 22 "/usr/include/c++/4.2.1/cstdio" 2


#line 1 "/usr/include/stdio.h" 1
/*
 * Copyright (c) 2000, 2005, 2007, 2009, 2010 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 *
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 *
 * @APPLE_LICENSE_HEADER_END@
 */
/*-
 * Copyright (c) 1990, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * This code is derived from software contributed to Berkeley by
 * Chris Torek.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)stdio.h	8.5 (Berkeley) 4/29/95
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 64 "/usr/include/stdio.h" 2

#line 1 "/usr/include/Availability.h" 1

#line 65 "/usr/include/stdio.h" 2


#line 1 "/usr/include/_types.h" 1

#line 67 "/usr/include/stdio.h" 2



/* DO NOT REMOVE THIS COMMENT: fixincludes needs to see:
 * __gnuc_va_list and include <stdarg.h> */
typedef __darwin_va_list	va_list;











typedef __darwin_off_t		fpos_t;



/*
 * NB: to fit things in six character monocase externals, the stdio
 * code uses the prefix `__s' for stdio objects, typically followed
 * by a three-character attempt at a mnemonic.
 */

/* stdio buffers */
struct __sbuf {
	unsigned char	*_base;
	int		_size;
};

/* hold a buncha junk that would grow the ABI */
struct __sFILEX;

/*
 * stdio state variables.
 *
 * The following always hold:
 *
 *	if (_flags&(__SLBF|__SWR)) == (__SLBF|__SWR),
 *		_lbfsize is -_bf._size, else _lbfsize is 0
 *	if _flags&__SRD, _w is 0
 *	if _flags&__SWR, _r is 0
 *
 * This ensures that the getc and putc macros (or inline functions) never
 * try to write or read from a file that is in `read' or `write' mode.
 * (Moreover, they can, and do, automatically switch from read mode to
 * write mode, and back, on "r+" and "w+" files.)
 *
 * _lbfsize is used only to make the inline line-buffered output stream
 * code as compact as possible.
 *
 * _ub, _up, and _ur are used when ungetc() pushes back more characters
 * than fit in the current _bf, or when ungetc() pushes back a character
 * that does not match the previous one in _bf.  When this happens,
 * _ub._base becomes non-nil (i.e., a stream has ungetc() data iff
 * _ub._base!=NULL) and _up and _ur save the current values of _p and _r.
 *
 * NB: see WARNING above before changing the layout of this structure!
 */
typedef	struct __sFILE {
	unsigned char *_p;	/* current position in (some) buffer */
	int	_r;		/* read space left for getc() */
	int	_w;		/* write space left for putc() */
	short	_flags;		/* flags, below; this FILE is free if 0 */
	short	_file;		/* fileno, if Unix descriptor, else -1 */
	struct	__sbuf _bf;	/* the buffer (at least 1 byte, if !NULL) */
	int	_lbfsize;	/* 0 or -_bf._size, for inline putc */

	/* operations */
	void	*_cookie;	/* cookie passed to io functions */
	int	(*_close)(void *);
	int	(*_read) (void *, char *, int);
	fpos_t	(*_seek) (void *, fpos_t, int);
	int	(*_write)(void *, const char *, int);

	/* separate buffer for long sequences of ungetc() */
	struct	__sbuf _ub;	/* ungetc buffer */
	struct __sFILEX *_extra; /* additions to FILE to not break ABI */
	int	_ur;		/* saved _r when _r is counting ungetc data */

	/* tricks to meet minimum requirements even when malloc() fails */
	unsigned char _ubuf[3];	/* guarantee an ungetc() buffer */
	unsigned char _nbuf[1];	/* guarantee a getc() buffer */

	/* separate buffer for fgetln() when line crosses buffer boundary */
	struct	__sbuf _lb;	/* buffer for fgetln() */

	/* Unix stdio files get aligned to block boundaries on fseek() */
	int	_blksize;	/* stat.st_blksize (may be != _bf._size) */
	fpos_t	_offset;	/* current lseek offset (see WARNING) */
} FILE;

extern "C" {
extern FILE *__stdinp;
extern FILE *__stdoutp;
extern FILE *__stderrp;
}





	/* RD and WR are never simultaneously asserted */













/*
 * The following three definitions are for ANSI C, which took them
 * from System V, which brilliantly took internal interface macros and
 * made them official arguments to setvbuf(), without renaming them.
 * Hence, these ugly _IOxxx names are *supposed* to appear in user code.
 *
 * Although numbered as their counterparts above, the implementation
 * does not rely on this.
 */







				/* must be == _POSIX_STREAM_MAX <limits.h> */



/* System V/ANSI C; this is the wrong way to do this, do *not* use these. */




























/* ANSI-C */

extern "C" {
void	 clearerr(FILE *);
int	 fclose(FILE *);
int	 feof(FILE *);
int	 ferror(FILE *);
int	 fflush(FILE *);
int	 fgetc(FILE *);
int	 fgetpos(FILE * , fpos_t *);
char	*fgets(char * , int, FILE *);



FILE	*fopen(const char * , const char * ) ;

int	 fprintf(FILE * , const char * , ...)  ;
int	 fputc(int, FILE *);
int	 fputs(const char * , FILE * ) __asm("_" "fputs" "$UNIX2003");
size_t	 fread(void * , size_t, size_t, FILE * );
FILE	*freopen(const char * , const char * ,
                 FILE * ) __asm("_" "freopen" "$UNIX2003");
int	 fscanf(FILE * , const char * , ...)  ;
int	 fseek(FILE *, long, int);
int	 fsetpos(FILE *, const fpos_t *);
long	 ftell(FILE *);
size_t	 fwrite(const void * , size_t, size_t, FILE * ) __asm("_" "fwrite" "$UNIX2003");
int	 getc(FILE *);
int	 getchar(void);
char	*gets(char *);
void	 perror(const char *);
int	 printf(const char * , ...)  ;
int	 putc(int, FILE *);
int	 putchar(int);
int	 puts(const char *);
int	 remove(const char *);
int	 rename (const char *, const char *);
void	 rewind(FILE *);
int	 scanf(const char * , ...)  ;
void	 setbuf(FILE * , char * );
int	 setvbuf(FILE * , char * , int, size_t);
int	 sprintf(char * , const char * , ...)  ;
int	 sscanf(const char * , const char * , ...)  ;
FILE	*tmpfile(void);
char	*tmpnam(char *);
int	 ungetc(int, FILE *);
int	 vfprintf(FILE * , const char * , va_list)  ;
int	 vprintf(const char * , va_list)  ;
int	 vsprintf(char * , const char * , va_list)  ;
}



/* Additional functionality provided by:
 * POSIX.1-1988
 */




extern "C" {









FILE	*fdopen(int, const char *) ;

int	 fileno(FILE *);
}



/* Additional functionality provided by:
 * POSIX.2-1992 C Language Binding Option
 */


extern "C" {
int	 pclose(FILE *);



FILE	*popen(const char *, const char *) ;

}





/* Additional functionality provided by:
 * POSIX.1c-1995,
 * POSIX.1i-1995,
 * and the omnibus ISO/IEC 9945-1: 1996
 */



/* Functions internal to the implementation. */
extern "C" {
int	__srget(FILE *);
int	__svfscanf(FILE *, const char *, va_list)  ;
int	__swbuf(int, FILE *);
}

/*
 * The __sfoo macros are here so that we can
 * define function versions in the C library.
 */


static inline int __sputc(int _c, FILE *_p) {
	if (--_p->_w >= 0 || (_p->_w >= _p->_lbfsize && (char)_c != '\n'))
		return (*_p->_p++ = _c);
	else
		return (__swbuf(_c, _p));
}



















extern "C" {
void	 flockfile(FILE *);
int	 ftrylockfile(FILE *);
void	 funlockfile(FILE *);
int	 getc_unlocked(FILE *);
int	 getchar_unlocked(void);
int	 putc_unlocked(int, FILE *);
int	 putchar_unlocked(int);

/* Removed in Issue 6 */

int	 getw(FILE *);
int	 putw(int, FILE *);


char	*tempnam(const char *, const char *) __asm("_" "tempnam" "$UNIX2003");
}












/* Additional functionality provided by:
 * POSIX.1-2001
 * ISO C99
 */







extern "C" {
int	 fseeko(FILE *, off_t, int);
off_t	 ftello(FILE *);
}



extern "C" {
int	 snprintf(char * , size_t, const char * , ...)  ;
int	 vfscanf(FILE * , const char * , va_list)  ;
int	 vscanf(const char * , va_list)  ;
int	 vsnprintf(char * , size_t, const char * , va_list)  ;
int	 vsscanf(const char * , const char * , va_list)  ;
}




/* Additional functionality provided by:
 * POSIX.1-2008
 */







extern "C" {
int	dprintf(int, const char * , ...)   ;
int	vdprintf(int, const char * , va_list)   ;
ssize_t getdelim(char ** , size_t * , int, FILE * ) ;
ssize_t getline(char ** , size_t * , FILE * ) ;
}




/* Darwin extensions */


extern "C" {
extern const int sys_nerr;		/* perror(3) external variables */
extern const char *const sys_errlist[];

int	 asprintf(char **, const char *, ...)  ;
char	*ctermid_r(char *);
char	*fgetln(FILE *, size_t *);
const char *fmtcheck(const char *, const char *);
int	 fpurge(FILE *);
void	 setbuffer(FILE *, char *, int);
int	 setlinebuf(FILE *);
int	 vasprintf(char **, const char *, va_list)  ;
FILE	*zopen(const char *, const char *, int);


/*
 * Stdio function-access interface.
 */
FILE	*funopen(const void *,
                 int (*)(void *, char *, int),
                 int (*)(void *, const char *, int),
                 fpos_t (*)(void *, fpos_t, int),
                 int (*)(void *));
}






















#line 24 "/usr/include/c++/4.2.1/cstdio" 2

// Get rid of those macros defined in <stdio.h> in lieu of real functions.










































namespace std  {

  using ::FILE;
  using ::fpos_t;

  using ::clearerr;
  using ::fclose;
  using ::feof;
  using ::ferror;
  using ::fflush;
  using ::fgetc;
  using ::fgetpos;
  using ::fgets;
  using ::fopen;
  using ::fprintf;
  using ::fputc;
  using ::fputs;
  using ::fread;
  using ::freopen;
  using ::fscanf;
  using ::fseek;
  using ::fsetpos;
  using ::ftell;
  using ::fwrite;
  using ::getc;
  using ::getchar;
  using ::gets;
  using ::perror;
  using ::printf;
  using ::putc;
  using ::putchar;
  using ::puts;
  using ::remove;
  using ::rename;
  using ::rewind;
  using ::scanf;
  using ::setbuf;
  using ::setvbuf;
  using ::sprintf;
  using ::sscanf;
  using ::tmpfile;
  using ::tmpnam;
  using ::ungetc;
  using ::vfprintf;
  using ::vprintf;
  using ::vsprintf;

}









namespace __gnu_cxx  {


  
  
  
  
  
  
  
  
  



  using ::snprintf;
  using ::vfscanf;
  using ::vscanf;
  using ::vsnprintf;
  using ::vsscanf;


}

namespace std  {

  using ::__gnu_cxx::snprintf;
  using ::__gnu_cxx::vfscanf;
  using ::__gnu_cxx::vscanf;
  using ::__gnu_cxx::vsnprintf;
  using ::__gnu_cxx::vsscanf;

}





#line 17 "/usr/include/c++/4.2.1/bits/c++locale.h" 2

#line 1 "/usr/include/c++/4.2.1/cstdarg" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/cstdarg
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c stdarg.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 20.4.6  C library
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cstdarg" 2

#line 1 "/usr/include/c++/4.2.1/cstdarg" 1

// Adhere to section 17.4.1.2 clause 5 of ISO 14882:1998




namespace std  {

  using ::va_list;

}



#line 18 "/usr/include/c++/4.2.1/bits/c++locale.h" 2



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



#line 15 "/usr/include/c++/4.2.1/iosfwd" 2

#line 1 "/usr/include/c++/4.2.1/bits/c++io.h" 1
// Underlying io library details -*- C++ -*-

// Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file c++io.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */

// c_io_stdio.h - Defines for using "C" stdio.h





#line 1 "/usr/include/c++/4.2.1/cstdio" 1

#line 15 "/usr/include/c++/4.2.1/bits/c++io.h" 2

#line 1 "/usr/include/c++/4.2.1/cstddef" 1

#line 16 "/usr/include/c++/4.2.1/bits/c++io.h" 2

#line 1 "/usr/include/c++/4.2.1/bits/gthr.h" 1
/* Threads compatibility routines for libgcc2.  */
/* Compile this one with gcc.  */
/* Copyright (C) 1997, 1998, 2004 Free Software Foundation, Inc.

This file is part of GCC.

GCC is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2, or (at your option) any later
version.

GCC is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with GCC; see the file COPYING.  If not, write to the Free
Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.  */

/* As a special exception, if you link this library with other files,
   some of which are compiled with GCC, to produce an executable,
   this library does not by itself cause the resulting executable
   to be covered by the GNU General Public License.
   This exception does not however invalidate any other reasons why
   the executable file might be covered by the GNU General Public License.  */








/* If this file is compiled with threads support, it must
       #define __GTHREADS 1
   to indicate that threads support is present.  Also it has define
   function
     int __gthread_active_p ()
   that returns 1 if thread system is active, 0 if not.

   The threads interface must define the following types:
     __gthread_key_t
     __gthread_once_t
     __gthread_mutex_t
     __gthread_recursive_mutex_t

   The threads interface must define the following macros:

     __GTHREAD_ONCE_INIT
     		to initialize __gthread_once_t
     __GTHREAD_MUTEX_INIT
     		to initialize __gthread_mutex_t to get a fast
		non-recursive mutex.
     __GTHREAD_MUTEX_INIT_FUNCTION
     		some systems can't initialize a mutex without a
		function call.  On such systems, define this to a
		function which looks like this:
		  void __GTHREAD_MUTEX_INIT_FUNCTION (__gthread_mutex_t *)
		Don't define __GTHREAD_MUTEX_INIT in this case
     __GTHREAD_RECURSIVE_MUTEX_INIT
     __GTHREAD_RECURSIVE_MUTEX_INIT_FUNCTION
     		as above, but for a recursive mutex.

   The threads interface must define the following static functions:

     int __gthread_once (__gthread_once_t *once, void (*func) ())

     int __gthread_key_create (__gthread_key_t *keyp, void (*dtor) (void *))
     int __gthread_key_delete (__gthread_key_t key)

     void *__gthread_getspecific (__gthread_key_t key)
     int __gthread_setspecific (__gthread_key_t key, const void *ptr)

     int __gthread_mutex_lock (__gthread_mutex_t *mutex);
     int __gthread_mutex_trylock (__gthread_mutex_t *mutex);
     int __gthread_mutex_unlock (__gthread_mutex_t *mutex);

     int __gthread_recursive_mutex_lock (__gthread_recursive_mutex_t *mutex);
     int __gthread_recursive_mutex_trylock (__gthread_recursive_mutex_t *mutex);
     int __gthread_recursive_mutex_unlock (__gthread_recursive_mutex_t *mutex);

   All functions returning int should return zero on success or the error
   number.  If the operation is not supported, -1 is returned.

   Currently supported threads packages are
     TPF threads with -D__tpf__
     POSIX/Unix98 threads with -D_PTHREADS
     POSIX/Unix95 threads with -D_PTHREADS95
     DCE threads with -D_DCE_THREADS
     Solaris/UI threads with -D_SOLARIS_THREADS
*/

/* Check first for thread specific defines.  */



















#line 1 "/usr/include/c++/4.2.1/bits/gthr-default.h" 1
/* Threads compatibility routines for libgcc2 and libobjc.  */
/* Compile this one with gcc.  */
/* Copyright (C) 1997, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007
   Free Software Foundation, Inc.

This file is part of GCC.

GCC is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2, or (at your option) any later
version.

GCC is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with GCC; see the file COPYING.  If not, write to the Free
Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.  */

/* As a special exception, if you link this library with other files,
   some of which are compiled with GCC, to produce an executable,
   this library does not by itself cause the resulting executable
   to be covered by the GNU General Public License.
   This exception does not however invalidate any other reasons why
   the executable file might be covered by the GNU General Public License.  */




/* POSIX threads specific definitions.
   Easy, since the interface is just one-to-one mapping.  */



/* Some implementations of <pthread.h> require this to be defined.  */





#line 1 "/usr/include/pthread.h" 1
/*
 * Copyright (c) 2000-2003 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 *
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 *
 * @APPLE_LICENSE_HEADER_END@
 */
/*
 * Copyright 1996 1995 by Open Software Foundation, Inc. 1997 1996 1995 1994 1993 1992 1991
 *              All Rights Reserved
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appears in all copies and
 * that both the copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * OSF DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL OSF BE LIABLE FOR ANY SPECIAL, INDIRECT, OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN ACTION OF CONTRACT,
 * NEGLIGENCE, OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION
 * WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */
/*
 * MkLinux
 */

/*
 * POSIX Threads - IEEE 1003.1c
 */





#line 1 "/usr/include/_types.h" 1

#line 55 "/usr/include/pthread.h" 2


#line 1 "/usr/include/pthread_impl.h" 1
/*
 * Copyright (c) 2000-2003 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */



/*
 * Internal implementation details
 */

/* This whole header file will disappear, so don't depend on it... */



/*
 * [Internal] data structure signatures
 */










/*
 * POSIX scheduling policies
 */










#line 56 "/usr/include/pthread.h" 2


#line 1 "/usr/include/sched.h" 1
/*
 * Copyright (c) 2000-2003 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */





#line 1 "/usr/include/pthread_impl.h" 1

#line 27 "/usr/include/sched.h" 2

#line 1 "/usr/include/sys/cdefs.h" 1

#line 28 "/usr/include/sched.h" 2

extern "C" {
/*
 * Scheduling paramters
 */

struct sched_param { int sched_priority;  char __opaque[4]; };


extern int sched_yield(void);
extern int sched_get_priority_min(int);
extern int sched_get_priority_max(int);
}




#line 57 "/usr/include/pthread.h" 2

#line 1 "/usr/include/time.h" 1
/*
 * Copyright (c) 2000 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*
 * Copyright (c) 1989, 1993
 *	The Regents of the University of California.  All rights reserved.
 * (c) UNIX System Laboratories, Inc.
 * All or some portions of this file are derived from material licensed
 * to the University of California by American Telephone and Telegraph
 * Co. or Unix System Laboratories, Inc. and are reproduced herein with
 * the permission of UNIX System Laboratories, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)time.h	8.3 (Berkeley) 1/21/94
 */





#line 1 "/usr/include/_types.h" 1

#line 66 "/usr/include/time.h" 2



#line 1 "/usr/include/_structs.h" 1
/*
 * Copyright (c) 2004 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */


#line 1 "/usr/include/sys/_structs.h" 1

#line 26 "/usr/include/_structs.h" 2


#line 68 "/usr/include/time.h" 2







typedef	__darwin_clock_t	clock_t;












struct tm {
	int	tm_sec;		/* seconds after the minute [0-60] */
	int	tm_min;		/* minutes after the hour [0-59] */
	int	tm_hour;	/* hours since midnight [0-23] */
	int	tm_mday;	/* day of the month [1-31] */
	int	tm_mon;		/* months since January [0-11] */
	int	tm_year;	/* years since 1900 */
	int	tm_wday;	/* days since Sunday [0-6] */
	int	tm_yday;	/* days since January 1 [0-365] */
	int	tm_isdst;	/* Daylight Savings Time flag */
	long	tm_gmtoff;	/* offset from CUT in seconds */
	char	*tm_zone;	/* timezone abbreviation */
};










extern char *tzname[];


extern int getdate_err;

extern long timezone __asm("_" "timezone" "$UNIX2003");

extern int daylight;

extern "C" {
char *asctime(const struct tm *);
clock_t clock(void) __asm("_" "clock" "$UNIX2003");
char *ctime(const time_t *);
double difftime(time_t, time_t);
struct tm *getdate(const char *);
struct tm *gmtime(const time_t *);
struct tm *localtime(const time_t *);
time_t mktime(struct tm *) __asm("_" "mktime" "$UNIX2003");
size_t strftime(char * , size_t, const char * , const struct tm * ) __asm("_" "strftime" "$UNIX2003");
char *strptime(const char * , const char * , struct tm * ) __asm("_" "strptime" "$UNIX2003");
time_t time(time_t *);


void tzset(void);


/* [TSF] Thread safe functions */
char *asctime_r(const struct tm * , char * );
char *ctime_r(const time_t *, char *);
struct tm *gmtime_r(const time_t * , struct tm * );
struct tm *localtime_r(const time_t * , struct tm * );


time_t posix2time(time_t);



void tzsetwall(void);
time_t time2posix(time_t);
time_t timelocal(struct tm * const);
time_t timegm(struct tm * const);



int nanosleep(const struct timespec *, struct timespec *) __asm("_" "nanosleep"  "$UNIX2003");

}







#line 58 "/usr/include/pthread.h" 2



typedef __darwin_pthread_attr_t		pthread_attr_t;




typedef __darwin_pthread_cond_t		pthread_cond_t;




typedef __darwin_pthread_condattr_t	pthread_condattr_t;




typedef __darwin_pthread_key_t		pthread_key_t;




typedef __darwin_pthread_mutex_t	pthread_mutex_t;




typedef __darwin_pthread_mutexattr_t	pthread_mutexattr_t;




typedef __darwin_pthread_once_t		pthread_once_t;




typedef __darwin_pthread_rwlock_t	pthread_rwlock_t;




typedef __darwin_pthread_rwlockattr_t	pthread_rwlockattr_t;




typedef __darwin_pthread_t		pthread_t;






typedef __darwin_mach_port_t		mach_port_t;









/*
 * These symbols indicate which [optional] features are available
 * They can be tested at compile time via '#ifdef XXX'
 * The way to check for pthreads is like so:

 * #include <unistd.h>
 * #ifdef _POSIX_THREADS
 * #include <pthread.h>
 * #endif

 */

/* These will be moved to unistd.h */

/*
 * Note: These data structures are meant to be opaque.  Only enough
 * structure is exposed to support initializers.
 * All of the typedefs will be moved to <sys/types.h>
 */


#line 1 "/usr/include/sys/cdefs.h" 1

#line 108 "/usr/include/pthread.h" 2

#line 1 "/usr/include/Availability.h" 1

#line 109 "/usr/include/pthread.h" 2

extern "C" {
/*
 * Threads
 */


/*
 * Cancel cleanup handler management.  Note, since these are implemented as macros,
 * they *MUST* occur in matched pairs!
 */
















/*
 * Thread attributes
 */












/* Value returned from pthread_join() when a thread is canceled */


/* We only support PTHREAD_SCOPE_SYSTEM */



/* We only support PTHREAD_PROCESS_PRIVATE */



/*
 * Mutex protocol attributes
 */




/*
 * Mutex type attributes
 */




/*
 * RWLock variables
 */


/*
 * Mutex variables
 */








/*
 * Condition variable attributes
 */

/*
 * Condition variables
 */



/*
 * Initialization control (once) variables
 */



/*
 * Prototypes for all PTHREAD interfaces
 */
int       pthread_atfork(void (*)(void), void (*)(void),
                      void (*)(void));
int       pthread_attr_destroy(pthread_attr_t *);
int       pthread_attr_getdetachstate(const pthread_attr_t *,
				      int *);
int       pthread_attr_getguardsize(const pthread_attr_t * ,
                                      size_t * );
int       pthread_attr_getinheritsched(const pthread_attr_t * ,
				       int * );
int       pthread_attr_getschedparam(const pthread_attr_t * ,
                                     struct sched_param * );
int       pthread_attr_getschedpolicy(const pthread_attr_t * ,
				      int * );
int       pthread_attr_getscope(const pthread_attr_t * , int * );
int       pthread_attr_getstack(const pthread_attr_t * ,
                                      void ** , size_t * );
int       pthread_attr_getstackaddr(const pthread_attr_t * ,
                                      void ** );
int       pthread_attr_getstacksize(const pthread_attr_t * ,
                                      size_t * );
int       pthread_attr_init(pthread_attr_t *);
int       pthread_attr_setdetachstate(pthread_attr_t *,
				      int );
int       pthread_attr_setguardsize(pthread_attr_t *, size_t );
int       pthread_attr_setinheritsched(pthread_attr_t *,
				       int );
int       pthread_attr_setschedparam(pthread_attr_t * ,
                                     const struct sched_param * );
int       pthread_attr_setschedpolicy(pthread_attr_t *,
				      int );
int       pthread_attr_setscope(pthread_attr_t *, int);
int       pthread_attr_setstack(pthread_attr_t *,
                                      void *, size_t );
int       pthread_attr_setstackaddr(pthread_attr_t *,
                                      void *);
int       pthread_attr_setstacksize(pthread_attr_t *, size_t );
int       pthread_cancel(pthread_t ) __asm("_" "pthread_cancel" "$UNIX2003");

int       pthread_cond_broadcast(pthread_cond_t *);
int       pthread_cond_destroy(pthread_cond_t *);
int       pthread_cond_init(pthread_cond_t * ,
                            const pthread_condattr_t * ) __asm("_" "pthread_cond_init" "$UNIX2003");
int       pthread_cond_signal(pthread_cond_t *);
int       pthread_cond_timedwait(pthread_cond_t * ,
				 pthread_mutex_t * ,
				 const struct timespec * ) __asm("_" "pthread_cond_timedwait"  "$UNIX2003");
int       pthread_cond_wait(pthread_cond_t * ,
			    pthread_mutex_t * ) __asm("_" "pthread_cond_wait"  "$UNIX2003");
int       pthread_condattr_destroy(pthread_condattr_t *);
int       pthread_condattr_init(pthread_condattr_t *);
int       pthread_condattr_getpshared(const pthread_condattr_t * ,
			int * );
int       pthread_condattr_setpshared(pthread_condattr_t *,
			int );
int       pthread_create(pthread_t * ,
                         const pthread_attr_t * ,
                         void *(*)(void *),
                         void * );
int       pthread_detach(pthread_t );
int       pthread_equal(pthread_t ,
			pthread_t );
void      pthread_exit(void *) ;
int       pthread_getconcurrency(void);
int       pthread_getschedparam(pthread_t , int * , struct sched_param * );
void     *pthread_getspecific(pthread_key_t );
int       pthread_join(pthread_t , void **) __asm("_" "pthread_join"  "$UNIX2003");
int       pthread_key_create(pthread_key_t *, void (*)(void *));
int       pthread_key_delete(pthread_key_t );
int       pthread_mutex_destroy(pthread_mutex_t *);
int       pthread_mutex_getprioceiling(const pthread_mutex_t * , int * );
int       pthread_mutex_init(pthread_mutex_t * , const pthread_mutexattr_t * );
int       pthread_mutex_lock(pthread_mutex_t *);
int       pthread_mutex_setprioceiling(pthread_mutex_t * , int, int * );
int       pthread_mutex_trylock(pthread_mutex_t *);
int       pthread_mutex_unlock(pthread_mutex_t *);
int       pthread_mutexattr_destroy(pthread_mutexattr_t *) __asm("_" "pthread_mutexattr_destroy" "$UNIX2003");
int       pthread_mutexattr_getprioceiling(const pthread_mutexattr_t * , int * );
int       pthread_mutexattr_getprotocol(const pthread_mutexattr_t * , int * );
int       pthread_mutexattr_getpshared(const pthread_mutexattr_t * , int * );
int       pthread_mutexattr_gettype(const pthread_mutexattr_t * , int * );
int       pthread_mutexattr_init(pthread_mutexattr_t *);
int       pthread_mutexattr_setprioceiling(pthread_mutexattr_t *, int);
int       pthread_mutexattr_setprotocol(pthread_mutexattr_t *, int);
int       pthread_mutexattr_setpshared(pthread_mutexattr_t *, int );
int       pthread_mutexattr_settype(pthread_mutexattr_t *, int);
int       pthread_once(pthread_once_t *, void (*)(void));
int       pthread_rwlock_destroy(pthread_rwlock_t * ) __asm("_" "pthread_rwlock_destroy" "$UNIX2003");
int       pthread_rwlock_init(pthread_rwlock_t * , const pthread_rwlockattr_t * ) __asm("_" "pthread_rwlock_init" "$UNIX2003");
int       pthread_rwlock_rdlock(pthread_rwlock_t *) __asm("_" "pthread_rwlock_rdlock" "$UNIX2003");
int       pthread_rwlock_tryrdlock(pthread_rwlock_t *) __asm("_" "pthread_rwlock_tryrdlock" "$UNIX2003");
int       pthread_rwlock_trywrlock(pthread_rwlock_t *) __asm("_" "pthread_rwlock_trywrlock" "$UNIX2003");
int       pthread_rwlock_wrlock(pthread_rwlock_t *) __asm("_" "pthread_rwlock_wrlock" "$UNIX2003");
int       pthread_rwlock_unlock(pthread_rwlock_t *) __asm("_" "pthread_rwlock_unlock" "$UNIX2003");
int       pthread_rwlockattr_destroy(pthread_rwlockattr_t *);
int       pthread_rwlockattr_getpshared(const pthread_rwlockattr_t * ,
			int * );
int       pthread_rwlockattr_init(pthread_rwlockattr_t *);
int       pthread_rwlockattr_setpshared(pthread_rwlockattr_t *,
			int );
pthread_t pthread_self(void);

int       pthread_setcancelstate(int , int *) __asm("_" "pthread_setcancelstate" "$UNIX2003");
int       pthread_setcanceltype(int , int *) __asm("_" "pthread_setcanceltype" "$UNIX2003");
int       pthread_setconcurrency(int);
int       pthread_setschedparam(pthread_t ,
				int ,
                                const struct sched_param *);
int       pthread_setspecific(pthread_key_t ,
			      const void *);
void      pthread_testcancel(void) __asm("_" "pthread_testcancel" "$UNIX2003");


/* returns non-zero if pthread_create or cthread_fork have been called */
int		pthread_is_threaded_np(void);

int pthread_threadid_np(pthread_t,__uint64_t*) ;

int pthread_rwlock_longrdlock_np(pthread_rwlock_t *) ;
int pthread_rwlock_yieldwrlock_np(pthread_rwlock_t *) ;
int pthread_rwlock_downgrade_np(pthread_rwlock_t *);
int pthread_rwlock_upgrade_np(pthread_rwlock_t *);
int pthread_rwlock_tryupgrade_np(pthread_rwlock_t *);
int pthread_rwlock_held_np(pthread_rwlock_t *);
int pthread_rwlock_rdheld_np(pthread_rwlock_t *);
int pthread_rwlock_wrheld_np(pthread_rwlock_t *);

/*SPI to set and get pthread name*/
int		pthread_getname_np(pthread_t,char*,size_t) ;
int		pthread_setname_np(const char*) ;
/* returns non-zero if the current thread is the main thread */
int		pthread_main_np(void);

/* return the mach thread bound to the pthread */
mach_port_t 	pthread_mach_thread_np(pthread_t);
size_t	 	pthread_get_stacksize_np(pthread_t);
void *		pthread_get_stackaddr_np(pthread_t);

/* Like pthread_cond_signal(), but only wake up the specified pthread */
int		pthread_cond_signal_thread_np(pthread_cond_t *, pthread_t);

/* Like pthread_cond_timedwait, but use a relative timeout */
int		pthread_cond_timedwait_relative_np(pthread_cond_t *,
				 pthread_mutex_t *,
				 const struct timespec *);

/* Like pthread_create(), but leaves the thread suspended */
int       pthread_create_suspended_np(pthread_t *,
                         const pthread_attr_t *,
                         void *(*)(void *),
                         void *);
int       pthread_kill(pthread_t, int);

pthread_t pthread_from_mach_thread_np(mach_port_t) ;

int       pthread_sigmask(int, const sigset_t *, sigset_t *) __asm("_" "pthread_sigmask" "$UNIX2003");
void	  pthread_yield_np(void);

}


#line 40 "/usr/include/c++/4.2.1/bits/gthr-default.h" 2

#line 1 "/usr/include/unistd.h" 1

#line 41 "/usr/include/c++/4.2.1/bits/gthr-default.h" 2

typedef pthread_key_t __gthread_key_t;
typedef pthread_once_t __gthread_once_t;
typedef pthread_mutex_t __gthread_mutex_t;
typedef pthread_mutex_t __gthread_recursive_mutex_t;
























/* Typically, __gthrw_foo is a weak reference to symbol foo.  */


/* On Tru64, /usr/include/pthread.h uses #pragma extern_prefix "__" to
   map a subset of the POSIX pthread API to mangled versions of their
   names.  */




















































































  





  
  

  
  

  


  
    
      
	
	  

	  
	  
	  
	

      
      
	

      
    

  







  
    
  






static inline int
__gthread_active_p (void)
{
  return 1;
}

























  
    
      
      
	
	  


	  
	      
					      
	    
	
    

  






  
      
      
    

  








  
  

  
    

  
    
  
    

  






  
    
  
    


      
      
      
      

      
	
	  
	    

	  
	    

	  
	    
	  
	    
	  

	  




	  
	    
	


      
    








  
    
      
      

      
	
      
	
    
  


    






  
    






  
    
    

  
  






  
    
  
    






  
    
  
    
      
      
    






  
    
  
    








  
    
      

      
	
	  
	  
	  
	
    

  






  
    
      

      




      
	
	  
	  
	    
	
      

      
	

      
      
    
  






  
      
    
      
    

  






  
      
    
      
    

  






  
      
    
      
    

  








  
    
      

      
	
	  
	  
	  
	
    

  






  
    
      
	

      
      
    
  






  
    
			      
  
    






  
    
  
    






  
    
  
    




static inline int
__gthread_once (__gthread_once_t *once, void (*func) (void))
{
  if (__gthread_active_p ())
    return pthread_once (once, func);
  else
    return -1;
}

static inline int
__gthread_key_create (__gthread_key_t *key, void (*dtor) (void *))
{
  return pthread_key_create (key, dtor);
}

static inline int
__gthread_key_delete (__gthread_key_t key)
{
  return pthread_key_delete (key);
}

static inline void *
__gthread_getspecific (__gthread_key_t key)
{
  return pthread_getspecific (key);
}

static inline int
__gthread_setspecific (__gthread_key_t key, const void *ptr)
{
  return pthread_setspecific (key, ptr);
}

static inline int
__gthread_mutex_lock (__gthread_mutex_t *mutex)
{
  if (__gthread_active_p ())
    return pthread_mutex_lock (mutex);
  else
    return 0;
}

static inline int
__gthread_mutex_trylock (__gthread_mutex_t *mutex)
{
  if (__gthread_active_p ())
    return pthread_mutex_trylock (mutex);
  else
    return 0;
}

static inline int
__gthread_mutex_unlock (__gthread_mutex_t *mutex)
{
  if (__gthread_active_p ())
    return pthread_mutex_unlock (mutex);
  else
    return 0;
}


static inline int
__gthread_recursive_mutex_init_function (__gthread_recursive_mutex_t *mutex)
{
  if (__gthread_active_p ())
    {
      pthread_mutexattr_t attr;
      int r;

      r = pthread_mutexattr_init (&attr);
      if (!r)
	r = pthread_mutexattr_settype (&attr, 2);
      if (!r)
	r = pthread_mutex_init (mutex, &attr);
      if (!r)
	r = pthread_mutexattr_destroy (&attr);
      return r;
    }
  return 0;
}


static inline int
__gthread_recursive_mutex_lock (__gthread_recursive_mutex_t *mutex)
{
  return __gthread_mutex_lock (mutex);
}

static inline int
__gthread_recursive_mutex_trylock (__gthread_recursive_mutex_t *mutex)
{
  return __gthread_mutex_trylock (mutex);
}

static inline int
__gthread_recursive_mutex_unlock (__gthread_recursive_mutex_t *mutex)
{
  return __gthread_mutex_unlock (mutex);
}





#line 98 "/usr/include/c++/4.2.1/bits/gthr.h" 2

/* Fallback to single thread definitions.  */










#line 17 "/usr/include/c++/4.2.1/bits/c++io.h" 2

namespace std  {

  typedef __gthread_mutex_t __c_lock;

  // for basic_file.h
  typedef FILE __c_file;

}



#line 15 "/usr/include/c++/4.2.1/iosfwd" 2

#line 1 "/usr/include/c++/4.2.1/cctype" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/cctype
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c ctype.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: <ccytpe>
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cctype" 2

#line 1 "/usr/include/ctype.h" 1
/*
 * Copyright (c) 2000, 2005, 2008 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*
 * Copyright (c) 1989, 1993
 *	The Regents of the University of California.  All rights reserved.
 * (c) UNIX System Laboratories, Inc.
 * All or some portions of this file are derived from material licensed
 * to the University of California by American Telephone and Telegraph
 * Co. or Unix System Laboratories, Inc. and are reproduced herein with
 * the permission of UNIX System Laboratories, Inc.
 *
 * This code is derived from software contributed to Berkeley by
 * Paul Borman at Krystal Technologies.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)ctype.h	8.4 (Berkeley) 1/21/94
 */





#line 1 "/usr/include/runetype.h" 1
/*-
 * Copyright (c) 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * This code is derived from software contributed to Berkeley by
 * Paul Borman at Krystal Technologies.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)runetype.h	8.1 (Berkeley) 6/2/93
 */





#line 1 "/usr/include/_types.h" 1

#line 42 "/usr/include/runetype.h" 2










typedef	__darwin_ct_rune_t	ct_rune_t;




typedef	__darwin_rune_t		rune_t;



















/*
 * The lower 8 bits of runetype[] contain the digit value of the rune.
 */
typedef struct {
	__darwin_rune_t	__min;		/* First rune of the range */
	__darwin_rune_t	__max;		/* Last rune (inclusive) of the range */
	__darwin_rune_t	__map;		/* What first maps to in maps */
	__uint32_t	*__types;	/* Array of types in range */
} _RuneEntry;

typedef struct {
	int		__nranges;	/* Number of ranges stored */
	_RuneEntry	*__ranges;	/* Pointer to the ranges */
} _RuneRange;

typedef struct {
	char		__name[14];	/* CHARCLASS_NAME_MAX = 14 */
	__uint32_t	__mask;		/* charclass mask */
} _RuneCharClass;

typedef struct {
	char		__magic[8];	/* Magic saying what version we are */
	char		__encoding[32];	/* ASCII name of this encoding */

	__darwin_rune_t	(*__sgetrune)(const char *, __darwin_size_t, char const **);
	int		(*__sputrune)(__darwin_rune_t, char *, __darwin_size_t, char **);
	__darwin_rune_t	__invalid_rune;

	__uint32_t	__runetype[(1 <<8 )];
	__darwin_rune_t	__maplower[(1 <<8 )];
	__darwin_rune_t	__mapupper[(1 <<8 )];

	/*
	 * The following are to deal with Runes larger than _CACHED_RUNES - 1.
	 * Their data is actually contiguous with this structure so as to make
	 * it easier to read/write from/to disk.
	 */
	_RuneRange	__runetype_ext;
	_RuneRange	__maplower_ext;
	_RuneRange	__mapupper_ext;

	void		*__variable;	/* Data which depends on the encoding */
	int		__variable_len;	/* how long that data is */

	/*
	 * extra fields to deal with arbitrary character classes
	 */
	int		__ncharclasses;
	_RuneCharClass	*__charclasses;
} _RuneLocale;



extern "C" {
extern _RuneLocale _DefaultRuneLocale;
extern _RuneLocale *_CurrentRuneLocale;
}



#line 69 "/usr/include/ctype.h" 2














































/*
 * _EXTERNALIZE_CTYPE_INLINES_ is defined in locale/nomacros.c to tell us
 * to generate code for extern versions of all intermediate inline functions.
 */







/*
 * _EXTERNALIZE_CTYPE_INLINES_TOP_ is defined in locale/isctype.c to tell us
 * to generate code for extern versions of all top-level inline functions.
 */







/*
 * Use inline functions if we are allowed to and the compiler supports them.
 */



/* See comments in <machine/_type.h> about __darwin_ct_rune_t. */
extern "C" {
unsigned long		___runetype(__darwin_ct_rune_t);
__darwin_ct_rune_t	___tolower(__darwin_ct_rune_t);
__darwin_ct_rune_t	___toupper(__darwin_ct_rune_t);
}

static inline int
isascii(int _c)
{
	return ((_c & ~0x7F) == 0);
}





	


extern "C" {
int             	__maskrune(__darwin_ct_rune_t, unsigned long);   
}


static inline int
__istype(__darwin_ct_rune_t _c, unsigned long _f)
{

	

	return (isascii(_c) ? !!(_DefaultRuneLocale.__runetype[_c] & _f)
		: !!__maskrune(_c, _f));

}

static inline __darwin_ct_rune_t
__isctype(__darwin_ct_rune_t _c, unsigned long _f)
{

	

	return (_c < 0 || _c >= (1 <<8 )) ? 0 :
		!!(_DefaultRuneLocale.__runetype[_c] & _f);

}





	





	


extern "C" {
__darwin_ct_rune_t	__toupper(__darwin_ct_rune_t);
__darwin_ct_rune_t	__tolower(__darwin_ct_rune_t);
}


static inline int
__wcwidth(__darwin_ct_rune_t _c)
{
	unsigned int _x;

	if (_c == 0)
		return (0);
	_x = (unsigned int)__maskrune(_c, 0xe0000000L|0x00040000L);
	if ((_x & 0xe0000000L) != 0)
		return ((_x & 0xe0000000L) >> 30);
	return ((_x & 0x00040000L) != 0 ? 1 : -1);
}






static inline int
isalnum(int _c)
{
	return (__istype(_c, 0x00000100L|0x00000400L));
}

static inline int
isalpha(int _c)
{
	return (__istype(_c, 0x00000100L));
}

static inline int
isblank(int _c)
{
	return (__istype(_c, 0x00020000L));
}

static inline int
iscntrl(int _c)
{
	return (__istype(_c, 0x00000200L));
}

/* ANSI -- locale independent */
static inline int
isdigit(int _c)
{
	return (__isctype(_c, 0x00000400L));
}

static inline int
isgraph(int _c)
{
	return (__istype(_c, 0x00000800L));
}

static inline int
islower(int _c)
{
	return (__istype(_c, 0x00001000L));
}

static inline int
isprint(int _c)
{
	return (__istype(_c, 0x00040000L));
}

static inline int
ispunct(int _c)
{
	return (__istype(_c, 0x00002000L));
}

static inline int
isspace(int _c)
{
	return (__istype(_c, 0x00004000L));
}

static inline int
isupper(int _c)
{
	return (__istype(_c, 0x00008000L));
}

/* ANSI -- locale independent */
static inline int
isxdigit(int _c)
{
	return (__isctype(_c, 0x00010000L));
}

static inline int
toascii(int _c)
{
	return (_c & 0x7F);
}

static inline int
tolower(int _c)
{
        return (__tolower(_c));
}

static inline int
toupper(int _c)
{
        return (__toupper(_c));
}


static inline int
digittoint(int _c)
{
	return (__maskrune(_c, 0x0F));
}

static inline int
ishexnumber(int _c)
{
	return (__istype(_c, 0x00010000L));
}

static inline int
isideogram(int _c)
{
	return (__istype(_c, 0x00080000L));
}

static inline int
isnumber(int _c)
{
	return (__istype(_c, 0x00000400L));
}

static inline int
isphonogram(int _c)
{
	return (__istype(_c, 0x00200000L));
}

static inline int
isrune(int _c)
{
	return (__istype(_c, 0xFFFFFFF0L));
}

static inline int
isspecial(int _c)
{
	return (__istype(_c, 0x00100000L));
}












































#line 22 "/usr/include/c++/4.2.1/cctype" 2

// Get rid of those macros defined in <ctype.h> in lieu of real functions.














namespace std  {

  using ::isalnum;
  using ::isalpha;
  using ::iscntrl;
  using ::isdigit;
  using ::isgraph;
  using ::islower;
  using ::isprint;
  using ::ispunct;
  using ::isspace;
  using ::isupper;
  using ::isxdigit;
  using ::tolower;
  using ::toupper;

}



#line 15 "/usr/include/c++/4.2.1/iosfwd" 2

#line 1 "/usr/include/c++/4.2.1/bits/stringfwd.h" 1
// String support -*- C++ -*-

// Copyright (C) 2001, 2002, 2005 Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file stringfwd.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */

//
// ISO C++ 14882: 21 Strings library
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 16 "/usr/include/c++/4.2.1/bits/stringfwd.h" 2

namespace std  {

  template<typename _Alloc>
    class allocator;

  template<class _CharT>
    struct char_traits;

  template<typename _CharT, typename _Traits = char_traits<_CharT>,
           typename _Alloc = allocator<_CharT> >
    class basic_string;

  template<> struct char_traits<char>;

  typedef basic_string<char>    string;


  template<> struct char_traits<wchar_t>;

  typedef basic_string<wchar_t> wstring;


}



#line 15 "/usr/include/c++/4.2.1/iosfwd" 2

#line 1 "/usr/include/c++/4.2.1/bits/postypes.h" 1
// Position types -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file postypes.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */

//
// ISO C++ 14882: 27.4.1 - Types
// ISO C++ 14882: 27.4.3 - Template class fpos
//







#line 1 "/usr/include/c++/4.2.1/cwchar" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/cwchar
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c wchar.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 21.4
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cwchar" 2

#line 1 "/usr/include/c++/4.2.1/cstddef" 1

#line 22 "/usr/include/c++/4.2.1/cwchar" 2

#line 1 "/usr/include/c++/4.2.1/ctime" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/ctime
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c time.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 20.5  Date and time
//







#line 1 "/usr/include/c++/4.2.1/cstddef" 1

#line 21 "/usr/include/c++/4.2.1/ctime" 2

#line 1 "/usr/include/time.h" 1

#line 22 "/usr/include/c++/4.2.1/ctime" 2

// Get rid of those macros defined in <time.h> in lieu of real functions.










namespace std  {

  using ::clock_t;
  using ::time_t;
  using ::tm;

  using ::clock;
  using ::difftime;
  using ::mktime;
  using ::time;
  using ::asctime;
  using ::ctime;
  using ::gmtime;
  using ::localtime;
  using ::strftime;

}



#line 23 "/usr/include/c++/4.2.1/cwchar" 2



#line 1 "/usr/include/wchar.h" 1
/*-
 * Copyright (c)1999 Citrus Project,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * $FreeBSD: /repoman/r/ncvs/src/include/wchar.h,v 1.34 2003/03/13 06:29:53 tjr Exp $
 */

/*-
 * Copyright (c) 1999, 2000 The NetBSD Foundation, Inc.
 * All rights reserved.
 *
 * This code is derived from software contributed to The NetBSD Foundation
 * by Julian Coleman.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *        This product includes software developed by the NetBSD
 *        Foundation, Inc. and its contributors.
 * 4. Neither the name of The NetBSD Foundation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE NETBSD FOUNDATION, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE FOUNDATION OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *	$NetBSD: wchar.h,v 1.8 2000/12/22 05:31:42 itojun Exp $
 */





#line 1 "/usr/include/_types.h" 1

#line 70 "/usr/include/wchar.h" 2

#line 1 "/usr/include/sys/cdefs.h" 1

#line 71 "/usr/include/wchar.h" 2

#line 1 "/usr/include/Availability.h" 1

#line 72 "/usr/include/wchar.h" 2












typedef	__darwin_mbstate_t	mbstate_t;




























#line 1 "/usr/include/wchar.h" 1

#line 1 "/usr/include/stdio.h" 1

#line 91 "/usr/include/wchar.h" 2

#line 1 "/usr/include/time.h" 1

#line 92 "/usr/include/wchar.h" 2

#line 1 "/usr/include/_wctype.h" 1
/*-
 * Copyright (c)1999 Citrus Project,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

/*
 * Common header for wctype.h and wchar.h
 *
 * Contains everything required by wctype.h except:
 *
 *	typedef __darwin_wctrans_t wctrans_t;
 *	int iswblank(wint_t);
 *	wint_t towctrans(wint_t, wctrans_t);
 *	wctrans_t wctrans(const char *);
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 42 "/usr/include/_wctype.h" 2

#line 1 "/usr/include/_types.h" 1

#line 43 "/usr/include/_wctype.h" 2








typedef __darwin_wctype_t	wctype_t;











#line 1 "/usr/include/ctype.h" 1

#line 51 "/usr/include/_wctype.h" 2

/*
 * Use inline functions if we are allowed to and the compiler supports them.
 */



static inline int
iswalnum(wint_t _wc)
{
	return (__istype(_wc, 0x00000100L|0x00000400L));
}

static inline int
iswalpha(wint_t _wc)
{
	return (__istype(_wc, 0x00000100L));
}

static inline int
iswcntrl(wint_t _wc)
{
	return (__istype(_wc, 0x00000200L));
}

static inline int
iswctype(wint_t _wc, wctype_t _charclass)
{
	return (__istype(_wc, _charclass));
}

static inline int
iswdigit(wint_t _wc)
{
	return (__isctype(_wc, 0x00000400L));
}

static inline int
iswgraph(wint_t _wc)
{
	return (__istype(_wc, 0x00000800L));
}

static inline int
iswlower(wint_t _wc)
{
	return (__istype(_wc, 0x00001000L));
}

static inline int
iswprint(wint_t _wc)
{
	return (__istype(_wc, 0x00040000L));
}

static inline int
iswpunct(wint_t _wc)
{
	return (__istype(_wc, 0x00002000L));
}

static inline int
iswspace(wint_t _wc)
{
	return (__istype(_wc, 0x00004000L));
}

static inline int
iswupper(wint_t _wc)
{
	return (__istype(_wc, 0x00008000L));
}

static inline int
iswxdigit(wint_t _wc)
{
	return (__isctype(_wc, 0x00010000L));
}

static inline wint_t
towlower(wint_t _wc)
{
        return (__tolower(_wc));
}

static inline wint_t
towupper(wint_t _wc)
{
        return (__toupper(_wc));
}






















extern "C" {
wctype_t
	wctype(const char *);
}







#line 93 "/usr/include/wchar.h" 2


/* Initially added in Issue 4 */
extern "C" {
wint_t	btowc(int);
wint_t	fgetwc(FILE *);
wchar_t	*fgetws(wchar_t * , int, FILE * );
wint_t	fputwc(wchar_t, FILE *);
int	fputws(const wchar_t * , FILE * );
int	fwide(FILE *, int);
int	fwprintf(FILE * , const wchar_t * , ...) ;
int	fwscanf(FILE * , const wchar_t * , ...) ;
wint_t	getwc(FILE *);
wint_t	getwchar(void);
size_t	mbrlen(const char * , size_t, mbstate_t * );
size_t	mbrtowc(wchar_t * , const char * , size_t,
	    mbstate_t * );
int	mbsinit(const mbstate_t *);
size_t	mbsrtowcs(wchar_t * , const char ** , size_t,
	    mbstate_t * );
wint_t	putwc(wchar_t, FILE *);
wint_t	putwchar(wchar_t);
int	swprintf(wchar_t * , size_t, const wchar_t * ,
	    ...) ;
int	swscanf(const wchar_t * , const wchar_t * , ...) ;
wint_t	ungetwc(wint_t, FILE *);
int	vfwprintf(FILE * , const wchar_t * ,
	    __darwin_va_list) ;
int	vswprintf(wchar_t * , size_t, const wchar_t * ,
	    __darwin_va_list) ;
int	vwprintf(const wchar_t * , __darwin_va_list) ;
size_t	wcrtomb(char * , wchar_t, mbstate_t * );
wchar_t	*wcscat(wchar_t * , const wchar_t * );
wchar_t	*wcschr(const wchar_t *, wchar_t);
int	wcscmp(const wchar_t *, const wchar_t *);
int	wcscoll(const wchar_t *, const wchar_t *);
wchar_t	*wcscpy(wchar_t * , const wchar_t * );
size_t	wcscspn(const wchar_t *, const wchar_t *);
size_t	wcsftime(wchar_t * , size_t, const wchar_t * ,
	    const struct tm * ) __asm("_" "wcsftime" "$UNIX2003");
size_t	wcslen(const wchar_t *);
wchar_t	*wcsncat(wchar_t * , const wchar_t * , size_t);
int	wcsncmp(const wchar_t *, const wchar_t *, size_t);
wchar_t	*wcsncpy(wchar_t *  , const wchar_t * , size_t);
wchar_t	*wcspbrk(const wchar_t *, const wchar_t *);
wchar_t	*wcsrchr(const wchar_t *, wchar_t);
size_t	wcsrtombs(char * , const wchar_t ** , size_t,
	    mbstate_t * );
size_t	wcsspn(const wchar_t *, const wchar_t *);
wchar_t	*wcsstr(const wchar_t * , const wchar_t * );
size_t	wcsxfrm(wchar_t * , const wchar_t * , size_t);
int	wctob(wint_t);
double	wcstod(const wchar_t * , wchar_t ** );
wchar_t	*wcstok(wchar_t * , const wchar_t * ,
	    wchar_t ** );
long	 wcstol(const wchar_t * , wchar_t ** , int);
unsigned long
	 wcstoul(const wchar_t * , wchar_t ** , int);
wchar_t	*wmemchr(const wchar_t *, wchar_t, size_t);
int	wmemcmp(const wchar_t *, const wchar_t *, size_t);
wchar_t	*wmemcpy(wchar_t * , const wchar_t * , size_t);
wchar_t	*wmemmove(wchar_t *, const wchar_t *, size_t);
wchar_t	*wmemset(wchar_t *, wchar_t, size_t);
int	wprintf(const wchar_t * , ...) ;
int	wscanf(const wchar_t * , ...) ;
int	wcswidth(const wchar_t *, size_t);
int	wcwidth(wchar_t);
}



/* Additional functionality provided by:
 * POSIX.1-2001
 * ISO C99
 */


extern "C" {
int	vfwscanf(FILE * , const wchar_t * ,
	    __darwin_va_list) ;
int	vswscanf(const wchar_t * , const wchar_t * ,
	    __darwin_va_list) ;
int	vwscanf(const wchar_t * , __darwin_va_list) ;
float	wcstof(const wchar_t * , wchar_t ** );
long double
	wcstold(const wchar_t * , wchar_t ** ) ;

long long
	wcstoll(const wchar_t * , wchar_t ** , int);
unsigned long long
	wcstoull(const wchar_t * , wchar_t ** , int);

}




/* Additional functionality provided by:
 * POSIX.1-2008
 */


extern "C" {
size_t  mbsnrtowcs(wchar_t * , const char ** , size_t,
            size_t, mbstate_t * );
wchar_t *wcpcpy(wchar_t * , const wchar_t * ) ;
wchar_t *wcpncpy(wchar_t * , const wchar_t * , size_t) ;
wchar_t *wcsdup(const wchar_t *) ;
int     wcscasecmp(const wchar_t *, const wchar_t *) ;
int     wcsncasecmp(const wchar_t *, const wchar_t *, size_t n) ;
size_t  wcsnlen(const wchar_t *, size_t) const ;
size_t  wcsnrtombs(char * , const wchar_t ** , size_t,
            size_t, mbstate_t * );
}




/* Darwin extensions */


extern "C" {
wchar_t *fgetwln(FILE * , size_t *) ;
size_t	wcslcat(wchar_t *, const wchar_t *, size_t);
size_t	wcslcpy(wchar_t *, const wchar_t *, size_t);
}



/* Poison the following routines if -fshort-wchar is set */










#line 25 "/usr/include/c++/4.2.1/cwchar" 2


// Need to do a bit of trickery here with mbstate_t as char_traits
// assumes it is in wchar.h, regardless of wchar_t specializations.



  
  
    
  



namespace std  {

  using ::mbstate_t;

}

// Get rid of those macros defined in <wchar.h> in lieu of real functions.



































































namespace std  {

  using ::wint_t;

  using ::btowc;
  using ::fgetwc;
  using ::fgetws;
  using ::fputwc;
  using ::fputws;
  using ::fwide;
  using ::fwprintf;
  using ::fwscanf;
  using ::getwc;
  using ::getwchar;
  using ::mbrlen;
  using ::mbrtowc;
  using ::mbsinit;
  using ::mbsrtowcs;
  using ::putwc;
  using ::putwchar;
  using ::swprintf;
  using ::swscanf;
  using ::ungetwc;
  using ::vfwprintf;

  using ::vfwscanf;

  using ::vswprintf;

  using ::vswscanf;

  using ::vwprintf;

  using ::vwscanf;

  using ::wcrtomb;
  using ::wcscat;
  using ::wcscmp;
  using ::wcscoll;
  using ::wcscpy;
  using ::wcscspn;
  using ::wcsftime;
  using ::wcslen;
  using ::wcsncat;
  using ::wcsncmp;
  using ::wcsncpy;
  using ::wcsrtombs;
  using ::wcsspn;
  using ::wcstod;

  using ::wcstof;

  using ::wcstok;
  using ::wcstol;
  using ::wcstoul;
  using ::wcsxfrm;
  using ::wctob;
  using ::wmemcmp;
  using ::wmemcpy;
  using ::wmemmove;
  using ::wmemset;
  using ::wprintf;
  using ::wscanf;

  using ::wcschr;

  inline wchar_t*
  wcschr(wchar_t* __p, wchar_t __c)
  { return wcschr(const_cast<const wchar_t*>(__p), __c); }

  using ::wcspbrk;

  inline wchar_t*
  wcspbrk(wchar_t* __s1, const wchar_t* __s2)
  { return wcspbrk(const_cast<const wchar_t*>(__s1), __s2); }

  using ::wcsrchr;

  inline wchar_t*
  wcsrchr(wchar_t* __p, wchar_t __c)
  { return wcsrchr(const_cast<const wchar_t*>(__p), __c); }

  using ::wcsstr;

  inline wchar_t*
  wcsstr(wchar_t* __s1, const wchar_t* __s2)
  { return wcsstr(const_cast<const wchar_t*>(__s1), __s2); }

  using ::wmemchr;

  inline wchar_t*
  wmemchr(wchar_t* __p, wchar_t __c, size_t __n)
  { return wmemchr(const_cast<const wchar_t*>(__p), __c, __n); }

}







namespace __gnu_cxx  {


  
    


  using ::wcstold;


  
    
  
    


  using ::wcstoll;
  using ::wcstoull;


}

namespace std  {

  using ::__gnu_cxx::wcstold;
  using ::__gnu_cxx::wcstoll;
  using ::__gnu_cxx::wcstoull;

}







#line 15 "/usr/include/c++/4.2.1/bits/postypes.h" 2



#line 1 "/usr/include/stdint.h" 1
/*
 * Copyright (c) 2000-2010 Apple Inc.
 * All rights reserved.
 */










/* from ISO/IEC 988:1999 spec */

/* 7.18.1.1 Exact-width integer types */


typedef signed char           int8_t;




typedef short                int16_t;




typedef int                  int32_t;




typedef long long            int64_t;




typedef unsigned char         uint8_t;




typedef unsigned short       uint16_t;




typedef unsigned int         uint32_t;




typedef unsigned long long   uint64_t;


/* 7.18.1.2 Minimum-width integer types */
typedef int8_t           int_least8_t;
typedef int16_t         int_least16_t;
typedef int32_t         int_least32_t;
typedef int64_t         int_least64_t;
typedef uint8_t         uint_least8_t;
typedef uint16_t       uint_least16_t;
typedef uint32_t       uint_least32_t;
typedef uint64_t       uint_least64_t;


/* 7.18.1.3 Fastest-width integer types */
typedef int8_t            int_fast8_t;
typedef int16_t          int_fast16_t;
typedef int32_t          int_fast32_t;
typedef int64_t          int_fast64_t;
typedef uint8_t          uint_fast8_t;
typedef uint16_t        uint_fast16_t;
typedef uint32_t        uint_fast32_t;
typedef uint64_t        uint_fast64_t;


/* 7.18.1.4 Integer types capable of holding object pointers */








typedef unsigned long   uintptr_t;



/* 7.18.1.5 Greatest-width integer types */





typedef long long                intmax_t;








typedef unsigned long long      uintmax_t;



/* 7.18.2 Limits of specified-width integer types:
 *   These #defines specify the minimum and maximum limits
 *   of each of the types declared above.
 */


/* 7.18.2.1 Limits of exact-width integer types */







   /*
      Note:  the literal "most negative int" cannot be written in C --
      the rules in the standard (section 6.4.4.1 in C99) will give it
      an unsigned type, so INT32_MIN (and the most negative member of
      any larger signed type) must be written via a constant expression.
   */








/* 7.18.2.2 Limits of minimum-width integer types */















/* 7.18.2.3 Limits of fastest minimum-width integer types */















/* 7.18.2.4 Limits of integer types capable of holding object pointers */















/* 7.18.2.5 Limits of greatest-width integer types */





/* 7.18.3 "Other" */








/* We have no sig_atomic_t yet, so no SIG_ATOMIC_{MIN,MAX}.
   Should end up being {-127,127} or {0,255} ... or bigger.
   My bet would be on one of {U}INT32_{MIN,MAX}. */















/* WCHAR_MIN should be 0 if wchar_t is an unsigned type and
   (-WCHAR_MAX-1) if wchar_t is a signed type.  Unfortunately,
   it turns out that -fshort-wchar changes the signedness of
   the type. */














/* 7.18.4 Macros for integer constants */















#line 16 "/usr/include/c++/4.2.1/bits/postypes.h" 2


namespace std  {

  // The types streamoff, streampos and wstreampos and the class
  // template fpos<> are described in clauses 21.1.2, 21.1.3, 27.1.2,
  // 27.2, 27.4.1, 27.4.3 and D.6. Despite all this verbage, the
  // behaviour of these types is mostly implementation defined or
  // unspecified. The behaviour in this implementation is as noted
  // below.

  /**
   *  @brief  Type used by fpos, char_traits<char>, and char_traits<wchar_t>.
   *
   *  @if maint
   *  In clauses 21.1.3.1 and 27.4.1 streamoff is described as an
   *  implementation defined type.
   *  Note: In versions of GCC up to and including GCC 3.3, streamoff
   *  was typedef long.
   *  @endif
  */  

  typedef int64_t       streamoff;

  


  /// Integral type for I/O operation counts and buffer sizes.
  typedef ptrdiff_t	streamsize; // Signed integral type

  template<typename _StateT>
    class fpos;

  /**
   *  @brief  Class representing stream positions.
   *
   *  The standard places no requirements upon the template parameter StateT.
   *  In this implementation StateT must be DefaultConstructible,
   *  CopyConstructible and Assignable.  The standard only requires that fpos
   *  should contain a member of type StateT. In this implementation it also
   *  contains an offset stored as a signed integer.
   *
   *  @param  StateT  Type passed to and returned from state().
   */
  template<typename _StateT>
    class fpos
    {
    private:
      streamoff	                _M_off;
      _StateT			_M_state;

    public:
      // The standard doesn't require that fpos objects can be default
      // constructed. This implementation provides a default
      // constructor that initializes the offset to 0 and default
      // constructs the state.
      fpos()
      : _M_off(0), _M_state() { }

      // The standard requires that fpos objects can be constructed
      // from streamoff objects using the constructor syntax, and
      // fails to give any meaningful semantics. In this
      // implementation implicit conversion is also allowed, and this
      // constructor stores the streamoff as the offset and default
      // constructs the state.
      /// Construct position from offset.
      fpos(streamoff __off)
      : _M_off(__off), _M_state() { }

      /// Convert to streamoff.
      operator streamoff() const { return _M_off; }

      /// Remember the value of @a st.
      void
      state(_StateT __st)
      { _M_state = __st; }

      /// Return the last set value of @a st.
      _StateT
      state() const
      { return _M_state; }

      // The standard requires that this operator must be defined, but
      // gives no semantics. In this implemenation it just adds it's
      // argument to the stored offset and returns *this.
      /// Add offset to this position.
      fpos&
      operator+=(streamoff __off)
      {
	_M_off += __off;
	return *this;
      }

      // The standard requires that this operator must be defined, but
      // gives no semantics. In this implemenation it just subtracts
      // it's argument from the stored offset and returns *this.
      /// Subtract offset from this position.
      fpos&
      operator-=(streamoff __off)
      {
	_M_off -= __off;
	return *this;
      }

      // The standard requires that this operator must be defined, but
      // defines it's semantics only in terms of operator-. In this
      // implementation it constructs a copy of *this, adds the
      // argument to that copy using operator+= and then returns the
      // copy.
      /// Add position and offset.
      fpos
      operator+(streamoff __off) const
      {
	fpos __pos(*this);
	__pos += __off;
	return __pos;
      }

      // The standard requires that this operator must be defined, but
      // defines it's semantics only in terms of operator+. In this
      // implementation it constructs a copy of *this, subtracts the
      // argument from that copy using operator-= and then returns the
      // copy.
      /// Subtract offset from position.
      fpos
      operator-(streamoff __off) const
      {
	fpos __pos(*this);
	__pos -= __off;
	return __pos;
      }

      // The standard requires that this operator must be defined, but
      // defines it's semantics only in terms of operator+. In this
      // implementation it returns the difference between the offset
      // stored in *this and in the argument.
      /// Subtract position to return offset.
      streamoff
      operator-(const fpos& __other) const
      { return _M_off - __other._M_off; }
    };

  // The standard only requires that operator== must be an
  // equivalence relation. In this implementation two fpos<StateT>
  // objects belong to the same equivalence class if the contained
  // offsets compare equal.
  /// Test if equivalent to another position.
  template<typename _StateT>
    inline bool
    operator==(const fpos<_StateT>& __lhs, const fpos<_StateT>& __rhs)
    { return streamoff(__lhs) == streamoff(__rhs); }

  template<typename _StateT>
    inline bool
    operator!=(const fpos<_StateT>& __lhs, const fpos<_StateT>& __rhs)
    { return streamoff(__lhs) != streamoff(__rhs); }

  // Clauses 21.1.3.1 and 21.1.3.2 describe streampos and wstreampos
  // as implementation defined types, but clause 27.2 requires that
  // they must both be typedefs for fpos<mbstate_t>
  /// File position for char streams.
  typedef fpos<mbstate_t> streampos;
  /// File position for wchar_t streams.
  typedef fpos<mbstate_t> wstreampos;

}



#line 16 "/usr/include/c++/4.2.1/iosfwd" 2

#line 1 "/usr/include/c++/4.2.1/bits/functexcept.h" 1
// Function-Based Exception Support -*- C++ -*-

// Copyright (C) 2001, 2004, 2005 Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file functexcept.h
 *  This header provides support for -fno-exceptions.
 */

//
// ISO C++ 14882: 19.1  Exception classes
//





#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 14 "/usr/include/c++/4.2.1/bits/functexcept.h" 2

#line 1 "/usr/include/c++/4.2.1/exception_defines.h" 1
// -fno-exceptions Support -*- C++ -*-

// Copyright (C) 2001, 2003 Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

//
// ISO C++ 14882: 19.1  Exception classes
//

/** @file exception_defines.h
 *  This is a Standard C++ Library header.
 */





// Iff -fno-exceptions, transform error handling code to work without it.










#line 15 "/usr/include/c++/4.2.1/bits/functexcept.h" 2

namespace std  {

  // Helper for exception objects in <except>
  void
  __throw_bad_exception(void) ;

  // Helper for exception objects in <new>
  void
  __throw_bad_alloc(void) ;

  // Helper for exception objects in <typeinfo>
  void
  __throw_bad_cast(void) ;

  void
  __throw_bad_typeid(void) ;

  // Helpers for exception objects in <stdexcept>
  void
  __throw_logic_error(const char*) ;

  void
  __throw_domain_error(const char*) ;

  void
  __throw_invalid_argument(const char*) ;

  void
  __throw_length_error(const char*) ;

  void
  __throw_out_of_range(const char*) ;

  void
  __throw_runtime_error(const char*) ;

  void
  __throw_range_error(const char*) ;

  void
  __throw_overflow_error(const char*) ;

  void
  __throw_underflow_error(const char*) ;

  // Helpers for exception objects in basic_ios
  void
  __throw_ios_failure(const char*) ;

}



#line 17 "/usr/include/c++/4.2.1/iosfwd" 2

namespace std  {

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_ios;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_streambuf;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_istream;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_ostream;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_iostream;

  template<typename _CharT, typename _Traits = char_traits<_CharT>,
	    typename _Alloc = allocator<_CharT> >
    class basic_stringbuf;

  template<typename _CharT, typename _Traits = char_traits<_CharT>,
	   typename _Alloc = allocator<_CharT> >
    class basic_istringstream;

  template<typename _CharT, typename _Traits = char_traits<_CharT>,
	   typename _Alloc = allocator<_CharT> >
    class basic_ostringstream;

  template<typename _CharT, typename _Traits = char_traits<_CharT>,
	   typename _Alloc = allocator<_CharT> >
    class basic_stringstream;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_filebuf;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_ifstream;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_ofstream;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class basic_fstream;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class istreambuf_iterator;

  template<typename _CharT, typename _Traits = char_traits<_CharT> >
    class ostreambuf_iterator;

  // _GLIBCXX_RESOLVE_LIB_DEFECTS
  // Not included.   (??? Apparently no LWG number?)
  class ios_base; 

  /** 
   *  @defgroup s27_2_iosfwd I/O Forward Declarations
   *
   *  Nearly all of the I/O classes are parameterized on the type of
   *  characters they read and write.  (The major exception is ios_base at
   *  the top of the hierarchy.)  This is a change from pre-Standard
   *  streams, which were not templates.
   *
   *  For ease of use and compatibility, all of the basic_* I/O-related
   *  classes are given typedef names for both of the builtin character
   *  widths (wide and narrow).  The typedefs are the same as the
   *  pre-Standard names, for example:
   *
   *  @code
   *     typedef basic_ifstream<char>  ifstream;
   *  @endcode
   *
   *  Because properly forward-declaring these classes can be difficult, you
   *  should not do it yourself.  Instead, include the &lt;iosfwd&gt;
   *  header, which contains only declarations of all the I/O classes as
   *  well as the typedefs.  Trying to forward-declare the typedefs
   *  themselves (e.g., "class ostream;") is not valid ISO C++.
   *
   *  For more specific declarations, see
   *  http://gcc.gnu.org/onlinedocs/libstdc++/27_io/howto.html#10
   *
   *  @{
  */
  typedef basic_ios<char> 		ios;		///< @isiosfwd
  typedef basic_streambuf<char> 	streambuf;	///< @isiosfwd
  typedef basic_istream<char> 		istream;	///< @isiosfwd
  typedef basic_ostream<char> 		ostream;	///< @isiosfwd
  typedef basic_iostream<char> 		iostream;	///< @isiosfwd
  typedef basic_stringbuf<char> 	stringbuf;	///< @isiosfwd
  typedef basic_istringstream<char> 	istringstream;	///< @isiosfwd
  typedef basic_ostringstream<char> 	ostringstream;	///< @isiosfwd
  typedef basic_stringstream<char> 	stringstream;	///< @isiosfwd
  typedef basic_filebuf<char> 		filebuf;	///< @isiosfwd
  typedef basic_ifstream<char> 		ifstream;	///< @isiosfwd
  typedef basic_ofstream<char> 		ofstream;	///< @isiosfwd
  typedef basic_fstream<char> 		fstream;	///< @isiosfwd


  typedef basic_ios<wchar_t> 		wios;		///< @isiosfwd
  typedef basic_streambuf<wchar_t> 	wstreambuf;	///< @isiosfwd
  typedef basic_istream<wchar_t> 	wistream;	///< @isiosfwd
  typedef basic_ostream<wchar_t> 	wostream;	///< @isiosfwd
  typedef basic_iostream<wchar_t> 	wiostream;	///< @isiosfwd
  typedef basic_stringbuf<wchar_t> 	wstringbuf;	///< @isiosfwd
  typedef basic_istringstream<wchar_t> 	wistringstream;	///< @isiosfwd
  typedef basic_ostringstream<wchar_t> 	wostringstream;	///< @isiosfwd
  typedef basic_stringstream<wchar_t> 	wstringstream;	///< @isiosfwd
  typedef basic_filebuf<wchar_t> 	wfilebuf;	///< @isiosfwd
  typedef basic_ifstream<wchar_t> 	wifstream;	///< @isiosfwd
  typedef basic_ofstream<wchar_t> 	wofstream;	///< @isiosfwd
  typedef basic_fstream<wchar_t> 	wfstream;	///< @isiosfwd

  /** @}  */

}



#line 15 "/usr/include/c++/4.2.1/ios" 2

#line 1 "/usr/include/c++/4.2.1/exception" 1
// Exception Handling support header for -*- C++ -*-

// Copyright (C) 1995, 1996, 1997, 1998, 1999, 2000, 2001, 2002, 2003,
// 2004, 2005, 2006, 2007
// Free Software Foundation
//
// This file is part of GCC.
//
// GCC is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// GCC is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with GCC; see the file COPYING.  If not, write to
// the Free Software Foundation, 51 Franklin Street, Fifth Floor,
// Boston, MA 02110-1301, USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file exception
 *  This is a Standard C++ Library header.
 */







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 13 "/usr/include/c++/4.2.1/exception" 2

extern "C++" {

namespace std 
{
  /**
   *  @brief Base class for all library exceptions.
   *
   *  This is the base class for all exceptions thrown by the standard
   *  library, and by certain language expressions.  You are free to derive
   *  your own %exception classes, or use a different hierarchy, or to
   *  throw non-class data (e.g., fundamental types).
   */
  class exception 
  {
  public:
    exception() throw() { }
    virtual ~exception() throw();

    /** Returns a C-style character string describing the general cause
     *  of the current error.  */
    virtual const char* what() const throw();
  };

  /** If an %exception is thrown which is not listed in a function's
   *  %exception specification, one of these may be thrown.  */
  class bad_exception : public exception 
  {
  public:
    bad_exception() throw() { }

    // This declaration is not useless:
    // http://gcc.gnu.org/onlinedocs/gcc-3.0.2/gcc_6.html#SEC118
    virtual ~bad_exception() throw();

    // See comment in eh_exception.cc.
    virtual const char* what() const throw();
  };

  /// If you write a replacement %terminate handler, it must be of this type.
  typedef void (*terminate_handler) ();

  /// If you write a replacement %unexpected handler, it must be of this type.
  typedef void (*unexpected_handler) ();

  /// Takes a new handler function as an argument, returns the old function.
  terminate_handler set_terminate(terminate_handler) throw();

  /** The runtime will call this function if %exception handling must be
   *  abandoned for any reason.  It can also be called by the user.  */
  void terminate() ;

  /// Takes a new handler function as an argument, returns the old function.
  unexpected_handler set_unexpected(unexpected_handler) throw();

  /** The runtime will call this function if an %exception is thrown which
   *  violates the function's %exception specification.  */
  void unexpected() ;

  /** [18.6.4]/1:  "Returns true after completing evaluation of a
   *  throw-expression until either completing initialization of the
   *  exception-declaration in the matching handler or entering @c unexpected()
   *  due to the throw; or after entering @c terminate() for any reason
   *  other than an explicit call to @c terminate().  [Note: This includes
   *  stack unwinding [15.2].  end note]"
   *
   *  2:  "When @c uncaught_exception() is true, throwing an %exception can
   *  result in a call of @c terminate() (15.5.1)."
   */
  bool uncaught_exception() throw();
} // namespace std

namespace __gnu_cxx  {

  /** A replacement for the standard terminate_handler which prints more
      information about the terminating exception (if any) on stderr.  Call
      @code
        std::set_terminate (__gnu_cxx::__verbose_terminate_handler)
      @endcode
      to use.  For more info, see
      http://gcc.gnu.org/onlinedocs/libstdc++/19_diagnostics/howto.html#4

      In 3.4 and later, this is on by default.
  */
  void __verbose_terminate_handler ();

}
  
} // extern "C++"





#line 15 "/usr/include/c++/4.2.1/ios" 2

#line 1 "/usr/include/c++/4.2.1/bits/char_traits.h" 1
// Character Traits for use by standard string and iostream -*- C++ -*-

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file char_traits.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */

//
// ISO C++ 14882: 21  Strings library
//







#line 1 "/usr/include/c++/4.2.1/cstring" 1

#line 15 "/usr/include/c++/4.2.1/bits/char_traits.h" 2

#line 1 "/usr/include/c++/4.2.1/bits/stl_algobase.h" 1
// Bits and pieces used in algorithms -*- C++ -*-

// Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/*
 *
 * Copyright (c) 1994
 * Hewlett-Packard Company
 *
 * Permission to use, copy, modify, distribute and sell this software
 * and its documentation for any purpose is hereby granted without fee,
 * provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear
 * in supporting documentation.  Hewlett-Packard Company makes no
 * representations about the suitability of this software for any
 * purpose.  It is provided "as is" without express or implied warranty.
 *
 *
 * Copyright (c) 1996-1998
 * Silicon Graphics Computer Systems, Inc.
 *
 * Permission to use, copy, modify, distribute and sell this software
 * and its documentation for any purpose is hereby granted without fee,
 * provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear
 * in supporting documentation.  Silicon Graphics makes no
 * representations about the suitability of this software for any
 * purpose.  It is provided "as is" without express or implied warranty.
 */

/** @file stl_algobase.h
 *  This is an internal header file, included by other library headers.
 *  You should not attempt to use it directly.
 */





#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 40 "/usr/include/c++/4.2.1/bits/stl_algobase.h" 2

#line 1 "/usr/include/c++/4.2.1/cstring" 1

#line 41 "/usr/include/c++/4.2.1/bits/stl_algobase.h" 2

#line 1 "/usr/include/c++/4.2.1/climits" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/climits
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c limits.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 18.2.2  Implementation properties: C library
//







#line 1 "/usr/include/limits.h" 1
/*
 * Copyright (c) 2000, 2004-2007, 2009 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*	$NetBSD: limits.h,v 1.8 1996/10/21 05:10:50 jtc Exp $	*/

/*
 * Copyright (c) 1988, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)limits.h	8.2 (Berkeley) 1/4/94
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 63 "/usr/include/limits.h" 2

#line 1 "/usr/include/machine/limits.h" 1
/* This is the `system' limits.h, independent of any particular
   compiler.  GCC provides its own limits.h which can be found in
   /usr/lib/gcc, although it is not very informative.  
   This file is public domain.  */


#line 1 "/usr/include/i386/limits.h" 1
/*
 * Copyright (c) 1988, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)limits.h	8.3 (Berkeley) 1/4/94
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 39 "/usr/include/i386/limits.h" 2

#line 1 "/usr/include/i386/_limits.h" 1
/*
 * Copyright (c) 2004 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * The contents of this file constitute Original Code as defined in and
 * are subject to the Apple Public Source License Version 1.1 (the
 * "License").  You may not use this file except in compliance with the
 * License.  Please obtain a copy of the License at
 * http://www.apple.com/publicsource and read it before using this file.
 * 
 * This Original Code and all software distributed under the License are
 * distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT.  Please see the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */







#line 40 "/usr/include/i386/limits.h" 2








/*
 * According to ANSI (section 2.2.4.2), the values below must be usable by
 * #if preprocessing directives.  Additionally, the expression must have the
 * same type as would an expression that is an object of the corresponding
 * type converted according to the integral promotions.  The subtraction for
 * INT_MIN and LONG_MIN is so the value is not unsigned; 2147483648 is an
 * unsigned int for 32-bit two's complement ANSI compilers (section 3.1.3.2).
 * These numbers work for pcc as well.  The UINT_MAX and ULONG_MAX values
 * are written as hex so that GCC will be quiet about large integer constants.
 */


















































#line 8 "/usr/include/machine/limits.h" 2






#line 64 "/usr/include/limits.h" 2

#line 1 "/usr/include/sys/syslimits.h" 1
/*
 * Copyright (c) 2000 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/*	$NetBSD: syslimits.h,v 1.15 1997/06/25 00:48:09 lukem Exp $	*/

/*
 * Copyright (c) 1988, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)syslimits.h	8.1 (Berkeley) 6/2/93
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 68 "/usr/include/sys/syslimits.h" 2


/*
 * Note: CHILD_MAX *must* be less than hard_maxproc, which is set at
 * compile time; you *cannot* set it higher than the hard limit!!
 */
































					/* = ((PRIO_MAX - PRIO_MIN) / 2) + 1 */
					/* range: 0 - 39 [(2 * NZERO) - 1] */
					/* 0 is not actually used */


					
					





#line 65 "/usr/include/limits.h" 2









































































/* Actually for XSI Visible */


/* Removed in Issue 6 */


















/* NZERO to be defined here. TBD. See also sys/param.h  */



#line 21 "/usr/include/c++/4.2.1/climits" 2



#line 42 "/usr/include/c++/4.2.1/bits/stl_algobase.h" 2

#line 1 "/usr/include/c++/4.2.1/cstdlib" 1
// -*- C++ -*- forwarding header.

// Copyright (C) 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
// Free Software Foundation, Inc.
//
// This file is part of the GNU ISO C++ Library.  This library is free
// software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2, or (at your option)
// any later version.

// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along
// with this library; see the file COPYING.  If not, write to the Free
// Software Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.

// As a special exception, you may use this file as part of a free software
// library without restriction.  Specifically, if other files instantiate
// templates or use macros or inline functions from this file, or you compile
// this file and link it with other files to produce an executable, this
// file does not by itself cause the resulting executable to be covered by
// the GNU General Public License.  This exception does not however
// invalidate any other reasons why the executable file might be covered by
// the GNU General Public License.

/** @file include/cstdlib
 *  This is a Standard C++ Library file.  You should @c #include this file
 *  in your programs, rather than any of the "*.h" implementation files.
 *
 *  This is the C++ version of the Standard C Library header @c stdlib.h,
 *  and its contents are (mostly) the same as that header, but are all
 *  contained in the namespace @c std (except for names which are defined
 *  as macros in C).
 */

//
// ISO C++ 14882: 20.4.6  C library
//







#line 1 "/usr/include/c++/4.2.1/bits/c++config.h" 1

#line 21 "/usr/include/c++/4.2.1/cstdlib" 2

#line 1 "/usr/include/c++/4.2.1/cstddef" 1

#line 22 "/usr/include/c++/4.2.1/cstdlib" 2












  
  
  






#line 1 "/usr/include/stdlib.h" 1
/*
 * Copyright (c) 2000, 2002 - 2008 Apple Inc. All rights reserved.
 *
 * @APPLE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this
 * file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_LICENSE_HEADER_END@
 */
/*-
 * Copyright (c) 1990, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)stdlib.h	8.5 (Berkeley) 5/19/95
 */





#line 1 "/usr/include/Availability.h" 1

#line 61 "/usr/include/stdlib.h" 2


#line 1 "/usr/include/_types.h" 1

#line 63 "/usr/include/stdlib.h" 2


#line 1 "/usr/include/sys/wait.h" 1
/*
 * Copyright (c) 2000 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/* Copyright (c) 1995 NeXT Computer, Inc. All Rights Reserved */
/*
 * Copyright (c) 1982, 1986, 1989, 1993, 1994
 *	The Regents of the University of California.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)wait.h	8.2 (Berkeley) 7/10/94
 */
 




#line 1 "/usr/include/sys/cdefs.h" 1

#line 66 "/usr/include/sys/wait.h" 2

#line 1 "/usr/include/sys/_types.h" 1

#line 67 "/usr/include/sys/wait.h" 2

/*
 * This file holds definitions relevent to the wait4 system call
 * and the alternate interfaces that use it (wait, wait3, waitpid).
 */

/*
 * [XSI] The type idtype_t shall be defined as an enumeration type whose
 * possible values shall include at least P_ALL, P_PID, and P_PGID.
 */
typedef enum {
	P_ALL,
	P_PID,
	P_PGID
} idtype_t;

/*
 * [XSI] The id_t and pid_t types shall be defined as described
 * in <sys/types.h>
 */






typedef __darwin_id_t	id_t;



/*
 * [XSI] The siginfo_t type shall be defined as described in <signal.h>
 * [XSI] The rusage structure shall be defined as described in <sys/resource.h>
 * [XSI] Inclusion of the <sys/wait.h> header may also make visible all
 * symbols from <signal.h> and <sys/resource.h>
 *
 * NOTE:	This requirement is currently being satisfied by the direct
 *		inclusion of <sys/signal.h> and <sys/resource.h>, below.
 *
 *		Software should not depend on the exposure of anything other
 *		than the types siginfo_t and struct rusage as a result of
 *		this inclusion.  If you depend on any types or manifest
 *		values othe than siginfo_t and struct rusage from either of
 *		those files, you should explicitly include them yourself, as
 *		well, or in future releases your stware may not compile
 *		without modification.
 */

#line 1 "/usr/include/sys/signal.h" 1
/*
 * Copyright (c) 2000-2006 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/* Copyright (c) 1995 NeXT Computer, Inc. All Rights Reserved */
/*
 * Copyright (c) 1982, 1986, 1989, 1991, 1993
 *	The Regents of the University of California.  All rights reserved.
 * (c) UNIX System Laboratories, Inc.
 * All or some portions of this file are derived from material licensed
 * to the University of California by American Telephone and Telegraph
 * Co. or Unix System Laboratories, Inc. and are reproduced herein with
 * the permission of UNIX System Laboratories, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the University of
 *	California, Berkeley and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)signal.h	8.2 (Berkeley) 1/21/94
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 72 "/usr/include/sys/signal.h" 2

#line 1 "/usr/include/sys/appleapiopts.h" 1

#line 73 "/usr/include/sys/signal.h" 2








#line 1 "/usr/include/machine/signal.h" 1
/*
 * Copyright (c) 2000-2007 Apple Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */





#line 1 "/usr/include/i386/signal.h" 1
/*
 * Copyright (c) 2000-2002 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */
/*
 * Copyright (c) 1992 NeXT Computer, Inc.
 *
 */





#line 1 "/usr/include/sys/cdefs.h" 1

#line 36 "/usr/include/i386/signal.h" 2


typedef int sig_atomic_t; 




#line 1 "/usr/include/sys/appleapiopts.h" 1

#line 41 "/usr/include/i386/signal.h" 2



/*
 * Information pushed on stack when a signal is delivered.
 * This is used by the kernel to restore state following
 * execution of the signal handler.  It is also made available
 * to the handler to allow it to properly restore state if
 * a non-standard exit is performed.
 */


#line 1 "/usr/include/i386/_structs.h" 1
/*
 * Copyright (c) 2004 Apple Computer, Inc. All rights reserved.
 *
 * @APPLE_OSREFERENCE_LICENSE_HEADER_START@
 * 
 * This file contains Original Code and/or Modifications of Original Code
 * as defined in and that are subject to the Apple Public Source License
 * Version 2.0 (the 'License'). You may not use this file except in
 * compliance with the License. The rights granted to you under the License
 * may not be used to create, or enable the creation or redistribution of,
 * unlawful or unlicensed copies of an Apple operating system, or to
 * circumvent, violate, or enable the circumvention or violation of, any
 * terms of an Apple operating system software license agreement.
 * 
 * Please obtain a copy of the License at
 * http://www.opensource.apple.com/apsl/ and read it before using this file.
 * 
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESS OR IMPLIED, AND APPLE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.
 * Please see the License for the specific language governing rights and
 * limitations under the License.
 * 
 * @APPLE_OSREFERENCE_LICENSE_HEADER_END@
 */


#line 1 "/usr/include/sys/appleapiopts.h" 1

#line 31 "/usr/include/i386/_structs.h" 2




















	
	
	





	
	
	






	
	
	





	
	
	










	
	
	





	
	
	






	
	
	





	
	
	



























#line 51 "/usr/include/i386/signal.h" 2








#line 32 "/usr/include/machine/signal.h" 2








#line 79 "/usr/include/sys/signal.h" 2











































/*
 * Language spec sez we must list exactly one parameter, even though we
 * actually supply three.  Ugh!
 * SIG_HOLD is chosen to avoid KERN_SIG_* values in <sys/signalvar.h>
 */














#line 1 "/usr/include/sys/_types.h" 1

#line 128 "/usr/include/sys/signal.h" 2





#line 1 "/usr/include/sys/_structs.h" 1

#line 130 "/usr/include/sys/signal.h" 2


























union sigval {
	/* Members as suggested by Annex C of POSIX 1003.1b. */
	int	sival_int;
	void	*sival_ptr;
};





struct sigevent {
	int				sigev_notify;				/* Notification type */
	int				sigev_signo;				/* Signal number */
	union sigval	sigev_value;				/* Signal value */
	void			(*sigev_notify_function)(union sigval);	  /* Notification function */
	pthread_attr_t	*sigev_notify_attributes;	/* Notification attributes */
};


typedef struct __siginfo {
	int	si_signo;		/* signal number */
	int	si_errno;		/* errno association */
	int	si_code;		/* signal code */
	pid_t	si_pid;			/* sending process */
	uid_t	si_uid;			/* sender's ruid */
	int	si_status;		/* exit value */
	void	*si_addr;		/* faulting instruction */
	union sigval si_value;		/* signal value */
	long	si_band;		/* band event for SIGPOLL */
	unsigned long	__pad[7];	/* Reserved for Future Use */
} siginfo_t;


/* 
 * When the signal is SIGILL or SIGFPE, si_addr contains the address of 
 * the faulting instruction.
 * When the signal is SIGSEGV or SIGBUS, si_addr contains the address of 
 * the faulting memory reference. Although for x86 there are cases of SIGSEGV
 * for which si_addr cannot be determined and is NULL. 
 * If the signal is SIGCHLD, the si_pid field will contain the child process ID,
 *  si_status contains the exit value or signal and
 *  si_uid contains the real user ID of the process that sent the signal.
 */

/* Values for si_code */

/* Codes for SIGILL */












/* Codes for SIGFPE */












/* Codes for SIGSEGV */






/* Codes for SIGBUS */







/* Codes for SIGTRAP */



/* Codes for SIGCHLD */










/* Codes for SIGPOLL */







/* union for signal handlers */
union __sigaction_u {
	void    (*__sa_handler)(int);
	void    (*__sa_sigaction)(int, struct __siginfo *,
		       void *);
};

/* Signal vector template for Kernel user boundary */
struct	__sigaction {
	union __sigaction_u __sigaction_u;  /* signal handler */
	void    (*sa_tramp)(void *, int, int, siginfo_t *, void *);
	sigset_t sa_mask;		/* signal mask to apply */
	int	sa_flags;		/* see signal options below */
};

/*
 * Signal vector "template" used in sigaction call.
 */
struct	sigaction {
	union __sigaction_u __sigaction_u;  /* signal handler */
	sigset_t sa_mask;		/* signal mask to apply */
	int	sa_flags;		/* see signal options below */
};



/* if SA_SIGINFO is set, sa_sigaction is to be used instead of sa_handler. */












/* This will provide 64bit register set in a 32bit user address space */



/* the following are the only bits we support from user space, the 
 * rest are for kernel use only.
 */


/*
 * Flags for sigprocmask:
 */




/* POSIX 1003.1b required values. */







typedef	void (*sig_t)(int);	/* type of signal function */


/*
 * Structure used in sigaltstack call.
 */







/*
 * 4.3 compatibility:
 * Signal vector "template" used in sigvec call.
 */
struct	sigvec {
	void	(*sv_handler)(int);	/* signal handler */
	int	sv_mask;		/* signal mask to apply */
	int	sv_flags;		/* see signal options below */
};











/*
 * Structure used in sigstack call.
 */
struct	sigstack {
	char	*ss_sp;			/* signal stack pointer */
	int	ss_onstack;		/* current status */
};


/*
 * Macro for converting signal number to a mask suitable for
 * sigblock().
 */






