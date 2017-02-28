package cern.cms.daq.nm.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.apache.log4j.Logger;

public class EventSessionHandler {

	private static final Logger logger = Logger.getLogger(EventSessionHandler.class);

	private int eventId = 0;
	private final Set<Session> sessions = new HashSet<>();
	private final List<Event> events = new ArrayList<>();

	public EventSessionHandler() {
		final EventSessionHandler handler = this;
		(new Thread() {
			public void run() {
				Random generator = new Random();
				for (int i = 0; i < 100; i++) {

					if (events.size() >= 10) {
						removeEvent(events.iterator().next().getId());
					}

					try {
						int sleepTime = generator.nextInt(5000) + 100;
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Event event = new Event();
					event.setId(i);
					event.setName("EVENT " + i);
					event.setType("Type A");
					event.setDescription("Event " + i);
					event.setStatus("On");
					handler.addEvent(event);

				}
			}
		}).start();
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
		event.setId(eventId);
		events.add(event);
		eventId++;
		JsonObject addMessage = createAddMessage(event);
		sendToAllConnectedSessions(addMessage);
	}

	public void removeEvent(int id) {
		Event event = getEventById(id);
		if (event != null) {
			events.remove(event);
			JsonProvider provider = JsonProvider.provider();
			JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id", id).build();
			sendToAllConnectedSessions(removeMessage);
		}
	}

	private Event getEventById(int id) {
		for (Event event : events) {
			if (event.getId() == id) {
				return event;
			}
		}
		return null;
	}

	private JsonObject createAddMessage(Event event) {
		JsonProvider provider = JsonProvider.provider();
		JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("id", event.getId())
				.add("name", event.getName()).add("type", event.getType()).add("status", event.getStatus())
				.add("description", event.getDescription()).build();

		logger.info("Creating message for event: " + event);
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