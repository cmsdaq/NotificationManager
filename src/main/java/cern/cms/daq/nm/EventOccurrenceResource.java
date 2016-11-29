package cern.cms.daq.nm;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fasterxml.jackson.annotation.JsonFormat;

import cern.cms.daq.nm.persistence.EventOccurrence;
import cern.cms.daq.nm.persistence.EventStatus;
import cern.cms.daq.nm.persistence.EventType;

@Entity
public class EventOccurrenceResource {

	private static final Logger logger = Logger.getLogger(EventOccurrenceResource.class);

	@NotNull
	private Long type_id;

	private Long id;

	@NotNull
	private String message;

	private List<String> action;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "CET")
	private Date date;

	/** Flag indicating if this notification should be displayed */
	private boolean display;

	/** Flag indicating if this notification should be played */
	private boolean play;

	private int soundId;
	private boolean closeable;

	public Long getType_id() {
		return type_id;
	}

	public void setType_id(Long type_id) {
		this.type_id = type_id;
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

	public EventOccurrence asEventOccurrence(Session session) {

		EventOccurrence eventOccurrence = new EventOccurrence();

		int MAX_CHARS = 4000;
		if (this.message.length() >= MAX_CHARS) {
			String message = "... (" + (this.message.length() - MAX_CHARS) + " trimmed)";
			logger.info("Message too long " + this.message.length() + " characters, shortening to " + MAX_CHARS);
			this.message = this.message.substring(0, MAX_CHARS - message.length()) + message;
		}

		logger.debug("Message ready to be persisted " + this.message.length());

		eventOccurrence.setMessage(this.message);

		Criteria cr = session.createCriteria(EventType.class);
		cr.add(Restrictions.eq("id", this.type_id));
		EventType eventType = (EventType) cr.uniqueResult();

		if (eventType == null) {
			// TODO: add other type
			logger.warn("cannot find event type with id " + this.type_id + ", adding to other type");
			return null;
		} else {
			logger.debug("Found event type " + eventType.getName());
		}

		eventOccurrence.setEventType(eventType);
		eventOccurrence.setStatus(EventStatus.Received);
		eventOccurrence.setDate(this.date);
		eventOccurrence.setActionSteps(this.getAction());
		eventOccurrence.setDisplay(this.display);
		eventOccurrence.setPlay(this.play);
		eventOccurrence.setSoundId(this.soundId);
		eventOccurrence.setCloseable(this.closeable);
		return eventOccurrence;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getAction() {
		return action;
	}

	public void setAction(List<String> actionSteps) {
		this.action = actionSteps;
	}

	@Override
	public String toString() {
		return "EventOccurrenceResource [type_id=" + type_id + ", id=" + id + ", message=" + message + ", actionSteps="
				+ action + ", date=" + date + "]";
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

	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
	}

}