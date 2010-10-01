package org.aleksz.ltj;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Util {

	public static String toString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
