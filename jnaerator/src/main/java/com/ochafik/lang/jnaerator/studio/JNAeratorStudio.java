/*
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
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
package com.ochafik.lang.jnaerator.studio;

import com.ochafik.lang.jnaerator.JNAeratorConfig.Runtime;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ochafik.io.JTextAreaOutputStream;
import com.ochafik.io.ReadText;
import com.ochafik.io.WriteText;
import com.ochafik.lang.SyntaxUtils;
import com.ochafik.lang.compiler.MemoryFileManager;
import com.ochafik.lang.jnaerator.ClassOutputter;
import com.ochafik.lang.jnaerator.JNAerator;
import com.ochafik.lang.jnaerator.JNAeratorCommandLineArgs;
import com.ochafik.lang.jnaerator.JNAeratorConfig;
import com.ochafik.lang.jnaerator.Result;
import com.ochafik.lang.jnaerator.SourceFiles;
import com.ochafik.lang.jnaerator.JNAerator.Feedback;
import com.ochafik.lang.jnaerator.JNAeratorConfig.OutputMode;
import com.ochafik.swing.SimpleDocumentAdapter;
import com.ochafik.swing.UndoRedoUtils;
import com.ochafik.swing.syntaxcoloring.CCTokenMarker;
import com.ochafik.swing.syntaxcoloring.JEditTextArea;
import com.ochafik.swing.syntaxcoloring.JavaTokenMarker;
import com.ochafik.swing.syntaxcoloring.TokenMarker;
import com.ochafik.util.SystemUtils;
import com.ochafik.util.listenable.ListenableCollections;
import com.ochafik.util.listenable.ListenableComboModel;
import com.ochafik.util.listenable.ListenableList;
import com.ochafik.util.string.StringUtils;
import java.util.List;

/*
 include com/ochafik/lang/jnaerator/examples/*.h
 */
/// https://jna.dev.java.net/servlets/ReadMsg?list=users&msgNo=1988
@SuppressWarnings("serial")
public class JNAeratorStudio extends JPanel {

    private static final long serialVersionUID = -6061806156049213635L;
    private static final String PREF_RADIX = "JNAeratorStudio.";
    JEditTextArea sourceArea = textArea(new JavaTokenMarker());
    JEditTextArea resultArea = textArea(new CCTokenMarker());
    JTextField libraryName = new JTextField("test");
    JLabel classCountLabel = new JLabel("JNAerated class :");
//	JList resultsList = new JList();
    JComboBox resultsListCombo = new JComboBox();
    JCheckBox directCallingCb = new JCheckBox("Direct Calling", false),
            structsAsTopLevelClassesCb = new JCheckBox("Top-level structs", true),
            charPtrAsString = new JCheckBox("(w)char* as (W)String", false),
            reificationCb = new JCheckBox("Reification", false),
            convertBodiesCb = new JCheckBox("Convert statements (BridJ)", false),
            extractInterfaceCb = new JCheckBox("Extract Interface (BridJ)", false),
            genRawBindingsCb = new JCheckBox("Raw bindings (BridJ)", false),
            //scalaSettersCb = new JCheckBox("Scala struct field setters", false),
            beautifyNamesCb = new JCheckBox("Beautify names", false),
            noCommentCb = new JCheckBox("No comments", false);
    JComboBox runtimeCombo;
    JComboBox modeCombo;
    JTextArea errorsArea = new JTextArea();
    JSplitPane sp;
    ListenableList<ResultContent> results = ListenableCollections.listenableList(new ArrayList<ResultContent>());
    MemoryFileManager memoryFileManager;
    static MouseWheelListener mouseWheelListener = new MouseWheelListener() {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            JEditTextArea ta = SyntaxUtils.as(e.getSource(), JEditTextArea.class);
            if (ta == null) {
                return;
            }

            int line = ta.getFirstLine();
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                int u = e.getUnitsToScroll();
                line += u > 0 ? 1 : -1;
                if (line < 0) {
                    line = 0;
                } else if (line >= ta.getLineCount()) {
                    line = ta.getLineCount() - 1;
                }

                ta.setFirstLine(line);
            }
        }
    };

    public void error(String title, String message, Throwable th) {
        error(this, title, message, th);
    }

    public static void error(Component parent, String title, String message, Throwable th) {
        StringWriter sw = new StringWriter();
        th.printStackTrace(new PrintWriter(sw));
        JScrollPane jsp = new JScrollPane(new JTextArea(sw.toString())) {
            public Dimension getMaximumSize() {
                return new Dimension(500, 500);
            }
        ;
        };
//		jsp.setMaximumSize(new Dimension(500, 500));
		JOptionPane.showMessageDialog(
                parent,
                new Object[]{
            message,
            jsp
        },
                title == null ? "JNAerator Error" : title,
                -1);
    }

    public File getDir(String name) {
        File dir = new File(getDir(), name);
        dir.mkdirs();
        return dir;
    }

    public File getDir() {
        File dir = new File(System.getProperty("user.home"));
        dir = new File(dir, ".jnaeratorStudio");
        dir = new File(dir, "pad");
        dir.mkdirs();
        return dir;
    }

    public File getInputFile() {
        return new File(getDir(), "input.h");
    }

    public File getOutputDir() {
        return new File(getDir(), "out");
    }

    public File getOutputJarFile() {
        String lib = libraryName.getText().trim();
        if (lib.length() == 0) {
            lib = "out";
        }
        return new File(getDir(), lib + ".jar");
    }

    void save() throws IOException {
        WriteText.writeText(sourceArea.getText(), getInputFile());
    }

    static JEditTextArea textArea(TokenMarker marker) {
        JEditTextArea ta = new JEditTextArea() {
            private static final long serialVersionUID = 1L;
//			int lastCode, lastLocation;
//			char lastChar = 0;

            @Override
            public void processKeyEvent(KeyEvent evt) {
                if (SystemUtils.isMacOSX()) {
                    int m = evt.getModifiers();
                    if ((m & InputEvent.META_MASK) != 0) {
                        m = (m & ~InputEvent.META_MASK) | InputEvent.CTRL_MASK;
                        evt = new KeyEvent(evt.getComponent(), evt.getID(), evt.getWhen(), m, evt.getKeyCode(), evt.getKeyChar(), evt.getKeyLocation());
                        if (evt.getID() == KeyEvent.KEY_TYPED) {
                            return;
                        }
                    }
                }

                super.processKeyEvent(evt);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(100, 100);
            }
        };
        ta.setBorder(BorderFactory.createLoweredBevelBorder());
        ta.setFocusTraversalKeysEnabled(false);
        ta.addMouseWheelListener(mouseWheelListener);
        ta.setPreferredSize(new Dimension(200, 300));
        ta.setTokenMarker(marker);
        return ta;
    }
    //static final File FILE = new File(".jnaeratorStudio.cpp");

    public void close(JFrame f) {
        try {
            save();
            setPref("window.width", f.getWidth());
            setPref("window.height", f.getHeight());
            setPref("window.extendedState", f.getExtendedState());
            setPref("options.libraryName", libraryName.getText());
            setPref("options.direct", directCallingCb.isSelected());
            setPref("options.topLevelStructs", structsAsTopLevelClassesCb.isSelected());
            setPref("options.reification", reificationCb.isSelected());
            setPref("options.convertBodies", convertBodiesCb.isSelected());
            setPref("options.extractInterface", extractInterfaceCb.isSelected());
            setPref("options.genRawBindings", genRawBindingsCb.isSelected());
            //setPref("options.scalaSetters", scalaSettersCb.isSelected());
            setPref("options.beautifyNames", beautifyNamesCb.isSelected());
            setPref("options.charPtrAsString", charPtrAsString.isSelected());
            setPref("options.targetRuntime", ((JNAeratorConfig.Runtime) runtimeCombo.getSelectedItem()).name());
            setPref("options.outputMode", ((JNAeratorConfig.OutputMode) modeCombo.getSelectedItem()).name());
            setPref("options.noComments", noCommentCb.isSelected());
            setPref("splitPane.orientation", sp.getOrientation());
            setPref("splitPane.dividedLocation", getProportionalDividerLocation(sp));
            prefNode().flush();
            System.exit(0);
        } catch (Throwable ex) {
            error(null, "Error while closing", ex);
        }
    }
    JTabbedPane sourceTabs = new JTabbedPane(JTabbedPane.TOP), resultTabs = new JTabbedPane(JTabbedPane.TOP);
    //JButton actButton = new JButton("JNAerate !");

    void switchOrientation() {
        boolean hor = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT;
        int l = sp.getDividerLocation(), d = hor ? sp.getWidth() : sp.getHeight();
        sp.setOrientation(hor ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);
        if (d != 0) {
            sp.setDividerLocation(l / (double) d);
        }
    }
    Action switchOrientationAction = new AbstractAction("Switch Orientation") {
        @Override
        public void actionPerformed(ActionEvent e) {
            switchOrientation();
        }
    },
            donateAction = new AbstractAction("Donate to the author") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                SystemUtils.runSystemOpenURL(new URL(JNAerator.DONATE_URL));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    },
            generateAction = new AbstractAction("JNAerate !") {
        @Override
        public void actionPerformed(ActionEvent e) {
            generate();
            generateButton.requestFocus();
        }
    },
            aboutJNAeratorAction = aboutLink("About JNAerator", JNAerator.ABOUT_JNAERATOR_URL),
            aboutRococoaAction = aboutLink("About Rococoa", JNAerator.ABOUT_ROCOCOA_URL),
            aboutBridJAction = aboutLink("About BridJ", JNAerator.ABOUT_BRIDJ_URL),
            aboutJNAAction = aboutLink("About JNA", JNAerator.ABOUT_JNA_URL),
            showExampleAction = new AbstractAction("Open Example") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sourceArea.getText().trim().length() > 0) {
                if (JOptionPane.showConfirmDialog(JNAeratorStudio.this, "This is going to overwrite the contents of your source text area.\nProceed ?", "Open Example", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                    return;
                }
                doShowExample(true);
            }
        }
    };

    AbstractAction aboutLink(final String title, final String urlString) {
        return new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    URL url = new URL(urlString);
                    System.out.println(title + ": " + url);
                    SystemUtils.runSystemOpenURL(url);
                } catch (Exception ex) {
                    error(null, "Error while opening page '" + title + "'", ex);
                }
            }
        };
    }
    Object lastJNAeratedArtifact;
    //JLabel statusLabel = new JLabel("", JLabel.RIGHT);
    JButton showJarButton;
    JPanel errorsPane = new JPanel(new BorderLayout());
    JProgressBar statusBar = new JProgressBar();
    private JButton generateButton;

    public JNAeratorStudio() {
        super(new BorderLayout());

        runtimeCombo = new JComboBox(JNAeratorConfig.Runtime.values()) {
            {
                setToolTipText("Target runtime library");
                setSelectedItem(JNAeratorConfig.Runtime.JNAerator);
            }
        };

        modeCombo = new JComboBox(OutputMode.values()) {
            {
                setToolTipText("Output mode");
                setSelectedItem(JNAeratorConfig.OutputMode.StandaloneJar);
            }
        };

        resultsListCombo.setModel(new ListenableComboModel<ResultContent>(results));

        //animator.setAcceleration(.2f); 
        //animator.setDeceleration(.2f);

        JToolBar tb = new JToolBar();
        generateButton = tb.add(generateAction);
        tb.add(donateAction);
        tb.add(showExampleAction);
        tb.add(switchOrientationAction);
        tb.addSeparator();
        tb.add(aboutJNAeratorAction);
        tb.addSeparator();
        tb.add(aboutBridJAction);
        tb.add(aboutJNAAction);
        tb.add(aboutRococoaAction);
        //tb.setOrientation(JToolBar.VERTICAL);
        add("North", tb);

        sourceArea.getDocument().addDocumentListener(new SimpleDocumentAdapter() {
            @Override
            public void updated(DocumentEvent e) {
                setReadyToJNAerate();
            }
        });

        add("South", statusBar);

        //statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

        JComponent sourcePane = new JPanel(new BorderLayout()), resultPane = new JPanel(new BorderLayout());
        Box libBox = Box.createHorizontalBox();
        showJarButton = new JButton("Show Output");
        showJarButton.setEnabled(false);
        showJarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastJNAeratedArtifact == null || !(lastJNAeratedArtifact instanceof File)) {
                    return;
                }

                File file = (File) lastJNAeratedArtifact;
                try {
                    if (file.isDirectory()) {
                        SystemUtils.runSystemOpenDirectory(file);
                    } else {
                        SystemUtils.runSystemOpenFileParent(file);
                    }
                } catch (Exception e1) {
                    showJarButton.setEnabled(false);
                    showJarButton.setToolTipText(e1.toString());
                }
            }
        });
        statusBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lastJNAeratedArtifact != null && lastJNAeratedArtifact instanceof Throwable) {
                    error(null, null, (Throwable) lastJNAeratedArtifact);
                } else {
                    generateAction.actionPerformed(null);
                }
            }
        });
        libBox.add(new JLabel("Target Runtime"));
        libBox.add(runtimeCombo);

        libBox.add(new JLabel("Library Name :", JLabel.RIGHT));
        libBox.add(libraryName);


        Box modeBox = Box.createHorizontalBox();
        modeBox.add(new JLabel("Output Mode"));
        modeBox.add(modeCombo);

        Box optBox = Box.createVerticalBox();
        optBox.add(libBox);
        optBox.add(modeBox);


        JPanel optPanel = new JPanel(new GridLayout(3, 3));
        optPanel.add(directCallingCb);
        optPanel.add(noCommentCb);
        optPanel.add(structsAsTopLevelClassesCb);
        optPanel.add(charPtrAsString);
        //optPanel.add(scalaSettersCb);
        optPanel.add(reificationCb);
        optPanel.add(convertBodiesCb);
        optPanel.add(genRawBindingsCb);
        optPanel.add(beautifyNamesCb);
        optPanel.add(extractInterfaceCb);
        optBox.add(optPanel);
        for (Component c : optBox.getComponents()) {
            ((JComponent) c).setAlignmentX(0);
        }

        sourcePane.add("North", optBox);//raryName);
        sourcePane.add("Center", sourceArea);
        sourceTabs.addTab("Source", sourcePane);


        Box resChoiceBox = Box.createHorizontalBox();
        resChoiceBox.add(classCountLabel);
        resChoiceBox.add(resultsListCombo);
        resChoiceBox.add(showJarButton);

        resultPane.add("North", resChoiceBox);
        resultPane.add("Center", resultArea);
        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sourceTabs, resultTabs);
        sp.setOneTouchExpandable(true);
        sp.setResizeWeight(0.5);
        //sp.setDividerLocation(0.5);

        errorsPane.add("Center", new JScrollPane(errorsArea));

        resultTabs.add("JNAeration Results", resultPane);
        resultTabs.add("Logs", errorsPane);
        add("Center", sp);
        //add("Center", new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, new JScrollPane(errorsArea)));

        resultsListCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                ResultContent c = (ResultContent) resultsListCombo.getSelectedItem();
                resultArea.setText(c == null ? "" : c.getContent());
                resultArea.scrollTo(0, 0);
            }
        });

        try {
            sourceArea.setText(ReadText.readText(getInputFile()));
            sourceArea.scrollTo(0, 0);
        } catch (Exception ex) {
        }

        if (sourceArea.getText().trim().length() == 0) {
            doShowExample(false);
        }

        UndoRedoUtils.registerNewUndoManager(sourceArea, sourceArea.getDocument());
    }

    void setReadyToJNAerate() {
        statusBar.setToolTipText("Click to JNAerate !");
        statusBar.setMaximum(1);
        statusBar.setMinimum(0);
        statusBar.setValue(0);
        statusBar.setStringPainted(true);
        statusBar.setString("Ready to JNAerate");
        statusBar.setIndeterminate(false);
    }

    private void doShowExample(boolean generate) {

        try {
            sourceArea.setText(ReadText.readText(getClass().getClassLoader().getResourceAsStream(
                    "com/ochafik/lang/jnaerator/examples/example.h")));
            sourceArea.scrollTo(0, 0);
            if (generate) {
                generate();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

//	interface ProgressCallbacks {
//		void sourcesParsed(SourceFiles sf);
//		void log(String s);
//		void filesGenerated(File outputDir);
//	}
    protected void generate() {

        try {
            save();
        } catch (IOException e1) {
            error(null, "Error while saving file", e1);
            return;
        }
        JNAeratorStudio.this.setEnabled(false);
        errorsArea.setText("");
        results.clear();
        resultArea.setText("");
        generateAction.setEnabled(false);
        showJarButton.setEnabled(false);
        showJarButton.setToolTipText(null);
        statusBar.setIndeterminate(true);
        statusBar.setToolTipText("JNAerating...");
        lastJNAeratedArtifact = null;

        new Thread() {
            public void run() {
                JNAeratorConfig config = new JNAeratorConfig();
                config.outputJar = getOutputJarFile();
                config.outputDir = getOutputDir();
                config.useJNADirectCalls = directCallingCb.isSelected();
                config.putTopStructsInSeparateFiles = structsAsTopLevelClassesCb.isSelected();
                config.reification = reificationCb.isSelected();
                config.convertBodies = convertBodiesCb.isSelected();
                config.genRawBindings = genRawBindingsCb.isSelected();
                config.beautifyNames = beautifyNamesCb.isSelected();
                //config.scalaStructSetters = scalaSettersCb.isSelected();
                config.stringifyConstCStringReturnValues = config.charPtrAsString = charPtrAsString.isSelected();
                config.runtime = (Runtime) runtimeCombo.getSelectedItem();
                config.outputMode = (OutputMode) modeCombo.getSelectedItem();
                config.noComments = noCommentCb.isSelected();
                config.defaultLibrary = libraryName.getText();
                config.libraryForElementsInNullFile = libraryName.getText();
                if (extractInterfaceCb.isSelected())
                    config.extractedLibraries.put(libraryName.getText(), "I" + StringUtils.capitalize(libraryName.getText()));
//				config.addFile(getFile(), "");
                config.preprocessorConfig.includeStrings.add(sourceArea.getText());
                if (config.runtime == Runtime.BridJ) {
                    config.genCPlusPlus = true;
                } else {
                    config.genCPlusPlus = config.genCPlusPlus || sourceArea.getText().contains("//@" + JNAeratorCommandLineArgs.OptionDef.CPlusPlusGen.clSwitch);
                }
                config.cacheDir = getDir("cache");

                final PrintStream out = System.out;
                final PrintStream err = System.err;
                JTextAreaOutputStream to = new JTextAreaOutputStream(errorsArea);
                PrintStream pto = new PrintStream(to);
                System.setOut(pto);
                System.setErr(pto);

                Feedback feedback = new Feedback() {
                    @Override
                    public void setStatus(final String string) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                statusBar.setString(string);
                                statusBar.setToolTipText(string);
                            }
                        });
                    }

                    @Override
                    public void setFinished(final File toOpen) {
                        lastJNAeratedArtifact = toOpen;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                statusBar.setToolTipText("Click to re-JNAerate !");
                                statusBar.setString("JNAeration completed");
                                showJarButton.setEnabled(true);
                                showJarButton.setToolTipText(toOpen.getAbsolutePath());
                                statusBar.setValue(statusBar.getMaximum());
                                statusBar.setIndeterminate(false);
                            }
                        });
                    }

                    @Override
                    public void setFinished(Throwable e) {
                        statusBar.setToolTipText("Click to examine the JNAeration error report");
                        lastJNAeratedArtifact = e;
                        setStatus("JNAeration failed : " + e.toString());
                        statusBar.setValue(statusBar.getMinimum());
                        statusBar.setIndeterminate(false);
                        //error(null, null, e);
                    }

                    @Override
                    public void wrappersGenerated(final Result result) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (resultsListCombo.getItemCount() > 0) {
                                    resultsListCombo.setSelectedIndex(0);
                                }
                            }
                        });
                    }

                    @Override
                    public void sourcesParsed(SourceFiles sourceFiles) {
                        final SourceFiles sourceFilesClone = sourceFiles;//.clone();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String title = "Parsing Tree";
                                for (int i = sourceTabs.getTabCount(); i-- != 0;) {
                                    if (title.equals(sourceTabs.getTitleAt(i))) {
                                        sourceTabs.removeTabAt(i);
                                        break;
                                    }
                                }

                                final JTree parsedTree = new JTree(new ElementNode(null, "ROOT", sourceFilesClone));
                                final JEditTextArea selectionContent = textArea(new CCTokenMarker());

                                parsedTree.addTreeSelectionListener(new TreeSelectionListener() {
                                    public void valueChanged(TreeSelectionEvent e) {
                                        TreePath selectionPath = parsedTree.getSelectionPath();
                                        AbstractNode c = selectionPath == null ? null : (AbstractNode) selectionPath.getLastPathComponent();
                                        selectionContent.setText(c == null ? "" : c.getContent());
                                        selectionContent.scrollTo(0, 0);
                                    }
                                });
                                JPanel parsePane = new JPanel(new BorderLayout());
                                parsePane.add("West", new JScrollPane(parsedTree));
                                parsePane.add("Center", selectionContent);

                                sourceTabs.addTab(title, parsePane);
                            }
                        });
                    }
                };
                try {
                    new JNAerator(config) {
                        public PrintWriter getSourceWriter(final ClassOutputter outputter, final String path) throws IOException {
                            ResultContent c = new ResultContent(path) {
                                protected void closed() {
                                    try {
                                        PrintWriter w = outputter.getSourceWriter(path);
                                        w.write(this.getContent());
                                        w.close();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            ;
                            };
							results.add(c);
                            return c.getPrintWriter();
                        }
                    ;
					}.jnaerate(feedback);
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            generateAction.setEnabled(true);
                            sourceArea.scrollTo(0, 0);
                            JNAeratorStudio.this.setEnabled(true);
                            System.setOut(out);
                            System.setErr(err);
                            classCountLabel.setText("JNAerated classes (" + results.size() + ") :");
                            setTabTitle(resultTabs, errorsPane, "Logs (" + (errorsArea.getLineCount() - 1) + " lines)");

                        }
                    });
                }
            }
        }.start();
    }

    public static class SyntaxException extends Exception {

        public SyntaxException(String message) {
            super(message);
        }
    }

//	private void displayError(Exception e) {
//		JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
//	}
    private static void setTabTitle(JTabbedPane tabs, Component c, String string) {
        for (int i = tabs.getTabCount(); i-- != 0;) {
            Component tc = tabs.getComponent(i);//tabs.getTabComponentAt(i); 
            if (tc == c) {
                tabs.setTitleAt(i, string);
                return;
            }

        }
    }

    static Preferences prefNode() {
        return Preferences.userNodeForPackage(JNAeratorStudio.class);
    }

    public static String getPref(String name, String defaultValue) {
        return prefNode().get(JNAeratorStudio.PREF_RADIX + name, defaultValue);
    }

    public static void setPref(String name, String value) {
        prefNode().put(JNAeratorStudio.PREF_RADIX + name, value);
    }

    public static void setPref(String name, boolean value) {
        prefNode().putBoolean(JNAeratorStudio.PREF_RADIX + name, value);
    }

    public static boolean getPref(String name, boolean defaultValue) {
        return prefNode().getBoolean(JNAeratorStudio.PREF_RADIX + name, defaultValue);
    }

    public static void setPref(String name, double value) {
        prefNode().putDouble(JNAeratorStudio.PREF_RADIX + name, value);
    }

    public static double getPref(String name, double defaultValue) {
        return prefNode().getDouble(JNAeratorStudio.PREF_RADIX + name, defaultValue);
    }

    public static void setPref(String name, int value) {
        prefNode().putInt(JNAeratorStudio.PREF_RADIX + name, value);
    }

    public static int getPref(String name, int defaultValue) {
        return prefNode().getInt(JNAeratorStudio.PREF_RADIX + name, defaultValue);
    }

    public static void main(String[] args) {
        String[] prefArgs = JNAerator.getJNAeratorArgsFromPref();
        if (args.length > 0 || prefArgs != null) {
            String[] nargs = null;
            if (prefArgs != null) {
                nargs = new String[0];
            } else if (args.length == 1) {
                nargs = new String[]{"@", args[0], "-gui"};
            } else if (args.length == 2 && args[0].equals("-open")) {
                nargs = new String[]{"@", args[1], "-gui"};
            }
            if (nargs != null) {
                JNAerator.main(nargs);
                return;
            }
        }
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }

        String ver = "";
        try {
            ver = " " + ReadText.readText(JNAeratorStudio.class.getClassLoader().getResourceAsStream("VERSION"));
        } catch (Exception ex) {
        }

        final JFrame f = new JFrame((JNAeratorStudio.class.getSimpleName() + ver).trim());
        final JNAeratorStudio js = new JNAeratorStudio();
        f.getContentPane().add("Center", js);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            js.libraryName.setText(getPref("options.libraryName", "test"));
            js.directCallingCb.setSelected(getPref("options.direct", false));
            js.structsAsTopLevelClassesCb.setSelected(getPref("options.topLevelStructs", true));
            js.reificationCb.setSelected(getPref("options.reification", false));
            js.convertBodiesCb.setSelected(getPref("options.convertBodies", false));
            js.extractInterfaceCb.setSelected(getPref("options.extractInterface", false));
            js.genRawBindingsCb.setSelected(getPref("options.genRawBindings", false));
            js.beautifyNamesCb.setSelected(getPref("options.beautifyNames", false));
            js.charPtrAsString.setSelected(getPref("options.charPtrAsString", false));
            //js.scalaSettersCb.setSelected(getPref("options.scalaSetters", false));
            js.noCommentCb.setSelected(getPref("options.noComments", false));

            js.sp.setOrientation(getPref("splitPane.orientation", JSplitPane.HORIZONTAL_SPLIT));
            js.sp.setDividerLocation(getPref("splitPane.dividedLocation", 0.5));
            f.setSize(getPref("window.width", 800), getPref("height", 600));
            f.setExtendedState(getPref("window.extendedState", JFrame.NORMAL));
            js.runtimeCombo.setSelectedItem(Runtime.valueOf(getPref("options.targetRuntime", Runtime.JNAerator.name())));

        } catch (Exception ex) {
            ex.printStackTrace();
            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.setSize(800, 800);
        }

        if (false) {
            java.lang.Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                js.close(f);
            }
        });

        f.setVisible(true);

    }

    protected static double getProportionalDividerLocation(JSplitPane sp) {
        boolean hor = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT;
        int l = sp.getDividerLocation(), d = hor ? sp.getWidth() : sp.getHeight();
        sp.setOrientation(hor ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);
        return d != 0 ? l / (double) d : 0.5;
    }
}
