package cern.cms.daq.nm.sound;

import java.util.HashMap;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class SoundTrigger {

	private HashMap<LogicModuleView, ConditionPriority> configuration;

	public SoundTrigger() {
		this.configuration = new HashMap<>();
		for (LogicModuleView logicModule : LogicModuleView.values()) {
			configuration.put(logicModule, ConditionPriority.IMPORTANT);
		}
	}

	public boolean triggerSound(Event event) {

		if (event.getEventSenderType() == null) {
			throw new NotificationException(
					"Event " + event.getId() + "  from unknown sender, cannot trigger the sound");
		}

		switch (event.getEventSenderType()) {
		case External:
			return triggerExternalSound(event);
		case Expert:
			return triggerExpertSound(event);
		}

		return false;

	}

	private boolean triggerExpertSound(Event event) {

		if (event.getPriority() == null) {
			throw new NotificationException(
					"Event " + event.getId() + " from expert has no priority, cannot trigger the sound");
		}

		if (event.getLogicModule() == null) {
			throw new NotificationException(
					"Event " + event.getId() + " from expert has no LM, cannot trigger the sound");
		} else if (!configuration.containsKey(event.getLogicModule())) {
			throw new NotificationException("There is no configuration for LM " + event.getLogicModule() + " of event "
					+ event.getId() + " from expert, cannot trigger the sound");
		}

		if (event.getPriority().ordinal() >= configuration.get(event.getLogicModule()).ordinal()) {
			return true;
		}
		return false;
	}

	private boolean triggerExternalSound(Event event) {

		return true;
	}

}
