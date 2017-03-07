package org.pjb.e2t.el;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.excel.ExcelContent;
import org.pjb.e2t.functions.FunctionCaller;
import org.pjb.e2t.generation.GenerationContext;

/**
 * Excel Expression Language Chaine entre #{ et } De la forme :
 * <COLONNE>[<LIGNE>][|<FONCTION>(<ARGUMENTS>)][|<FONCTION>(<ARGUMENTS>)]... ou
 * <FONCTION>(<ARGUMENTS>)][|<FONCTION>(<ARGUMENTS>)]...
 * 
 * <LIGNE> facultatif --> une itération est alors procédée pour toutes les
 * lignes de l'excel (avec les bornes définies dans la configuration)
 * 
 * <ARGUMENTS> contient des chaines entre quote simple ('), ou des nombres, ou
 * des EL, séparés entre virgules (,)
 * 
 * TODO gérer les feuilles (feuille!...) TODO récursivité non gérée, TODO
 * impossibilité actuellement de mettre des paramètres dynamiques type
 * <COLONNE>[<LIGNE>] en argument des fonctions
 * 
 * TODO revoir completement la gestion des EL pour utiliser des outils standards
 * 
 * @author pjBoirleaud
 */
public class EEL {
	public final static String GLOBAL_PATTERN_STR = "\\s*([a-zA-Z]*)([0-9]*)\\s*(\\|)?\\s*([a-zA-Z0-9]*\\s*\\(.*\\))*\\s*";
	public final static Pattern GLOBAL_PATTERN = Pattern.compile(GLOBAL_PATTERN_STR);

	public final static String FUNCTION_PATTERN_STR = "\\s*([a-zA-Z0-9]+)\\s*\\((.*)\\)\\s*";
	public final static Pattern FUNCTION_PATTERN = Pattern.compile(FUNCTION_PATTERN_STR);

	public final static String ARGUMENT_PATTERN_STR = "\\s*(\\')?([^\\']*)(\\')?\\s*";
	public final static Pattern ARGUMENT_PATTERN = Pattern.compile(ARGUMENT_PATTERN_STR);

	/**
	 * Evluation du contenu d'une eel, entre #{ et } <br/>
	 * Exemple: B2|replace('old','new')
	 * 
	 * @param eel
	 * @return
	 */
	protected static String evalEEL(String eel) {
		ExcelContent ec = ExcelContent.sheet(UserSettings.getInstance().getStringValue(
				SettingKeys.GENERATION + GenerationContext.getCurrentGenerationName() + SettingKeys.SHEET));

		Matcher m = GLOBAL_PATTERN.matcher(eel);
		if (!m.matches()) {
			throw new RuntimeException("Mauvais pattern d'EEL pour la chaîne '" + eel + "'");
		}
		String column = StringUtils.isEmpty(m.group(1)) ? null : m.group(1);
		if (column == null) {
			// TODO à gérer un jour (les ExcelColumnIterator)
			throw new RuntimeException("Colonne obligatoire dans l'EEL '" + eel
					+ "' (seules les lignes sont facultatives - non géré pour le moment)");
		}
		String line = StringUtils.isEmpty(m.group(2)) ? null : m.group(2);
		String value, functions;
		if ((eel.contains("(") && !eel.contains("|")) || (eel.contains("(") && eel.indexOf("(") < eel.indexOf("|"))) {
			value = null;
			functions = eel;
			line = column = null;
		} else {
			value = ec.getCell(column, line);
			functions = StringUtils.isEmpty(m.group(4)) ? null : m.group(4);
		}
		if (functions != null) {
			return evalFunctions(functions, value, StringUtils.isNotEmpty(m.group(3)));
		}
		return value;
	}

	/**
	 * Applique les fonctions à l'EEL.
	 * 
	 * Si appel direct à la fonction (pas de colonne, ligne, et donc opérateur
	 * pipe) : La fonction est appelée classiquement
	 * 
	 * Sinon, la fonction est appelée, avec comme premier argument la valeur de
	 * ce qui est à gauche du pipe + les autres arguments à la suite, à partir
	 * du second, donc.
	 * 
	 * @param functions
	 * @param value
	 * @param leftPipe
	 * @return
	 */
	private static String evalFunctions(String functions, String value, boolean leftPipe) {
		String[] functionsTab = functions.split("\\|");
		for (int i = 0; i < functionsTab.length; ++i) {
			Matcher m = FUNCTION_PATTERN.matcher(functionsTab[i]);
			if (!m.matches()) {
				throw new RuntimeException("Mauvais pattern d'EEL.function pour la chaîne '" + functionsTab[i] + "'");
			}
			String function = m.group(1);
			String args = StringUtils.isEmpty(m.group(2)) ? null : m.group(2);
			String[] parsedArgsTab = null;
			if (args != null) {
				String[] argsTab = args.split("\\,");
				parsedArgsTab = new String[argsTab.length + (i != 0 || leftPipe ? 1 : 0)];
				if (i != 0 || leftPipe) {
					parsedArgsTab[0] = value;
				}
				for (int j = 0; j < argsTab.length; ++j) {
					Matcher m2 = ARGUMENT_PATTERN.matcher(argsTab[j]);
					if (!m2.matches()) {
						throw new RuntimeException(
								"Mauvais pattern d'EEL.function.arg pour la chaîne '" + argsTab[j] + "'");
					}
					parsedArgsTab[j + (i != 0 || leftPipe ? 1 : 0)] = EL.eval(m2.group(2));
				}
			} else {
				if (i != 0 || leftPipe) {
					parsedArgsTab = new String[1];
					parsedArgsTab[0] = value;
				}
			}
			value = FunctionCaller.call(function, parsedArgsTab);
		}
		return value;
	}
}
