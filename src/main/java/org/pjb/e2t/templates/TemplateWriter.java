package org.pjb.e2t.templates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.el.EL;

/**
 * Classe d'écriture de templates avec évaluation de toutes les EL (EEL et PEL).
 * 
 * @author pjBoirleaud
 */
public class TemplateWriter {
	/**
	 * Applique les EL et écrit le template sur le file system, avec comme
	 * chemin celui défini en configuration pour la génération 'generationName',
	 * clé : generation.<generationName>.output
	 * 
	 * L'output (le chemin d'écriture) peut contenir aussi des EL.
	 * 
	 * (L'output est multiple si il y a des EEL itératives (colonnes définies,
	 * mais pas les lignes) --> le Main est maître de l'itération.)
	 * 
	 * @param generationName
	 * @param template
	 */
	public static void writeTemplateAndEvalELs(String generationName, String template) {
		String filterChain = UserSettings.getInstance()
				.getStringValue(SettingKeys.GENERATION + generationName + SettingKeys.FILTER);
		if (filterChain != null && "false".equals(EL.eval(filterChain))) {
			// System.out.println("template filtré: '" + generationName + "'");
			// System.out.println("filterChain: " +filterChain+" -
			// EL.eval(filterChain): "+EL.eval(filterChain));
			return;
		}

		BufferedWriter bw = null;
		// TODO gérer les File.Separator proprement, ça plante avec les
		// evaluations EL
		try {
			String path = (UserSettings.getInstance().getStringValue(SettingKeys.OUTPUTS_PATH)
					+ /* (File.separator.equals("/") ? "/" : "\\\\") */ "/" + UserSettings.getInstance()
							.getStringValue(SettingKeys.GENERATION + generationName + SettingKeys.OUTPUT));
			// System.out.println("===="+path);
			path = EL.eval(path);
			/*
			 * System.out.println("===="+path); if (File.separator.equals('/'))
			 * { path = path.replace("\\", "/"); } else { path =
			 * path.replace("/", "\\\\"); } System.out.println("++++"+path);
			 */
			String rootPath = path.substring(0, path.lastIndexOf("/")); // File.separator
			File rootFolder = new File(rootPath);
			if (!rootFolder.exists()) {
				System.out.println("Création dossiers: " + rootPath);
				rootFolder.mkdirs();
			}

			bw = new BufferedWriter(new FileWriter(path));

			bw.append(EL.eval(template));
			bw.close();
		} catch (Exception e) {
			System.out.println("template=" + template);
			throw new RuntimeException("Exception lors de l'écriture du template '" + generationName + "'", e);
		}
	}
}
