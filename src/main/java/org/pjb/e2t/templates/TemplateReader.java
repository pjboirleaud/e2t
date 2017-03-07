package org.pjb.e2t.templates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.el.EL;

/**
 * Classe de lecture et mise en cache de templates
 * 
 * @author pjBoirleaud
 */
public class TemplateReader {
	private static final Map<String, String> cache = new HashMap<String, String>();

	/**
	 * Retourne le template correspondant à la génération 'generationName'. Le
	 * template est mis en cache à la première lecture.
	 * 
	 * @param generationName
	 * @return
	 */
	public static String getTemplate(String generationName) {
		String templateContent = cache.get(generationName);
		if (templateContent != null) {
			return templateContent;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(UserSettings.getInstance().getStringValue(SettingKeys.TEMPLATES_PATH)
					+ File.separator + UserSettings.getInstance()
							.getStringValue(SettingKeys.GENERATION + generationName + SettingKeys.TEMPLATE)));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
				if (line != null) {
					sb.append(System.lineSeparator());
				}
			}
			templateContent = sb.toString();
			cache.put(generationName, templateContent);
			br.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception lors de la lecture du template '" + generationName + "'", e);
		}
		return templateContent;
	}

	/**
	 * Retourne le template ayant comme chemin sur le file system 'fileName'. Le
	 * template est mis en cache à la première lecture.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getTemplateFromFile(String fileName) {
		fileName = UserSettings.getInstance().getStringValue(SettingKeys.TEMPLATES_PATH) + File.separator + fileName;

		String templateContent = cache.get(fileName);
		if (templateContent != null) {
			return templateContent;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(EL.eval(fileName)));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			templateContent = sb.toString();
			cache.put(fileName, templateContent);
			br.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception lors de la lecture du template '" + fileName + "'", e);
		}
		return templateContent;
	}
}
