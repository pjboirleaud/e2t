package org.pjb.e2t.functions;

import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.el.EL;
import org.pjb.e2t.excel.ExcelContent;
import org.pjb.e2t.generation.GenerationContext;
import org.pjb.e2t.templates.TemplateReader;

/**
 * Pour un template donné, appel à un sous-template autant de fois que de lignes
 * à lire dans l'excel, avec update de l'iterateur de l'excel à chaque passe.
 * 
 * @author pjBoirleaud
 *
 */
public class TemplatesIterator {
	/**
	 * Pour un template donné, appel à un sous-template autant de fois que de
	 * lignes à lire dans l'excel, avec update de l'iterateur de l'excel à
	 * chaque passe.
	 * 
	 * @param file
	 *            path du fichier sous-template
	 * @return
	 */
	public static String forEachLine(String file) {
		ExcelContent ec = ExcelContent.sheet(UserSettings.getInstance().getStringValue(
				SettingKeys.GENERATION + GenerationContext.getCurrentGenerationName() + SettingKeys.SHEET));
		String template = TemplateReader.getTemplateFromFile(file);
		String content = "";
		ec.saveState();
		ec.reset();
		do {
			content += EL.eval(template);
			if (ec.hasNextLine()) {
				ec.nextLine();
			} else {
				break;
			}
		} while (true);
		ec.restoreState();
		return content;
	}

	/**
	 * Pour un template donné, appel à un sous-template autant de fois que de
	 * lignes à lire dans l'excel, avec update de l'iterateur de l'excel à
	 * chaque passe.
	 * 
	 * @param file
	 *            path du fichier sous-template
	 * @param callBackFilter
	 *            callback appelé à chaque itération : le contenu (de
	 *            l'itération) est remplacé par le retour du callback (le
	 *            contenu de l'itération est mis en premier argument du
	 *            callback)
	 * @return
	 */
	public static String forEachLine(String file, String callBackFilter) {
		ExcelContent ec = ExcelContent.sheet(UserSettings.getInstance().getStringValue(
				SettingKeys.GENERATION + GenerationContext.getCurrentGenerationName() + SettingKeys.SHEET));
		String template = TemplateReader.getTemplateFromFile(file);
		String content = "";
		ec.saveState();
		ec.reset();
		do {
			content += FunctionCaller.call(callBackFilter, new String[] { EL.eval(template) });
			if (ec.hasNextLine()) {
				ec.nextLine();
			} else {
				break;
			}
		} while (true);
		ec.restoreState();
		return content;
	}
}
