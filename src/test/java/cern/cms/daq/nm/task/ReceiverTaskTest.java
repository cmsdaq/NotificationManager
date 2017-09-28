package cern.cms.daq.nm.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.websocket.Session;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cern.cms.daq.nm.EventResource;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.PersistenceManager;
import cern.cms.daq.nm.sound.Sound;
import cern.cms.daq.nm.sound.SoundDispatcher;
import cern.cms.daq.nm.websocket.EventSessionHandler;

public class ReceiverTaskTest {

	ReceiverTask sut;
	ConcurrentLinkedQueueStub<EventResource> eventResourceBuffer;
	SoundDispatcherStub soundDispatcher;
	EventPersistorStub eventPersistor;
	EventSessionHandler dashboard;

	@Before
	public void prepare() {

		eventResourceBuffer = new ConcurrentLinkedQueueStub<EventResource>();
		ConcurrentLinkedQueue<Event> eventBuffer = new ConcurrentLinkedQueue<Event>();

		eventPersistor = spy(new EventPersistorStub());
		soundDispatcher = spy(new SoundDispatcherStub());
		dashboard = spy(new EventSessionHandlerStub());
		sut = new ReceiverTask(eventResourceBuffer, eventBuffer, eventPersistor, soundDispatcher, dashboard);
	}

	@Test
	public void cosumesEventsTest() {
		eventResourceBuffer.add(generate());
		sut.run();
		Assert.assertEquals("Consumed", 0, eventResourceBuffer.size());
		verify(eventPersistor).persist(any(Event.class));
		verify(soundDispatcher).dispatch(any(Event.class));
		verify(soundDispatcher, never()).selectSound(any(Event.class));
		verify(dashboard).addEvent(any(Event.class));
	}

	@Test
	public void audibleTest() {
		eventResourceBuffer.add(generate());
		soundDispatcher.fakeTrigger = true;
		sut.run();
		Assert.assertEquals("Consumed", 0, eventResourceBuffer.size());
		verify(eventPersistor).persist(any(Event.class));
		verify(soundDispatcher).dispatch(any(Event.class));
		verify(soundDispatcher).selectSound(any(Event.class));
		verify(dashboard).addEvent(any(Event.class));
	}

	@Test
	public void processingLimitTest() {
		List<EventResource> fakeList = new ArrayList<>();
		fakeList.add(generate());

		eventResourceBuffer.fake = fakeList.iterator();
		eventResourceBuffer.fakeConcurrentAdd = true;

		eventResourceBuffer.add(generate());

		sut.run();
		Assert.assertEquals("Not consumed", 1, eventResourceBuffer.size());
		sut.run();

		Assert.assertEquals("Consumed", 0, eventResourceBuffer.size());

	}

	@Test
	public void emptyTest() {
		sut.run();
		verify(eventPersistor, never()).persist(any(Event.class));
		verify(soundDispatcher, never()).dispatch(any(Event.class));
		verify(dashboard, never()).addEvent(any(Event.class));
	}

	public static EventResource generate() {
		EventResource eventResource = new EventResource();
		eventResource.setSender("JUNIT");
		return eventResource;
	}

}

class ConcurrentLinkedQueueStub<EventResource> extends ConcurrentLinkedQueue<EventResource> {

	public Iterator<EventResource> fake;
	public boolean fakeConcurrentAdd = false;

	@Override
	public EventResource poll() {
		if (fakeConcurrentAdd && fake.hasNext()) {
			this.add(fake.next());
		}

		return super.poll();
	}

}

class EventPersistorStub extends PersistenceManager {

	public EventPersistorStub() {
		super(new EntityManagerFactoryStub());
	}

	@Override
	public void persist(Set<Event> events) {
		return;
	}

	@Override
	public void persist(Event event) {
		return;
	}

}

class SoundDispatcherStub extends SoundDispatcher {

	public boolean fakeTrigger = false;

	public SoundDispatcherStub() {
		super(null, null, null, false);
	}

	@Override
	public void dispatch(Event event) {
		return;
	}

	@Override
	public boolean triggerSound(Event event) {
		return fakeTrigger;
	}

	@Override
	public Sound selectSound(Event event) {
		return null;
	}

}

class EventSessionHandlerStub extends EventSessionHandler {

	@Override
	public void addSession(Session session) {
	}

	@Override
	public void removeSession(Session session) {
	}

	@Override
	public List<Event> getEvents() {
		return null;
	}

	@Override
	public void addEvent(Event event) {
	}

}

class EntityManagerFactoryStub implements EntityManagerFactory {

	@Override
	public EntityManager createEntityManager() {
		return null;
	}

	@Override
	public EntityManager createEntityManager(Map map) {
		return null;
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return null;
	}

	@Override
	public Metamodel getMetamodel() {
		return null;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public void close() {

	}

	@Override
	public Map<String, Object> getProperties() {
		return null;
	}

	@Override
	public Cache getCache() {
		return null;
	}

	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return null;
	}

}