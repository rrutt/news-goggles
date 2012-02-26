package com.live.rrutt.newsgoggles.lib;

import alice.tuprolog.*;
import alice.tuprolog.Number;

import java.io.*;

/**
 * @author Rick Rutt
 */
public class PrologLibrary extends Library {

	private static final long serialVersionUID = -1743358458966440903L;

	public static boolean traceEnabled = true;

	protected OutputStream outputStream = System.out;

	public static String stringValueFromTerm(Term t) {
		String result = "";

		Term tt = t.getTerm();
		if (tt instanceof Struct) {
			result = ((Struct) tt).getName();
			if (result.equals(".")) {
				result = tt.toString();						
			}
		} else if (tt instanceof Number) {
			Number n = (Number) tt;
			if (n instanceof Int) {
				result = new java.lang.Integer(n.intValue()).toString();
			} else {
				result = n.toString();
			}
		}

		return result;
	}

	public boolean trace_enabled_0() throws Exception {
		return traceEnabled;
	}

	public boolean enable_trace_0() throws Exception {
		traceEnabled = true;		
		System.out.println("+++ enable_trace.");
		return true;
	}

	public boolean disable_trace_0() throws Exception {
		traceEnabled = false;		
		System.out.println("--- disable_trace.");
		return true;
	}

	public boolean trace_1(Term arg0) throws Exception {
		if (traceEnabled) {
			String text = stringValueFromTerm(arg0);
			System.out.print(text);
		}
		return true;
	}

	public boolean trace_nl_0() throws Exception {
		if (traceEnabled) {
			System.out.print("\n");
		}
		return true;
	}
}
