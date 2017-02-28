package cern.cms.daq.nm.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class NotificationOccurrence {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATETIME_FIELD")
	private java.util.Date date;

	@ManyToOne(optional = false)
	private Event eventOccurrence;

	@Enumerated(EnumType.ORDINAL)
	private NotificationStatus status;

	@Enumerated(EnumType.ORDINAL)
	private Channel channel;

	@ManyToOne(optional = false)
	private DummyUser user;

    @Column(columnDefinition="VARCHAR2(4000)") 
	private String message;
    

	public DummyUser getUser() {
		return user;
	}

	public void setUser(DummyUser user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Event getEventOccurrence() {
		return eventOccurrence;
	}

	public void setEventOccurrence(Event eventOccurrence) {
		this.eventOccurrence = eventOccurrence;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "NotificationOccurrence [id=" + id + ", date=" + date + ", eventOccurrence=" + eventOccurrence
				+ ", status=" + status + ", channel=" + channel + ", user=" + user + ", message=" + message + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((eventOccurrence == null) ? 0 : eventOccurrence.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationOccurrence other = (NotificationOccurrence) obj;
		if (channel != other.channel)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (eventOccurrence == null) {
			if (other.eventOccurrence != null)
				return false;
		} else if (!eventOccurrence.equals(other.eventOccurrence))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (status != other.status)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
