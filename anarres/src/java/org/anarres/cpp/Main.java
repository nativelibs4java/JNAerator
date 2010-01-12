/*
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.anarres.cpp;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import static org.anarres.cpp.Token.*;

/**
 * (Currently a simple test class).
 */
public class Main {

    private static class Option extends LongOpt {
        private String  eg;
        private String  help;
        public Option(String word, int arg, int ch,
                        String eg, String help) {
            super(word, arg, null, ch);
            this.eg = eg;
            this.help = help;
        }
    }

	private static final Option[]	OPTS = new Option[] {
		new Option("help",   LongOpt.NO_ARGUMENT,       'h', null,
			"Displays help and usage information."),
		new Option("define", LongOpt.REQUIRED_ARGUMENT, 'D', "name=definition",
			"Defines the given macro."),
		new Option("undefine", LongOpt.REQUIRED_ARGUMENT, 'U', "name",
			"Undefines the given macro, previously either builtin or defined using -D."),
		new Option("include", LongOpt.REQUIRED_ARGUMENT, 1, "file",
			"Process file as if \"#" + "include \"file\"\" appeared as the first line of the primary source file."),
		new Option("incdir", LongOpt.REQUIRED_ARGUMENT, 'I', "dir",
			"Adds the directory dir to the list of directories to be searched for header files."),
		new Option("iquote", LongOpt.REQUIRED_ARGUMENT, 0, "dir",
			"Adds the directory dir to the list of directories to be searched for header files included using \"\"."),
		new Option("warning", LongOpt.REQUIRED_ARGUMENT, 'W', "type",
			"Enables the named warning class ("  + getWarnings() + ")."),
		new Option("no-warnings", LongOpt.NO_ARGUMENT, 'w', null,
			"Disables ALL warnings."),
		new Option("verbose", LongOpt.NO_ARGUMENT, 'v', null,
			"Operates incredibly verbosely."),
		new Option("debug", LongOpt.NO_ARGUMENT, 3, null,
			"Operates incredibly verbosely."),
		new Option("version", LongOpt.NO_ARGUMENT, 2, null,
			"Prints jcpp's version number (" + Version.getVersion() + ")"),
	};

	private static CharSequence getWarnings() {
		StringBuilder	buf = new StringBuilder();
		for (Warning w : Warning.values()) {
			if (buf.length() > 0)
				buf.append(", ");
			String	name = w.name().toLowerCase();
			buf.append(name.replace('_', '-'));
		}
		return buf;
	}

	public static void main(String[] args) throws Exception {
		(new Main()).run(args);
	}

	public void run(String[] args) throws Exception {
		Option[]opts = OPTS;
        String	sopts = getShortOpts(opts);
        Getopt	g = new Getopt("jcpp", args, sopts, opts);
		int		c;
		String	arg;
		int		idx;

		Preprocessor	pp = new Preprocessor();
		pp.addFeature(Feature.DIGRAPHS);
		pp.addFeature(Feature.TRIGRAPHS);
		pp.addFeature(Feature.LINEMARKERS);
		pp.addWarning(Warning.IMPORT);
		pp.setListener(new PreprocessorListener());
		pp.addMacro("__JCPP__");
		pp.getSystemIncludePath().add("/usr/local/include");
		pp.getSystemIncludePath().add("/usr/include");
		pp.getFrameworksPath().add("/System/Library/Frameworks");
		pp.getFrameworksPath().add("/Library/Frameworks");
		pp.getFrameworksPath().add("/Local/Library/Frameworks");

        GETOPT: while ((c = g.getopt()) != -1) {
            switch (c) {
				case 'D':
					arg = g.getOptarg();
					idx = arg.indexOf('=');
					if (idx == -1)
						pp.addMacro(arg);
					else
						pp.addMacro(arg.substring(0, idx),
									arg.substring(idx + 1));
					break;
				case 'U':
					pp.getMacros().remove(g.getOptarg());
					break;
				case 'I':
					pp.getSystemIncludePath().add(g.getOptarg());
					break;
				case 0:	// --iquote=
					pp.getQuoteIncludePath().add(g.getOptarg());
					break;
				case 'W':
					arg = g.getOptarg().toUpperCase();
					arg = arg.replace('-', '_');
					if (arg.equals("ALL"))
						pp.addWarnings(EnumSet.allOf(Warning.class));
					else
						pp.addWarning(Enum.valueOf(Warning.class, arg));
					break;
                case 'w':
					pp.getWarnings().clear();
					break;
				case 1:	// --include=
					// pp.addInput(new File(g.getOptarg()));
					// Comply exactly with spec.
					pp.addInput(new StringLexerSource(
						"#" + "include \"" + g.getOptarg() + "\"\n"
					));
					break;
				case 2:	// --version
					version(System.out);
					return;
				case 'v':
					pp.addFeature(Feature.VERBOSE);
					break;
				case 3:
					pp.addFeature(Feature.DEBUG);
					break;
                case 'h':
                    usage(getClass().getName(), opts);
					return;
                default:
                    throw new Exception("Illegal option " + (char)c);
                case '?':
					continue;	/* Make failure-proof. */
			}
		}

		for (int i = g.getOptind(); i < args.length; i++)
			pp.addInput(new FileLexerSource(new File(args[i])));
		if (g.getOptind() == args.length)
			pp.addInput(new InputLexerSource(System.in));

		if (pp.getFeature(Feature.VERBOSE)) {
			System.err.println("#"+"include \"...\" search starts here:");
			for (String dir : pp.getQuoteIncludePath())
				System.err.println("  " + dir);
			System.err.println("#"+"include <...> search starts here:");
			for (String dir : pp.getSystemIncludePath())
				System.err.println("  " + dir);
			System.err.println("End of search list.");
		}

		try {
			for (;;) {
				Token	tok = pp.token();
				if (tok == null)
					break;
				if (tok.getType() == Token.EOF)
					break;
				System.out.print(tok.getText());
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			Source	s = pp.getSource();
			while (s != null) {
				System.err.println(" -> " + s);
				s = s.getParent();
			}
		}

	}

	private void version(PrintStream out) {
		out.println("Anarres Java C Preprocessor version " + Version.getVersion());
		out.println("Copyright (C) 2008 Shevek (http://www.anarres.org/).");
		out.println("This is free software; see the source for copying conditions.  There is NO");
		out.println("warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
	}


    private static String getShortOpts(Option[] opts)
                        throws Exception {
        StringBuilder   buf = new StringBuilder();
        for (int i = 0; i < opts.length; i++) {
            char    c = (char)opts[i].getVal();
			if (!Character.isLetterOrDigit(c))
				continue;
            for (int j = 0; j < buf.length(); j++)
                if (buf.charAt(j) == c)
                    throw new Exception(
                            "Duplicate short option " + c
                                );
            buf.append(c);
            switch (opts[i].getHasArg()) {
                case LongOpt.NO_ARGUMENT:
                    break;
                case LongOpt.OPTIONAL_ARGUMENT:
                    buf.append("::");
                    break;
                case LongOpt.REQUIRED_ARGUMENT:
                    buf.append(":");
                    break;
            }
        }
        return buf.toString();
    }

    /* This is incomplete but nearly there. */
    /**
     * Wraps a string.
     *
     * The output form is:
     * <pre>
     * prefix     in[0]
     * &lt;--indent-&gt; in[1]
     * &lt;--indent-&gt; in[2]
     * &lt;-----width----&gt;
     * </pre>
     */
    /* XXX There's some of this in commons. */
    private static String wrap(String in, String prefix,
                            int indent, int width) {
        StringBuilder   buf = new StringBuilder(prefix);

        while (buf.length() < indent)
            buf.append(' ');

        int             start = 0;

        while (start < in.length()) {
            while (start < in.length() &&
                    Character.isWhitespace(in.charAt(start)))
                start++;

            int     end = start + width - indent;

            if (end > in.length()) {
                buf.append(in.substring(start));
                break;
            }

            int     idx = end;
            while (!Character.isWhitespace(in.charAt(idx)))
                idx--;

            if (idx == start) {
                idx = end - 1;
                buf.append(in.substring(start, idx));
                buf.append('-');
            }
            else {
                buf.append(in.substring(start, idx));
                start = idx;
            }

            start = idx;
        }

        return buf.toString();
	}

    private static void usage(String command, Option[] options) {
        StringBuilder   text = new StringBuilder("Usage: ");
        text.append(command).append('\n');
        for (int i = 0; i < options.length; i++) {
            StringBuilder   line = new StringBuilder();
            Option          opt = options[i];
            line.append("    --").append(opt.getName());
            switch (opt.getHasArg()) {
                case LongOpt.NO_ARGUMENT:
                    break;
                case LongOpt.OPTIONAL_ARGUMENT:
                    line.append("[=").append(opt.eg).append(']');
                    break;
                case LongOpt.REQUIRED_ARGUMENT:
                    line.append('=').append(opt.eg);
                    break;
            }
			if (Character.isLetterOrDigit(opt.getVal()))
				line.append(" (-").append((char)opt.getVal()).append(")");
            if (line.length() < 30) {
                while (line.length() < 30)
                    line.append(' ');
            }
            else {
                line.append('\n');
                for (int j = 0; j < 30; j++)
                    line.append(' ');
            }
            /* This should use wrap. */
            line.append(opt.help);
            line.append('\n');
            text.append(line);
        }

        System.out.println(text);
    }



#if (false)
	public static void oldmain(String[] args) throws Exception {
		List<String>	path = new ArrayList<String>();
		path.add("/usr/include");
		path.add("/usr/local/include");
		path.add("/usr/lib/gcc/i686-pc-linux-gnu/4.1.2/include");

		Source			source = new FileLexerSource(new File(args[0]));
		Preprocessor	pp = new Preprocessor(source);
		pp.setSystemIncludePath(path);

		for (int i = 1; i < args.length; i++) {
			pp.push_source(new FileLexerSource(new File(args[i])),true);
		}

		Macro			m = new Macro("__WORDSIZE");
		m.addToken(new Token(INTEGER, -1, -1, "32", Integer.valueOf(32)));
		pp.addMacro(m);

		m = new Macro("__STDC__");
		m.addToken(new Token(INTEGER, -1, -1, "1", Integer.valueOf(1)));
		pp.addMacro(m);

		try {
			for (;;) {
				Token	tok = pp.token();
				if (tok != null && tok.getType() == Token.EOF)
					break;
				switch (2) {
					case 0:
						System.out.print(tok);
						break;
					case 1:
						System.out.print("[" + tok.getText() + "]");
						break;
					case 2:
						System.out.print(tok.getText());
						break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Source	s = pp.getSource();
			while (s != null) {
				System.out.println(" -> " + s);
				s = s.getParent();
			}

			/*
			Iterator<State>	it = pp.states.iterator();
			while (it.hasNext()) {
				System.out.println(" -? " + it.next());
			}
			*/

		}

		Map<String,Macro>	macros = pp.getMacros();
		List<String>		keys = new ArrayList<String>(
				macros.keySet()
					);
		Collections.sort(keys);
		Iterator<String>	mt = keys.iterator();
		while (mt.hasNext()) {
			String	key = mt.next();
			Macro	macro = macros.get(key);
			System.out.println("#" + "macro " + macro);
		}

	}
#end

}
