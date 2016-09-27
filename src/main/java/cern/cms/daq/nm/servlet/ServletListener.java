package cern.cms.daq.nm.servlet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.Initializer;
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

		Application.initialize(propertyFilePath);

		//EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("shifts");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("notifications");

		
		
		e.getServletContext().setAttribute("emf", emf);
		//e.getServletContext().setAttribute("emf-shifters", emf2);
		Initializer.initDefaults(emf);
		TaskManager.initialize(emf, null);
		TaskManager.get().schedule();
	}

	public void contextDestroyed(ServletContextEvent e) {
		EntityManagerFactory emf = (EntityManagerFactory) e.getServletContext().getAttribute("emf");
		//EntityManagerFactory emf2 = (EntityManagerFactory) e.getServletContext().getAttribute("emf-shifters");
		emf.close();
		//emf2.close();
	}


}