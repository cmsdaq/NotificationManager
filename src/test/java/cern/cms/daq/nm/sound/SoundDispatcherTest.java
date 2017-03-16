package cern.cms.daq.nm.sound;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

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
		SoundSystemConnector connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(connectorStub, configuration, selector, true);

		configuration.fakeTriggerDecision = true;
		boolean sent = soundDispatcher.dispatch(generateEvent("a", Sound.DEFAULT, soundDispatcher));

		Assert.assertTrue(sent);
		verify(connectorStub).play(Sound.DEFAULT);
		verify(connectorStub).sayAndListen("a");
	}

	@Test
	public void testDispatchEventNotPlayedAtAll() throws IOException {
		SoundSystemConnector connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(connectorStub, configuration, selector, true);

		configuration.fakeTriggerDecision = false;
		boolean sent = soundDispatcher.dispatch(generateEvent("a", Sound.DEFAULT, soundDispatcher));

		Assert.assertFalse(sent);
		verify(connectorStub, never()).play(Mockito.any(Sound.class));
		verify(connectorStub, never()).sayAndListen(Mockito.anyString());
	}

	@Test
	public void testDispatchEventEmptyTTS() throws IOException {
		SoundSystemConnector connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(connectorStub, configuration, selector, true);

		configuration.fakeTriggerDecision = true;
		boolean sent = soundDispatcher.dispatch(generateEvent("", Sound.DEFAULT, soundDispatcher));

		Assert.assertTrue(sent);
		verify(connectorStub).play(Sound.DEFAULT);
		verify(connectorStub, never()).sayAndListen(Mockito.anyString());
	}

	@Test
	public void testDispatchEventNoTTS() throws IOException {
		SoundSystemConnector connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(connectorStub, configuration, selector, true);

		configuration.fakeTriggerDecision = true;
		boolean sent = soundDispatcher.dispatch(generateEvent(null, Sound.DEFAULT, soundDispatcher));

		Assert.assertTrue(sent);
		verify(connectorStub).play(Sound.DEFAULT);
		verify(connectorStub, never()).sayAndListen(Mockito.anyString());
	}

	@Test
	public void testDispatchEventNoSound() throws IOException {
		SoundSystemConnector connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(connectorStub, configuration, selector, true);

		configuration.fakeTriggerDecision = true;
		boolean sent = soundDispatcher.dispatch(generateEvent("a", null, soundDispatcher));

		Assert.assertTrue(sent);
		verify(connectorStub, never()).play(Mockito.any(Sound.class));
		verify(connectorStub).sayAndListen("a");
	}

	@Test
	public void testDispatchCommunicationProblem() throws IOException {
		SoundSystemConnectorStub connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundConfigurationStub configuration = new SoundConfigurationStub();
		SoundSelectorStub selector = new SoundSelectorStub();
		SoundDispatcher soundDispatcher = new SoundDispatcher(connectorStub, configuration, selector, true);

		configuration.fakeTriggerDecision = true;
		connectorStub.fakeProblem = true;
		boolean sent = soundDispatcher.dispatch(generateEvent("a", null, soundDispatcher));
		Assert.assertFalse(sent);

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

class SoundSystemConnectorStub extends SoundSystemConnector {

	public boolean fakeProblem = false;

	public SoundSystemConnectorStub() {
		super("", 0);
	}

	@Override
	public String sayAndListen(String statement) throws IOException {
		logger.trace("Say: " + statement);
		if (fakeProblem) {
			throw new IOException("Network problem");
		}
		return "All ok";
	}

	@Override
	public String play(Sound sound) throws IOException {
		logger.trace("Play: " + sound);
		if (fakeProblem) {
			throw new IOException("Network problem");
		}
		return "All ok";
	}

	@Override
	public String listen() throws IOException {
		if (fakeProblem) {
			throw new IOException("Network problem");
		}
		return "All ok";
	}

}

class SoundSelectorStub extends SoundSelector {

	@Override
	public Sound selectSound(Event event) {
		return event.getSound();
	}

}
