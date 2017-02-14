package cern.cms.daq.nm.sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.EventOccurrenceResource;
import cern.cms.daq.nm.task.TaskManager;

public class ExternalSoundReceiver implements Runnable {
	private final Socket csocket;
	private final static Logger logger = Logger.getLogger(ExternalSoundReceiver.class);

	ExternalSoundReceiver(Socket csocket) {
		this.csocket = csocket;
	}

	public static void startSoundReceiver(int socketPort) throws Exception {
		ServerSocket ssock = new ServerSocket(socketPort);
		logger.info("Listening for external clients to connect");

		while (true) {
			Socket sock = ssock.accept();
			logger.info("External client connected");
			new Thread(new ExternalSoundReceiver(sock)).start();
		}

	}

	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			char[] buffer = new char[200];
			int count = bufferedReader.read(buffer, 0, 200);
			String externalNotification = new String(buffer, 0, count);

			EventOccurrenceResource eventOccurrenceResource = new EventOccurrenceResource();
			eventOccurrenceResource.setMessage(externalNotification);
			eventOccurrenceResource.setDate(new Date());
			eventOccurrenceResource.setPlay(true);
			eventOccurrenceResource.setDisplay(true);
			// eventOccurrenceResource.setId(1L);
			eventOccurrenceResource.setCloseable(true);
			eventOccurrenceResource.setType_id(1L);
			TaskManager.get().getEventResourceBuffer().add(eventOccurrenceResource);

			csocket.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
