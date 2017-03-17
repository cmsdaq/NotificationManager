package cern.cms.daq.nm.sound;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class SoundSelector {

	private final static Logger logger = Logger.getLogger(SoundSelector.class);

	private HashMap<LogicModuleView, Pair<Sound, Sound>> configuration;

	public SoundSelector() {
		this.configuration = new HashMap<>();
		for (LogicModuleView logicModule : LogicModuleView.values()) {
			Sound startSound = Sound.DEFAULT;
			Sound empty = Sound.DROP;
			configuration.put(logicModule, Pair.of(startSound, empty));
		}
	}

	public void configure(Set<Triple<LogicModuleView, EventType, Sound>> soundConfiguration) {

		int sucessfullyConfigured = 0;

		for (Triple<LogicModuleView, EventType, Sound> souncConfEntry : soundConfiguration) {

			try {
				updateConfiguration(souncConfEntry);
				sucessfullyConfigured++;
			} catch (NotificationException e) {
				logger.warn("Could not update the configuration for the entry: " + souncConfEntry + ", problem: "
						+ e.getMessage());
			}
		}
		if (sucessfullyConfigured > 0) {
			logger.info(sucessfullyConfigured + " sound configuration entries successfuly processed");
		}

	}

	private void updateConfiguration(Triple<LogicModuleView, EventType, Sound> entry) {
		if (configuration.containsKey(entry.getLeft())) {
			Pair<Sound, Sound> defaultConfiguration = configuration.get(entry.getLeft());
			Sound soundOnStart = defaultConfiguration.getLeft();
			Sound soundOnEnd = defaultConfiguration.getRight();

			switch (entry.getMiddle()) {
			case Single:
				soundOnStart = entry.getRight();
				break;
			case ConditionStart:
				soundOnStart = entry.getRight();
				break;
			case ConditionUpdate:
				// TODO: change the configuration for this case
				break;
			case ConditionEnd:
				soundOnEnd = entry.getRight();
				break;

			}
			configuration.put(entry.getLeft(), Pair.of(soundOnStart, soundOnEnd));

		} else {
			throw new NotificationException(
					"Default configuration does not contain custom entry for LM: " + entry.getLeft());
		}
	}

	public Sound selectSound(Event event) {

		if (event.getSound() != null) {
			throw new NotificationException("Event " + event.getId() + " has already assigned the sound");
		}

		if (event.getEventSenderType() == null) {
			throw new NotificationException(
					"Event " + event.getId() + "  from unknown sender, cannot trigger the sound");
		}

		switch (event.getEventSenderType()) {
		case External:
			return selectExternalSound(event);
		case Expert:
			return selectExpertSound(event);
		}

		return null;

	}

	public Sound selectExternalSound(Event event) {

		return Sound.EXTERNAL_DEFAULT;
	}

	public Sound selectExpertSound(Event event) {

		if (event.getLogicModule() == null) {
			throw new NotificationException(
					"Event " + event.getId() + " from expert has no LM, cannot assign the sound");
		} else if (!configuration.containsKey(event.getLogicModule())) {
			throw new NotificationException("There is no configuration for LM " + event.getLogicModule() + " of event "
					+ event.getId() + " from expert, cannot assign the sound");
		}

		if (event.getEventType() == null) {
			throw new NotificationException("Cannot select sound for event without a type");
		}

		Pair<Sound, Sound> sounds = configuration.get(event.getLogicModule());
		switch (event.getEventType()) {
		case ConditionStart:
			return sounds.getLeft();
		case ConditionEnd:
			return sounds.getRight();
		case Single:
			return sounds.getLeft();
		default:
			return null;
		}

	}

}
