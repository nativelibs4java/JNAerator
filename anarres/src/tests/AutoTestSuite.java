import java.lang.reflect.Modifier;

import java.io.File;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestSuite;
import junit.framework.TestCase;
import junit.framework.Test;

public class AutoTestSuite extends TestSuite {
	private String		testPackage;
	private Set<String>	testCases;
	private boolean		testAll;
	private File		root;

	public AutoTestSuite() {
		this.testPackage = System.getProperty("test.package");
		String	tcase = System.getProperty("test.case");
		if (tcase != null && tcase.length() > 0) {
			this.testCases = new HashSet(Arrays.asList(
				tcase.split("[,:]")
			));
		}
		else {
			this.testCases = null;
		}
		this.testAll = System.getProperty("test.all") != null;
		this.root = new File(System.getProperty("test.root"));

		Set<Class> tests = new HashSet();

		findClasses("", root,  tests);

		Iterator<Class> i = tests.iterator();

		while(i.hasNext()) {
			addTestSuite(i.next());
		}
	}

	public void addTestSuite(Class clazz) {
		if (testPackage != null) {
			String	name = clazz.getPackage().getName();
			if (!name.startsWith(testPackage)) {
				/*
				System.out.println("Skipping test in package '" +
						name + "' - does not start with '" +
						testPackage + "'");
				*/
				return;
			}
		}
		if (testCases != null) {
			String	name = clazz.getName();
			name = name.substring(name.lastIndexOf('.') + 1);
			if (!testCases.contains(name)) {
				/*
				System.out.println("Skipping test in class '" +
						name + "' - does not start with '" +
						testCases + "'");
				*/
				return;
			}
		}
		/*
		if (
				testCases == null && 
				testPackage == null && 
				!testAll &&
				Optional.class.isAssignableFrom(clazz)
		   )
		{
			return;
		}
		*/
		System.out.println("Adding test class '" + clazz + "'");
		super.addTestSuite(clazz);
	}

	public static Test suite() {
		return new AutoTestSuite();
	}

	private final void findClasses(String pkg, File root, Set<Class> result) {
		File[] children = root.listFiles();
		for(int i = 0; i<children.length; i++) {
			File child = children[i];
			if(child.isDirectory()) {
				findClasses(
						pkg + child.getName() + ".",
						child,
						result
						);
			} else if(child.isFile()) {
				String name = child.getName();
				// System.out.println("Checking: " + pkg + name);
				if(name.endsWith(".class") && name.indexOf('$') == -1) {
					try {
						Class test = Class.forName(pkg + 
								name.substring(0,name.length() - 6));
						int modifiers = test.getModifiers();
						if(
								(modifiers & Modifier.ABSTRACT) > 0 ||
								(modifiers & Modifier.INTERFACE) > 0 ||
								!TestCase.class.isAssignableFrom(test) ||
								TestSuite.class.isAssignableFrom(test)
						  ) 
							continue;
						result.add(test);
					} catch (ClassNotFoundException cnfe) { 
						cnfe.printStackTrace();
					}
				}
			}
		}
	}
}
