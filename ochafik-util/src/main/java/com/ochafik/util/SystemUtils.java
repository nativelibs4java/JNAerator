/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class SystemUtils {
	public static final void runSystemCommand(String[] cmd) throws NoSuchMethodException {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NoSuchMethodException(ex.toString());
		}
	}
	public static final void runSystemCommand(String cmd) throws NoSuchMethodException {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NoSuchMethodException(ex.toString());
		}
	}
	static String osName;
	static {
		// For possible values, see http://lopica.sourceforge.net/os.html
		osName = System.getProperty("os.name");
	}
	
	public static boolean isMacOSX() {
		return osName.equals("Mac OS X");
	}
	public static boolean isWindows() {
		return osName.indexOf("Windows") >= 0;
	}
	public static boolean isLinux() {
		return osName.indexOf("Linux") >= 0;
	}
	public static boolean isSolaris() {
		return osName.indexOf("Solaris") >= 0 || osName.indexOf("SunOS") >= 0;
	}
	public static boolean isUnix() {
		return File.separatorChar == '/';
	}
	public static final void runSystemOpenURL(URL url) throws NoSuchMethodException, IOException {
		if (isMacOSX()) {
			runSystemCommand(new String[] {"open",url.toString()});
		} else if (isWindows()) {
			runSystemCommand(new String[] {"rundll32","url.dll,FileProtocolHandler", url.toString()});
		} else if (isLinux()) {
			if (hasUnixCommand("gnome-open")) {
				runSystemCommand(new String[] {"gnome-open", url.toString()});
			} else {
				runSystemCommand(new String[] {"konqueror", url.toString()});
			}
		} else {
			if (url.getProtocol().equals("file")) {
				runSystemOpenFileParent(new File(url.getFile()));
			} else {
				runSystemCommand(new String[] {"mozilla",url.toString()});
			}
		}
	}
	public static final boolean hasUnixCommand(String name) {
		try {
			Process p = Runtime.getRuntime().exec(new String[] {"which",name});
			return p.waitFor() == 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public static final void runSystemOpenFileWith(File fileToOpen) throws NoSuchMethodException, IOException {
		runSystemCommand(new String[] {"RUNDLL32.EXE", "SHELL32.DLL,OpenAs_RunDLL",fileToOpen.getCanonicalPath()});
	}
	public static final void runSystemOpenFile(File fileToOpen) throws NoSuchMethodException, IOException {
		if (isMacOSX()) {
			runSystemCommand(new String[] {"open",fileToOpen.getCanonicalPath()});
		} else if (isWindows()) {
			runSystemCommand(new String[] {"start",fileToOpen.getCanonicalPath()});
		} else if (isLinux()) {
			if (hasUnixCommand("gnome-open")) {
				runSystemCommand(new String[] {"gnome-open", fileToOpen.getCanonicalPath()});
			} else {
				runSystemCommand(new String[] {"konqueror", fileToOpen.getCanonicalPath()});
			}
		} else if (isSolaris()) {
			if (fileToOpen.isDirectory()) {
				runSystemCommand(new String[] {"/usr/dt/bin/dtfile","-folder",fileToOpen.getCanonicalPath()});
			}
		}
	}
	public static final void runSystemOpenDirectory(File file) throws NoSuchMethodException, IOException {
		if (isWindows()) {
			runSystemCommand(new String[] {"explorer", file.getCanonicalPath()});
		} else {
			runSystemOpenFile(file);
		}
	}
	public static final void runImageEditor(File imageFile) throws NoSuchMethodException, IOException {
		if (!imageFile.exists()) throw new FileNotFoundException(imageFile.toString());
		if (isWindows()) {
			Runtime.getRuntime().exec(new String[] { "mspaint.exe", imageFile.getAbsolutePath()});
		} else {
			throw new NoSuchMethodException("Implement me ! Image editor on " + osName);
		}
	}
	public static final void runSystemOpenFileParent(File fileToShow) throws NoSuchMethodException, IOException {
		if (isMacOSX()) {
			runSystemCommand(new String[] {"open",fileToShow.getParentFile().getAbsolutePath()});
		} else if (isWindows()) {
			runSystemCommand("explorer /e,/select,\""+fileToShow.getCanonicalPath()+"\"");
		} else if (isLinux()) {
			if (hasUnixCommand("gnome-open")) {
				runSystemCommand(new String[] {"gnome-open", fileToShow.getParentFile().getAbsolutePath()});
			} else {
				runSystemCommand(new String[] {"konqueror", fileToShow.getParentFile().getAbsolutePath()});
			}
		} else if (isSolaris()) {
			runSystemCommand(new String[] {"/usr/dt/bin/dtfile","-folder",fileToShow.getParentFile().getCanonicalPath()});
		}
	}
	public static final JComponent createFileShowAndOpenWithPanel(final File file) {
		String osName=System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") >= 0) {
			//JButton bOpen=new JButton("Open");
			JButton bSee=new JButton("Dir.");
			JButton bOpenWith=new JButton("Open with...");
			
			bSee.setToolTipText("<html><body>Browse directory :<br><code>"+file.getParentFile().toString()+"</code></body></html>");
			//bOpen.setToolTipText("<html><body>Open file :<br><code>"+file.toString()+"</code></body></html>");
			
			/*bOpen.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
					try {
						runSystemOpenFile(file);
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					} catch (NoSuchMethodException ex) {
						ex.printStackTrace();
					}
			}});*/
			bSee.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
					try {
						runSystemOpenFileParent(file);
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					} catch (NoSuchMethodException ex) {
						ex.printStackTrace();
					}
			}});
			bOpenWith.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
					try {
						runSystemOpenFileWith(file);
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					} catch (NoSuchMethodException ex) {
						ex.printStackTrace();
					}
			}});
			
			Box box=Box.createHorizontalBox();
			//box.add(bOpen);
			box.add(bSee);
			box.add(bOpenWith);
			return box;
		} else {
			System.err.println("Does not handle OS '"+osName+"'");
			return null;
		}
	}
	public static final JPopupMenu createOpenOpenWithShowDirPopupPanel(final File file) {
		JPopupMenu menu=new JPopupMenu();
		
		//JButton bOpen=new JButton("Open");
		JMenuItem miOpen=new JMenuItem("Open");
		JMenuItem miOpenWith=new JMenuItem("Open with...");
		JMenuItem miShowDir=new JMenuItem("Open parent directory");
		
		boolean isWindows = osName.indexOf("windows") >= 0;
			
		miShowDir.setToolTipText("<html><body>Browse directory :<br><code>"+file.getParentFile().toString()+"</code></body></html>");
		//bOpen.setToolTipText("<html><body>Open file :<br><code>"+file.toString()+"</code></body></html>");
		
		/*bOpen.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
				try {
					runSystemOpenFile(file);
				} catch (IOException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchMethodException ex) {
					ex.printStackTrace();
				}
		}});*/
		miShowDir.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
				try {
					runSystemOpenFileParent(file);
				} catch (IOException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchMethodException ex) {
					ex.printStackTrace();
				}
		}});
		miOpenWith.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
				try {
					runSystemOpenFileWith(file);
				} catch (IOException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchMethodException ex) {
					ex.printStackTrace();
				}
		}});
		miOpen.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) {
				try {
					runSystemOpenURL(file.toURI().toURL());
				} catch (IOException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NoSuchMethodException ex) {
					ex.printStackTrace();
				}
		}});
		menu.add(miOpen);
		if (isWindows)
			menu.add(miOpenWith);
		
		menu.addSeparator();
		menu.add(miShowDir);
		return menu;
	}
	public static final boolean addOpenOpenWithShowDirPopupPanel(final File file, JComponent component) {
		String osName=System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") >= 0) {
			final JPopupMenu menu = createOpenOpenWithShowDirPopupPanel(file);
			component.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent evt) {
					if (evt.isPopupTrigger()||evt.getButton() > 1) {
						//menu.show((Component)evt.getSource(),evt.getX(),evt.getY());
						menu.show(evt.getComponent(),evt.getX(),evt.getY());
					}
			}});
			return true;
		} else {
			System.err.println("Does not handle OS '"+osName+"'");
			return false;
		}
	}
	public static boolean isPopupTrigger(MouseEvent e) {
		if (e.isPopupTrigger() || e.getButton() != MouseEvent.BUTTON1)
			return true;
		
		if (isMacOSX())
			return e.isControlDown();
		
		return false; 
	}
}
