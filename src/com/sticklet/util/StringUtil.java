package com.sticklet.util;


public class StringUtil {
	public static String capitalize(String str) {
		String cap = null;
		if (str != null) {
			cap = str.substring(0, 1).toUpperCase().concat(str.substring(1));
		}
		return cap;
	}
}