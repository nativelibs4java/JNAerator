//http://whine.fr/2009/un-format-pas-si-macho/
//http://www.cilinder.be/docs/next/NeXTStep/3.3/nd/DevTools/14_MachO/MachO.htmld/index.html

typedef int cpu_type_t;
typedef int cpu_subtype_t;


#define CPU_TYPE_MC680x0     ((cpu_type_t) 6)
#define CPU_SUBTYPE_MC68030  ((cpu_subtype_t) 1)
#define CPU_SUBTYPE_MC68040  ((cpu_subtype_t) 2)



struct fat_header
{
	uint32_t magic;
	uint32_t nfat_arch;
};

struct fat_arch
{
	cpu_type_t cputype;
	cpu_subtype_t cpusubtype;
	uint32_t offset;
	uint32_t size;
	uint32_t align;
};

#define MH_MAGIC    0xfeedface  /* the Mach magic number */

#define MH_OBJECT   0x1    /* relocatable object file */
#define MH_EXECUTE  0x2    /* executable object file */
#define MH_FVMLIB   0x3    /* fixed vm shared library file */
#define MH_CORE     0x4    /* core file */
#define MH_PRELOAD  0x5    /* preloaded executable file */

#define MH_NOUNDEFS  0x1  /* object file has no undefined references;
can be executed */
#define MH_INCRLINK  0x2  /* object file is the output of an
incremental link against a base file;
can't be link-edited again */

#define long int

struct mach_header {
	unsigned long  magic;      /* Mach magic number identifier */
	cpu_type_t     cputype;    /* cpu specifier */
	cpu_subtype_t  cpusubtype; /* machine specifier */
	unsigned long  filetype;   /* type of file */
	unsigned long  ncmds;      /* number of load commands */
	unsigned long  sizeofcmds; /* size of all load commands */
	unsigned long  flags;      /* flags */
};


#define LC_SEGMENT     0x1   /* file segment to be mapped */
#define LC_SYMTAB      0x2   /* link-edit stab symbol table info
(obsolete) */
#define LC_SYMSEG      0x3   /* link-edit gdb symbol table info */
#define LC_THREAD      0x4   /* thread */
#define LC_UNIXTHREAD  0x5   /* UNIX thread (includes a stack) */
#define LC_LOADFVMLIB  0x6   /* load a fixed VM shared library */
#define LC_IDFVMLIB    0x7   /* fixed VM shared library id */
#define LC_IDENT       0x8   /* object identification information
(obsolete) */
#define LC_FVMFILE      0x9   /* fixed VM file inclusion */


struct load_command {
	unsigned long  cmd;      /* type of load command */
	unsigned long  cmdsize;  /* total size of command in bytes */
};

struct symtab_command {
	unsigned long  cmd;      /* LC_SYMTAB */
	unsigned long  cmdsize;  /* sizeof(struct symtab_command) */
	unsigned long  symoff;   /* symbol table offset */
	unsigned long  nsyms;    /* number of symbol table entries */
	unsigned long  stroff;   /* string table offset */
	unsigned long  strsize;  /* string table size in bytes */
};

struct nlist {
	union {
		char      *n_name;   /* for use when in-core */
		long       n_strx;   /* index into file string table */
	} n_un;
#if 1
	unsigned char  n_type;   /* type flag; see below */
#else
	unsigned char  N_STAB:3,
		N_TYPE:4,
		N_EXT:1;
#endif
	unsigned char  n_sect;   /* section number or NO_SECT */
	short          n_desc;   /* see the header file stab.h */
	unsigned       n_value;  /* value of this symbol table entry
	(or stab offset) */
};

/*
* Simple values for n_type.
*/

#define N_UNDF  0x0     /* undefined */
#define N_ABS   0x2     /* absolute */
#define N_TEXT  0x4     /* text */
#define N_DATA  0x6     /* data */
#define N_BSS   0x8     /* bss */

#define N_EXT   01      /* external bit, or’ed in */

/*
* Other permanent symbol table entries have some of the N_STAB bits set.
* These are given in <stab.h>
*/

#define N_STAB  0xe0    /* if any of these bits set, don’t discard */

#define N_GSYM  0x20    /* global symbol: name,,0,type,0 */
#define N_FNAME 0x22    /* procedure name (f77 kludge): name,,0 */
#define N_FUN   0x24    /* procedure: name,,0,linenumber,address */
#define N_STSYM 0x26    /* static symbol: name,,0,type,address */
#define N_LCSYM 0x28    /* .lcomm symbol: name,,0,type,address */
#define N_RSYM  0x40    /* register sym: name,,0,type,register */
#define N_SLINE 0x44    /* src line: 0,,0,linenumber,address */
#define N_SSYM  0x60    /* structure elt: name,,0,type,struct_offset */
#define N_SO    0x64    /* source file name: name,,0,0,address */
#define N_LSYM  0x80    /* local sym: name,,0,type,offset */
#define N_SOL   0x84    /* #included file name: name,,0,0,address */
#define N_PSYM  0xa0    /* parameter: name,,0,type,offset */
#define N_ENTRY 0xa4    /* alternate entry: name,linenumber,address */
#define N_LBRAC 0xc0    /* left bracket: 0,,0,nesting level,address */
#define N_RBRAC 0xe0    /* right bracket: 0,,0,nesting level,address */
#define N_BCOMM 0xe2    /* begin common: name,, */
#define N_ECOMM 0xe4    /* end common: name,, */
#define N_ECOML 0xe8    /* end common (local name): ,,address */
#define N_LENG  0xfe    /* second stab entry with length information */


#define N_UNDF  0x0   /* undefined; n_sect == NO_SECT */
#define N_ABS   0x2   /* absolute; n_sect == NO_SECT */
#define N_SECT  0xe   /* defined in section number n_sect */
#define N_INDR  0xa   /* indirect */

#define FAT_MAGIC 0xCAFEBABE
#define FAT_CIGAM 0xEBABEFAC
