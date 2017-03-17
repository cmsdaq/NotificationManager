package cern.cms.daq.nm.sound;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import cern.cms.daq.nm.persistence.Event;

public class DominantSelector {

	private final static Logger logger = Logger.getLogger(DominantSelector.class);

	/**
	 * 
	 * Used only for selecting events with Sender Type Expert TODO: check if
	 * there is priority TODO: check if there is logic module
	 */
	public Pair<Event, Set<Event>> selectDominantEvent(Set<Event> events) {

		Set<Event> secondaryEvents = new HashSet<>();
		Event dominant = null;

		for (Event event : events) {
			
			if (dominant == null) {
				dominant = event;
			} else {

				if (dominant.getPriority().ordinal() < event.getPriority().ordinal()) {
					dominant = event;

				} else if (dominant.getPriority().ordinal() == event.getPriority().ordinal()) {

					if (dominant.getLogicModule().getUsefulness() < event.getLogicModule().getUsefulness()) {
						dominant = event;
					} else if (dominant.getLogicModule().getUsefulness() == event.getLogicModule().getUsefulness()) {
						// this means that the usefulness of LM was not
						// configured according to recommendation
						logger.warn("Misconfiguration of LM usefulness, there are two LM with same usefulness level: "
								+ dominant.getLogicModule() + "," + event.getLogicModule()
								+ ", adjust usfulness according to recomendations.");

					} else {
						// nothing to do: current event not more useful than
						// current dominant
					}

				} else {
					// nothing to do: current event not more important than
					// current dominant
				}

			}
		}

		for (Event event : events) {
			if (event != dominant) {
				secondaryEvents.add(event);
			}
		}

		return Pair.of(dominant, secondaryEvents);
	}

}
