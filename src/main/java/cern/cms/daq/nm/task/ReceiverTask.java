package cern.cms.daq.nm.task;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import cern.cms.daq.nm.EventOccurrenceResource;
import cern.cms.daq.nm.persistence.EventOccurrence;

/**
 * 
 * This task processes events from external sources received via API. Following
 * steps are taken:
 * <ol>
 * <li>take event occurrence resource ({@link EventOccurrenceResource} objects)
 * from API buffer</li>
 * <li>convert into event occurrences ({@link EventOccurrence} objects)</li>
 * <li>persist converted object to database</li>
 * <li>pass converted object to dispatcher buffer</li>
 * </ol>
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class ReceiverTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(ReceiverTask.class);

	/**
	 * Outcoming buffer
	 */
	private final ConcurrentLinkedQueue<EventOccurrence> eventBuffer;

	/**
	 * Incoming buffer
	 */
	private ConcurrentLinkedQueue<EventOccurrenceResource> eventResourceBuffer;

	private EntityManagerFactory emf;

	public ReceiverTask(EntityManagerFactory emf, ConcurrentLinkedQueue<EventOccurrenceResource> eventResourceBuffer,
			ConcurrentLinkedQueue<EventOccurrence> eventBuffer) {
		this.emf = emf;
		this.eventBuffer = eventBuffer;
		this.eventResourceBuffer = eventResourceBuffer;
	}

	@Override
	public void run() {

		if (!eventResourceBuffer.isEmpty()) {
			int size = eventResourceBuffer.size();
			logger.info("Run receiver task " + size + " on queue");
			int i = 0;

			EntityManager em = emf.createEntityManager();
			Queue<EventOccurrence> tmpReceiverBuffer = new ArrayDeque<>();
			em.getTransaction().begin();
			Session session = em.unwrap(Session.class);

			while (!eventResourceBuffer.isEmpty() && i < size) {
				i++;
				EventOccurrenceResource current = eventResourceBuffer.poll();
				logger.info("Received: " + current);
				EventOccurrence eventOccurrence = current.asEventOccurrence(session);

				em.persist(eventOccurrence);

				logger.info("Persisted: " + eventOccurrence);
				if (current.getId() != null) {
					em.flush();
					Long nmId = eventOccurrence.getId();
					logger.info("Mapping this id: " + nmId);
					TaskManager.get().getExpertIdToNmId().put(current.getId(), nmId);
				}

				// Add to temporary buffer - event occurrence cannot be added to
				// buffer before tranaction has successfully commited.
				tmpReceiverBuffer.add(eventOccurrence);
			}

			try {
				em.getTransaction().commit();
				while (!tmpReceiverBuffer.isEmpty()) {
					eventBuffer.add(tmpReceiverBuffer.poll());
				}

			} finally {
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
				em.close();
			}
		}
	}

}