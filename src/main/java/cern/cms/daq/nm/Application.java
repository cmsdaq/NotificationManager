package cern.cms.daq.nm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cern.cms.daq.nm.persistence.PersistenceManager;

public class Application {

	private final Properties prop;

	private PersistenceManager persistenceManager;

	public static Application get() {
		if (instance == null) {
			throw new RuntimeException("Not initialized");
		}
		return instance;
	}

	public static void initialize(String propertiesFile) {

		instance = new Application(propertiesFile);

		for (Setting setting : Setting.values()) {
			if (!instance.prop.containsKey(setting.getCode())) {
				throw new RuntimeException("Required property missing " + setting.getCode());
			}
		}

		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("notifications",
				instance.getProp());
		// EntityManagerFactory emf2 =
		// Persistence.createEntityManagerFactory("shifts");
		instance.persistenceManager = new PersistenceManager(entityManagerFactory);

		// e.getServletContext().setAttribute("emf", emf);
		// e.getServletContext().setAttribute("emf-shifters", emf2);

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

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	public void setPersistenceManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

}
