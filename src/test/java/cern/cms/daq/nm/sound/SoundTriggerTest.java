package cern.cms.daq.nm.sound;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventSenderType;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class SoundTriggerTest {

	@Test
	public void dontTriggerLowPriorityCondition() {

		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setEventType(EventType.ConditionStart);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(Priority.DEFAULTT);

		Assert.assertFalse(soundConfiguration.triggerSound(event));
	}

	@Test(expected = NotificationException.class)
	public void missingEventTypeTriggerTest() {

		Logger.getRootLogger().setLevel(Level.TRACE);
		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(Priority.IMPORTANT);

		Assert.assertTrue(soundConfiguration.triggerSound(event));
	}

	@Test
	public void triggerConditionStartTest() {

		Logger.getRootLogger().setLevel(Level.TRACE);
		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setEventType(EventType.ConditionStart);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(Priority.IMPORTANT);

		Assert.assertTrue(soundConfiguration.triggerSound(event));
	}

	@Test
	public void triggerConditionEndTest() {

		Logger.getRootLogger().setLevel(Level.TRACE);
		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setEventType(EventType.ConditionEnd);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(Priority.CRITICAL);

		Assert.assertTrue(soundConfiguration.triggerSound(event));
	}

	@Test
	public void dontTriggerConditionEndTest() {

		Logger.getRootLogger().setLevel(Level.TRACE);
		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setEventType(EventType.ConditionEnd);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(Priority.IMPORTANT);

		Assert.assertFalse(soundConfiguration.triggerSound(event));
	}

	@Test
	public void triggerExpertSingleTest() {

		Logger.getRootLogger().setLevel(Level.TRACE);
		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setEventType(EventType.Single);
		event.setLogicModule(LogicModuleView.LHCBeamModeComparator);
		event.setPriority(Priority.IMPORTANT);

		Assert.assertTrue(soundConfiguration.triggerSound(event));
	}

	@Test
	public void dontTriggerExpertSingleTest() {

		Logger.getRootLogger().setLevel(Level.TRACE);
		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setEventType(EventType.Single);
		event.setLogicModule(LogicModuleView.LevelZeroStateComparator);
		event.setPriority(Priority.DEFAULTT);

		Assert.assertFalse(soundConfiguration.triggerSound(event));
	}

	@Test
	public void triggerExternalSoundTest() {

		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.External);

		Assert.assertTrue(soundConfiguration.triggerSound(event));
	}

	@Test(expected = NotificationException.class)
	public void dontTriggerUnknownSourceSoundTest() {

		SoundTrigger soundConfiguration = new SoundTrigger();
		Event event = new Event();
		soundConfiguration.triggerSound(event);
	}

}
