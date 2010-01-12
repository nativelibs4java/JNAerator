package com.ochafik.lang.jnaerator.nativesupport;


import static com.ochafik.lang.jnaerator.nativesupport.NativeExportUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.ochafik.lang.jnaerator.nativesupport.machoexport.FatMachOLibrary.*;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.FatMachOLibrary;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.fat_arch;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.fat_header;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.load_command;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.mach_header;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.nlist;
import com.ochafik.lang.jnaerator.nativesupport.machoexport.symtab_command;
import com.ochafik.lang.reflect.DebugUtils;


public class FatMachOExport {

	public static char[] parseDllExports(File f) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		byte[] b = GetFileBytes(raf, 0, 100);
		
		int i = readBigEndianInt(raf, 0);
		String h = Integer.toHexString(i);
		fat_header fat = deserializeBigEndianStruct(new fat_header(), raf, 0);
		if (fat.magic == FAT_MAGIC) {
			long archsBase = fat.size();
			long archSize = new fat_arch().size();
			for (int iFatArch = 0; iFatArch < fat.nfat_arch; iFatArch++) {
				long archBase = archsBase + archSize * iFatArch;
				fat_arch arch = deserializeBigEndianStruct(new fat_arch(), raf, archBase);

				if (arch.cputype == 0)
					continue;
				
				mach_header mach = deserializeBigEndianStruct(new mach_header(), raf, arch.offset);
				if (mach.cputype != arch.cputype || mach.cpusubtype != arch.cpusubtype)
					throw new IOException("Failed to recognize Fat + MachO format !");
				
				if (mach.magic == MH_MAGIC) {
					OutputSymbols(raf, mach, arch.offset);
				}
				
				
			}
		}
		return null;
	}

	private static void OutputSymbols(RandomAccessFile raf, mach_header mach, int machOffset) throws IOException {
		int currentCommandOffset = machOffset + mach.size();
		boolean passedLastOk = false;
		for (int iLoadCmd = 0; iLoadCmd < mach.ncmds; iLoadCmd++) {
			load_command lc = deserializeBigEndianStruct(new load_command(), raf, currentCommandOffset);
			switch (lc.cmd) {
			case LC_SYMTAB:
				symtab_command sc = deserializeBigEndianStruct(new symtab_command(), raf, currentCommandOffset);
				byte[] tbytes = GetFileBytes(raf, sc.symoff + machOffset, 100);
				
				int symtabsize = new nlist().size(), scsize = sc.size();
				for (int iSymTab = 0; iSymTab < sc.nsyms; iSymTab++) {
					long nlOffset = machOffset + scsize + sc.symoff + iSymTab * symtabsize;
//					if ((nlOffset % 8) != 0)
//						nlOffset = ((nlOffset / 8) + 1) * 8;
					nlist nl = deserializeBigEndianStruct(new nlist(), raf, nlOffset);
					nl.n_un.setType(Integer.TYPE);
//					nl.n_un.read();
//					if (nl.N_EXT != 1)
//						continue;
					if (nl.n_un.n_strx == 0)
						continue;
					
					short desc = nl.n_desc;
					if (nl.n_desc != -1)
						continue;
					
					int stab = nl.n_type & N_STAB;
					if ((stab) != 0) {
						String h = Integer.toHexString(stab);
						h = h.toString();
						continue;
					}
					
					try {
						long stroff = sc.stroff + machOffset + nl.n_un.n_strx;
//						byte[] obytes = GetFileBytes(raf, stroff - 10, 20);
//						char[] chars = new char[obytes.length];
//						for (int i = 0; i < obytes.length; i++)
//							chars[i] = (char)(0xff & obytes[i]);
						
						byte[] sbytes = NativeExportUtils.GetFileBytes(raf, stroff, -1);
	//					char[] chars = new char[sbytes.length];
	//					for (int i = 0; i < sbytes.length; i++)
	//						chars[i] = (char)(0xff & sbytes[i]);
	//					
	//					System.out.println(new String(chars));
						String symbol = new String(sbytes);
						
						int sector = nl.n_sect;
						if (symbol.equals("_kCFUserNotificationSoundPathKey"))
							passedLastOk = true;
						//	symbol = symbol.toString();
						System.out.println(symbol);// + ": sect = " + sector);
//						if (passedLastOk)
//							DebugUtils.println(nl);
					
					} catch (Exception ex) {
						ex.printStackTrace();
					}
 				}
				
				byte[] sbytes = GetFileBytes(raf, sc.stroff + machOffset, 100);
				char[] chars = new char[sbytes.length];
				for (int i = 0; i < sbytes.length; i++)
					chars[i] = (char)(0xff & sbytes[i]);
				int nsyms = sc.nsyms;
				break;
			case LC_FVMFILE:
			case LC_IDENT:
			case LC_LOADFVMLIB:
			case LC_SEGMENT:
			case LC_SYMSEG:
			case LC_THREAD:
			case LC_UNIXTHREAD:
			}
			currentCommandOffset += lc.cmdsize;
		}
	}

}
