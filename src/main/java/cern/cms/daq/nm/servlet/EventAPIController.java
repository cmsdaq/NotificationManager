package cern.cms.daq.nm.servlet;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cern.cms.daq.nm.EventResource;
import cern.cms.daq.nm.task.TaskManager;

@RestController
@RequestMapping("/events")
public class EventAPIController {

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String get(HttpServletResponse res) {
		return "n/i";
	}

	// this method response to POST request http://localhost/nm/rest/events
	// receives json data sent by client --> map it to EventOccurrenceResource
	// object
	// return EventOccurrenceResource object as json
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody void post(@Valid @RequestBody final List<EventResource> eventResources) {
		for (EventResource eventResource : eventResources) {
			TaskManager.get().getEventResourceBuffer().add(eventResource);
		}
	}
}