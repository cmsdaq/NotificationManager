package cern.cms.daq.nm.sound;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Priority of the condition
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public enum ConditionPriority {

	FILTERED("filtered"),
	FILTERED_IMPORTANT("filtered-important"),
	EXPERIMENTAL("experimental"),
	DEFAULTT("default"),
	IMPORTANT("important"),
	WARNING("warning"),
	CRITICAL("critical"),;

	private ConditionPriority(String code) {
		this.code = code;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	private final String code;

}