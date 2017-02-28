package cern.cms.daq.nm.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.persistence.Event;

public class EventSessionHandler {

	private static final Logger logger = Logger.getLogger(EventSessionHandler.class);

	private final Set<Session> sessions = new HashSet<>();
	private final List<Event> events = new ArrayList<>();

	public EventSessionHandler() {
		final EventSessionHandler handler = this;
		/*
		 * (new Thread() { public void run() { Random generator = new Random();
		 * for (int i = 0; i < 100; i++) {
		 * 
		 * if (events.size() >= 10) { removeEvent(events.iterator().next()); }
		 * 
		 * try { int sleepTime = generator.nextInt(5000) + 100;
		 * Thread.sleep(sleepTime); } catch (InterruptedException e) {
		 * e.printStackTrace(); } Event event = new Event(); event.setId((long)
		 * i); event.setTitle("EVENT " + i); event.setDate(new Date());
		 * event.setMessage("Event " + i); event.setTextToSpeech(
		 * "EXPERT Event: On"); handler.addEvent(event);
		 * 
		 * } } }).start();
		 */
	}

	public void addSession(Session session) {
		sessions.add(session);
		for (Event event : events) {
			JsonObject addMessage = createAddMessage(event);
			sendToSession(session, addMessage);
		}
	}

	public void removeSession(Session session) {
		sessions.remove(session);
	}

	public List<Event> getEvents() {
		return new ArrayList<>(events);
	}

	public void addEvent(Event event) {

		if (events.size() >= 10) {
			removeEvent(events.iterator().next());
		}
		events.add(event);
		JsonObject addMessage = createAddMessage(event);
		sendToAllConnectedSessions(addMessage);
	}

	public void removeEvent(Event event) {
		if (event != null) {
			events.remove(event);
			JsonProvider provider = JsonProvider.provider();
			JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id", event.getId())
					.build();
			sendToAllConnectedSessions(removeMessage);
		}
	}

	private JsonObject createAddMessage(Event event) {
		JsonProvider provider = JsonProvider.provider();
		logger.info("is provider null? " + provider == null);
		logger.info("Creating message for event: " + event);

		String tts = event.getTextToSpeech() != null ? event.getTextToSpeech() : "";
		String message = event.getMessage() != null ? event.getMessage() : "";
		String title = event.getTitle() != null ? event.getTitle() : "";

		JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("id", event.getId())
				.add("name", title).add("type", event.getDate().toString()).add("status", tts)
				.add("description", message).build();

		logger.info("Created message for event: " + addMessage);
		return addMessage;
	}

	private void sendToAllConnectedSessions(JsonObject message) {
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	private void sendToSession(Session session, JsonObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
			sessions.remove(session);
			logger.error(ex);
		}
	}
}