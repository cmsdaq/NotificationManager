package cern.cms.daq.nm.sound;

import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class SoundSelector {

	private HashMap<LogicModuleView, Pair<Sound, Sound>> configuration;

	public SoundSelector() {
		this.configuration = new HashMap<>();
		for (LogicModuleView logicModule : LogicModuleView.values()) {
			Sound startSound = Sound.DEFAULT;
			Sound empty = Sound.DROP;
			configuration.put(logicModule, Pair.of(startSound, empty));
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
		return Sound.DEFAULT;
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
		default:
			return null;
		}

	}

}
