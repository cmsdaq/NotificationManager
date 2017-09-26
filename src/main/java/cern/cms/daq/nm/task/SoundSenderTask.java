package cern.cms.daq.nm.task;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventSenderType;
import cern.cms.daq.nm.persistence.PersistenceManager;
import cern.cms.daq.nm.sound.DominantSelector;
import cern.cms.daq.nm.sound.Sound;
import cern.cms.daq.nm.sound.SoundSystemConnector;

/**
 *
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class SoundSenderTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(SoundSenderTask.class);

	private final SoundSystemConnector soundSystemConnector;

	private final DominantSelector dominantSoundSelector;

	/**
	 * Incoming buffer
	 */
	private ConcurrentLinkedQueue<Event> audibleEventBuffer;

	private PersistenceManager persistenceManager;

	public SoundSenderTask(PersistenceManager emf, ConcurrentLinkedQueue<Event> audibleEventBuffer,
			SoundSystemConnector soundSystemConnector) {
		this.persistenceManager = emf;
		this.audibleEventBuffer = audibleEventBuffer;
		this.soundSystemConnector = soundSystemConnector;
		this.dominantSoundSelector = new DominantSelector();
	}

	@Override
	public void run() {

		if (!audibleEventBuffer.isEmpty()) {
			int size = audibleEventBuffer.size();
			logger.debug("Run sound task, " + size + " on queue");
			int i = 0;

			Set<Event> toProcessThisRound = new HashSet<>();
			Event dominantEvent = null;
			Set<Event> mutedEvents = null;

			while (!audibleEventBuffer.isEmpty() && i < size) {
				i++;
				Event current = audibleEventBuffer.poll();
				logger.debug("Received: " + current);

				if (current.getEventSenderType() == EventSenderType.External) {
					try {
						sendDominant(current);
						logger.info("Sound command successfully sent to CMS-WOW");
					} catch (IOException e) {
						logger.error("Problem sending external message",e);
					}
				} else {
					toProcessThisRound.add(current);
				}
			}

			if (toProcessThisRound.size() > 0) {
				Pair<Event, Set<Event>> r = dominantSoundSelector.selectDominantEvent(toProcessThisRound);
				dominantEvent = r.getLeft();
				mutedEvents = r.getRight();

				try {
					sendDominant(dominantEvent);
				} catch (IOException e) {
					logger.warn("There was a problem sending event to SoundSystem: " + dominantEvent.getId());
				}

				if (mutedEvents.size() > 0) {
					logger.info("Event: " + dominantEvent.getId() + " with priority: " + dominantEvent.getPriority()
							+ ", and usefulness index: " + dominantEvent.getLogicModule().getUsefulness()
							+ " dominated " + mutedEvents.size() + " events");
					logger.info("Dominating event: " + dominantEvent);
					logger.info("Dominated events: ");
					for (Event dominated : mutedEvents) {
						logger.info("    > id=" + dominated.getId() + ", priority= " + dominated.getPriority() + ", lm="
								+ dominated.getLogicModule() + ", usefulness="
								+ dominated.getLogicModule().getUsefulness() + ", title=" + dominated.getTitle());
					}
					persistenceManager.persistMuted();
				}
			}
		}
	}

	private void sendDominant(Event event) throws IOException {
		boolean sent = false;
		Sound sound = event.getSound();
		String soundFilename = "";
		if (sound != null && sound != Sound.OTHER) {
			soundFilename = sound.getFilename();
		} else if (sound == Sound.OTHER) {
			soundFilename = event.getCustomSound();
		}
		logger.info(
				"Dispatching event with id: " + event.getId() + " to sound system. Sound: " + sound + ", sound file: "
						+ soundFilename + ", TTS: " + event.getTextToSpeech() + " from sender: " + event.getSender());
		if (sound != null) {
			String r = soundSystemConnector.play(soundFilename);
			logger.info("PLAY command sent. CMS-WOW response: " + r);
            logger.info("All ok".equalsIgnoreCase(r) ? "Response: All ok" : "Response non-all-ok");
			sent = true;
		}
		if (event.getTextToSpeech() != null && !"".equals(event.getTextToSpeech())) {
			String r = soundSystemConnector.sayAndListen(event.getTextToSpeech());
			logger.info("TALK command sent. CMS-WOW response: " + r);
            logger.info("All ok".equalsIgnoreCase(r) ? "Response: All ok" : "Response non-all-ok");
			sent = true;
		}

	}

}