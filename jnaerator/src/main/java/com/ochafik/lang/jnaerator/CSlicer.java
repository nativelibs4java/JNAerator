package com.ochafik.lang.jnaerator;

import com.ochafik.io.IOUtils;
import com.ochafik.lang.jnaerator.JNAeratorConfig;
import com.ochafik.lang.jnaerator.JNAeratorConfigUtils;
import com.ochafik.lang.jnaerator.JNAeratorParser;
import com.ochafik.lang.jnaerator.PreprocessorUtils;
import com.ochafik.lang.jnaerator.Result;
import com.ochafik.lang.jnaerator.SourceFiles;
import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.SourceFile;
import com.ochafik.util.string.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anarres.cpp.LexerException;

/**
mvn compile exec:java -Dexec.mainClass=com.ochafik.lang.jnaerator.CSlicer
 */
public class CSlicer {

    private CharSequence getContent() {
        return content;
    }

    private void pop(BlockType blockType) {
        if (blocks.isEmpty())
            throw new RuntimeException("Empty blocks, got closing " + blockType);
        if (blocks.pop() != blockType)
            System.out.println("bad pop");
    }

    enum BlockType {

        Paren, Curly, Square
    }

    enum State {

        String, Char, Normal, StringEscape, CharEscape,
        NormalSlash, SingleLineComment, MultilineComment, StarInMultilineComment
    }
    State state = State.Normal;
    Stack<BlockType> blocks = new Stack<BlockType>();
    StringBuilder content = new StringBuilder();
    int semiColonCount;
    void appendLine(String s) {
        for (char c : s.toCharArray()) {
            append(c);
        }
        append('\n');
    }

    private void append(char c) {
        if (state == State.NormalSlash) {
            switch (c) {
                case '/':
                    state = State.SingleLineComment;
                    return;
                case '*':
                    state = State.MultilineComment;
                    return;
                default:
                    content.append('/');
                    state = State.Normal;
            }
        }
        switch (state) {
            case Char:
                switch (c) {
                    case '\'':
                        state = State.Normal;
                        break;
                    case '\\':
                        state = State.CharEscape;
                        break;
                }
                break;
            case String:
                switch (c) {
                    case '"':
                        state = State.Normal;
                        break;
                    case '\\':
                        state = State.StringEscape;
                        break;
                }
                break;
            case CharEscape:
                state = State.Char;
                break;
            case StringEscape:
                state = State.String;
                break;
            case SingleLineComment:
                if (c == '\n')
                    state = State.Normal;
                break;
            case MultilineComment:
                if (c == '*')
                    state = State.StarInMultilineComment;
                break;
            case StarInMultilineComment:
                if (c == '/') {
                    state = State.Normal;
                    return;
                }
                else
                    state = State.MultilineComment;
                break;
            case Normal:
                switch (c) {
                    case '/':
                        state = State.NormalSlash;
                        break;
                    case '\'':
                        state = State.Char;
                        break;
                    case '"':
                        state = State.String;
                        break;
                    case '(':
                        blocks.push(BlockType.Paren);
                        break;
                    case '{':
                        blocks.push(BlockType.Curly);
                        break;
                    case '[':
                        blocks.push(BlockType.Square);
                        break;
                    case ')':
                        pop(BlockType.Paren);
                        break;
                    case '}':
                        pop(BlockType.Curly);
                        break;
                    case ']':
                        pop(BlockType.Square);
                        break;
                    case ';':
                        semiColonCount++;
                        break;
                }
                break;
            default:
                throw new RuntimeException("state unhandled : " + state);
        }
        switch (state) {
            case MultilineComment:
            case SingleLineComment:
            case NormalSlash:
            case StarInMultilineComment:
                break;
            default:
                content.append(c);
        }
    }

    boolean isParsable() {
        return state == State.Normal && blocks.isEmpty();
    }

    public int getSemiColonCount() {
        return semiColonCount;
    }
    
    public static String removeComments(String s) {
        CSlicer tester = new CSlicer();
        tester.appendLine(s);
        return tester.getContent().toString();
        //return s.replaceAll("(?m)/(/.*$|\\*(\\*[^/]|[^*])*?\\*/)", " ");
    }
//    public static boolean[] computeCumulativeParseability(String[] lines) {
//        int n = lines.length;
//        boolean[] ret = new boolean[n];
//        Tester tester = new Tester();
//        for (int i = 0; i < n; i++) {
//            String line = lines[i];
//            tester.append(line);
//            ret[i] = tester.isParsable();
//        }
//        return ret;
//    }
    public static String[] getLines(String source) {
        String[] lines = source.split("\n");
        List<String> ret = new ArrayList<String>(lines.length);
        for (String line : lines) {
            String s = line.trim();
            if (s.length() != 0)
                ret.add(s);
        }
        return ret.toArray(new String[ret.size()]);
            
    }
    interface Callback {
        boolean apply(CharSequence content, int semiColonCount);
    }
    public static void getParseableIncrements(String source, Callback cb) {
        String[] lines = getLines(source);
        CSlicer tester = new CSlicer();
        for (String line : lines) {
            //System.out.print('.');
            tester.appendLine(line);
            if (tester.isParsable()) {
                if (!cb.apply(tester.getContent(), tester.getSemiColonCount()))
                    break;
            } else {
                //System.err.println("Not parseable : state = " + tester.state + ", blocks = " + StringUtils.implode(tester.blocks, ","));
            }
        }
    }
    public static void main(String[] args) throws IOException, LexerException {
        String source = preprocess(new File("test.h"));
        source = removeComments(source);
        
        getParseableIncrements(source, new Callback() {
            int lastDeclCount, lastSemiCount, lastContentLength;
            int nErr;
            @Override
            public boolean apply(CharSequence content, int semiColonCount) {
                try {
                    //System.out.print('c');
                    if (semiColonCount == lastSemiCount) 
                        return true;
                    
                    String source = content.toString() + ";";
                    List<Declaration> decls = parseDeclarations(source);
                    int nDecls = decls.size();
                    int length = content.length();
                    System.out.println("Parsed " + nDecls + " declarations in " + length + " chars");
                    if (nDecls == lastDeclCount) {
                        String diff = content.subSequence(lastContentLength, length).toString();
                        String errFile = "err-" + (++nErr) + ".h";
                        System.out.println("Failed to parse new declaration in : \n\t" + diff.replaceAll("\n", "\n\t") + "\nCode was saved in " + errFile);
                        System.out.println("Last parsed declaration :");
                        System.out.println("\t" + String.valueOf(decls.get(decls.size() - 1)).replaceAll("\n", "\n\t"));
                        PrintStream out = new PrintStream(new FileOutputStream(errFile));
                        out.println(source);
                        out.println("#if 0 // Here's the diff to the last compilable code :");
                        out.println(diff);
                        out.println("#endif");
                        out.println("#if 0 // Here's the last parsed declaration");
                        out.println(String.valueOf(decls.get(decls.size() - 1)));
                        out.println("#endif");
                        out.close();
                        //return false;
                    }
                    lastContentLength = length;
                    lastSemiCount = semiColonCount;
                    lastDeclCount = nDecls;
                    return true;
                } catch (Throwable ex) {
                    Logger.getLogger(CSlicer.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
        });
    }
    static String preprocess(File source) throws IOException, LexerException {
        JNAeratorConfig config = new JNAeratorConfig();
        JNAeratorConfigUtils.autoConfigure(config);
        setParseInOneChunk(config);
        config.preprocessorConfig.includes.add("C:\\program files\\Microsoft SDKs\\Windows\\v7.0A\\Include");
        config.preprocessorConfig.includes.add("C:\\program files\\Microsoft Visual Studio 10.0\\VC\\include");
        //config.preprocessorConfig.includeStrings.add(source);
        config.addSourceFile(source, null, false, true);
        Result result = new Result(config, null, null);
        String pre = PreprocessorUtils.preprocessSources(config, Collections.EMPTY_LIST, false, result.typeConverter, null);
        return pre;
    }
    static void setParseInOneChunk(JNAeratorConfig config) {
        config.parseInOneChunk = true;
//        try {
//            Field f = JNAeratorConfig.class.getField("parseInOneChunk");
//            f.setAccessible(true);
//            f.set(null, true);
//        } catch (Throwable ex) {
//            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    static List<Declaration> parseDeclarations(String source) throws IOException, LexerException {
        JNAeratorConfig config = new JNAeratorConfig();
        JNAeratorConfigUtils.autoConfigure(config);
        //config.noCPlusPlus = true;
        setParseInOneChunk(config);
        config.preprocessorConfig.includeStrings.add(source);
        Result result = new Result(config, null, null);
        SourceFiles parse = JNAeratorParser.parse(config, result.typeConverter, null);
        
        List<Declaration> ret = new ArrayList<Declaration>();
        flatten(parse, ret);
        return ret;
    }
    static void flatten(Element element, List<Declaration> out) {
        if (element instanceof SourceFiles) {
            for (SourceFile f : ((SourceFiles)element).getSourceFiles())
                flatten(f, out);
        }
        if (element instanceof DeclarationsHolder) {
            for (Declaration d : ((DeclarationsHolder)element).getDeclarations())
                flatten(d, out);
        } else if (element instanceof Declaration) {
            out.add((Declaration)element);
        }
    }
    
}
