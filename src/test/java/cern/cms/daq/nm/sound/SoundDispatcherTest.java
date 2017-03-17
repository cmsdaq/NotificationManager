package cern.cms.daq.nm.sound;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cern.cms.daq.nm.persistence.Event;

/**
 * The focus here is to test how dispatcher handles the SoundSystemConnector
 * based on availability of fields TTS and Sound
 * 
 * Triggering the sounds is not the focus here, for this check the
 * SoundConfiguration and it's tests
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class SoundDispatcherTest {

	@BeforeClass
	public static void init() {
		Logger.getLogger(SoundDispatcher.class).setLevel(Level.TRACE);
		Logger.getLogger(SoundSystemConnector.class).setLevel(Level.TRACE);
	}

	@Test
	public void testDispatch() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(queue, configuration, selector, true);

		configuration.fakeTriggerDecision = true;
		soundDispatcher.dispatch(generateEvent("a", Sound.DEFAULT, soundDispatcher));

		
		Assert.assertEquals(1,queue.size());
	}

	@Test
	public void testDispatchEventNotPlayedAtAll() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(queue, configuration, selector, true);

		configuration.fakeTriggerDecision = false;
		soundDispatcher.dispatch(generateEvent("a", Sound.DEFAULT, soundDispatcher));


		Assert.assertEquals(0,queue.size());
	}


	private Event generateEvent(String textToSpeech, Sound sound, SoundDispatcher dispatcher) {
		Event event = new Event();
		event.setTextToSpeech(textToSpeech);
		event.setSound(sound);
		event.setAudible(dispatcher.triggerSound(event));
		if (sound == null)
			event.setSound(dispatcher.selectSound(event));
		return event;
	}
}

class SoundConfigurationStub extends SoundTrigger {

	public boolean fakeTriggerDecision = false;

	@Override
	public boolean triggerSound(Event event) {
		return fakeTriggerDecision;
	}

}

class SoundSelectorStub extends SoundSelector {

	@Override
	public Sound selectSound(Event event) {
		return event.getSound();
	}

}
