package cern.cms.daq.nm.sound;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

/**
 * Parses external system notifications
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class ActionMarshaller {

	private static final Logger logger = Logger.getLogger(ActionMarshaller.class);

	/**
	 * Parses input from external system. The method accepts both cases with
	 * <commandSequence> wrapper and without it.
	 * 
	 * @param input
	 *            raw message from external system
	 * @return list of Action objects parsed from input message
	 */
	public List<Alarm> parseInput(String input) {
		logger.info("Message to parse: " + input);
		List<Alarm> alarms;
		try {
			alarms = parse(input);
			return alarms;
		} catch (JAXBException e) {
			logger.warn("Parsing with command sequence wrapper unsucessful. Will add fake wrapper.");
			logger.warn(e);
			String fakeWrapper = "<CommandSequence>" + input + "</CommandSequence>";

			try {
				alarms = parse(fakeWrapper);
				return alarms;
			} catch (JAXBException e1) {

				logger.error(e1);
				return null;
			}
		}

	}

	/**
	 * Parses the input
	 * 
	 * @param input
	 *            raw message from external system
	 * @return list of Action objects parsed from input message
	 * @throws JAXBException
	 *             exception in case parsing problem occures
	 */
	private List<Alarm> parse(String input) throws JAXBException {

		JAXBContext jaxbContext;
		jaxbContext = JAXBContext.newInstance(CommandSequence.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		CommandSequence commandSequence = (CommandSequence) jaxbUnmarshaller.unmarshal(stream);
		List<Alarm> alarm = commandSequence.getAlarm();
		logger.info("Alarm sucessfully parsed: " + alarm);
		return alarm;

	}
}
