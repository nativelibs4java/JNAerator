typedef signed char __int8_t;
typedef unsigned char __uint8_t;
typedef short __int16_t;
typedef unsigned short __uint16_t;
typedef int __int32_t;
typedef unsigned int __uint32_t;
typedef long long __int64_t;
typedef unsigned long long __uint64_t;
typedef long __darwin_intptr_t;
typedef unsigned int __darwin_natural_t;
typedef int __darwin_ct_rune_t; /* ct_rune_t */
/**
 * mbstate_t is an opaque object to keep conversion state, during multibyte<br>
 * stream conversions.  The content must not be referenced by user programs.
 */
typedef union  __mbstate_t {
	char[128] __mbstate8;
	long long _mbstateL; /* for alignment */
} __mbstate_t;
typedef __mbstate_t __darwin_mbstate_t; /* mbstate_t */
typedef int __darwin_ptrdiff_t; /* ptr1 - ptr2 */
typedef unsigned long __darwin_size_t; /* sizeof() */
typedef void* __darwin_va_list; /* va_list */
typedef __darwin_ct_rune_t __darwin_wchar_t; /* wchar_t */
typedef __darwin_wchar_t __darwin_rune_t; /* rune_t */
typedef __darwin_ct_rune_t __darwin_wint_t; /* wint_t */
typedef unsigned long __darwin_clock_t; /* clock() */
typedef __uint32_t __darwin_socklen_t; /* socklen_t (duh) */
typedef long __darwin_ssize_t; /* byte count or error */
typedef long __darwin_time_t; /* time() */
struct __darwin_pthread_handler_rec {
	__routine_callback __routine; /* Routine to call */
	void* __arg; /* Argument to pass */
	__darwin_pthread_handler_rec* __next;
	typedef void (*__routine_callback)(void* voidPtr1);
};
struct _opaque_pthread_attr_t {
	long __sig;
	char[36] __opaque;
};
struct _opaque_pthread_cond_t {
	long __sig;
	char[24] __opaque;
};
struct _opaque_pthread_condattr_t {
	long __sig;
	char[4] __opaque;
};
struct _opaque_pthread_mutex_t {
	long __sig;
	char[40] __opaque;
};
struct _opaque_pthread_mutexattr_t {
	long __sig;
	char[8] __opaque;
};
struct _opaque_pthread_once_t {
	long __sig;
	char[4] __opaque;
};
struct _opaque_pthread_rwlock_t {
	long __sig;
	char[124] __opaque;
};
struct _opaque_pthread_rwlockattr_t {
	long __sig;
	char[12] __opaque;
};
struct _opaque_pthread_t {
	long __sig;
	__darwin_pthread_handler_rec* __cleanup_stack;
	char[596] __opaque;
};
typedef __int64_t __darwin_blkcnt_t; /* total blocks */
typedef __int32_t __darwin_blksize_t; /* preferred block size */
typedef __int32_t __darwin_dev_t; /* dev_t */
typedef unsigned int __darwin_fsblkcnt_t; /* Used by statvfs and fstatvfs */
typedef unsigned int __darwin_fsfilcnt_t; /* Used by statvfs and fstatvfs */
typedef __uint32_t __darwin_gid_t; /* [???] process and group IDs */
typedef __uint32_t __darwin_id_t; /* [XSI] pid_t, uid_t, or gid_t*/
typedef __uint64_t __darwin_ino64_t; /* [???] Used for 64 bit inodes */
typedef __darwin_ino64_t __darwin_ino_t; /* [???] Used for inodes */
typedef __darwin_natural_t __darwin_mach_port_name_t; /* Used by mach */
typedef __darwin_mach_port_name_t __darwin_mach_port_t; /* Used by mach */
typedef __uint16_t __darwin_mode_t; /* [???] Some file attributes */
typedef __int64_t __darwin_off_t; /* [???] Used for file sizes */
typedef __int32_t __darwin_pid_t; /* [???] process and group IDs */
typedef _opaque_pthread_attr_t __darwin_pthread_attr_t;
typedef _opaque_pthread_cond_t __darwin_pthread_cond_t;
typedef _opaque_pthread_condattr_t __darwin_pthread_condattr_t;
typedef unsigned long __darwin_pthread_key_t; /* [???] Used for pthreads */
typedef _opaque_pthread_mutex_t __darwin_pthread_mutex_t;
typedef _opaque_pthread_mutexattr_t __darwin_pthread_mutexattr_t;
typedef _opaque_pthread_once_t __darwin_pthread_once_t;
typedef _opaque_pthread_rwlock_t __darwin_pthread_rwlock_t;
typedef _opaque_pthread_rwlockattr_t __darwin_pthread_rwlockattr_t;
typedef _opaque_pthread_t* __darwin_pthread_t;
typedef __uint32_t __darwin_sigset_t; /* [???] signal set */
typedef __int32_t __darwin_suseconds_t; /* [???] microseconds */
typedef __uint32_t __darwin_uid_t; /* [???] user IDs */
typedef __uint32_t __darwin_useconds_t; /* [???] microseconds */
typedef unsigned char[16] __darwin_uuid_t;
typedef char[37] __darwin_uuid_string_t;
typedef int __darwin_nl_item;
typedef int __darwin_wctrans_t;
typedef unsigned long __darwin_wctype_t;
struct accessx_descriptor {
	unsigned int ad_name_offset;
	int ad_flags;
	int[2] ad_pad;
};
typedef __darwin_gid_t gid_t;
typedef __darwin_intptr_t intptr_t;
typedef __darwin_off_t off_t;
typedef __darwin_pid_t pid_t;
/**
 * DO NOT REMOVE THIS COMMENT: fixincludes needs to see:<br>
 * _GCC_SIZE_T
 */
typedef __darwin_size_t size_t;
typedef __darwin_ssize_t ssize_t;
typedef __darwin_uid_t uid_t; /* user id 	*/
typedef __darwin_useconds_t useconds_t;
/// Original signature : <code>void _exit(int)</code>
void _exit(int int1);
/// Original signature : <code>int access(const char*, int)</code>
int access(const char* charPtr1, int int1);
/// Original signature : <code>int alarm(unsigned int)</code>
unsigned int alarm(unsigned int int1);
/// Original signature : <code>int chdir(const char*)</code>
int chdir(const char* charPtr1);
/// Original signature : <code>int chown(const char*, uid_t, gid_t)</code>
int chown(const char* charPtr1, uid_t uid_t1, gid_t gid_t1);
/// Original signature : <code>int close(int)</code>
int close(int int1);
/// Original signature : <code>int dup(int)</code>
int dup(int int1);
/// Original signature : <code>int dup2(int, int)</code>
int dup2(int int1, int int2);
/// Original signature : <code>int execl(const char*, const char*, null)</code>
int execl(const char* charPtr1, const char* charPtr2, ...);
/// Original signature : <code>int execle(const char*, const char*, null)</code>
int execle(const char* charPtr1, const char* charPtr2, ...);
/// Original signature : <code>int execlp(const char*, const char*, null)</code>
int execlp(const char* charPtr1, const char* charPtr2, ...);
/// Original signature : <code>int execv(const char*, const char**)</code>
int execv(const char* charPtr1, const char** charPtrPtr1);
/// Original signature : <code>int execve(const char*, const char**, const char**)</code>
int execve(const char* charPtr1, const char** charPtrPtr1, const char** charPtrPtr2);
/// Original signature : <code>int execvp(const char*, const char**)</code>
int execvp(const char* charPtr1, const char** charPtrPtr1);
/// Original signature : <code>pid_t fork()</code>
pid_t fork();
/// Original signature : <code>fpathconf(int, int)</code>
long fpathconf(int int1, int int2);
/// Original signature : <code>char* getcwd(char*, size_t)</code>
char* getcwd(char* charPtr1, size_t size_t1);
/// Original signature : <code>gid_t getegid()</code>
gid_t getegid();
/// Original signature : <code>uid_t geteuid()</code>
uid_t geteuid();
/// Original signature : <code>gid_t getgid()</code>
gid_t getgid();
/// Original signature : <code>int getgroups(int, gid_t[])</code>
int getgroups(int int1, gid_t gid_tArr1[]);
/// Original signature : <code>char* getlogin()</code>
char* getlogin();
/// Original signature : <code>pid_t getpgrp()</code>
pid_t getpgrp();
/// Original signature : <code>pid_t getpid()</code>
pid_t getpid();
/// Original signature : <code>pid_t getppid()</code>
pid_t getppid();
/// Original signature : <code>uid_t getuid()</code>
uid_t getuid();
/// Original signature : <code>int isatty(int)</code>
int isatty(int int1);
/// Original signature : <code>int link(const char*, const char*)</code>
int link(const char* charPtr1, const char* charPtr2);
/// Original signature : <code>off_t lseek(int, off_t, int)</code>
off_t lseek(int int1, off_t off_t1, int int2);
/// Original signature : <code>pathconf(const char*, int)</code>
long pathconf(const char* charPtr1, int int1);
/// Original signature : <code>int pause()</code>
int pause();
 (int[2]);
/// Original signature : <code>ssize_t read(int, void*, size_t)</code>
ssize_t read(int int1, void* voidPtr1, size_t size_t1);
/// Original signature : <code>int rmdir(const char*)</code>
int rmdir(const char* charPtr1);
/// Original signature : <code>int setgid(gid_t)</code>
int setgid(gid_t gid_t1);
/// Original signature : <code>int setpgid(pid_t, pid_t)</code>
int setpgid(pid_t pid_t1, pid_t pid_t2);
/// Original signature : <code>pid_t setsid()</code>
pid_t setsid();
/// Original signature : <code>int setuid(uid_t)</code>
int setuid(uid_t uid_t1);
/// Original signature : <code>int sleep(unsigned int)</code>
unsigned int sleep(unsigned int int1);
/// Original signature : <code>sysconf(int)</code>
long sysconf(int int1);
/// Original signature : <code>pid_t tcgetpgrp(int)</code>
pid_t tcgetpgrp(int int1);
/// Original signature : <code>int tcsetpgrp(int, pid_t)</code>
int tcsetpgrp(int int1, pid_t pid_t1);
/// Original signature : <code>char* ttyname(int)</code>
char* ttyname(int int1);
/// Original signature : <code>int ttyname_r(int, char*, size_t)</code>
int ttyname_r(int int1, char* charPtr1, size_t size_t1);
/// Original signature : <code>int unlink(const char*)</code>
int unlink(const char* charPtr1);
/// Original signature : <code>ssize_t write(int, const void*, size_t)</code>
ssize_t write(int int1, const void* voidPtr1, size_t size_t1);
/// Original signature : <code>size_t confstr(int, char*, size_t)</code>
size_t confstr(int int1, char* charPtr1, size_t size_t1);
/// Original signature : <code>[] null(const char*)</code>
[] (const char* charPtr1);
extern char* optarg; /* getopt(3) external variables */
extern int optind;
extern int opterr;
extern int optopt;
extern ""C"" {
/// Original signature : <code>void* brk(const void*)</code>
	void* brk(const void* voidPtr1);
	/// Original signature : <code>int chroot(const char*)</code>
	int chroot(const char* charPtr1);
	/// Original signature : <code>char* crypt(const char*, const char*)</code>
	char* crypt(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* ctermid(char*)</code>
	char* ctermid(char* charPtr1);
	/// Original signature : <code>void encrypt(char*, int)</code>
	void encrypt(char* charPtr1, int int1);
	/// Original signature : <code>int fchdir(int)</code>
	int fchdir(int int1);
	/// Original signature : <code>gethostid()</code>
	long gethostid();
	/// Original signature : <code>pid_t getpgid(pid_t)</code>
	pid_t getpgid(pid_t pid_t1);
	/// Original signature : <code>pid_t getsid(pid_t)</code>
	pid_t getsid(pid_t pid_t1);
	/// Original signature : <code>int getdtablesize()</code>
	int getdtablesize();
	/// Original signature : <code>int getpagesize()</code>
	int getpagesize();
	/// Original signature : <code>char* getpass(const char*)</code>
	char* getpass(const char* charPtr1);
	/**
	 * obsoleted by getcwd()<br>
	 * Original signature : <code>char* getwd(char*)</code><br>
	 * @param charPtr1 obsoleted by getcwd()
	 */
	char* getwd(char* charPtr1);
	/// Original signature : <code>int lchown(const char*, uid_t, gid_t)</code>
	int lchown(const char* charPtr1, uid_t uid_t1, gid_t gid_t1);
	/// Original signature : <code>int lockf(int, int, off_t)</code>
	int lockf(int int1, int int2, off_t off_t1);
	/// Original signature : <code>int nice(int)</code>
	int nice(int int1);
	/// Original signature : <code>ssize_t pread(int, void*, size_t, off_t)</code>
	ssize_t pread(int int1, void* voidPtr1, size_t size_t1, off_t off_t1);
	/// Original signature : <code>ssize_t pwrite(int, const void*, size_t, off_t)</code>
	ssize_t pwrite(int int1, const void* voidPtr1, size_t size_t1, off_t off_t1);
	/**
	 * Note that Issue 5 changed the argument as intprt_t,<br>
	 * but we keep it as int for binary compatability.<br>
	 * Original signature : <code>void* sbrk(int)</code>
	 */
	void* sbrk(int int1);
	/// Original signature : <code>pid_t setpgrp()</code>
	pid_t setpgrp();
	/// Original signature : <code>int setregid(gid_t, gid_t)</code>
	int setregid(gid_t gid_t1, gid_t gid_t2);
	/// Original signature : <code>int setreuid(uid_t, uid_t)</code>
	int setreuid(uid_t uid_t1, uid_t uid_t2);
	/// Original signature : <code>void swab(const void*, void*, ssize_t)</code>
	void swab(const void* voidPtr1, void* voidPtr2, ssize_t ssize_t1);
	/// Original signature : <code>void sync()</code>
	void sync();
	/// Original signature : <code>int truncate(const char*, off_t)</code>
	int truncate(const char* charPtr1, off_t off_t1);
	/// Original signature : <code>useconds_t ualarm(useconds_t, useconds_t)</code>
	useconds_t ualarm(useconds_t useconds_t1, useconds_t useconds_t2);
	/// Original signature : <code>int usleep(useconds_t)</code>
	int usleep(useconds_t useconds_t1);
	/// Original signature : <code>pid_t vfork()</code>
	pid_t vfork();
	/// Original signature : <code>int fsync(int)</code>
	int fsync(int int1);
	/// Original signature : <code>int ftruncate(int, off_t)</code>
	int ftruncate(int int1, off_t off_t1);
	/// Original signature : <code>int getlogin_r(char*, size_t)</code>
	int getlogin_r(char* charPtr1, size_t size_t1);
}
extern ""C"" {
/// Original signature : <code>int fchown(int, uid_t, gid_t)</code>
	int fchown(int int1, uid_t uid_t1, gid_t gid_t1);
	/// Original signature : <code>int gethostname(char*, size_t)</code>
	int gethostname(char* charPtr1, size_t size_t1);
	/// Original signature : <code>ssize_t readlink(const char*, char*, size_t)</code>
	ssize_t readlink(const char* charPtr1, char* charPtr2, size_t size_t1);
	/// Original signature : <code>int setegid(gid_t)</code>
	int setegid(gid_t gid_t1);
	/// Original signature : <code>int seteuid(uid_t)</code>
	int seteuid(uid_t uid_t1);
	/// Original signature : <code>int symlink(const char*, const char*)</code>
	int symlink(const char* charPtr1, const char* charPtr2);
}
struct timespec {
	__darwin_time_t tv_sec;
	long tv_nsec;
};
struct timeval {
	__darwin_time_t tv_sec; /* seconds */
	__darwin_suseconds_t tv_usec; /* and microseconds */
};
extern ""C"" {
typedef struct fd_set {
		__int32_t[(((1024) % (sizeof(__int32_t) * 8)) == 0)] fds_bits;
	} fd_set;
}
/**
 * This inline avoids argument side-effect issues with FD_ISSET()<br>
 * Original signature : <code>int __darwin_fd_isset(int, fd_set*)</code>
 */
static inline int __darwin_fd_isset(int _n, fd_set* _p) {
	return (_p->fds_bits[_n / (sizeof(__int32_t) * 8)] & (1 << (_n % (sizeof(__int32_t) * 8))));
}
typedef __darwin_time_t time_t;
typedef __darwin_suseconds_t suseconds_t;
typedef __darwin_sigset_t sigset_t;
extern ""C"" {
/// Original signature : <code>int pselect(int, fd_set*, fd_set*, fd_set*, timespec*, const sigset_t*)</code>
	int pselect(int int1, fd_set* fd_setPtr1, fd_set* fd_setPtr2, fd_set* fd_setPtr3, timespec* timespecPtr1, const sigset_t* sigset_tPtr1);
	/// Original signature : <code>int select(int, fd_set*, fd_set*, fd_set*, timeval*)</code>
	int select(int int1, fd_set* fd_setPtr1, fd_set* fd_setPtr2, fd_set* fd_setPtr3, timeval* timevalPtr1);
}
typedef __darwin_dev_t dev_t;
typedef __darwin_mode_t mode_t;
typedef __darwin_uuid_t uuid_t;
extern ""C"" {
/// Original signature : <code>void _Exit(int)</code>
	void _Exit(int int1);
	/// Original signature : <code>int accessx_np(accessx_descriptor*, size_t, int*, uid_t)</code>
	int accessx_np(accessx_descriptor* accessx_descriptorPtr1, size_t size_t1, int* intPtr1, uid_t uid_t1);
	/// Original signature : <code>int acct(const char*)</code>
	int acct(const char* charPtr1);
	/// Original signature : <code>int add_profil(char*, size_t, unsigned long, unsigned int)</code>
	int add_profil(char* charPtr1, size_t size_t1, unsigned long u1, unsigned int int1);
	/// Original signature : <code>void endusershell()</code>
	void endusershell();
	/// Original signature : <code>int execvP(const char*, const char*, const char**)</code>
	int execvP(const char* charPtr1, const char* charPtr2, const char** charPtrPtr1);
	/// Original signature : <code>char* fflagstostr(unsigned long)</code>
	char* fflagstostr(unsigned long u1);
	/// Original signature : <code>int getdomainname(char*, int)</code>
	int getdomainname(char* charPtr1, int int1);
	/// Original signature : <code>int getgrouplist(const char*, int, int*, int*)</code>
	int getgrouplist(const char* charPtr1, int int1, int* intPtr1, int* intPtr2);
	/// Original signature : <code>int gethostuuid(uuid_t, timespec*)</code>
	int gethostuuid(uuid_t uuid_t1, timespec* timespecPtr1);
	/// Original signature : <code>mode_t getmode(const void*, mode_t)</code>
	mode_t getmode(const void* voidPtr1, mode_t mode_t1);
	/// Original signature : <code>int getpeereid(int, uid_t*, gid_t*)</code>
	int getpeereid(int int1, uid_t* uid_tPtr1, gid_t* gid_tPtr1);
	/// Original signature : <code>int getsgroups_np(int*, uuid_t)</code>
	int getsgroups_np(int* intPtr1, uuid_t uuid_t1);
	/// Original signature : <code>char* getusershell()</code>
	char* getusershell();
	/// Original signature : <code>int getwgroups_np(int*, uuid_t)</code>
	int getwgroups_np(int* intPtr1, uuid_t uuid_t1);
	/// Original signature : <code>int initgroups(const char*, int)</code>
	int initgroups(const char* charPtr1, int int1);
	/// Original signature : <code>int iruserok(unsigned long, int, const char*, const char*)</code>
	int iruserok(unsigned long u1, int int1, const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>int iruserok_sa(const void*, int, int, const char*, const char*)</code>
	int iruserok_sa(const void* voidPtr1, int int1, int int2, const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>int issetugid()</code>
	int issetugid();
	/// Original signature : <code>char* mkdtemp(char*)</code>
	char* mkdtemp(char* charPtr1);
	/// Original signature : <code>int mknod(const char*, mode_t, dev_t)</code>
	int mknod(const char* charPtr1, mode_t mode_t1, dev_t dev_t1);
	/// Original signature : <code>int mkstemp(char*)</code>
	int mkstemp(char* charPtr1);
	/// Original signature : <code>int mkstemps(char*, int)</code>
	int mkstemps(char* charPtr1, int int1);
	/// Original signature : <code>char* mktemp(char*)</code>
	char* mktemp(char* charPtr1);
	/// Original signature : <code>int nfssvc(int, void*)</code>
	int nfssvc(int int1, void* voidPtr1);
	/// Original signature : <code>int profil(char*, size_t, unsigned long, unsigned int)</code>
	int profil(char* charPtr1, size_t size_t1, unsigned long u1, unsigned int int1);
	/// Original signature : <code>int pthread_setugid_np(uid_t, gid_t)</code>
	int pthread_setugid_np(uid_t uid_t1, gid_t gid_t1);
	/// Original signature : <code>int pthread_getugid_np(uid_t*, gid_t*)</code>
	int pthread_getugid_np(uid_t* uid_tPtr1, gid_t* gid_tPtr1);
	/// Original signature : <code>int rcmd(char**, int, const char*, const char*, const char*, int*)</code>
	int rcmd(char** charPtrPtr1, int int1, const char* charPtr1, const char* charPtr2, const char* charPtr3, int* intPtr1);
	/// Original signature : <code>int rcmd_af(char**, int, const char*, const char*, const char*, int*, int)</code>
	int rcmd_af(char** charPtrPtr1, int int1, const char* charPtr1, const char* charPtr2, const char* charPtr3, int* intPtr1, int int2);
	/// Original signature : <code>int reboot(int)</code>
	int reboot(int int1);
	/// Original signature : <code>int revoke(const char*)</code>
	int revoke(const char* charPtr1);
	/// Original signature : <code>int rresvport(int*)</code>
	int rresvport(int* intPtr1);
	/// Original signature : <code>int rresvport_af(int*, int)</code>
	int rresvport_af(int* intPtr1, int int1);
	/// Original signature : <code>int ruserok(const char*, int, const char*, const char*)</code>
	int ruserok(const char* charPtr1, int int1, const char* charPtr2, const char* charPtr3);
	/// Original signature : <code>int setdomainname(const char*, int)</code>
	int setdomainname(const char* charPtr1, int int1);
	/// Original signature : <code>int setgroups(int, const gid_t*)</code>
	int setgroups(int int1, const gid_t* gid_tPtr1);
	/// Original signature : <code>void sethostid(long)</code>
	void sethostid(long l1);
	/// Original signature : <code>int sethostname(const char*, int)</code>
	int sethostname(const char* charPtr1, int int1);
	/// Original signature : <code>void setkey(const char*)</code>
	void setkey(const char* charPtr1);
	/// Original signature : <code>int setlogin(const char*)</code>
	int setlogin(const char* charPtr1);
	/// Original signature : <code>void* setmode(const char*)</code>
	void* setmode(const char* charPtr1);
	/// Original signature : <code>int setrgid(gid_t)</code>
	int setrgid(gid_t gid_t1);
	/// Original signature : <code>int setruid(uid_t)</code>
	int setruid(uid_t uid_t1);
	/// Original signature : <code>int setsgroups_np(int, const uuid_t)</code>
	int setsgroups_np(int int1, const uuid_t uuid_t1);
	/// Original signature : <code>void setusershell()</code>
	void setusershell();
	/// Original signature : <code>int setwgroups_np(int, const uuid_t)</code>
	int setwgroups_np(int int1, const uuid_t uuid_t1);
	/// Original signature : <code>int strtofflags(char**, unsigned long*, unsigned long*)</code>
	int strtofflags(char** charPtrPtr1, unsigned long* uPtr1, unsigned long* uPtr2);
	/// Original signature : <code>int swapon(const char*)</code>
	int swapon(const char* charPtr1);
	/// Original signature : <code>int syscall(int, null)</code>
	int syscall(int int1, ...);
	/// Original signature : <code>int ttyslot()</code>
	int ttyslot();
	/// Original signature : <code>int undelete(const char*)</code>
	int undelete(const char* charPtr1);
	/// Original signature : <code>int unwhiteout(const char*)</code>
	int unwhiteout(const char* charPtr1);
	/// Original signature : <code>void* valloc(size_t)</code>
	void* valloc(size_t size_t1);
	extern char* suboptarg; /* getsubopt(3) external variable */
	/// Original signature : <code>int getsubopt(char**, const char**, char**)</code>
	int getsubopt(char** charPtrPtr1, const char** charPtrPtr2, char** charPtrPtr3);
	/// Original signature : <code>int fgetattrlist(int, void*, void*, size_t, unsigned long)</code>
	int fgetattrlist(int int1, void* voidPtr1, void* voidPtr2, size_t size_t1, unsigned long u1);
	/// Original signature : <code>int fsetattrlist(int, void*, void*, size_t, unsigned long)</code>
	int fsetattrlist(int int1, void* voidPtr1, void* voidPtr2, size_t size_t1, unsigned long u1);
	/// Original signature : <code>int getattrlist(const char*, void*, void*, size_t, unsigned long)</code>
	int getattrlist(const char* charPtr1, void* voidPtr1, void* voidPtr2, size_t size_t1, unsigned long u1);
	/// Original signature : <code>int setattrlist(const char*, void*, void*, size_t, unsigned long)</code>
	int setattrlist(const char* charPtr1, void* voidPtr1, void* voidPtr2, size_t size_t1, unsigned long u1);
	/// Original signature : <code>int exchangedata(const char*, const char*, unsigned long)</code>
	int exchangedata(const char* charPtr1, const char* charPtr2, unsigned long u1);
	/// Original signature : <code>int getdirentriesattr(int, void*, void*, size_t, unsigned long*, unsigned long*, unsigned long*, unsigned long)</code>
	int getdirentriesattr(int int1, void* voidPtr1, void* voidPtr2, size_t size_t1, unsigned long* uPtr1, unsigned long* uPtr2, unsigned long* uPtr3, unsigned long u1);
	struct fssearchblock;
	struct searchstate;
	/// Original signature : <code>int searchfs(const char*, fssearchblock*, unsigned long*, unsigned int, unsigned int, searchstate*)</code>
	int searchfs(const char* charPtr1, fssearchblock* fssearchblockPtr1, unsigned long* uPtr1, unsigned int int1, unsigned int int2, searchstate* searchstatePtr1);
	/// Original signature : <code>int fsctl(const char*, unsigned long, void*, unsigned int)</code>
	int fsctl(const char* charPtr1, unsigned long u1, void* voidPtr1, unsigned int int1);
	/// Original signature : <code>int ffsctl(int, unsigned long, void*, unsigned int)</code>
	int ffsctl(int int1, unsigned long u1, void* voidPtr1, unsigned int int2);
	extern int optreset;
}
extern ""C"" {
/// Original signature : <code>void __dtrace_probe$cxa_runtime$cxa_exception_rethrow$v1()</code>
	extern void __dtrace_probe$cxa_runtime$cxa_exception_rethrow$v1();
	/// Original signature : <code>int __dtrace_isenabled$cxa_runtime$cxa_exception_rethrow$v1()</code>
	extern int __dtrace_isenabled$cxa_runtime$cxa_exception_rethrow$v1();
	/// Original signature : <code>void __dtrace_probe$cxa_runtime$cxa_exception_throw$v1$766f6964202a(void*)</code>
	extern void __dtrace_probe$cxa_runtime$cxa_exception_throw$v1$766f6964202a(void* voidPtr1);
	/// Original signature : <code>int __dtrace_isenabled$cxa_runtime$cxa_exception_throw$v1()</code>
	extern int __dtrace_isenabled$cxa_runtime$cxa_exception_throw$v1();
}
namespace std {

}
struct lconv {
	char* decimal_point;
	char* thousands_sep;
	char* grouping;
	char* int_curr_symbol;
	char* currency_symbol;
	char* mon_decimal_point;
	char* mon_thousands_sep;
	char* mon_grouping;
	char* positive_sign;
	char* negative_sign;
	char int_frac_digits;
	char frac_digits;
	char p_cs_precedes;
	char p_sep_by_space;
	char n_cs_precedes;
	char n_sep_by_space;
	char p_sign_posn;
	char n_sign_posn;
	char int_p_cs_precedes;
	char int_n_cs_precedes;
	char int_p_sep_by_space;
	char int_n_sep_by_space;
	char int_p_sign_posn;
	char int_n_sign_posn;
};
extern ""C"" {
/// Original signature : <code>lconv* localeconv()</code>
	lconv* localeconv();
}
extern ""C"" {
/// Original signature : <code>char* setlocale(int, const char*)</code>
	char* setlocale(int int1, const char* charPtr1);
}
namespace std {
using::lconv;
	using::setlocale;
	using::localeconv;
}
typedef __darwin_ptrdiff_t ptrdiff_t;
typedef __darwin_wint_t wint_t;
namespace std {
using::ptrdiff_t;
	using::size_t;
}
extern ""C"" {
/// Original signature : <code>void* memchr(const void*, int, size_t)</code>
	void* memchr(const void* voidPtr1, int int1, size_t size_t1);
	/// Original signature : <code>int memcmp(const void*, const void*, size_t)</code>
	int memcmp(const void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void* memcpy(void*, const void*, size_t)</code>
	void* memcpy(void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void* memmove(void*, const void*, size_t)</code>
	void* memmove(void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void* memset(void*, int, size_t)</code>
	void* memset(void* voidPtr1, int int1, size_t size_t1);
	/// Original signature : <code>char* strcat(char*, const char*)</code>
	char* strcat(char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strchr(const char*, int)</code>
	char* strchr(const char* charPtr1, int int1);
	/// Original signature : <code>int strcmp(const char*, const char*)</code>
	int strcmp(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>int strcoll(const char*, const char*)</code>
	int strcoll(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strcpy(char*, const char*)</code>
	char* strcpy(char* charPtr1, const char* charPtr2);
	/// Original signature : <code>size_t strcspn(const char*, const char*)</code>
	size_t strcspn(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strerror(int)</code>
	char* strerror(int int1);
	/// Original signature : <code>size_t strlen(const char*)</code>
	size_t strlen(const char* charPtr1);
	/// Original signature : <code>char* strncat(char*, const char*, size_t)</code>
	char* strncat(char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>int strncmp(const char*, const char*, size_t)</code>
	int strncmp(const char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>char* strncpy(char*, const char*, size_t)</code>
	char* strncpy(char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>char* strpbrk(const char*, const char*)</code>
	char* strpbrk(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strrchr(const char*, int)</code>
	char* strrchr(const char* charPtr1, int int1);
	/// Original signature : <code>size_t strspn(const char*, const char*)</code>
	size_t strspn(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strstr(const char*, const char*)</code>
	char* strstr(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strtok(char*, const char*)</code>
	char* strtok(char* charPtr1, const char* charPtr2);
	/// Original signature : <code>size_t strxfrm(char*, const char*, size_t)</code>
	size_t strxfrm(char* charPtr1, const char* charPtr2, size_t size_t1);
}
extern ""C"" {
/// Original signature : <code>char* strtok_r(char*, const char*, char**)</code>
	char* strtok_r(char* charPtr1, const char* charPtr2, char** charPtrPtr1);
}
extern ""C"" {
/// Original signature : <code>int strerror_r(int, char*, size_t)</code>
	int strerror_r(int int1, char* charPtr1, size_t size_t1);
	/// Original signature : <code>char* strdup(const char*)</code>
	char* strdup(const char* charPtr1);
	/// Original signature : <code>void* memccpy(void*, const void*, int, size_t)</code>
	void* memccpy(void* voidPtr1, const void* voidPtr2, int int1, size_t size_t1);
}
extern ""C"" {
/// Original signature : <code>char* stpcpy(char*, const char*)</code>
	char* stpcpy(char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* stpncpy(char*, const char*, size_t)</code>
	char* stpncpy(char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>char* strndup(const char*, size_t)</code>
	char* strndup(const char* charPtr1, size_t size_t1);
	/// Original signature : <code>size_t strnlen(const char*, size_t)</code>
	size_t strnlen(const char* charPtr1, size_t size_t1);
	/// Original signature : <code>char* strsignal(int)</code>
	char* strsignal(int sig);
}
extern ""C"" {
/// Original signature : <code>void* memmem(const void*, size_t, const void*, size_t)</code>
	void* memmem(const void* voidPtr1, size_t size_t1, const void* voidPtr2, size_t size_t2);
	/// Original signature : <code>void memset_pattern4(void*, const void*, size_t)</code>
	void memset_pattern4(void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void memset_pattern8(void*, const void*, size_t)</code>
	void memset_pattern8(void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void memset_pattern16(void*, const void*, size_t)</code>
	void memset_pattern16(void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>char* strcasestr(const char*, const char*)</code>
	char* strcasestr(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>char* strnstr(const char*, const char*, size_t)</code>
	char* strnstr(const char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>size_t strlcat(char*, const char*, size_t)</code>
	size_t strlcat(char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>size_t strlcpy(char*, const char*, size_t)</code>
	size_t strlcpy(char* charPtr1, const char* charPtr2, size_t size_t1);
	/// Original signature : <code>void strmode(int, char*)</code>
	void strmode(int int1, char* charPtr1);
	/// Original signature : <code>char* strsep(char**, const char*)</code>
	char* strsep(char** charPtrPtr1, const char* charPtr1);
	/**
	 * SUS places swab() in unistd.h.  It is listed here for source compatibility<br>
	 * Original signature : <code>void swab(const void*, void*, ssize_t)</code>
	 */
	void swab(const void* voidPtr1, void* voidPtr2, ssize_t ssize_t1);
}
extern ""C"" {
/// Original signature : <code>int bcmp(const void*, const void*, size_t)</code>
	int bcmp(const void* voidPtr1, const void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void bcopy(const void*, void*, size_t)</code>
	void bcopy(const void* voidPtr1, void* voidPtr2, size_t size_t1);
	/// Original signature : <code>void bzero(void*, size_t)</code>
	void bzero(void* voidPtr1, size_t size_t1);
	/// Original signature : <code>char* index(const char*, int)</code>
	char* index(const char* charPtr1, int int1);
	/// Original signature : <code>char* rindex(const char*, int)</code>
	char* rindex(const char* charPtr1, int int1);
	/// Original signature : <code>int ffs(int)</code>
	int ffs(int int1);
	/// Original signature : <code>int strcasecmp(const char*, const char*)</code>
	int strcasecmp(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>int strncasecmp(const char*, const char*, size_t)</code>
	int strncasecmp(const char* charPtr1, const char* charPtr2, size_t size_t1);
}
extern ""C"" {
/// Original signature : <code>int ffsl(long)</code>
	int ffsl(long l1);
	/// Original signature : <code>int fls(int)</code>
	int fls(int int1);
	/// Original signature : <code>int flsl(long)</code>
	int flsl(long l1);
}
namespace std {
using::memcpy;
	using::memmove;
	using::strcpy;
	using::strncpy;
	using::strcat;
	using::strncat;
	using::memcmp;
	using::strcmp;
	using::strcoll;
	using::strncmp;
	using::strxfrm;
	using::strcspn;
	using::strspn;
	using::strtok;
	using::memset;
	using::strerror;
	using::strlen;
	using::memchr;
	/// Original signature : <code>void* memchr(void*, int, size_t)</code>
	inline void* memchr(void* __p, int __c, size_t __n) {
		return memchr(const_cast<const void* >(__p), __c, __n);
	}
	using::strchr;
	/// Original signature : <code>char* strchr(char*, int)</code>
	inline char* strchr(char* __s1, int __n) {
		return __builtin_strchr(const_cast<const char* >(__s1), __n);
	}
	using::strpbrk;
	/// Original signature : <code>char* strpbrk(char*, const char*)</code>
	inline char* strpbrk(char* __s1, const char* __s2) {
		return __builtin_strpbrk(const_cast<const char* >(__s1), __s2);
	}
	using::strrchr;
	/// Original signature : <code>char* strrchr(char*, int)</code>
	inline char* strrchr(char* __s1, int __n) {
		return __builtin_strrchr(const_cast<const char* >(__s1), __n);
	}
	using::strstr;
	/// Original signature : <code>char* strstr(char*, const char*)</code>
	inline char* strstr(char* __s1, const char* __s2) {
		return __builtin_strstr(const_cast<const char* >(__s1), __s2);
	}
}
/**
 * DO NOT REMOVE THIS COMMENT: fixincludes needs to see:<br>
 * __gnuc_va_list and include <stdarg.h>
 */
typedef __darwin_va_list va_list;
typedef __darwin_off_t fpos_t;
/// stdio buffers
struct __sbuf {
	unsigned char* _base;
	int _size;
};
/// hold a buncha junk that would grow the ABI
struct __sFILEX;
/**
 * stdio state variables.<br>
 * * The following always hold:<br>
 * *	if (_flags&(__SLBF|__SWR)) == (__SLBF|__SWR),<br>
 * 	_lbfsize is -_bf._size, else _lbfsize is 0<br>
 * if _flags&__SRD, _w is 0<br>
 * if _flags&__SWR, _r is 0<br>
 * * This ensures that the getc and putc macros (or inline functions) never<br>
 * try to write or read from a file that is in `read' or `write' mode.<br>
 * (Moreover, they can, and do, automatically switch from read mode to<br>
 * write mode, and back, on "r+" and "w+" files.)<br>
 * * _lbfsize is used only to make the inline line-buffered output stream<br>
 * code as compact as possible.<br>
 * * _ub, _up, and _ur are used when ungetc() pushes back more characters<br>
 * than fit in the current _bf, or when ungetc() pushes back a character<br>
 * that does not match the previous one in _bf.  When this happens,<br>
 * _ub._base becomes non-nil (i.e., a stream has ungetc() data iff<br>
 * _ub._base!=NULL) and _up and _ur save the current values of _p and _r.<br>
 * * NB: see WARNING above before changing the layout of this structure!
 */
typedef struct FILE {
	unsigned char* _p; /* current position in (some) buffer */
	int _r; /* read space left for getc() */
	int _w; /* write space left for putc() */
	short _flags; /* flags, below; this FILE is free if 0 */
	short _file; /* fileno, if Unix descriptor, else -1 */
	__sbuf _bf; /* the buffer (at least 1 byte, if !NULL) */
	int _lbfsize; /* 0 or -_bf._size, for inline putc */
	/// operations
	void* _cookie; /* cookie passed to io functions */
	_close_callback _close;
	_read_callback _read;
	_seek_callback _seek;
	_write_callback _write;
	/// separate buffer for long sequences of ungetc()
	__sbuf _ub; /* ungetc buffer */
	__sFILEX* _extra; /* additions to FILE to not break ABI */
	int _ur; /* saved _r when _r is counting ungetc data */
	/// tricks to meet minimum requirements even when malloc() fails
	unsigned char[3] _ubuf; /* guarantee an ungetc() buffer */
	unsigned char[1] _nbuf; /* guarantee a getc() buffer */
	/// separate buffer for fgetln() when line crosses buffer boundary
	__sbuf _lb; /* buffer for fgetln() */
	/// Unix stdio files get aligned to block boundaries on fseek()
	int _blksize; /* stat.st_blksize (may be != _bf._size) */
	fpos_t _offset; /* current lseek offset (see WARNING) */
	typedef int (*_close_callback)(void* voidPtr1);
	typedef int (*_read_callback)(void* voidPtr1, char* charPtr1, int int1);
	typedef fpos_t (*_seek_callback)(void* voidPtr1, fpos_t fpos_t1, int int1);
	typedef int (*_write_callback)(void* voidPtr1, const char* charPtr1, int int1);
} __sFILE;
extern ""C"" {
extern FILE* __stdinp;
	extern FILE* __stdoutp;
	extern FILE* __stderrp;
}
extern ""C"" {
/// Original signature : <code>void clearerr(FILE*)</code>
	void clearerr(FILE* FILEPtr1);
	/// Original signature : <code>int fclose(FILE*)</code>
	int fclose(FILE* FILEPtr1);
	/// Original signature : <code>int feof(FILE*)</code>
	int feof(FILE* FILEPtr1);
	/// Original signature : <code>int ferror(FILE*)</code>
	int ferror(FILE* FILEPtr1);
	/// Original signature : <code>int fflush(FILE*)</code>
	int fflush(FILE* FILEPtr1);
	/// Original signature : <code>int fgetc(FILE*)</code>
	int fgetc(FILE* FILEPtr1);
	/// Original signature : <code>int fgetpos(FILE*, fpos_t*)</code>
	int fgetpos(FILE* FILEPtr1, fpos_t* fpos_tPtr1);
	/// Original signature : <code>char* fgets(char*, int, FILE*)</code>
	char* fgets(char* charPtr1, int int1, FILE* FILEPtr1);
	/// Original signature : <code>FILE* fopen(const char*, const char*)</code>
	FILE* fopen(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>int fprintf(FILE*, const char*, null)</code>
	int fprintf(FILE* FILEPtr1, const char* charPtr1, ...);
	/// Original signature : <code>int fputc(int, FILE*)</code>
	int fputc(int int1, FILE* FILEPtr1);
	/// Original signature : <code>int fputs(const char*, FILE*)</code>
	int fputs(const char* charPtr1, FILE* FILEPtr1);
	/// Original signature : <code>size_t fread(void*, size_t, size_t, FILE*)</code>
	size_t fread(void* voidPtr1, size_t size_t1, size_t size_t2, FILE* FILEPtr1);
	/// Original signature : <code>FILE* freopen(const char*, const char*, FILE*)</code>
	FILE* freopen(const char* charPtr1, const char* charPtr2, FILE* FILEPtr1);
	/// Original signature : <code>int fscanf(FILE*, const char*, null)</code>
	int fscanf(FILE* FILEPtr1, const char* charPtr1, ...);
	/// Original signature : <code>int fseek(FILE*, long, int)</code>
	int fseek(FILE* FILEPtr1, long l1, int int1);
	/// Original signature : <code>int fsetpos(FILE*, const fpos_t*)</code>
	int fsetpos(FILE* FILEPtr1, const fpos_t* fpos_tPtr1);
	/// Original signature : <code>ftell(FILE*)</code>
	long ftell(FILE* FILEPtr1);
	/// Original signature : <code>size_t fwrite(const void*, size_t, size_t, FILE*)</code>
	size_t fwrite(const void* voidPtr1, size_t size_t1, size_t size_t2, FILE* FILEPtr1);
	/// Original signature : <code>int getc(FILE*)</code>
	int getc(FILE* FILEPtr1);
	/// Original signature : <code>int getchar()</code>
	int getchar();
	/// Original signature : <code>char* gets(char*)</code>
	char* gets(char* charPtr1);
	/// Original signature : <code>void perror(const char*)</code>
	void perror(const char* charPtr1);
	/// Original signature : <code>int printf(const char*, null)</code>
	int printf(const char* charPtr1, ...);
	/// Original signature : <code>int putc(int, FILE*)</code>
	int putc(int int1, FILE* FILEPtr1);
	/// Original signature : <code>int putchar(int)</code>
	int putchar(int int1);
	/// Original signature : <code>int puts(const char*)</code>
	int puts(const char* charPtr1);
	/// Original signature : <code>int remove(const char*)</code>
	int remove(const char* charPtr1);
	/// Original signature : <code>int rename(const char*, const char*)</code>
	int rename(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>void rewind(FILE*)</code>
	void rewind(FILE* FILEPtr1);
	/// Original signature : <code>int scanf(const char*, null)</code>
	int scanf(const char* charPtr1, ...);
	/// Original signature : <code>void setbuf(FILE*, char*)</code>
	void setbuf(FILE* FILEPtr1, char* charPtr1);
	/// Original signature : <code>int setvbuf(FILE*, char*, int, size_t)</code>
	int setvbuf(FILE* FILEPtr1, char* charPtr1, int int1, size_t size_t1);
	/// Original signature : <code>int sprintf(char*, const char*, null)</code>
	int sprintf(char* charPtr1, const char* charPtr2, ...);
	/// Original signature : <code>int sscanf(const char*, const char*, null)</code>
	int sscanf(const char* charPtr1, const char* charPtr2, ...);
	/// Original signature : <code>FILE* tmpfile()</code>
	FILE* tmpfile();
	/// Original signature : <code>char* tmpnam(char*)</code>
	char* tmpnam(char* charPtr1);
	/// Original signature : <code>int ungetc(int, FILE*)</code>
	int ungetc(int int1, FILE* FILEPtr1);
	/// Original signature : <code>int vfprintf(FILE*, const char*, va_list)</code>
	int vfprintf(FILE* FILEPtr1, const char* charPtr1, va_list va_list1);
	/// Original signature : <code>int vprintf(const char*, va_list)</code>
	int vprintf(const char* charPtr1, va_list va_list1);
	/// Original signature : <code>int vsprintf(char*, const char*, va_list)</code>
	int vsprintf(char* charPtr1, const char* charPtr2, va_list va_list1);
}
extern ""C"" {
/// Original signature : <code>FILE* fdopen(int, const char*)</code>
	FILE* fdopen(int int1, const char* charPtr1);
	/// Original signature : <code>int fileno(FILE*)</code>
	int fileno(FILE* FILEPtr1);
}
extern ""C"" {
/// Original signature : <code>int pclose(FILE*)</code>
	int pclose(FILE* FILEPtr1);
	/// Original signature : <code>FILE* popen(const char*, const char*)</code>
	FILE* popen(const char* charPtr1, const char* charPtr2);
}
extern ""C"" {
/// Original signature : <code>int __srget(FILE*)</code>
	int __srget(FILE* FILEPtr1);
	/// Original signature : <code>int __svfscanf(FILE*, const char*, va_list)</code>
	int __svfscanf(FILE* FILEPtr1, const char* charPtr1, va_list va_list1);
	/// Original signature : <code>int __swbuf(int, FILE*)</code>
	int __swbuf(int int1, FILE* FILEPtr1);
}
/// Original signature : <code>int __sputc(int, FILE*)</code>
static inline int __sputc(int _c, FILE* _p) {
	if (--_p->_w >= 0 || (_p->_w >= _p->_lbfsize && (char)_c != '\n')) 
		return (*++_p->_p = _c);
	else 
		return (__swbuf(_c, _p));
}
extern ""C"" {
/// Original signature : <code>void flockfile(FILE*)</code>
	void flockfile(FILE* FILEPtr1);
	/// Original signature : <code>int ftrylockfile(FILE*)</code>
	int ftrylockfile(FILE* FILEPtr1);
	/// Original signature : <code>void funlockfile(FILE*)</code>
	void funlockfile(FILE* FILEPtr1);
	/// Original signature : <code>int getc_unlocked(FILE*)</code>
	int getc_unlocked(FILE* FILEPtr1);
	/// Original signature : <code>int getchar_unlocked()</code>
	int getchar_unlocked();
	/// Original signature : <code>int putc_unlocked(int, FILE*)</code>
	int putc_unlocked(int int1, FILE* FILEPtr1);
	/// Original signature : <code>int putchar_unlocked(int)</code>
	int putchar_unlocked(int int1);
	/// Original signature : <code>int getw(FILE*)</code>
	int getw(FILE* FILEPtr1);
	/// Original signature : <code>int putw(int, FILE*)</code>
	int putw(int int1, FILE* FILEPtr1);
	/// Original signature : <code>char* tempnam(const char*, const char*)</code>
	char* tempnam(const char* charPtr1, const char* charPtr2);
}
extern ""C"" {
/// Original signature : <code>int fseeko(FILE*, off_t, int)</code>
	int fseeko(FILE* FILEPtr1, off_t off_t1, int int1);
	/// Original signature : <code>off_t ftello(FILE*)</code>
	off_t ftello(FILE* FILEPtr1);
}
extern ""C"" {
/// Original signature : <code>int snprintf(char*, size_t, const char*, null)</code>
	int snprintf(char* charPtr1, size_t size_t1, const char* charPtr2, ...);
	/// Original signature : <code>int vfscanf(FILE*, const char*, va_list)</code>
	int vfscanf(FILE* FILEPtr1, const char* charPtr1, va_list va_list1);
	/// Original signature : <code>int vscanf(const char*, va_list)</code>
	int vscanf(const char* charPtr1, va_list va_list1);
	/// Original signature : <code>int vsnprintf(char*, size_t, const char*, va_list)</code>
	int vsnprintf(char* charPtr1, size_t size_t1, const char* charPtr2, va_list va_list1);
	/// Original signature : <code>int vsscanf(const char*, const char*, va_list)</code>
	int vsscanf(const char* charPtr1, const char* charPtr2, va_list va_list1);
}
extern ""C"" {
/// Original signature : <code>int dprintf(int, const char*, null)</code>
	int dprintf(int int1, const char* charPtr1, ...);
	/// Original signature : <code>int vdprintf(int, const char*, va_list)</code>
	int vdprintf(int int1, const char* charPtr1, va_list va_list1);
	/// Original signature : <code>ssize_t getdelim(char**, size_t*, int, FILE*)</code>
	ssize_t getdelim(char** charPtrPtr1, size_t* size_tPtr1, int int1, FILE* FILEPtr1);
	/// Original signature : <code>ssize_t getline(char**, size_t*, FILE*)</code>
	ssize_t getline(char** charPtrPtr1, size_t* size_tPtr1, FILE* FILEPtr1);
}
extern ""C"" {
extern const int sys_nerr; /* perror(3) external variables */
	extern const char*[] sys_errlist;
	/// Original signature : <code>int asprintf(char**, const char*, null)</code>
	int asprintf(char** charPtrPtr1, const char* charPtr1, ...);
	/// Original signature : <code>char* ctermid_r(char*)</code>
	char* ctermid_r(char* charPtr1);
	/// Original signature : <code>char* fgetln(FILE*, size_t*)</code>
	char* fgetln(FILE* FILEPtr1, size_t* size_tPtr1);
	/// Original signature : <code>char* fmtcheck(const char*, const char*)</code>
	const char* fmtcheck(const char* charPtr1, const char* charPtr2);
	/// Original signature : <code>int fpurge(FILE*)</code>
	int fpurge(FILE* FILEPtr1);
	/// Original signature : <code>void setbuffer(FILE*, char*, int)</code>
	void setbuffer(FILE* FILEPtr1, char* charPtr1, int int1);
	/// Original signature : <code>int setlinebuf(FILE*)</code>
	int setlinebuf(FILE* FILEPtr1);
	/// Original signature : <code>int vasprintf(char**, const char*, va_list)</code>
	int vasprintf(char** charPtrPtr1, const char* charPtr1, va_list va_list1);
	/// Original signature : <code>FILE* zopen(const char*, const char*, int)</code>
	FILE* zopen(const char* charPtr1, const char* charPtr2, int int1);
	/**
	 * Stdio function-access interface.<br>
	 * Original signature : <code>FILE* funopen(const void*, funopen_arg1_callback, funopen_arg2_callback, funopen_arg3_callback, funopen_arg4_callback)</code>
	 */
	FILE* funopen(const void* voidPtr1, funopen_arg1_callback arg1, funopen_arg2_callback arg2, funopen_arg3_callback arg3, funopen_arg4_callback arg4);
	typedef int (*funopen_arg1_callback)(void* voidPtr1, char* charPtr1, int int1);
	typedef int (*funopen_arg2_callback)(void* voidPtr1, const char* charPtr1, int int1);
	typedef fpos_t (*funopen_arg3_callback)(void* voidPtr1, fpos_t fpos_t1, int int1);
	typedef int (*funopen_arg4_callback)(void* voidPtr1);
}
namespace std {
using::FILE;
	using::fpos_t;
	using::clearerr;
	using::fclose;
	using::feof;
	using::ferror;
	using::fflush;
	using::fgetc;
	using::fgetpos;
	using::fgets;
	using::fopen;
	using::fprintf;
	using::fputc;
	using::fputs;
	using::fread;
	using::freopen;
	using::fscanf;
	using::fseek;
	using::fsetpos;
	using::ftell;
	using::fwrite;
	using::getc;
	using::getchar;
	using::gets;
	using::perror;
	using::printf;
	using::putc;
	using::putchar;
	using::puts;
	using::remove;
	using::rename;
	using::rewind;
	using::scanf;
	using::setbuf;
	using::setvbuf;
	using::sprintf;
	using::sscanf;
	using::tmpfile;
	using::tmpnam;
	using::ungetc;
	using::vfprintf;
	using::vprintf;
	using::vsprintf;
}
namespace __gnu_cxx {
using::snprintf;
	using::vfscanf;
	using::vscanf;
	using::vsnprintf;
	using::vsscanf;
}
namespace std {
using::__gnu_cxx::snprintf;
	using::__gnu_cxx::vfscanf;
	using::__gnu_cxx::vscanf;
	using::__gnu_cxx::vsnprintf;
	using::__gnu_cxx::vsscanf;
}
namespace std {
using::va_list;
}
namespace std {
typedef int* __c_locale;
	/**
	 * and should be avoided.<br>
	 * Original signature : <code>int __convert_from_v(const __c_locale&, char*, const int, const char*, null)</code>
	 */
	inline int __convert_from_v(const __c_locale& __c_localePtr1, char* __out, const int __size, const char* __fmt, ...) {
		char* __old = std::setlocale(4, 0);
		char* __sav = 0;
		if (std::strcmp(__old, "C")) {
			 __sav = new char[std::strlen(__old) + 1];
			std::strcpy(__sav, __old);
			std::setlocale(4, "C");
		}
		va_list __args;
		va_start(__args, __fmt);
		const int __ret = std::vsnprintf(__out, __size, __fmt, __args);
		va_end(__args);
		if (__sav) {
			std::setlocale(4, __sav);
			delete[] __sav;
		}
		return __ret;
	}
}
extern ""C"" {
struct sched_param {
		int sched_priority;
		char[4] __opaque;
	};
	/// Original signature : <code>int sched_yield()</code>
	extern int sched_yield();
	/// Original signature : <code>int sched_get_priority_min(int)</code>
	extern int sched_get_priority_min(int int1);
	/// Original signature : <code>int sched_get_priority_max(int)</code>
	extern int sched_get_priority_max(int int1);
}
typedef __darwin_clock_t clock_t;
struct tm {
	int tm_sec; /* seconds after the minute [0-60] */
	int tm_min; /* minutes after the hour [0-59] */
	int tm_hour; /* hours since midnight [0-23] */
	int tm_mday; /* day of the month [1-31] */
	int tm_mon; /* months since January [0-11] */
	int tm_year; /* years since 1900 */
	int tm_wday; /* days since Sunday [0-6] */
	int tm_yday; /* days since January 1 [0-365] */
	int tm_isdst; /* Daylight Savings Time flag */
	long tm_gmtoff; /* offset from CUT in seconds */
	char* tm_zone; /* timezone abbreviation */
};
extern char*[] tzname;
extern int getdate_err;
extern long timezone;
extern int daylight;
/// Original signature : <code>char* asctime(tm*)</code>
char* asctime(tm* tmPtr1);
/// Original signature : <code>clock_t clock()</code>
clock_t clock();
/// Original signature : <code>char* ctime(const time_t*)</code>
char* ctime(const time_t* time_tPtr1);
/// Original signature : <code>double difftime(time_t, time_t)</code>
double difftime(time_t time_t1, time_t time_t2);
/// Original signature : <code>tm* getdate(const char*)</code>
tm* getdate(const char* charPtr1);
/// Original signature : <code>tm* gmtime(const time_t*)</code>
tm* gmtime(const time_t* time_tPtr1);
/// Original signature : <code>tm* localtime(const time_t*)</code>
tm* localtime(const time_t* time_tPtr1);
/// Original signature : <code>time_t mktime(tm*)</code>
time_t mktime(tm* tmPtr1);
/// Original signature : <code>size_t strftime(char*, size_t, const char*, tm*)</code>
size_t strftime(char* charPtr1, size_t size_t1, const char* charPtr2, tm* tmPtr1);
/// Original signature : <code>char* strptime(const char*, const char*, tm*)</code>
char* strptime(const char* charPtr1, const char* charPtr2, tm* tmPtr1);
/// Original signature : <code>time_t time(time_t*)</code>
time_t time(time_t* time_tPtr1);
/// Original signature : <code>void tzset()</code>
void tzset();
/**
 * [TSF] Thread safe functions<br>
 * Original signature : <code>char* asctime_r(tm*, char*)</code>
 */
char* asctime_r(tm* tmPtr1, char* charPtr1);
/// Original signature : <code>char* ctime_r(const time_t*, char*)</code>
char* ctime_r(const time_t* time_tPtr1, char* charPtr1);
/// Original signature : <code>tm* gmtime_r(const time_t*, tm*)</code>
tm* gmtime_r(const time_t* time_tPtr1, tm* tmPtr1);
/// Original signature : <code>tm* localtime_r(const time_t*, tm*)</code>
tm* localtime_r(const time_t* time_tPtr1, tm* tmPtr1);
/// Original signature : <code>time_t posix2time(time_t)</code>
time_t posix2time(time_t time_t1);
/// Original signature : <code>void tzsetwall()</code>
void tzsetwall();
/// Original signature : <code>time_t time2posix(time_t)</code>
time_t time2posix(time_t time_t1);
