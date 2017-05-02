package cern.cms.daq.nm.sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.Application;
import cern.cms.daq.nm.Setting;

public class SoundSystemConnector {

	private final String url;
	private final int port;
	private Socket connection;

	protected final static Logger logger = Logger.getLogger(SoundSystemConnector.class);

	protected SoundSystemConnector(String urlString, int port) {
		this.url = urlString;
		this.port = port;
	}

	public String sayAndListen(String statement) throws IOException {

		logger.info("Speak statement: " + statement);
		setUpConnection(url, port);
		say("<talk>" + statement + "</talk>");
		return listen();
	}

	public String play(String soundFilename) throws IOException {
		setUpConnection(url, port);
		say("<play file=\"" + soundFilename + "\"/>");
		return listen();
	}

	private void say(String statement) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		printWriter.print(statement);
		printWriter.flush();
	}

	private String listen() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		char[] buffer = new char[200];
		int count = bufferedReader.read(buffer, 0, 200);
		return new String(buffer, 0, count);
	}

	private void setUpConnection(String urlString, int port) {
		try {
			URL theURL = new URL(urlString);
			connection = new Socket(theURL.getHost(), port);
		} catch (IOException e) {
			logger.error("Request to CMS-WOW failed: ", e);
		}
	}

	public static SoundSystemConnector buildSoundSystemConnector() {
		String soundUrl = "";
		int soundPort = 0;
		logger.info("Sound enabled, parsing url and port");
		String soundProp = (String) Application.get().getProp().get(Setting.SOUND_URL.getCode());
		String soundPortProp = (String) Application.get().getProp().get(Setting.SOUND_PORT.getCode());
		try {
			soundPort = Integer.parseInt(soundPortProp);
		} catch (NumberFormatException e) {
			logger.error("Cannot parse sound port", e);
		}

		if (soundProp != "" && soundPort != 0) {
			soundUrl = soundProp;
		}

		logger.info("Initializing sound system with url: " + soundUrl + ":" + soundPort);
		return new SoundSystemConnector(soundUrl, soundPort);
	}
}