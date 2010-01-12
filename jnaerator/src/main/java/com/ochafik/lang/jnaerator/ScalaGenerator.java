package com.ochafik.lang.jnaerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.ochafik.io.ReadText;
import com.ochafik.lang.jnaerator.Result.ClassWritingNotifiable;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.runtime.JNAeratorRuntime;
import com.ochafik.util.string.StringUtils;
import com.sun.jna.Callback;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;

/*
include com/ochafik/lang/jnaerator/runtime/scala/*.scala.part
*/
public class ScalaGenerator implements ClassWritingNotifiable {

	class ScalaClassFile {
		public String path, name;
		public StringWriter content = new StringWriter();
	}
	Map<String, ScalaClassFile> outByLib = new HashMap<String, ScalaClassFile>();
	Result result;
	public ScalaGenerator(Result result) throws FileNotFoundException {
		this.result = result;
		result.classWritingNotifiables.add(this);
	}
	
	@Override
	public Struct writingClass(Identifier fullClassName, Struct interf, Signatures signatures, String currentLibrary) {
		
		try {
			visit(fullClassName, interf, currentLibrary);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return interf;
	}

	PrintWriter openFile(File f) throws FileNotFoundException {
		File p = f.getAbsoluteFile().getParentFile();
		if (!p.exists())
			p.mkdirs();
		result.feedback.setStatus("Generating " + f);
		return new PrintWriter(f);
	}
	StringWriter getLibOut(String pack, String lib) throws FileNotFoundException {
		ScalaClassFile out = outByLib.get(lib);
		if (out == null) {
			outByLib.put(lib, out = new ScalaClassFile());
			out.name = getLibScalaClassName(lib);
			out.path = pack.replace('.', '/').replace('\\', '/') + '/' + StringUtils.capitalize(lib) + ".scala";
		}
		return out.content;
	}
	private String getLibScalaClassName(String lib) {
		return lib;// + "_scala";
	}

	private void visit(final Identifier fullClassName, Element interf, String currentLibrary) throws FileNotFoundException {
		final Identifier pack = fullClassName.resolveAllButLastIdentifier();
		String spack = result.getLibraryPackage(currentLibrary).toString();
		final PrintWriter out = new PrintWriter(getLibOut(spack, currentLibrary));
		interf.accept(new Scanner() {
			Stack<Identifier> path = new Stack<Identifier>();
			{
				path.add(pack);
			}
			@Override
			public void visitStruct(Struct struct) {
				path.push(ident(path.peek(), struct.getTag()));
				super.visitStruct(struct);
				path.pop();
			}
			@Override
			public void visitJavaInterface(Struct struct) {
				super.visitJavaInterface(struct);
				
				if (struct.getParents().contains(ident(Callback.class))) {
					Function f = null;
					for (Declaration d : struct.getDeclarations()) {
						if (d instanceof Function) {
							f = (Function) d;
							break;
						}
					}
					if (f != null) {
						List<Arg> args = f.getArgs();
						List<String> argTypes = new ArrayList<String>(),
							argNames = new ArrayList<String>(),
							argDefs = new ArrayList<String>();
						String rt = getScalaType(f.getValueType());
						for (Arg a : args) {
							String vt = getScalaType(a.getValueType());
							String n = a.getName();
							argTypes.add(vt);
							argNames.add(n);
							argDefs.add(n + ": " + vt);
						}
						String cbClassName = struct.getTag().toString(), scbClassName = cbClassName + "_scala";
						String cbClassPath = path.peek().toString();
						int ac = argTypes.size();
						String fsig = (ac == 0 ? "" : ac == 1 ? argTypes.get(0)  : "(" + StringUtils.implode(argTypes, ", ") + ")") + " => " + rt;
						out.println("class " + scbClassName + "(scala_func: " + fsig + ") extends " + cbClassPath + " {");
						out.println("\toverride def " + f.getName() + "(" + StringUtils.implode(argDefs, ", ") + "): " + rt + " = {");
						out.println("\t\tscala_func(" + StringUtils.implode(argNames, ", ") + ")");
						out.println("\t}");
						out.println("}");
						out.println("implicit def scala_func2" + scbClassName + "(scala_func: " + fsig + ") = {");
						out.println("\tnew " + scbClassName + "(scala_func)");
						out.println("}");
					}
				}
			}
		});
		out.flush();
	}
	protected String getScalaType(TypeRef valueType) {
		String vt = valueType.toString();
		if (vt.equals("void"))
			vt = "Unit";
		return vt;
	}
	public void jnaerationCompleted() throws IOException {
		List<String> availableLibs = new ArrayList<String>();
 		for (Map.Entry<String, ScalaClassFile> e : outByLib.entrySet()) {
 			ScalaClassFile f = e.getValue();
 			String lib = e.getKey();
			f.content.close();
			String s = f.content.toString().trim();
			if (s.length() == 0)
				continue;
			availableLibs.add(lib);
			PrintWriter out = openFile(new File(result.config.scalaOut, f.path));
			out.println("trait " + f.name + " extends " + result.getLibraryClassFullName(lib) + " {");
			out.println(s);
			out.println("}");
			out.close();
		}
 		outputSampleScalaSource(availableLibs);
		outputScalaRuntime(availableLibs);
		
	}

	URL getScalaPartResource(String name) throws IOException {
		String path = "com/ochafik/lang/jnaerator/runtime/scala/" + name + ".scala.part";
		URL url = JNAeratorRuntime.class.getClassLoader().getResource(path);
		return url;
	}
	static final String SCALA_JNAERATOR_RT_CLASS_NAME = "ScalaJNAerator";
	static final String SCALA_JNA_RT_CLASS_NAME = "ScalaJNA";
	static final String SCALA_ROCOCOA_RT_CLASS_NAME = "ScalaRococoa";
	
	private void outputScalaRuntime(List<String> availableLibs) throws IOException {
		PrintWriter out = openFile(new File(result.config.scalaOut, SCALA_JNAERATOR_RT_CLASS_NAME + ".scala"));
		out.println(ReadText.readText(getScalaPartResource(SCALA_JNA_RT_CLASS_NAME)));
		boolean objc = result.hasObjectiveC();
		
		if (objc)
			out.println(ReadText.readText(getScalaPartResource(SCALA_ROCOCOA_RT_CLASS_NAME)));
		out.print("trait " + SCALA_JNAERATOR_RT_CLASS_NAME + " extends " + SCALA_JNA_RT_CLASS_NAME);
		if (objc)
			out.print(" with " + SCALA_ROCOCOA_RT_CLASS_NAME);
		//out.println(" {");
		out.close();
	}

	private void outputSampleScalaSource(List<String> availableLibs) throws FileNotFoundException {

		PrintWriter out = openFile(new File(result.config.scalaOut, "JNAeratorSample.scala"));
		out.println("import com.sun.jna._;");
		out.println("import com.sun.jna.ptr._;");
		if (result.hasObjectiveC())
			out.println("import org.rococoa._;");
		out.println();
		out.println("import com.ochafik.lang.jnaerator.runtime._;");
		out.println("import com.ochafik.lang.jnaerator.runtime.globals._;");
		out.println();
		out.println("import " + SCALA_JNAERATOR_RT_CLASS_NAME + "._;");
		for (String lib : availableLibs) {
			String scn = getLibScalaClassName(lib);
//			out.println("import " + scn + ";");
			out.println("import " + scn + "._;");
			
//			Identifier fn = result.getLibraryClassFullName(lib);
//			out.println("import " + fn + "._;");
//			Identifier pack = fn.resolveAllButLastIdentifier();
//			if (pack != null)
//				out.println("import " + pack + "._;");
		}
		out.println();
		
		out.println("object ExampleApp extends Application {");
		//out.println("\twith " + SCALA_JNAERATOR_RT_CLASS_NAME);
//		for (String lib : availableLibs)
//			out.println("\twith " + getLibScalaClassName(lib));
//		out.println("{");
		out.println("  override def main(args : Array[String]) : Unit = {");
		out.println("    ");
		out.println("  }");
		out.println("}");
		out.close();
	}
}
