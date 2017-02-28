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

@Entity
public class EventResource {

	private static final Logger logger = Logger.getLogger(EventResource.class);

	@NotNull
	private String message;

	private String title;

	private String sender;

	private String textToSpeech;
	
	private Long conditionId;

	private EventType eventType;

	private EventSenderType eventSenderType;
	
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "CET")
	private Date date;

	/** Flag indicating if this notification should be displayed */
	private boolean display;

	/** Flag indicating if this notification should be played */
	private boolean play;

	private int soundId;


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

	public Event asEventOccurrence(Session session) {

		Event event = new Event();

		int MAX_CHARS = 4000;
		if (this.message.length() >= MAX_CHARS) {
			String message = "... (" + (this.message.length() - MAX_CHARS) + " trimmed)";
			logger.info("Message too long " + this.message.length() + " characters, shortening to " + MAX_CHARS);
			this.message = this.message.substring(0, MAX_CHARS - message.length()) + message;
		}

		logger.debug("Message ready to be persisted " + this.message.length());

		event.setMessage(this.message);

		event.setStatus(EventStatus.Received);
		event.setDate(this.date);
		event.setDisplay(this.display);
		event.setPlay(this.play);
		event.setSoundId(this.soundId);
		event.setSender(sender);
		event.setTitle(title);
		event.setTextToSpeech(textToSpeech);
		event.setEventSenderType(eventSenderType);
		event.setEventType(eventType);
		return event;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}

	public int getSoundId() {
		return soundId;
	}

	public void setSoundId(int soundId) {
		this.soundId = soundId;
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

}