package org.pjb.e2t.functions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe utilitaire autour des String.
 * 
 * @author pjBoirleaud
 */
public class StringUtils {
	/**
	 * Remplace la chaine args[0] par args[i+1], si args[0]=args[i], i pair.
	 * Sinon retourne la chaine initiale.
	 * 
	 * Exemple : #{replace('Hello', 'Hello', 'H', 'World', 'W')} --> retourne
	 * 'H' #{replace('World', 'Hello', 'H', 'World', 'W')} --> retourne 'W'
	 * #{replace('Chaine initiale', 'Hello', 'H', 'World', 'W')} --> retourne
	 * 'Chaine initiale'
	 * 
	 * @param args
	 * @return
	 */
	public static String replace(String[] args) {
		String value = args[0];
		for (int i = 1; i < args.length; i = i + 2) {
			if (value.equals(args[i])) {
				return args[i + 1];
			}
		}
		return value;
	}

	/**
	 * Concatène tous les arguments (trimés) entre eux à partir de l'indice 1,
	 * en insérant entre chacun la chaîne args[0] Si un argument est vide /
	 * blanc ou null, pas d'insertion de args[0]
	 * 
	 * Exemple : #{concatInsertingString('_', 'A','B','C')} --> retourne 'A_B_C'
	 * #{concatInsertingString('_', 'A','','C')} --> retourne 'A_C'
	 * #{concatInsertingString('_', #{A1}, #{B1})} --> utilisation d'EEL dans
	 * une EEL
	 * 
	 * @param args
	 * @return
	 */
	public static String concatInsertingString(String[] args) {
		String valueToInsert = args[0];
		String value = "";
		for (int i = 1; i < args.length; ++i) {
			value += org.apache.commons.lang.StringUtils.trim(args[i]);
			if (i + 1 < args.length && org.apache.commons.lang.StringUtils.isNotEmpty(args[i + 1])) {
				value += valueToInsert;
			}
		}
		return value;
	}

	/**
	 * Retourne la chaine 'str', mais trimée, puis substring(0, 'length'), puis
	 * paddée à droite avec des blancs, jusqu'à une longueur de 'length'.
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String length(String str, int length) {
		if (org.apache.commons.lang.StringUtils.isEmpty(str)) {
			str = "";
		}
		str = str.trim();
		if (str.length() > length) {
			str = str.substring(0, length);
		}
		return org.apache.commons.lang.StringUtils.rightPad(str, length);
	}

	/**
	 * Retourne la date du jour au format yyyy-MM-dd-HH:mm:ss (pour les
	 * générations)
	 * 
	 * @return
	 */
	public static String date() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		return df.format(new Date());
	}

	/**
	 * Retourne la date du jour au format yyyy-MM-dd-HH-mm-ss (pour les
	 * répertoires)
	 * 
	 * @return
	 */
	public static String dateDir() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return df.format(new Date());
	}

	/**
	 * Retourne le hostname
	 * 
	 * @return
	 */
	public static String hostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "";
		}
	}
}
