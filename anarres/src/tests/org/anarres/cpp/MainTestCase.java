package org.anarres.cpp;

import java.io.*;

import junit.framework.Test;

import static org.anarres.cpp.Token.*;

public class MainTestCase extends BaseTestCase {

	public void testMain() throws Exception {
		Main.main(new String[] { "--version" });
	}

}
