package cern.cms.daq.nm.sound;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class ActionMarshaller {

	private static final Logger logger = Logger.getLogger(ActionMarshaller.class);

	/**
	 * Parse external notification
	 * 
	 * @param input
	 *            xml input from external source
	 * @return Alarm object representing the request
	 * 
	 * @TODO: handle case when there is not sender
	 */
	public Alarm parseInput(String input) {

		logger.info("Message to parse: " + input);

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Alarm.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
			Alarm alarm = (Alarm) jaxbUnmarshaller.unmarshal(stream);
			logger.info("Alarm sucessfully parsed: " + alarm);
			return alarm;
		} catch (JAXBException e) {
			logger.error("Problem parsing xml", e);
		}

		return null;

	}

	public Alarm parseInput2(String input) {
		logger.info("Message to parse again: " + input);

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(CommandSequence.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
			CommandSequence commandSequence = (CommandSequence) jaxbUnmarshaller.unmarshal(stream);
			Alarm alarm = commandSequence.getAlarm();
			logger.info("Alarm sucessfully parsed: " + alarm);
			return alarm;
		} catch (JAXBException e) {
			logger.error("Problem parsing xml", e);
		}

		return null;
	}
}
