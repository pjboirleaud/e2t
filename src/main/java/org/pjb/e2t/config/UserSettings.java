package org.pjb.e2t.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * Classe utilitaire de lecture depuis la configuration (e2t.properties)
 * 
 * @author pjBoirleaud
 */
public class UserSettings {
	private static UserSettings instance = new UserSettings("e2t.properties"); // singleton

	private String path;

	private Map<String, String> cache = new HashMap<String, String>();

	/**
	 * Default constructor.
	 * 
	 * @param path
	 */
	public UserSettings(String path) {
		this.path = path;
		loadProperties();
	}

	/**
	 * Récupération de la configuration par défaut : e2t.properties
	 * 
	 * @return
	 */
	public static UserSettings getInstance() {
		return instance;
	}

	/**
	 * Charge toute la configuration et la met en cache.
	 */
	private void loadProperties() {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File(path)));
			@SuppressWarnings("unchecked")
			Enumeration<String> propertyNames = (Enumeration<String>) properties.propertyNames();
			while (propertyNames.hasMoreElements()) {
				String key = propertyNames.nextElement();
				cache.put(key, properties.getProperty(key));
			}
		} catch (IOException e) {
			throw new RuntimeException("@UserSettings.load / could not load properties '" + path + "'");
		}
	}

	/**
	 * Retourne la liste des clés du properties débutant par la valeur de
	 * 'prefix'.
	 * 
	 * @param prefix
	 * @return
	 */
	public List<String> findStringKeys(String prefix) {
		List<String> list = new ArrayList<String>();
		for (Entry<String, String> entry : cache.entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				list.add(entry.getKey());
			}
		}
		return list;
	}

	/**
	 * Retourne la valeur en configuration pour la clé 'key' (String).
	 * 
	 * @param key
	 * @return
	 */
	public String getStringValue(String key) {
		return cache.get(key);
	}

	/**
	 * Retourne la valeur en configuration pour la clé 'key' (String). <br/>
	 * Si la clé n'existe pas, retourne la valeur par défaut 'defaultValue'.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getStringValue(String key, String defaultValue) {
		String value = cache.get(key);
		return StringUtils.isNotEmpty(value) ? value : defaultValue;
	}

	/**
	 * Retourne la valeur en configuration pour la clé 'key' (Boolean:
	 * true/false/TRUE/FALSE). <br/>
	 * Si la clé n'existe pas, retourne la valeur par défaut 'defaultValue'.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanValue(String key, boolean defaultValue) {
		String value = getStringValue(key);
		if (StringUtils.isNotEmpty(value)) {
			return Boolean.TRUE.toString().equals(value.toLowerCase());
		}
		return defaultValue;
	}

	/**
	 * Retourne la valeur en configuration pour la clé 'key' (Integer). <br/>
	 * Si la clé n'existe pas, retourne la valeur par défaut 'defaultValue'.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getIntegerValue(String key, int defaultValue) {
		String value = getStringValue(key);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("@UserSettings.getIntegerValue / bad properties value format in file '"
						+ path + "', key='" + key + "', value='" + value + "'");
			}
		}
		return defaultValue;
	}

	/**
	 * Retourne la valeur en configuration pour la clé 'key' (Float). <br/>
	 * Si la clé n'existe pas, retourne la valeur par défaut 'defaultValue'.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public float getFloatValue(String key, float defaultValue) {
		String value = getStringValue(key);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("@UserSettings.getFloatValue / bad properties value format in file '" + path
						+ "', key='" + key + "', value='" + value + "'");
			}
		}
		return defaultValue;
	}
}
