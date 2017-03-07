package org.pjb.e2t.functions;

import java.util.HashMap;
import java.util.Map;

public class Variables {
	private static Map<String, String> map = new HashMap<String, String>();

	public static void set(String key, String value) {
		map.put(key, value);
	}

	public static String get(String key) {
		return map.get(key);
	}

}
