package org.pjb.e2t.el;

import org.apache.commons.lang.StringUtils;
import org.pjb.e2t.config.SettingKeys;
import org.pjb.e2t.config.UserSettings;
import org.pjb.e2t.generation.GenerationContext;

/**
 * Property Expression Language Chaine entre ${ et } De la forme : <CLE>
 * 
 * TODO revoir completement la gestion des EL pour utiliser des outils standards
 * 
 * @author pjBoirleaud
 */
public class PEL {

	/**
	 * Evluation du contenu d'une pel, entre ${ et } <br/>
	 * Récupère un paramètre en provenance du fichier e2t.properties <br/>
	 * 
	 * Ordre de récupération : <br/>
	 * generation.<ID_GENERATION_COURANTE>.<pel> en VM, puis en properties <br/>
	 * puis <pel> en VM, puis en properties
	 * 
	 * @param pel
	 * @return
	 */
	protected static String evalPEL(String pel) {
		String ret = System.getProperty(
				SettingKeys.GENERATION + GenerationContext.getCurrentGenerationName() + SettingKeys.SEPARATOR + pel);
		if (StringUtils.isNotEmpty(ret)) {
			return evalEL(ret);
		}
		ret = UserSettings.getInstance().getStringValue(
				SettingKeys.GENERATION + GenerationContext.getCurrentGenerationName() + SettingKeys.SEPARATOR + pel);
		if (StringUtils.isNotEmpty(ret)) {
			return evalEL(ret);
		}
		ret = System.getProperty(pel);
		if (StringUtils.isNotEmpty(ret)) {
			return evalEL(ret);
		}
		ret = UserSettings.getInstance().getStringValue(pel);
		if (StringUtils.isNotEmpty(ret)) {
			return evalEL(ret);
		}
		throw new RuntimeException("Paramètre '" + SettingKeys.GENERATION + GenerationContext.getCurrentGenerationName()
				+ SettingKeys.SEPARATOR + pel + "' ou '" + pel + "' introuvable en argument de VM ou properties");
	}

	private static String evalEL(String pel) {
		return EL.eval(pel);
	}
}
