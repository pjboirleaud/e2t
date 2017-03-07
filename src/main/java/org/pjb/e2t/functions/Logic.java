package org.pjb.e2t.functions;

public class Logic {
	public static String ifThenElse(boolean condition, String thenStr, String elseStr) {
		return condition ? thenStr : elseStr;
	}

	public static boolean equals(String o1, String o2) {
		String s1, s2;
		if (o1 == null)
			s1 = "";
		if (o2 == null)
			s2 = "";
		s1 = o1.toString();
		s2 = o2.toString();
		return s1.equals(s2);
	}

	public static boolean notEquals(String o1, String o2) {
		return !equals(o1, o2);
	}

	/**
	 * o1 and not empty(o2) Exemple #{uneFonctionBoolean() | andNotEmpty(#{D})}
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean andNotEmpty(String o1, String o2) {
		String s1, s2;
		if (o1 == null)
			s1 = "";
		if (o2 == null)
			s2 = "";
		s1 = o1.toString();
		s2 = o2.toString();
		return "true".equals(s1) && !"".equals(s2.trim());
	}

	/**
	 * not Exemple #{isXXX() | not()}
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean not(boolean condition) {
		return !condition;
	}
}
