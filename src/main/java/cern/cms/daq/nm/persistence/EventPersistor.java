package cern.cms.daq.nm.persistence;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

public class EventPersistor {

	private static final Logger logger = Logger.getLogger(EventPersistor.class);

	private final EntityManager entryEntityManager;

	// Persistence.createEntityManagerFactory(persistenceUnitName, props)
	public EventPersistor(EntityManagerFactory entityManagerFactory) {

		entryEntityManager = entityManagerFactory.createEntityManager();
	}

	/**
	 * Persist multiple events in one transaction
	 * 
	 * @param events
	 */
	public void persist(Set<Event> events) {

		EntityTransaction tx = entryEntityManager.getTransaction();
		tx.begin();
		for (Event event : events) {
			entryEntityManager.persist(event);
		}
		tx.commit();
	}

	/**
	 * Persist event
	 * 
	 * @param event
	 */
	public void persist(Event event) {
		EntityTransaction tx = entryEntityManager.getTransaction();
		tx.begin();
		entryEntityManager.persist(event);
		tx.commit();
	}

}
