package cern.cms.daq.nm.persistence;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class PersistenceManager {

	private EntityManagerFactory entityManagerFactory;

	public PersistenceManager(EntityManagerFactory entityManagerFactory) {

		this.entityManagerFactory = entityManagerFactory;
	}

	public Pair<List<Event>, Long> getEvents(Date startDate, Date endDate, List<EventType> filteredTypes,
			List<LogicModuleView> filteredSources, int page, int entries) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Session session = entityManager.unwrap(Session.class);

		Criteria eventCriteria = session.createCriteria(Event.class);
		Criteria orderCriteria = session.createCriteria(Event.class);

		eventCriteria.addOrder(Order.desc("date"));

		if (!filteredTypes.isEmpty()) {
			eventCriteria.add(Restrictions.in("eventType", filteredTypes));
			orderCriteria.add(Restrictions.in("eventType", filteredTypes));
		}
		if (!filteredSources.isEmpty()) {
			eventCriteria.add(Restrictions.in("logicModule", filteredSources));
			orderCriteria.add(Restrictions.in("logicModule", filteredSources));
		}
		if (startDate != null && endDate != null) {
			eventCriteria.add(Restrictions.between("date", startDate, endDate));
			orderCriteria.add(Restrictions.between("date", startDate, endDate));
		}

		/* process pagination */
		eventCriteria.setFirstResult((page - 1) * entries);
		eventCriteria.setMaxResults(entries);

		@SuppressWarnings("unchecked")
		List<Event> events = eventCriteria.list();

		orderCriteria.setProjection(Projections.rowCount());
		orderCriteria.setFirstResult(0);
		Long count = (Long) orderCriteria.uniqueResult();

		entityManager.close();
		return Pair.of(events, count);
	}

	/**
	 * Persist multiple events in one transaction
	 * 
	 * @param events
	 */
	public void persist(Set<Event> events) {

		EntityManager entryEntityManager = entityManagerFactory.createEntityManager();
		EntityTransaction tx = entryEntityManager.getTransaction();
		tx.begin();
		for (Event event : events) {
			entryEntityManager.persist(event);
		}
		tx.commit();
		entryEntityManager.close();
	}

	/**
	 * Persist event
	 * 
	 * @param event
	 */
	public void persist(Event event) {
		EntityManager entryEntityManager = entityManagerFactory.createEntityManager();
		EntityTransaction tx = entryEntityManager.getTransaction();
		tx.begin();
		entryEntityManager.persist(event);
		tx.commit();
		entryEntityManager.close();
			
	}

	/**
	 * Put to database fact that the event was muted - dominanted by event no x
	 */
	public void persistMuted() {
		// TODO Auto-generated method stub

	}
}
