package cern.cms.daq.nm.sound;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;

public class SoundDispatcher {

	private final static Logger logger = Logger.getLogger(SoundDispatcher.class);

	private final ConcurrentLinkedQueue<Event> connector;

	private final SoundTrigger soundTrigger;

	private final SoundSelector soundSelector;

	private final boolean soundEnabled;

	public SoundDispatcher(ConcurrentLinkedQueue<Event> audibleEventBuffer, SoundTrigger soundTrigger,
			SoundSelector soundSelector, boolean soundEnabled) {
		super();
		this.connector = audibleEventBuffer;
		this.soundTrigger = soundTrigger;
		this.soundSelector = soundSelector;
		this.soundEnabled = soundEnabled;
	}

	public void dispatch(Event event) {
		if (soundEnabled) {
			try {
				if (event.isAudible()) {
					connector.add(event);
				}
			} catch (NotificationException e) {
				logger.warn("Exception dispatching event " + event.getId() + ", caused by: " + e.getMessage());
			}
		}
	}

	public boolean triggerSound(Event event) {
		return soundTrigger.triggerSound(event);
	}

	public Sound selectSound(Event event) {
		return soundSelector.selectSound(event);
	}

}
