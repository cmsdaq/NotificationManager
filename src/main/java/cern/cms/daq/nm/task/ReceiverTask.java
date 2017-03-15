package cern.cms.daq.nm.task;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import cern.cms.daq.nm.EventResource;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.sound.Sound;
import cern.cms.daq.nm.sound.SoundSystemManager;
import cern.cms.daq.nm.websocket.EventWebSocketServer;

/**
 * 
 * This task processes events from external sources received via API. Following
 * steps are taken:
 * <ol>
 * <li>take event occurrence resource ({@link EventResource} objects) from API
 * buffer</li>
 * <li>convert into event occurrences ({@link Event} objects)</li>
 * <li>persist converted object to database</li>
 * <li>pass converted object to dispatcher buffer</li>
 * </ol>
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class ReceiverTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(ReceiverTask.class);

	private final SoundSystemManager soundSystemManager;
	/**
	 * Outcoming buffer
	 */
	private final ConcurrentLinkedQueue<Event> eventBuffer;

	/**
	 * Incoming buffer
	 */
	private ConcurrentLinkedQueue<EventResource> eventResourceBuffer;

	private EntityManagerFactory emf;

	private final boolean dispatchToSoundSystem;

	public ReceiverTask(EntityManagerFactory emf, ConcurrentLinkedQueue<EventResource> eventResourceBuffer,
			ConcurrentLinkedQueue<Event> eventBuffer, SoundSystemManager soundSystemManager,
			boolean dispatchToSoundSystem) {
		this.emf = emf;
		this.eventBuffer = eventBuffer;
		this.eventResourceBuffer = eventResourceBuffer;
		this.soundSystemManager = soundSystemManager;
		this.dispatchToSoundSystem = dispatchToSoundSystem;
	}

	@Override
	public void run() {

		if (!eventResourceBuffer.isEmpty()) {
			int size = eventResourceBuffer.size();
			logger.debug("Run receiver task " + size + " on queue");
			int i = 0;

			EntityManager em = emf.createEntityManager();
			Queue<Event> tmpReceiverBuffer = new ArrayDeque<>();
			em.getTransaction().begin();
			Session session = em.unwrap(Session.class);

			while (!eventResourceBuffer.isEmpty() && i < size) {
				i++;
				EventResource current = eventResourceBuffer.poll();
				logger.debug("Received: " + current);
				Event eventOccurrence = current.asEventOccurrence(session);

				long start = System.currentTimeMillis();

				em.persist(eventOccurrence);

				long end = System.currentTimeMillis();
				logger.debug("Event persistence time: " + (end - start) + "ms");
				EventWebSocketServer.sessionHandler.addEvent(eventOccurrence);

				if (dispatchToSoundSystem && eventOccurrence.isPlay()) {
					try {
						logger.debug("Dispatching to Sound system");
						Sound sound = Sound.DEFAULT;
						if (eventOccurrence.getSound() != null) {
							sound = eventOccurrence.getSound();
						}
						String r = soundSystemManager.play(sound);
						logger.debug("Result of sending play command: " + r);

						if (eventOccurrence.getTextToSpeech() != null
								&& !"".equals(eventOccurrence.getTextToSpeech())) {

							String r2 = soundSystemManager.sayAndListen(
									eventOccurrence.getTitle() + ": " + eventOccurrence.getTextToSpeech());
							logger.debug("Result of sending speak command: " + r2);
						} else {
							logger.info("Nothing to play for the event " + eventOccurrence.getId());
						}
					} catch (RuntimeException e) {
						logger.error(e);
					} catch (IOException e) {
						logger.error(e);
					}
				}

				logger.debug("Persisted: " + eventOccurrence);

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