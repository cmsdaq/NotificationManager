package cern.cms.daq.nm.sound;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;

/**
 * Reads the configuration of sound system from configuration file
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class SoundConfigurationReader {

	public static final Logger logger = Logger.getLogger(SoundConfigurationReader.class);

	public Set<Triple<LogicModuleView, EventType, Sound>> readSoundSelectConfigurations(Properties properties) {
		Set<Triple<LogicModuleView, EventType, Sound>> result = new HashSet<>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			try {
				String key = (String) entry.getKey();
				if (key.startsWith("sound.select.")) {
					Object value = entry.getValue();
					if (value instanceof String) {
						result.add(readSoundSelectConfiguration(key, (String) value));
					} else {
						logger.warn("problem parsing value: " + value + ", expecting sound name or filename");
					}
				}
			} catch (NotificationException e) {
				logger.warn("Cannot process this configuration: " + entry + ", " + e.getMessage());
			}
		}
		return result;
	}

	public Set<Triple<LogicModuleView, EventType, ConditionPriority>> readSoundTriggerConfigurations(
			Properties properties) {
		Set<Triple<LogicModuleView, EventType, ConditionPriority>> result = new HashSet<>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			try {
				String key = (String) entry.getKey();
				if (key.startsWith("sound.trigger.")) {
					Object value = entry.getValue();
					if (value instanceof String) {
						result.add(readSoundTriggerConfiguration(key, (String) value));
					} else {
						logger.warn("problem parsing value: " + value + ", expecting sound name or filename");
					}
				}
			} catch (NotificationException e) {
				logger.warn("Cannot process this configuration: " + entry + ", " + e.getMessage());
			}
		}
		return result;
	}

	private Triple<LogicModuleView, EventType, Sound> readSoundSelectConfiguration(String key, String value) {

		String[] parts = key.split("\\.");
		if (parts.length == 4) {
			return readConditionSoundSelectConfiguration(parts, value);
		} else if (parts.length == 3) {
			return readSimpleSoundSelectConfiguration(parts, value);
		} else {
			throw new NotificationException("configuration key has wrong format: " + key);
		}
	}

	private Triple<LogicModuleView, EventType, ConditionPriority> readSoundTriggerConfiguration(String key,
			String value) {

		String[] parts = key.split("\\.");
		if (parts.length == 4) {
			return readConditionSoundTriggerConfiguration(parts, value);
		} else if (parts.length == 3) {
			return readSimpleSoundTriggerConfiguration(parts, value);
		} else {
			throw new NotificationException("configuration key has wrong format: " + key);
		}
	}

	private Triple<LogicModuleView, EventType, Sound> readSimpleSoundSelectConfiguration(String[] parts, String value) {
		String lm = parts[2];
		LogicModuleView logicModule = getView(lm);
		Sound sound = getSound(value);
		return Triple.of(logicModule, EventType.Single, sound);
	}

	private Triple<LogicModuleView, EventType, ConditionPriority> readSimpleSoundTriggerConfiguration(String[] parts,
			String value) {
		String lm = parts[2];
		LogicModuleView logicModule = getView(lm);
		ConditionPriority priority = getPriority(value);
		return Triple.of(logicModule, EventType.Single, priority);
	}

	private Triple<LogicModuleView, EventType, Sound> readConditionSoundSelectConfiguration(String[] parts,
			String value) {
		String lm = parts[2];
		String type = parts[3];

		EventType eventType = null;
		if (type.equalsIgnoreCase("start")) {
			eventType = EventType.ConditionStart;
		} else if (type.equalsIgnoreCase("end")) {
			eventType = EventType.ConditionEnd;
		} else if (type.equalsIgnoreCase("update")) {
			eventType = EventType.ConditionUpdate;

		} else {
			throw new NotificationException("unknown event type: " + type);

		}

		LogicModuleView logicModule = getView(lm);

		Sound sound = getSound(value);

		return Triple.of(logicModule, eventType, sound);
	}

	private Triple<LogicModuleView, EventType, ConditionPriority> readConditionSoundTriggerConfiguration(String[] parts,
			String value) {
		String lm = parts[2];
		String type = parts[3];

		EventType eventType = null;
		if (type.equalsIgnoreCase("start")) {
			eventType = EventType.ConditionStart;
		} else if (type.equalsIgnoreCase("end")) {
			eventType = EventType.ConditionEnd;
		} else if (type.equalsIgnoreCase("update")) {
			eventType = EventType.ConditionUpdate;

		} else {
			throw new NotificationException("unknown event type: " + type);

		}

		LogicModuleView logicModule = getView(lm);

		ConditionPriority priority = getPriority(value);

		return Triple.of(logicModule, eventType, priority);
	}

	private LogicModuleView getView(String input) {

		try {
			LogicModuleView result = LogicModuleView.valueOf(input);
			return result;

		} catch (IllegalArgumentException e) {
			throw new NotificationException("unknown logic module: " + input);
		}

	}

	private ConditionPriority getPriority(String input) {

		for (ConditionPriority sound : ConditionPriority.values()) {
			if (sound.getCode().equalsIgnoreCase(input) || sound.name().equalsIgnoreCase(input)) {
				return sound;
			}
		}
		throw new NotificationException("unknown priority: " + input);

	}

	private Sound getSound(String input) {

		for (Sound sound : Sound.values()) {
			if (sound.getFilename().equalsIgnoreCase(input) || sound.name().equalsIgnoreCase(input)) {
				return sound;
			}
		}
		throw new NotificationException("unknown sound: " + input);
	}
}
