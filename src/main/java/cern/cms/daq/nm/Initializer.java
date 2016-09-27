package cern.cms.daq.nm;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.persistence.EventType;

public class Initializer {

	private final static Logger logger = Logger.getLogger(Initializer.class);

	static public void initDefaults(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();

		try {
			checkOrAddEventType(em, "Flowchart events", "Events described in flowchart will trigger the notification");
			checkOrAddEventType(em, "Warnings", "Warnings like 'no rate when expected' will trigger the notification");

		} finally {
			em.close();
		}
	}

	private static void checkOrAddEventType(EntityManager em, String name, String description) {

		try {

			Session session = em.unwrap(Session.class);

			Criteria cr = session.createCriteria(EventType.class);
			cr.add(Restrictions.eq("name", name));
			@SuppressWarnings("unchecked")
			List<EventType> a = cr.list();

			if (a.size() == 0) {
				logger.info("Creating event type " + name);

				em.getTransaction().begin();

				EventType event1 = new EventType();
				event1.setName(name);
				event1.setDescription(description);
				em.persist(event1);

			}
		} finally {
			// Close the database connection:
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
		}
	}

}
