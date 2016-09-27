package cern.cms.daq.nm.task;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;

import cern.cms.daq.nm.persistence.EventOccurrence;
import cern.cms.daq.nm.persistence.EventStatus;
import cern.cms.daq.nm.persistence.EventType;

// produkuj eventOccurrence do bufora
public class GeneratorTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(GeneratorTask.class);

	private final ConcurrentLinkedQueue<EventOccurrence> eventBuffer;
	private EntityManagerFactory emf;

	public GeneratorTask(EntityManagerFactory emf, ConcurrentLinkedQueue<EventOccurrence> eventBuffer) {
		this.emf = emf;
		this.eventBuffer = eventBuffer;
	}

	@Override
	public void run() {

		logger.info("Run generation task");
		EntityManager em = emf.createEntityManager();

		Session session = em.unwrap(Session.class);
		Criteria cr = session.createCriteria(EventType.class);
		@SuppressWarnings("unchecked")
		List<EventType> eventList = cr.list();

		EventOccurrence eventOccurrence = new EventOccurrence();
		eventOccurrence.setMessage("Example message " + new Date());
		eventOccurrence.setEventType(eventList.get(0));
		eventOccurrence.setStatus(EventStatus.Received);
		eventOccurrence.setDate(new Date());

		eventBuffer.add(eventOccurrence);

		try {
			em.getTransaction().begin();
			em.persist(eventOccurrence);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}

}