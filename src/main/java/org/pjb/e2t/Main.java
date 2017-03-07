package org.pjb.e2t;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.excel.ExcelContent;
import org.pjb.e2t.functions.FunctionCaller;
import org.pjb.e2t.generation.GenerationContext;
import org.pjb.e2t.templates.TemplateReader;
import org.pjb.e2t.templates.TemplateWriter;

/**
 * Main class.
 * 
 * @author pjBoirleaud
 */
public class Main {
	public static void main(String[] args) {
		System.out.println("=====-----     e2t " + UserSettings.getInstance().getStringValue(SettingKeys.E2T_VERSION)
				+ "     -----=====");

		// Backup de la génération précédente (outputs --> backups/<DATE>)
		File outputs = new File(UserSettings.getInstance().getStringValue(SettingKeys.OUTPUTS_PATH));
		if (!outputs.exists()) {
			outputs.mkdir();
		}
		File backup = new File(UserSettings.getInstance().getStringValue(SettingKeys.BACKUPS_PATH) + File.separator
				+ FunctionCaller.call("dateDir", null));
		try {
			System.out.println("Backup outputs: " + UserSettings.getInstance().getStringValue(SettingKeys.BACKUPS_PATH)
					+ File.separator + FunctionCaller.call("dateDir", null));
			FileUtils.moveDirectory(outputs, backup);
			outputs.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Exception lors de la sauvegarde du dossier de génération des templates", e);
		}

		// Backup Excel
		try {
			System.out.println("Backup excel: " + UserSettings.getInstance().getStringValue(SettingKeys.BACKUPS_PATH)
					+ File.separator + FunctionCaller.call("dateDir", null));
			FileUtils.copyFileToDirectory(new File(UserSettings.getInstance().getStringValue(SettingKeys.EXCEL_PATH)),
					backup);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Exception lors de la sauvegarde du fichier excel", e);
		}

		// Constitution de la liste des génération à partir de la conf
		List<String> generationNames = new ArrayList<String>();
		List<String> conf = UserSettings.getInstance().findStringKeys(SettingKeys.GENERATION);
		for (String k : conf) {
			k = k.substring(SettingKeys.GENERATION.length());
			k = k.substring(0, k.indexOf(SettingKeys.SEPARATOR));
			if (!generationNames.contains(k)) {
				generationNames.add(k);
			}
		}

		// Générations à partir des templates
		for (String generationName : generationNames) {
			String generationLabel = UserSettings.getInstance()
					.getStringValue(SettingKeys.GENERATION + generationName + SettingKeys.COMMENT);
			GenerationContext.setCurrentGenerationName(generationName);
			if (UserSettings.getInstance()
					.getBooleanValue(SettingKeys.GENERATION + generationName + SettingKeys.DESACTIVATE, false)) {
				System.out.println(generationLabel + " --> désactivé");
				continue;
			}
			ExcelContent ec = ExcelContent.sheet(UserSettings.getInstance()
					.getStringValue(SettingKeys.GENERATION + generationName + SettingKeys.SHEET));
			String template = TemplateReader.getTemplate(generationName);
			ec.reset();
			do {
				TemplateWriter.writeTemplateAndEvalELs(generationName, template);
				if (ec.hasNextLine()) {
					ec.nextLine();
				} else {
					break;
				}
			} while (true);
			System.out.println(generationLabel + " --> OK");
		}

	}
}
