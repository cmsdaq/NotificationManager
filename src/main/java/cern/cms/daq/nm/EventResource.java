package cern.cms.daq.nm;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.fasterxml.jackson.annotation.JsonFormat;

import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventSenderType;
import cern.cms.daq.nm.persistence.EventStatus;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;
import cern.cms.daq.nm.sound.ConditionPriority;
import cern.cms.daq.nm.sound.Sound;

@Entity
public class EventResource {

	private static final Logger logger = Logger.getLogger(EventResource.class);

	@NotNull
	private String message;

	@NotNull
	private String title;

	@NotNull
	private EventType eventType;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "CET")
	private Date date;

	@NotNull
	private EventSenderType eventSenderType;

	@NotNull
	private String sender;

	private String textToSpeech;

	private String sound;

	private Long conditionId;

	private ConditionPriority priority;

	private LogicModuleView logicModule;

	public Event asEventOccurrence(Session session) {

		Event event = new Event();

		String messageTrimmed = this.message;
		int MAX_CHARS_FOR_DESCRIPTION = 4000;
		if (this.message != null && this.message.length() > MAX_CHARS_FOR_DESCRIPTION) {
			String messageTrimText = "... (" + (this.message.length() - MAX_CHARS_FOR_DESCRIPTION) + " trimmed)";
			logger.info("Message too long " + this.message.length() + " characters, shortening to "
					+ MAX_CHARS_FOR_DESCRIPTION);
			messageTrimmed = this.message.substring(0, MAX_CHARS_FOR_DESCRIPTION - messageTrimText.length())
					+ messageTrimText;
		}

		String titleTrimmed = this.title;
		int MAX_CHARS_FOR_TITLE = 40;
		if (this.title != null && this.title.length() > MAX_CHARS_FOR_TITLE) {
			String titleTrimText = "...";
			logger.info("Title too long " + this.title.length() + " characters, shortening to " + MAX_CHARS_FOR_TITLE);
			titleTrimmed = this.title.substring(0, MAX_CHARS_FOR_TITLE - titleTrimText.length()) + titleTrimText;
		}

		String senderTrimmed = this.sender;
		if (this.sender != null && this.sender.length() > 200) {
			senderTrimmed = this.sender.substring(0, 200);
		}

		String textToSpeechTrimmed = this.textToSpeech;
		if (this.textToSpeech != null && this.textToSpeech.length() > 200) {
			textToSpeechTrimmed = this.textToSpeech.substring(0, 200);
		}

		logger.debug("Message ready to be persisted " + this.message.length());

		event.setMessage(messageTrimmed);
		event.setTitle(titleTrimmed);
		event.setSender(senderTrimmed);
		event.setTextToSpeech(textToSpeechTrimmed);

		event.setStatus(EventStatus.Received);
		event.setDate(this.date);

		if (sound != null) {
			event.setSound(Sound.getByFilename(sound));
			if (event.getSound() == Sound.OTHER) {
				event.setCustomSound(sound);
			}
		}
		event.setEventSenderType(eventSenderType);
		event.setEventType(eventType);
		event.setLogicModule(logicModule);
		event.setPriority(priority);
		return event;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTextToSpeech() {
		return textToSpeech;
	}

	public void setTextToSpeech(String textToSpeech) {
		this.textToSpeech = textToSpeech;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public EventSenderType getEventSenderType() {
		return eventSenderType;
	}

	public void setEventSenderType(EventSenderType eventSenderType) {
		this.eventSenderType = eventSenderType;
	}

	public Long getConditionId() {
		return conditionId;
	}

	public void setConditionId(Long conditionId) {
		this.conditionId = conditionId;
	}

	public LogicModuleView getLogicModule() {
		return logicModule;
	}

	public void setLogicModule(LogicModuleView logicModule) {
		this.logicModule = logicModule;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public ConditionPriority getPriority() {
		return priority;
	}

	public void setPriority(ConditionPriority priority) {
		this.priority = priority;
	}

}