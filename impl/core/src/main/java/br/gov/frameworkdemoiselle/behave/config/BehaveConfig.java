package br.gov.frameworkdemoiselle.behave.config;

import java.io.IOException;
import java.util.Properties;

import br.gov.frameworkdemoiselle.behave.exception.BehaveException;
import br.gov.frameworkdemoiselle.behave.util.PropertiesLoaderUtil;

public class BehaveConfig {

	public static Properties properties;

	public static String LANGUAGE = "pt";
	public static String IDENTIFICATION_SCENARIO_PATTERN = "";
	public static String PREFIXES_BDD_PATTERN = "";
	public static String ORIGINAL_STORY_FILE_EXTENSION = "bdd";
	public static String CONVERTED_STORY_FILE_EXTENSION = "story";
	//

	public static Long BROWSER_MAX_WAIT = 10000L;
	public static Long BROWSER_MIN_WAIT = 100L;

	static {
		try {
			properties = PropertiesLoaderUtil.getInstance().getProperties();

			// Parser Properties
			LANGUAGE = properties.getProperty("behave.parser.language");
			IDENTIFICATION_SCENARIO_PATTERN = properties.getProperty("behave.parser.identification.scenario.pattern." + LANGUAGE);
			PREFIXES_BDD_PATTERN = properties.getProperty("behave.parser.prefixes.bdd.pattern." + LANGUAGE);

			ORIGINAL_STORY_FILE_EXTENSION = properties.getProperty("behave.parser.story.extension.original");
			CONVERTED_STORY_FILE_EXTENSION = properties.getProperty("behave.parser.story.extension.converted");

			if (properties.getProperty("behave.runner.browser.maxWait") != null)
				BROWSER_MAX_WAIT = Long.parseLong(properties.getProperty("behave.runner.browser.maxWait"));

			if (properties.getProperty("behave.runner.browser.minWait") != null)
				BROWSER_MIN_WAIT = Long.parseLong(properties.getProperty("behave.runner.browser.minWait"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna uma propriedade qualquer. Util quando o usuário deseja adicionar
	 * uma nova proprieade em seu projeto
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		if (properties == null || !properties.containsKey(key)) {
			throw new BehaveException("chave [" + key + "] não encontrondada");
		} else {
			return properties.getProperty(key);
		}
	}

	/**
	 * Retorna uma propriedade qualquer. Util quando o usuário deseja adicionar
	 * uma nova proprieade em seu projeto
	 * 
	 * @param key
	 * @return
	 */
	public static boolean contains(String key) {
		return properties.containsKey(key);
	}

}
