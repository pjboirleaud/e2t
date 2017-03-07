package org.pjb.e2t.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.functions.FunctionCaller;

public class LibExtClassLoaderFactory {

	private static ClassLoader cl = null;

	public static ClassLoader getClassLoader() {
		if (cl != null) {
			return cl;
		}

		String libExtPath = UserSettings.getInstance().getStringValue(SettingKeys.LIB_EXT_PATH);

		File libExt = new File(libExtPath);
		if (!libExt.isDirectory()) {
			System.err.println("Could not scan " + libExtPath + " : folder does not exists");
			return FunctionCaller.class.getClassLoader();
		}

		List<URL> foundFiles = new ArrayList<URL>();
		scanForJars(libExt, foundFiles);
		if (foundFiles.size() == 0) {
			return FunctionCaller.class.getClassLoader();
		}

		cl = new URLClassLoader(toURLArray(foundFiles), ClassLoader.getSystemClassLoader());
		return cl;
	}

	private static void scanForJars(File folder, List<URL> foundFiles) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				scanForJars(fileEntry, foundFiles);
			} else {
				String fileName = fileEntry.getName();
				if (fileName.endsWith("jar")) {
					try {
						System.out.println("LibExtClassLoader: found '" + fileName + "' to load.");
						foundFiles.add(fileEntry.toURI().toURL());
					} catch (Exception e) {
						System.err.println("Exception @ LibExtClassLoader.scanForJars");
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static URL[] toURLArray(List<URL> list) {
		URL[] array = new URL[list.size()];
		int i = 0;
		for (URL url : list) {
			array[i++] = url;
		}
		return array;
	}

}
