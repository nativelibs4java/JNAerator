package com.ochafik.swing.syntaxcoloring;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.ochafik.io.ReadText;

public class Test {
	public static void main(String args[]) {
		JFrame f=new JFrame("Test");
		String text=args.length==0 ? "" : ReadText.readText(new File(args[0]));
		JEditTextArea ta=new JEditTextArea();
		ta.setText(text);
		f.getContentPane().add("Center",new JScrollPane(ta));
		f.pack();
		f.show();
	}
}
