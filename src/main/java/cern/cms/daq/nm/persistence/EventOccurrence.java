package cern.cms.daq.nm.persistence;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class EventOccurrence {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@ManyToOne(optional = false)
	private EventType eventType;


    @Column(columnDefinition="VARCHAR2(4000)") 
	private String message;


	@ElementCollection
	@CollectionTable(name = "Actions", joinColumns = @JoinColumn(name = "event_occurrence_id"))
	@Column(name = "action")
	private List<String> actionSteps;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATETIME_FIELD")
	private java.util.Date date;
	
	@Enumerated(EnumType.ORDINAL)
	private EventStatus status;

	@Column(nullable = true)
    private long duration;
	
	/** Flag indicating if this notification should be displayed */
	private boolean display;

	/** Flag indicating if this notification should be played */
	private boolean play;
	
	@Transient
	private int soundId;
    
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

	@Override
	public String toString() {
		return "EventOccurrence [id=" + id + ", eventType=" + eventType + ", message=" + message + ", actionSteps="
				+ actionSteps + ", date=" + date + ", status=" + status + ", duration=" + duration + "]";
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public List<String> getActionSteps() {
		return actionSteps;
	}

	public void setActionSteps(List<String> actionSteps) {
		this.actionSteps = actionSteps;
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
}
