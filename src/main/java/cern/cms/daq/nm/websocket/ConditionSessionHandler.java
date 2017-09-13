package cern.cms.daq.nm.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.apache.log4j.Logger;

public class ConditionSessionHandler {

	private static final Logger logger = Logger.getLogger(ConditionSessionHandler.class);

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	Condition c;

	public ConditionSessionHandler() {
		System.out.println("initializing the Mock conditions session handler");

		(new Thread() {
			public void run() {
				c = new Condition();
				c.setTitle("FED deadtime");
				c.setDescription("FED deadtime x%");
				c.setDuration(100000L);
				setCurrent(c);
				Random generator = new Random();

				for (int i = 0; i < 100; i++) {

					try {
						int sleepTime = generator.nextInt(8000) + 100;
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					c.setDescription("FED deadtime <<" + generator.nextInt(100) + ">> %");

					update(c);

				}
			}
		}).stop();
	}
	
	private final Set<Session> sessions = new HashSet<>();

	/**
	 * Add new session - called on page load
	 * 
	 * @param session
	 */
	public void addSession(Session session) {
		sessions.add(session);

		JsonObject addMessage = createAddMessage(c);
		JsonObject currentMessage = createCurrentMessagee(1L);

		sendToSession(session, currentMessage);
		sendToSession(session, addMessage);
	}

	/**
	 * Remove ression - called when the browser is turned off
	 * 
	 * @param session
	 */
	public void removeSession(Session session) {
		sessions.remove(session);
	}

	public void setCurrent(Condition condition) {
		logger.info("Setting current");
		JsonObject addMessage = createAddMessage(condition);
		sendToAllConnectedSessions(addMessage);
	}

	public void update(Condition condition) {
		logger.info("Updating");
		JsonObject addMessage = createUpdateMessage(condition);
		sendToAllConnectedSessions(addMessage);
	}

	/**
	 * Create message
	 * 
	 * @param condition
	 * @return
	 */
	private JsonObject createAddMessage(Condition condition) {
		JsonProvider provider = JsonProvider.provider();
		logger.info("Creating current condition message for : " + condition);

		String message = condition.getActionSteps() != null ? condition.getActionSteps().toString() : "";
		String description = condition.getDescription() != null ? condition.getDescription() : "";
		String title = condition.getTitle() != null ? condition.getTitle() : "#" + condition.getId();
		String duration = condition.getEnd() == null ? "Ongoing" : "finished";
		JsonArrayBuilder actionArrayBuilder = provider.createArrayBuilder();
		if (condition.getActionSteps() != null) {
			for (String step : condition.getActionSteps()) {
				actionArrayBuilder.add(step);
			}
		}
		JsonArray actionArray = actionArrayBuilder.build();

		
		JsonObject object = provider.createObjectBuilder().add("id", condition.getId()).add("title", title)
				.add("status", "ongoing").add("description", description)
				.add("duration", duration).add("steps", actionArray).add("timestamp", dateFormat.format( new Date())).build();

		JsonArray objects = provider.createArrayBuilder().add(object).build();
		
		JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("objects", objects).build();

		logger.info("Created message for current condition: " + addMessage);
		return addMessage;
	}
	

	public JsonObject createCurrentMessagee(Long id){
		JsonProvider provider = JsonProvider.provider();
		JsonObject addMessage = provider.createObjectBuilder().add("action", "select").add("id", id).build();

		return addMessage;
	}


	/**
	 * Create message
	 * 
	 * @param condition
	 * @return
	 */
	private JsonObject createUpdateMessage(Condition condition) {
		JsonProvider provider = JsonProvider.provider();
		logger.debug("Creating update for condition: " + condition);

		String status = "updated";

		JsonObject object = provider.createObjectBuilder().add("id", condition.getId()).add("status", status)
				.add("description", condition.getDescription()).build();

		JsonArray objects = provider.createArrayBuilder().add(object).build();
		JsonObject updateMessage = provider.createObjectBuilder().add("action", "update").add("objects", objects).build();

		logger.debug("Created message for event: " + updateMessage);
		return updateMessage;
	}

	/**
	 * Send message to all connected sessions
	 * 
	 * @param message
	 */
	private void sendToAllConnectedSessions(JsonObject message) {
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	/**
	 * Send message to session
	 * 
	 * @param session
	 * @param message
	 */
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

class Condition {

	private static int id;
	private String title;
	private String description;
	private Long duration;

	public Condition() {
		id++;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Object getEnd() {
		return new Date();
	}

	public Object getStart() {
		return new Date();
	}

	public List<String> getActionSteps() {
		return Arrays.asList("Step 1", "Step 2");
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

}