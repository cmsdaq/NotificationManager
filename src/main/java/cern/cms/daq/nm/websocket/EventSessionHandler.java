package cern.cms.daq.nm.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.persistence.Event;

public class EventSessionHandler {

    private static final Logger logger = Logger.getLogger(EventSessionHandler.class);

    private final Set<Session> sessions = new HashSet<>();
    private final List<Event> events = new ArrayList<>();

    private final int maximumNumberOfEventsHandled = 100;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EventSessionHandler() {
    }

    public void addSession(Session session) {
        sessions.add(session);
        JsonObject addMessage = createAddMessage(events);
        sendToSession(session, addMessage);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public void addEvent(Event event) {

        if (events.size() >= maximumNumberOfEventsHandled) {
            events.remove(events.iterator().next());
        }
        events.add(event);
        JsonObject addMessage = createAddMessage(event);
        sendToAllConnectedSessions(addMessage);
    }

    private JsonObject createAddMessage(List<Event> events) {
        JsonProvider provider = JsonProvider.provider();
        logger.debug("is provider null? " + provider == null);


        JsonArrayBuilder objectsBuilder = provider.createArrayBuilder();
        for (Event event : events) {
            logger.debug("Creating message for event: " + event);
            if (event.getTextToSpeech() != null) {
                //objectBuilder
            }

            String tts = event.getTextToSpeech() != null ? event.getTextToSpeech() : "";
            String message = event.getMessage() != null ? event.getMessage() : "";
            String title = event.getTitle() != null ? event.getTitle() : "";

            String soundPlayed = event.getSound() != null ? event.getSound().getDisplayName() : "";


            JsonObject object = provider.createObjectBuilder().add("id", event.getId())
                    .add("title", title).add("timestamp", dateFormat.format(event.getDate())).add("tts", tts)
                    .add("sound", soundPlayed).add("description", message).build();
            objectsBuilder.add(object);

        }


        JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("objects", objectsBuilder.build()).build();


        logger.debug("Created message for event: " + addMessage);
        return addMessage;
    }

    private JsonObject createAddMessage(Event event) {
        List<Event> events = new ArrayList<>();

        return createAddMessage(events);
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