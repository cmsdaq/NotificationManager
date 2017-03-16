package cern.cms.daq.nm.sound;

import org.junit.Assert;
import org.junit.Test;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventSenderType;
import cern.cms.daq.nm.persistence.LogicModuleView;

public class SoundTriggerTest {

	@Test
	public void dontTriggerExpertSoundTest() {

		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(ConditionPriority.DEFAULTT);

		Assert.assertFalse(soundConfiguration.triggerSound(event));
	}

	@Test
	public void triggerExpertSoundTest() {

		SoundTrigger soundConfiguration = new SoundTrigger();

		Event event = new Event();
		event.setEventSenderType(EventSenderType.Expert);
		event.setLogicModule(LogicModuleView.RunOngoing);
		event.setPriority(ConditionPriority.IMPORTANT);

		Assert.assertTrue(soundConfiguration.triggerSound(event));
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
