package cern.cms.daq.nm.servlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Application {

	public static final String EXPERT_BROWSER = "expert.url";
	public static final String LANDING = "landing";
	public static final String SOUND_URL = "sound.url";
	public static final String SOUND_PORT = "sound.port";
	public static final String SOUND_ENABLED = "sound.enabled";

	private final Properties prop;

	public static Application get() {
		if (instance == null) {
			throw new RuntimeException("Not initialized");
		}
		return instance;
	}

	public static void initialize(String propertiesFile) {
		String message = "Required property missing ";
		instance = new Application(propertiesFile);
		if (!instance.prop.containsKey(EXPERT_BROWSER))
			throw new RuntimeException(message + EXPERT_BROWSER);
		if (!instance.prop.containsKey(LANDING))
			throw new RuntimeException(message + LANDING);
		if (!instance.prop.containsKey(SOUND_URL))
			throw new RuntimeException(message + SOUND_URL);
		if (!instance.prop.containsKey(SOUND_PORT))
			throw new RuntimeException(message + SOUND_PORT);
		if (!instance.prop.containsKey(SOUND_ENABLED))
			throw new RuntimeException(message + SOUND_ENABLED);
	}

	private Application(String propertiesFile) {
		prop = load(propertiesFile);
	}

	private static Application instance;

	private Properties load(String propertiesFile) {

		try {
			FileInputStream propertiesInputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(propertiesInputStream);

			return properties;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot run application without configuration file");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot run application without configuration file");
		}
	}

	public Properties getProp() {
		return prop;
	}
}
