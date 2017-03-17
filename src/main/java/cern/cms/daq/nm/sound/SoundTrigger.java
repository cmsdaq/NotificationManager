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

public class SoundTrigger {

	private final static Logger logger = Logger.getLogger(SoundTrigger.class);
	private HashMap<LogicModuleView, Pair<ConditionPriority, ConditionPriority>> configuration;

	public SoundTrigger() {
		this.configuration = new HashMap<>();
		for (LogicModuleView logicModule : LogicModuleView.values()) {
			configuration.put(logicModule, Pair.of(ConditionPriority.IMPORTANT, ConditionPriority.CRITICAL));
		}
	}

	public void configure(Set<Triple<LogicModuleView, EventType, ConditionPriority>> soundConfiguration) {

		int sucessfullyConfigured = 0;

		for (Triple<LogicModuleView, EventType, ConditionPriority> souncConfEntry : soundConfiguration) {

			try {
				updateConfiguration(souncConfEntry);
				sucessfullyConfigured++;
			} catch (NotificationException e) {
				logger.warn("Could not update the configuration for the entry: " + souncConfEntry + ", problem: "
						+ e.getMessage());
			}
		}
		if (sucessfullyConfigured > 0) {
			logger.info(sucessfullyConfigured + " sound TRIGGER configuration entries successfuly processed");
		}

	}

	private void updateConfiguration(Triple<LogicModuleView, EventType, ConditionPriority> entry) {
		if (configuration.containsKey(entry.getLeft())) {
			Pair<ConditionPriority, ConditionPriority> defaultConfiguration = configuration.get(entry.getLeft());
			ConditionPriority triggerForStart = defaultConfiguration.getLeft();
			ConditionPriority triggerForEnd = defaultConfiguration.getRight();

			switch (entry.getMiddle()) {
			case Single:
				triggerForStart = entry.getRight();
				break;
			case ConditionStart:
				triggerForStart = entry.getRight();
				break;
			case ConditionUpdate:
				// TODO: change the configuration for this case
				break;
			case ConditionEnd:
				triggerForEnd = entry.getRight();
				break;

			}
			configuration.put(entry.getLeft(), Pair.of(triggerForStart, triggerForEnd));

		} else {
			throw new NotificationException(
					"Default configuration does not contain custom entry for LM: " + entry.getLeft());
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

		if (event.getEventType() == null) {
			throw new NotificationException("missing event type");
		}

		switch (event.getEventType()) {
		case ConditionStart:
			if (event.getPriority().ordinal() >= configuration.get(event.getLogicModule()).getLeft().ordinal()) {
				return true;
			} else
				return false;
		case ConditionUpdate:
			// TODO: change configuration to triple
			return false;
		case ConditionEnd:
			if (event.getPriority().ordinal() >= configuration.get(event.getLogicModule()).getRight().ordinal()) {
				return true;
			} else
				return false;
		case Single:
			if (event.getPriority().ordinal() >= configuration.get(event.getLogicModule()).getLeft().ordinal()) {
				return true;
			} else
				return false;
		default:
			return false;
		}

	}

	private boolean triggerExternalSound(Event event) {

		return true;
	}

}
