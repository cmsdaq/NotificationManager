package cern.cms.daq.nm.sound;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Priority of the condition or event
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public enum Priority {

	FILTERED("filtered"),
	FILTERED_IMPORTANT("filtered-important"),
	EXPERIMENTAL("experimental"),
	DEFAULTT("default"),
	IMPORTANT("important"),
	WARNING("warning"),
	CRITICAL("critical"),;

	private Priority(String code) {
		this.code = code;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	private final String code;

}