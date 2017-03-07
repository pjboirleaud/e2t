package org.pjb.e2t.el;

/**
 * Classe permettant l'évaluation de toutes les EEL (Excel Expression Language)
 * et PEL (Property Expression Language) contenues dans une chaîne de
 * caractères.
 * 
 * TODO revoir completement la gestion des EL pour utiliser des outils standards
 * 
 * @author pjBoirleaud
 */
public class EL {

	/**
	 * Evaluation de toutes les EEL (Excel Expression Language) et PEL
	 * (Parameter Expression Language) contenues dans la chaîne 'str'.
	 * 
	 * TODO gérer les ' ' " " contenant des { } # $
	 * 
	 * @param str
	 * @return
	 */
	public static String eval(String str) {
		String ret = "", el = "";
		int subEL = 0;
		boolean pel = false, eel = false;
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) == '\\') { // pour pouvoir échaper $ et #
				if (i + 1 == str.length()) {
					return ret + "\\";
				} else {
					ret += str.charAt(++i);
				}
				continue;
			}
			if (!pel && !eel) {
				if (str.charAt(i) == '$' && str.length() > i + 1 && str.charAt(i + 1) == '{') {
					pel = true;
					el = "";
					++i;
				} else if (str.charAt(i) == '#' && str.length() > i + 1 && str.charAt(i + 1) == '{') {
					eel = true;
					el = "";
					++i;
				} else {
					ret += str.charAt(i);
				}
			} else if ((str.charAt(i) == '$' || str.charAt(i) == '#') && str.length() > i + 1
					&& str.charAt(i + 1) == '{') {
				++subEL;
				++i;
				el += (str.charAt(i - 1) + "{");
			} else if (str.charAt(i) == '}') {
				if (subEL > 0) {
					--subEL;
					el += "}";
				} else {
					if (pel) {
						ret += PEL.evalPEL(el);
					} else {
						ret += EEL.evalEEL(el);
					}
					pel = eel = false;
				}
			} else {
				el += str.charAt(i);
			}
		}
		return ret;
	}
}
