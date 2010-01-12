/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ochafik.lang.jnaerator.JNAerator.Feedback;
import com.ochafik.lang.jnaerator.studio.JNAeratorStudio;
import com.ochafik.lang.reflect.DebugUtils;
import com.ochafik.swing.candy.JLinkLabel;
import com.ochafik.util.SystemUtils;

public class SimpleGUI implements Feedback {
	JFrame frame;
	JLabel status;
	JButton cancelButton;
	final JNAeratorConfig config;
	final Thread threadToCancel;
	private String title;
	private String ftitle;
	public SimpleGUI(JNAeratorConfig config) {
		this.config = config;
		frame = new JFrame("JNAerator");
		this.threadToCancel = Thread.currentThread();
		title = config.entryName;
		if (title == null) {
			//title = StringUtils.implode(conf.libraryByFile.keySet(), ", ");
			//if (title.length() == 0)
				title = "<unnamed configuration>";
		}
		ftitle = "JNAerator : " + title; 
		frame.getContentPane().add("North", new JLinkLabel(title, new Runnable() {public void run() {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintStream pout = new PrintStream(bout);
			DebugUtils.println(SimpleGUI.this.config, pout);
			JTextArea ta = new JTextArea();
			ta.setWrapStyleWord(true);
			ta.setLineWrap(true);
			ta.setText(new String(bout.toByteArray()));
			JScrollPane sp = new JScrollPane(ta);
			Dimension s = new Dimension(500, 400);
			ta.setMaximumSize(new Dimension(s.width, Integer.MAX_VALUE));
			sp.setMaximumSize(s);
			sp.setMinimumSize(s);
			sp.setPreferredSize(s);
			JOptionPane.showMessageDialog(frame, sp, ftitle, JOptionPane.INFORMATION_MESSAGE);
		}}));
		frame.getContentPane().add("Center", status = new JLabel("Initializing..."));
		frame.setMinimumSize(new Dimension(400, 0));
		frame.getContentPane().add("South", cancelButton = new JButton("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (finished) {
					if (toOpenWhenFinished instanceof File) {
						try {
							File f = (File)toOpenWhenFinished;
							f = f.getAbsoluteFile();
							if (f.isDirectory())
								SystemUtils.runSystemOpenDirectory(f);
							else
								SystemUtils.runSystemOpenFileParent(f);
							
						} catch (NoSuchMethodException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else if (toOpenWhenFinished instanceof Throwable) {

						JNAeratorStudio.error(frame, null, null, (Throwable)toOpenWhenFinished);
					}
				} else {
					tryCancel();
				}
			}

		});
		cancelButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					tryQuit();
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				tryQuit();
			}
		});
	}
	void tryQuit() {
		if (finished)
			System.exit(0);
		tryCancel();	
	}

	private void tryCancel() {
		if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel ?", ftitle, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
			threadToCancel.interrupt();
			System.exit(0);
		}
	}
	Object toOpenWhenFinished;
	boolean finished;

	void show() {
		frame.pack();
		frame.setVisible(true);
	}
	void hide() {
		frame.setVisible(false);
	}

	public void setStatus(final String string) {
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			status.setText(string);
		}});
	}

	public void setFinished(final File fileToOpen) {
		toOpenWhenFinished = fileToOpen;
		finished = true;
		setStatus("JNAeration completed (hit Escape to quit)");
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			String name = fileToOpen.getName();
			cancelButton.setText(fileToOpen.isDirectory() ? 
				"Open output directory" : 
				"Show file '" + name + "'"
			);
			cancelButton.setToolTipText(fileToOpen.getAbsolutePath());
		}});
	}
	@Override
	public void setFinished(final Throwable e) {
		finished = true;
		toOpenWhenFinished = e;
		setStatus("JNAeration failed !");
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			cancelButton.setText("Show error details");
			cancelButton.setToolTipText(e.toString());
		}});
	}
	@Override
	public void sourcesParsed(SourceFiles sourceFiles) {
		
	}
	@Override
	public void wrappersGenerated(Result result) {
		
	}

}