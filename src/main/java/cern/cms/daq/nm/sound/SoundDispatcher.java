package cern.cms.daq.nm.sound;

import java.io.IOException;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;

public class SoundDispatcher {

	private final static Logger logger = Logger.getLogger(SoundDispatcher.class);

	private final SoundSystemConnector connector;

	private final SoundTrigger soundTrigger;

	private final SoundSelector soundSelector;

	private final boolean soundEnabled;

	public SoundDispatcher(SoundSystemConnector connector, SoundTrigger soundTrigger, SoundSelector soundSelector,
			boolean soundEnabled) {
		super();
		this.connector = connector;
		this.soundTrigger = soundTrigger;
		this.soundSelector = soundSelector;
		this.soundEnabled = soundEnabled;
	}

	public boolean dispatch(Event event) {
		boolean sent = false;
		if (soundEnabled) {

			try {

				if (event.isAudible()) {

					try {
						Sound sound = event.getSound();
						logger.info("Dispatching event with id: " + event.getId() + " to sound system. Sound: " + sound
								+ ", TTS: " + event.getTextToSpeech() + " from sender: " + event.getSender());
						if (sound != null) {
							String r = connector.play(sound);
							logger.debug("Result of sending play command: " + r);
							sent = true;
						}
						if (event.getTextToSpeech() != null && !"".equals(event.getTextToSpeech())) {
							String r = connector.sayAndListen(event.getTextToSpeech());
							logger.debug("Result of sending speak command: " + r);
							sent = true;
						}
					} catch (IOException e) {
						throw new NotificationException("Could send event to Sound system: " + e.getMessage());
					}
				}
			} catch (NotificationException e) {
				logger.warn("Exception dispatching event " + event.getId() + ", caused by: " + e.getMessage());
			}
		}
		return sent;
	}

	public boolean triggerSound(Event event) {
		return soundTrigger.triggerSound(event);
	}

	public Sound selectSound(Event event) {
		return soundSelector.selectSound(event);
	}

}
