package cern.cms.daq.nm.servlet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.Application;
import cern.cms.daq.nm.Setting;
import cern.cms.daq.nm.sound.ExternalSoundReceiver;
import cern.cms.daq.nm.task.TaskManager;

public class ServletListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ServletListener.class);

	public void contextInitialized(ServletContextEvent e) {

		String propertyFilePath = System.getenv("NM_CONF");
		if (propertyFilePath == null) {
			logger.fatal(
					"No configuration file supplied. Set the path to configuration file in environment variable NM_CONF");
			throw new RuntimeException("NM_CONF variable is empty");
		}

		try {
			Application.initialize(propertyFilePath);

			logger.info("Database mode: " + Application.get().getProp().getProperty(Setting.DATABASE_MODE.getCode()));
			logger.info("Database url : " + Application.get().getProp().getProperty(Setting.DATABASE_URL.getCode()));

			final int externalNotificationPort = Integer
					.parseInt(Application.get().getProp().getProperty(Setting.EXTERNAL_NOTIFICATION_PORT.getCode()));
			// EntityManagerFactory emf2 =
			// Persistence.createEntityManagerFactory("shifts");
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("notifications",
					Application.get().getProp());

			e.getServletContext().setAttribute("emf", emf);
			// e.getServletContext().setAttribute("emf-shifters", emf2);
			TaskManager.initialize(emf, null);
			TaskManager.get().schedule();
			(new Thread() {
				public void run() {
					try {
						ExternalSoundReceiver.startSoundReceiver(externalNotificationPort);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (RuntimeException ex) {
			logger.fatal("Could not start NotificationManager due to: ", ex);
			ex.printStackTrace();

		}
	}

	public void contextDestroyed(ServletContextEvent e) {
		EntityManagerFactory emf = (EntityManagerFactory) e.getServletContext().getAttribute("emf");
		// EntityManagerFactory emf2 = (EntityManagerFactory)
		// e.getServletContext().getAttribute("emf-shifters");
		emf.close();
		// emf2.close();
	}

}