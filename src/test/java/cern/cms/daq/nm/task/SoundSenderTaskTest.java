package cern.cms.daq.nm.task;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.sound.Sound;
import cern.cms.daq.nm.sound.SoundSystemConnector;

public class SoundSenderTaskTest {

	@Test
	@Ignore
	// TODO: implement this test
	public void test() throws IOException {
		fail("Not yet implemented");
		SoundSystemConnectorStub connectorStub = new SoundSystemConnectorStub();
		boolean sent = true;
		Assert.assertFalse(sent);
		verify(connectorStub, never()).play(Mockito.anyString());
		verify(connectorStub, never()).sayAndListen(Mockito.anyString());
	}

	@Test
	public void testDispatch() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundSystemConnectorStub connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundSenderTask soundSenderTask = new SoundSenderTask(null, queue, connectorStub);
		queue.add(generateEvent("a", Sound.DEFAULT, true));
		soundSenderTask.run();

		verify(connectorStub).play(Sound.DEFAULT.getFilename());
		verify(connectorStub).sayAndListen("a");
		Assert.assertEquals("Queue consumed", 0, queue.size());
	}

	@Test
	public void testDispatchEventEmptyTTS() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundSystemConnectorStub connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundSenderTask soundSenderTask = new SoundSenderTask(null, queue, connectorStub);
		queue.add(generateEvent("", Sound.DEFAULT, true));
		soundSenderTask.run();

		verify(connectorStub).play(Sound.DEFAULT.getFilename());
		verify(connectorStub, never()).sayAndListen(Mockito.anyString());
	}

	@Test
	public void testDispatchEventNoTTS() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundSystemConnectorStub connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundSenderTask soundSenderTask = new SoundSenderTask(null, queue, connectorStub);
		queue.add(generateEvent(null, Sound.DEFAULT, true));
		soundSenderTask.run();

		verify(connectorStub).play(Sound.DEFAULT.getFilename());
		verify(connectorStub, never()).sayAndListen(Mockito.anyString());
	}

	@Test
	public void testDispatchEventNoSound() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundSystemConnectorStub connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundSenderTask soundSenderTask = new SoundSenderTask(null, queue, connectorStub);
		queue.add(generateEvent("a", null, true));
		soundSenderTask.run();

		verify(connectorStub, never()).play(Mockito.anyString());
		verify(connectorStub).sayAndListen("a");
	}

	@Test
	// TODO: verify thet exception was really thrown
	public void testDispatchCommunicationProblem() throws IOException {
		ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
		SoundSystemConnectorStub connectorStub = Mockito.spy(new SoundSystemConnectorStub());
		SoundSenderTask soundSenderTask = new SoundSenderTask(null, queue, connectorStub);
		queue.add(generateEvent("a", null, true));
		connectorStub.fakeProblem = true;
		soundSenderTask.run();
	}

	private Event generateEvent(String textToSpeech, Sound sound, boolean triggered) {
		Event event = new Event();
		event.setTextToSpeech(textToSpeech);
		event.setSound(sound);
		event.setAudible(triggered);
		return event;
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
		public String play(String sound) throws IOException {
			logger.trace("Play: " + sound);
			if (fakeProblem) {
				throw new IOException("Network problem");
			}
			return "All ok";
		}

	}

}
