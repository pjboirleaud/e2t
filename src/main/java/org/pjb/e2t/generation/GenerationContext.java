package org.pjb.e2t.generation;

/**
 * Contexte de génération : contient l'ID de la génération actuellement
 * exécutée.
 * 
 * @author pjBoirleaud
 *
 */
public class GenerationContext {
	private static String generationName;

	public static String getCurrentGenerationName() {
		return generationName;
	}

	public static void setCurrentGenerationName(String name) {
		generationName = name;
	}
}
