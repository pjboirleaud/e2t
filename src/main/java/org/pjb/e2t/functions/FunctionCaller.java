package org.pjb.e2t.functions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.pjb.e2t.classloader.LibExtClassLoaderFactory;
import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;

/**
 * Classe utilitaire d'appels de fonctions (méthodes statiques), Utilisée dans
 * les EEL (Excel Expression Language)
 * 
 * Exemples: #{B2 | trim()} #{B2 | fonction1('String 1', 123) | fonction2()}
 * 
 * @author pjBoirleaud
 */
public class FunctionCaller {
	/**
	 * Recherche & appel de la bonne méthode statique à partir de son nom + de
	 * la configuration en properties
	 * 
	 * Clé/Valeur en properties de la forme :
	 * NOM_FONCTION=PACKAGE.CLASSE.METHODE
	 * 
	 * @param functionName
	 * @param arguments
	 * @return
	 */
	public static String call(String functionName, String[] arguments) {
		String location = UserSettings.getInstance().getStringValue(SettingKeys.FUNCTION + functionName.trim());
		if (location == null) {
			throw new RuntimeException(
					"Fonction '" + functionName + "' non définie dans le properties de configuration.");
		}
		String className = location.substring(0, location.lastIndexOf("."));
		String methodName = location.substring(location.lastIndexOf(".") + 1, location.length());
		try {
			Class<?> clazz = Class.forName(className, true, LibExtClassLoaderFactory.getClassLoader());
			Method method = findMethod(clazz, methodName, arguments);
			try {
				Object ret = method.invoke(null, convertArgumentsTypes(method, arguments));
				if (ret == null) {
					return null;
				} else {
					return ret.toString();
				}
			} catch (Exception e) {
				throw new RuntimeException("Exception lors de l'invocation à la méthode: " + method.getName(), e);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("La classe '" + className + "' n'existe pas.");
		}
	}

	/**
	 * Types supportés : char, byte, short, int, long, float, double, boolean,
	 * String
	 * 
	 * @param clazz
	 * @param methodName
	 * @param arguments
	 * @return
	 */
	private static Method findMethod(Class<?> clazz, String methodName, String[] arguments) {
		if (arguments == null) {
			arguments = new String[0];
		}
		Method[] methods = clazz.getMethods();
		Method method;
		for (int i = 0; i < methods.length; ++i) {
			method = methods[i];
			if (!method.getName().equals(methodName) || !Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			if (method.getParameterTypes().length == arguments.length
					|| (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isArray())) {
				return method;
			}
		}
		throw new RuntimeException(
				"La méthode '" + methodName + "' n'a pas pu être trouvée dans la classe '" + clazz.getName() + "'");
	}

	/**
	 * Types supportés : char, byte, short, int, long, float, double, boolean,
	 * String
	 * 
	 * @param method
	 * @param arguments
	 * @return
	 */
	private static Object[] convertArgumentsTypes(Method method, String[] arguments) {
		if (arguments == null || arguments.length == 0) {
			return null;
		}
		Object[] ret;
		if (method.getParameterTypes()[0].isArray()) {
			ret = new Object[1];
			ret[0] = arguments;
			return ret;
		}
		ret = new Object[arguments.length];
		for (int i = 0; i < arguments.length; ++i) {
			if (method.getParameterTypes()[i].isAssignableFrom(Character.class)
					|| method.getParameterTypes()[i].isAssignableFrom(char.class)) {
				ret[i] = (arguments[i] != null && !"".equals(arguments[i].trim())) ? arguments[i].charAt(0) : null;
			} else if (method.getParameterTypes()[i].isAssignableFrom(Byte.class)
					|| method.getParameterTypes()[i].isAssignableFrom(byte.class)) {
				ret[i] = Byte.parseByte(arguments[i]);
			} else if (method.getParameterTypes()[i].isAssignableFrom(Short.class)
					|| method.getParameterTypes()[i].isAssignableFrom(short.class)) {
				ret[i] = Short.parseShort(arguments[i]);
			} else if (method.getParameterTypes()[i].isAssignableFrom(Integer.class)
					|| method.getParameterTypes()[i].isAssignableFrom(int.class)) {
				ret[i] = Integer.parseInt(arguments[i]);
			} else if (method.getParameterTypes()[i].isAssignableFrom(Long.class)
					|| method.getParameterTypes()[i].isAssignableFrom(long.class)) {
				ret[i] = Long.parseLong(arguments[i]);
			} else if (method.getParameterTypes()[i].isAssignableFrom(Float.class)
					|| method.getParameterTypes()[i].isAssignableFrom(float.class)) {
				ret[i] = Float.parseFloat(arguments[i]);
			} else if (method.getParameterTypes()[i].isAssignableFrom(Double.class)
					|| method.getParameterTypes()[i].isAssignableFrom(double.class)) {
				ret[i] = Double.parseDouble(arguments[i]);
			} else if (method.getParameterTypes()[i].isAssignableFrom(Boolean.class)
					|| method.getParameterTypes()[i].isAssignableFrom(boolean.class)) {
				ret[i] = Boolean.parseBoolean(arguments[i]);
			} else {
				ret[i] = arguments[i].toString();
			}
		}

		return ret;
	}
}
