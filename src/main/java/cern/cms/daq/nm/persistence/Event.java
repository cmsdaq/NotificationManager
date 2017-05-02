package cern.cms.daq.nm.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cern.cms.daq.nm.sound.Priority;
import cern.cms.daq.nm.sound.Sound;

@Entity
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "EVENT_TYPE")
	private EventType eventType;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "EVENT_SENDER_TYPE")
	private EventSenderType eventSenderType;

	@Column(columnDefinition = "VARCHAR2(40)")
	private String title;

	@Column(columnDefinition = "VARCHAR2(200)")
	private String textToSpeech;

	@Column(columnDefinition = "VARCHAR2(200)")
	private String sender;

	@Column(columnDefinition = "VARCHAR2(4000)")
	private String message;

	/** Sound or speech generated */
	private boolean audible;

	@Column(nullable = true)
	private int conditionId;

	@Enumerated(EnumType.ORDINAL)
	private Sound sound;

	@Enumerated(EnumType.ORDINAL)
	private Priority priority;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATETIME_FIELD")
	private java.util.Date date;

	@Enumerated(EnumType.ORDINAL)
	private EventStatus status;

	@Enumerated(EnumType.ORDINAL)
	private LogicModuleView logicModule;

	/** Flag indicating if this notification should be displayed */
	private boolean display;

	@Transient
	private String customSound;

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EventStatus getStatus() {
		return status;
	}

	public void setStatus(EventStatus status) {
		this.status = status;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public int getConditionId() {
		return conditionId;
	}

	public void setConditionId(int conditionId) {
		this.conditionId = conditionId;
	}

	public EventSenderType getEventSenderType() {
		return eventSenderType;
	}

	public void setEventSenderType(EventSenderType eventSenderType) {
		this.eventSenderType = eventSenderType;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", eventType=" + eventType + ", eventSenderType=" + eventSenderType + ", title="
				+ title + ", textToSpeech=" + textToSpeech + ", sender=" + sender + ", message=" + message
				+ ", conditionId=" + conditionId + ", sound=" + sound + ", date=" + date + ", status=" + status
				+ ", display=" + display + "]";
	}

	public LogicModuleView getLogicModule() {
		return logicModule;
	}

	public void setLogicModule(LogicModuleView logicModule) {
		this.logicModule = logicModule;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isAudible() {
		return audible;
	}

	public void setAudible(boolean audible) {
		this.audible = audible;
	}

	public String getCustomSound() {
		return customSound;
	}

	public void setCustomSound(String customSound) {
		this.customSound = customSound;
	}
}
