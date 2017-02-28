package cern.cms.daq.nm.persistence;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import cern.cms.daq.nm.Condition;

@Entity
public class Configuration {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@ElementCollection(targetClass = Channel.class)
	@JoinTable(name = "CONFIGURATIONS_CHANNELS")
	@Enumerated(EnumType.ORDINAL)
	private Set<Channel> channels;

	@ElementCollection(targetClass = Condition.class)
	@JoinTable(name = "CONFIGURATIONS_CONDITIONS")
	@Enumerated(EnumType.ORDINAL)
	private Set<Condition> conditions;

	@ElementCollection(targetClass = EventType.class)
	@JoinTable(name = "CONFIGURATIONS_EVENTTYPES")
	@Enumerated(EnumType.ORDINAL)
	private Set<EventType> eventTypes;

	@ManyToOne(optional = false)
	private DummyUser user;

	public Set<EventType> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(Set<EventType> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public Set<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(Set<Condition> conditions) {
		this.conditions = conditions;
	}

	public DummyUser getUser() {
		return user;
	}

	public void setUser(DummyUser user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Channel> getChannels() {
		return channels;
	}

	public void setChannels(Set<Channel> channels) {
		this.channels = channels;
	}

}
