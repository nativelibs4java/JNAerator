package com.ochafik.io;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
public class JTextAreaOutputStream extends OutputStream {
	JTextArea ta;
	public JTextAreaOutputStream(JTextArea t) {
		ta=t;
	}
	private class Appender implements Runnable {
		public Appender(String st) {
			string=st;
		}
		String string;
		public void run() {
			ta.append(string);
		}		
	};
	public void close() {ta=null;}
	public void write(int i) {
		SwingUtilities.invokeLater(new Appender(new String(new byte[]{(byte)i})));
	}
	public void write(byte b[]) {
		SwingUtilities.invokeLater(new Appender(new String(b)));
	}
	public void write(byte b[],int s,int l) {
		SwingUtilities.invokeLater(new Appender(new String(b,s,l)));
	}
}
		
