package org.pjb.e2t.config;

/**
 * Clés utilisées dans la configuration en properties (e2t.properties)
 * 
 * @author pjBoirleaud
 */
public class SettingKeys {

	public final static String E2T_VERSION = "e2t.version";

	public final static String SEPARATOR = ".";

	public final static String FUNCTION = "function" + SEPARATOR;
	public final static String GENERATION = "generation" + SEPARATOR;

	public final static String TEMPLATES_PATH = "templates.path";
	public final static String OUTPUTS_PATH = "outputs.path";
	public final static String BACKUPS_PATH = "backups.path";
	public final static String EXCEL_PATH = "excel.path";
	public final static String LIB_EXT_PATH = "lib.ext.path";

	public final static String EXCEL_ENCODING = "excel.encoding";

	public final static String EXCEL_DEFAULT_SHEET = "excel.defaultSheet";
	public final static String EXCEL_FIRST_LINE = "excel.first.line";
	public final static String EXCEL_LAST_LINE = "excel.last.line";
	public final static String EXCEL_FIRST_COLUMN = "excel.first.column";
	public final static String EXCEL_LAST_COLUMN = "excel.last.column";

	public final static String DESACTIVATE = SEPARATOR + "deactivate";
	public final static String COMMENT = SEPARATOR + "comment";
	public final static String SHEET = SEPARATOR + "sheet";
	public final static String TEMPLATE = SEPARATOR + "template";
	public final static String OUTPUT = SEPARATOR + "output";
	public final static String FILTER = SEPARATOR + "filter";

}
