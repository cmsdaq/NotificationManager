package cern.cms.daq.nm.sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import org.apache.log4j.Logger;

public class SoundSystemManager {

	private final String url;
	private final int port;
	private Socket connection;

	private final static Logger logger = Logger.getLogger(SoundSystemManager.class);

	public SoundSystemManager(String urlString, int port) {
		this.url = urlString;
		this.port = port;
	}

	public String sayAndListen(String statement) throws IOException {

		setUpConnection(url, port);
		say("<talk>" + statement + "</talk>");
		return listen();
	}

	public String play(Sound sound) throws IOException {
		setUpConnection(url, port);
		say("<play file=\"" + sound.getFilename() + "\"/>");
		return listen();
	}

	private void say(String statement) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		printWriter.print(statement);
		printWriter.flush();
	}

	public String listen() throws IOException {
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
			logger.error(e);
		}
	}
}