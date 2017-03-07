package org.pjb.e2t.excel;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * Lecture d'un fichier excel (/!\ attention, format 2003 ou antérieur)
 * 
 * @author pjBoirleaud
 */
public class ExcelReader {
	/**
	 * Lecture du fichier excel défini en configuration.
	 * 
	 * @return
	 */
	protected static String[][] read(String sheetName) {
		String[][] ret;
		ArrayList<String[]> table = new ArrayList<String[]>();
		String[] currentLine;

		String path = UserSettings.getInstance().getStringValue(SettingKeys.EXCEL_PATH);
		// String sheetName = UserSettings.getInstance().getStringValue(
		// SettingKeys.EXCEL_DEFAULT_SHEET);
		int firstLine = UserSettings.getInstance().getIntegerValue(SettingKeys.EXCEL_FIRST_LINE, 1) - 1;
		int firstColumn = columnNumber(UserSettings.getInstance().getStringValue(SettingKeys.EXCEL_FIRST_COLUMN));
		int lastLine = UserSettings.getInstance().getIntegerValue(SettingKeys.EXCEL_LAST_LINE, -1); // non
																									// obligatoire
		int lastColumn = columnNumber(UserSettings.getInstance().getStringValue(SettingKeys.EXCEL_LAST_COLUMN, "A"));

		Workbook workbook = null;
		try {
			WorkbookSettings settings = new WorkbookSettings();
			settings.setEncoding(UserSettings.getInstance().getStringValue(SettingKeys.EXCEL_ENCODING, "UTF-8"));
			workbook = Workbook.getWorkbook(new File(path), settings);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception lors de la lecture du fichier excel '" + path + "'.", e);
		}

		Sheet sheet = workbook.getSheet(sheetName);

		int line = firstLine;
		do {
			currentLine = new String[lastColumn + 1];
			for (int i = firstColumn; i < lastColumn + 1; ++i) {
				try {
					currentLine[i] = sheet.getCell(i, line).getContents();
				} catch (Exception e) {
					// TODO faire plus propre : hasCell ?
					break;
				}
			}
			if (areValuesAllEmpty(currentLine)) {
				break;
			}
			table.add(currentLine);
			if (lastLine != -1 && lastLine == line) {
				break;
			}
			++line;
		} while (true);

		ret = new String[table.size() + firstLine][lastColumn + 1];
		for (int i = firstLine; i < table.size() + firstLine; ++i) {
			ret[i] = table.get(i - firstLine);
		}

		return ret;
	}

	private static int columnNumber(String letters) {
		int n = 0;
		for (int i = 0; i < letters.length(); ++i) {
			n += (letters.toUpperCase().charAt(i) - 'A') + 26 * (letters.length() - i - 1);
		}
		return n;
	}

	private static boolean areValuesAllEmpty(String[] line) {
		for (int i = 0; i < line.length; ++i) {
			if (StringUtils.isNotEmpty(line[i])) {
				return false;
			}
		}
		return true;
	}
}
