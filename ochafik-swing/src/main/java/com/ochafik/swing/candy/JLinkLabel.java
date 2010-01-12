package com.ochafik.swing.candy;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import com.ochafik.xml.XMLUtils;

public class JLinkLabel extends JLabel {
	private static final long serialVersionUID = -2619802894792618038L;
	Runnable linkAction;
	boolean actInNewThread = false;
	
	public JLinkLabel(String text) {
		super(text);
		init(null);
	}
	
	public JLinkLabel(String text, int orientation) {
		super(text, orientation);
		init(null);
	}
	
	public JLinkLabel(String text, int orientation, Runnable linkAction) {
		this(text, orientation, linkAction, false);
	}
	public JLinkLabel(String text, int orientation, Runnable linkAction, boolean actInNewThread) {
		super(text, orientation);
		init(linkAction);
		setActInNewThread(actInNewThread);
	}
	public JLinkLabel(String text, Runnable linkAction) {
		this(text, linkAction, false);
	}
	public JLinkLabel(String text, Runnable linkAction, boolean actInNewThread) {
		super(text);
		init(linkAction);
		setActInNewThread(actInNewThread);
	}
	
	public void setActInNewThread(boolean actInNewThread) {
		this.actInNewThread = actInNewThread;
	}
	public boolean isActInNewThread() {
		return actInNewThread;
	}
	
	protected void enabledChanged() {
		String text = actualText;
		if (getLinkAction() == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			if (isEnabled()) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				if (!actualText.startsWith("<html>"))
					text = "<html><body width='100%'><a href='#'>" + XMLUtils.escapeEntities(actualText) + "</a></body></html>";
				
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				if (!actualText.startsWith("<html>"))
					text = "<html><body width='100%'><u>" + XMLUtils.escapeEntities(actualText) + "</u></body></html>";
			}
		}
		super.setText(text);
		setMaximumSize(getMinimumSize());
	}
	String actualText;
	@Override
	public void setText(String text) {
		this.actualText = text;
		enabledChanged();
	}
	private void init(Runnable linkAction2) {
		setLinkAction(linkAction2);
		enabledChanged();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				action();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (isEnabled() && getLinkAction() != null)
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}
	
	public boolean action() {
		Runnable linkAction = getLinkAction();
		if (!isEnabled() || linkAction == null)
			return false;
		
		if (isActInNewThread())
			new Thread(linkAction).start();
		else
			linkAction.run();
		return true;
	}

	public void setLinkAction(Runnable linkAction) {
		this.linkAction = linkAction;
		enabledChanged();
	}
	public Runnable getLinkAction() {
		return linkAction;
	}
	
}
