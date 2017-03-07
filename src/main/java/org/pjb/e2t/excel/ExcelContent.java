package org.pjb.e2t.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;

/**
 * Classe utilitaire de lecture du contenu lu depuis l'excel.
 * 
 * Lecture Colonne/Ligne ou Ligne
 * 
 * Dans le dernier cas, une itération est alors procédée pour toutes les lignes
 * de l'excel (avec les bornes définies dans la configuration)
 * 
 * @author pjBoirleaud
 */
public class ExcelContent {

	private static Map<String, ExcelContent> instances = new HashMap<String, ExcelContent>();

	public static ExcelContent sheet(String sheetNameOrBlank) {
		if (StringUtils.isEmpty(sheetNameOrBlank)) {
			sheetNameOrBlank = UserSettings.getInstance().getStringValue(SettingKeys.EXCEL_DEFAULT_SHEET);
		}
		if (!instances.containsKey(sheetNameOrBlank)) {
			ExcelContent ec = new ExcelContent(sheetNameOrBlank);
			instances.put(sheetNameOrBlank, ec);
			return ec;
		}
		return instances.get(sheetNameOrBlank);
	}

	private String[][] table;
	private boolean table_read = false;
	private int currentLine = 0;
	private boolean lineIterator = false;
	private int saved_currentLine = 0;
	private boolean saved_lineIterator = false;
	private String sheetName;

	private ExcelContent(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * Remise à zéro de l'état de l'excel, c.a.d. l'éventuel état de l'itération
	 * ligne à ligne dans l'excel
	 */
	public void reset() {
		if (!table_read) {
			table = ExcelReader.read(sheetName);
			table_read = true;
		}
		currentLine = UserSettings.getInstance().getIntegerValue(SettingKeys.EXCEL_FIRST_LINE, 1) - 1;
		lineIterator = false;
	}

	/**
	 * Sauvegarde de l'état d'itération dans l'excel
	 */
	public void saveState() {
		saved_currentLine = currentLine;
		saved_lineIterator = lineIterator;
	}

	/**
	 * Restauration de l'état d'itération précédemment sauvegardé
	 */
	public void restoreState() {
		currentLine = saved_currentLine;
		lineIterator = saved_lineIterator;
	}

	/**
	 * En cas d'itération sur les lignes, y a t-il une nouvelle ligne à lire ?
	 * 
	 * @return
	 */
	public boolean hasNextLine() {
		return lineIterator && currentLine < table.length - 1;
	}

	/**
	 * Déplacement du curseur d'itération sur la prochaine ligne.
	 */
	public void nextLine() {
		if (lineIterator) {
			++currentLine;
		}
	}

	/**
	 * Retourne le contenu de la ligne 'n'.
	 * 
	 * @param n
	 *            0..
	 * @return
	 */
	public String[] getLine(int n) {
		return table[n];
	}

	/**
	 * Retourne le contenu de la ligne 'n'.
	 * 
	 * @param n
	 *            format excel: "1"..
	 * @return
	 */
	public String[] getLine(String n) {
		return StringUtils.isEmpty(n) ? getLine() : table[lineNumber(n)];
	}

	/**
	 * Retourne la ligne courante (itérateur)
	 * 
	 * @return
	 */
	public String[] getLine() {
		lineIterator = true;
		return table[currentLine];
	}

	/**
	 * Retourne la cellule column/line.
	 * 
	 * @param column
	 *            0..
	 * @param line
	 *            0..
	 * @return
	 */
	public String getCell(int column, int line) {
		return table[line][column];
	}

	/**
	 * Retourne la cellule column/line.
	 * 
	 * @param column
	 *            format excel: "A"..
	 * @param line
	 *            format excel: "1"..
	 * @return
	 */
	public String getCell(String column, String line) {
		return StringUtils.isEmpty(line) ? getCell(column) : table[lineNumber(line)][columnNumber(column)];
	}

	/**
	 * Retourne la cellule column/ligne courante (itérateur).
	 * 
	 * @param column
	 *            0..
	 * @return
	 */
	public String getCell(int column) {
		lineIterator = true;
		return table[currentLine][column];
	}

	/**
	 * Retourne la cellule column/ligne courante (itérateur).
	 * 
	 * @param column
	 *            format excel: "A"..
	 * @return
	 */
	public String getCell(String column) {
		lineIterator = true;
		try {
			return table[currentLine][columnNumber(column)];
		} catch (Exception e) {
			System.out.println("currentLine: " + currentLine);
			System.out.println("column: " + column);
			System.out.println("columnNumber(column): " + columnNumber(column));
			System.out.println("table: " + table.length);
			if (table.length > 0) {
				System.out.println("table[]: " + table[0].length);
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retourne le numéro de colonne (0..) à partir du numéro de colonne format
	 * excel, valeurs à partir de de "A"
	 * 
	 * @param letters
	 * @return
	 */
	private int columnNumber(String letters) {
		int n = 0;
		for (int i = 0; i < letters.length(); ++i) {
			n += (letters.toUpperCase().charAt(i) - 'A') + 26 * (letters.length() - i - 1);
		}
		return n;
	}

	/**
	 * Retourne le numéro de ligne (0..) à partir du numéro de ligne format
	 * excel, valeurs à partir de de "1"
	 * 
	 * @param numbers
	 * @return
	 */
	private int lineNumber(String numbers) {
		return Integer.parseInt(numbers) - 1;
	}
}
