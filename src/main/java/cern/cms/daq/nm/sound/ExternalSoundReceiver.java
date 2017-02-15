package cern.cms.daq.nm.sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;

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
			logger.info("External client connected " + sock.getRemoteSocketAddress());
			new Thread(new ExternalSoundReceiver(sock)).start();
		}

	}

	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			char[] buffer = new char[4000];
			int count = bufferedReader.read(buffer, 0, 4000);
			String externalNotification = new String(buffer, 0, count);
			ActionMarshaller am = new ActionMarshaller();
			List<Alarm> alarms = am.parseInput(externalNotification);

			if (alarms != null) {

				for (Alarm alarm : alarms) {
					EventOccurrenceResource eventOccurrenceResource = new EventOccurrenceResource();
					eventOccurrenceResource.setMessage(alarm.toString());
					eventOccurrenceResource.setDate(new Date());
					eventOccurrenceResource.setPlay(true);
					eventOccurrenceResource.setDisplay(false);
					// eventOccurrenceResource.setId(1L);
					eventOccurrenceResource.setCloseable(false);
					eventOccurrenceResource.setType_id(0L);
					TaskManager.get().getEventResourceBuffer().add(eventOccurrenceResource);
				}

				logger.info("Request successfully processed.");
				PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(csocket.getOutputStream()));
				printWriter.print("All ok\n");
				printWriter.flush();
			} else {
				logger.error("External notification could not be parsed");
			}

		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				csocket.close();
			} catch (IOException e) {
				logger.info("Problem closing socket");
			}
		}
	}
}
