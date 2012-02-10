/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import java.io.File;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import static org.bridj.Platform.*;

/**
 *
 * @author ochafik
 */
public enum NativePlatform {
    // Linux
    linux_x64("so"), linux_x86("so"),
    
    // Android
    armeabi("so"),
    
    // Solaris
    sunos_x86("so"), sunos_sparc("so"),
    
    // MacOS X
    darwin_universal("dylib"),
    
    // Windows
    win32("dll"), win64("dll");
    
    final Pattern pattern;
    final String extension;
    NativePlatform(String extension) {
        this.extension = extension;
        this.pattern = Pattern.compile(".*?\\." + Pattern.quote(extension));
    }

    public static EnumSet<NativePlatform> getPossiblePlatformsOfLibraryFile(String path) {
        String name = new File(path).getName();
        EnumSet<NativePlatform> set = EnumSet.noneOf(NativePlatform.class);
        for (NativePlatform p : values()) {
            if (p.pattern.matcher(name).matches()) {
                set.add(p);
            }
        }
        return set;
    }

    public static NativePlatform getCurrentPlatform() {
        if (isWindows()) {
            return is64Bits() ? win64 : win32;
        } else if (isAndroid()) {
            return isArm() ? armeabi : linux_x86;
        } else if (isLinux()) {
            return is64Bits() ? linux_x64 : linux_x86;
        } else if (isSolaris()) {
            if (isSparc()) {
                return sunos_sparc;
            } else {
                return sunos_x86;
            }
        } else if (isMacOSX())
            return darwin_universal;
        
        return null;
        //throw new NoSuchElementException("Unknown platform !");
    }
    
}
