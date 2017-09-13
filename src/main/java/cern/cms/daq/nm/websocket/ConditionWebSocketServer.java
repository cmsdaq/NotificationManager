package cern.cms.daq.nm.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

//@ApplicationScoped
//@ServerEndpoint("/actionss")
public class ConditionWebSocketServer {

	private static final Logger logger = Logger.getLogger(ConditionWebSocketServer.class);

	public static ConditionSessionHandler sessionHandler = new ConditionSessionHandler();

	@OnOpen
	public void open(Session session) {
		logger.info("Connected " + session.getId());
		sessionHandler.addSession(session);
	}

	@OnClose
	public void close(Session session) {
		logger.info("Closed " + session.getId());
		sessionHandler.removeSession(session);
	}

	@OnError
	public void onError(Throwable error) {
		logger.error(error);
		// error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) {

	}
}