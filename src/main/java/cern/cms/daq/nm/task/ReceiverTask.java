package cern.cms.daq.nm.task;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.EventResource;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventPersistor;
import cern.cms.daq.nm.sound.Sound;
import cern.cms.daq.nm.sound.SoundDispatcher;
import cern.cms.daq.nm.websocket.EventSessionHandler;

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

	/**
	 * Outcoming buffer
	 */
	private final ConcurrentLinkedQueue<Event> eventBuffer;

	/**
	 * Incoming buffer
	 */
	private ConcurrentLinkedQueue<EventResource> eventResourceBuffer;

	private final EventPersistor eventPersistor;

	private final SoundDispatcher soundDispatcher;

	private final EventSessionHandler eventDashboard;

	public ReceiverTask(ConcurrentLinkedQueue<EventResource> eventResourceBuffer,
			ConcurrentLinkedQueue<Event> eventBuffer, EventPersistor eventPersistor, SoundDispatcher soundDispatcher,
			EventSessionHandler eventDashboard) {
		this.eventPersistor = eventPersistor;
		this.eventBuffer = eventBuffer;
		this.eventResourceBuffer = eventResourceBuffer;
		this.soundDispatcher = soundDispatcher;
		this.eventDashboard = eventDashboard;
	}

	@Override
	public void run() {

		if (!eventResourceBuffer.isEmpty()) {
			int size = eventResourceBuffer.size();
			logger.debug("Run receiver task " + size + " on queue");
			int i = 0;

			Queue<Event> tmpReceiverBuffer = new ArrayDeque<>();

			while (!eventResourceBuffer.isEmpty() && i < size) {
				i++;
				EventResource current = eventResourceBuffer.poll();
				logger.debug("Received: " + current);
				Event event = current.asEventOccurrence();

				boolean audible = soundDispatcher.triggerSound(event);
				event.setAudible(audible);

				if (audible && event.getSound() == null) {
					Sound selected = soundDispatcher.selectSound(event);
					event.setSound(selected);
				}

				long start = System.currentTimeMillis();

				eventPersistor.persist(event);

				long end = System.currentTimeMillis();
				logger.debug("Event persistence time: " + (end - start) + "ms");

				eventDashboard.addEvent(event);
				soundDispatcher.dispatch(event);

				// Add to temporary buffer - event occurrence cannot be added to
				// buffer before tranaction has successfully commited.
				tmpReceiverBuffer.add(event);
			}

		}
	}

}