package cern.cms.daq.nm.servlet;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cern.cms.daq.nm.EventCloseResource;
import cern.cms.daq.nm.persistence.EventOccurrence;
import cern.cms.daq.nm.task.TaskManager;

@RestController
@RequestMapping("/closeEvent")
public class CloseEventAPIController {

	private static final Logger logger = Logger.getLogger(CloseEventAPIController.class);

	@Autowired
	ServletContext context;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String get(HttpServletResponse res) {
		return "n/i";
	}

	private String process(Date endDate, Long id) {

		EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
		EntityManager em = emf.createEntityManager();
		try {

			EventOccurrence notification = em.find(EventOccurrence.class, id);
			logger.info("Found object with given id: " + notification);

			long duration = 0;
			if (notification.getDuration() == 0) {
				em.getTransaction().begin();
				long start = notification.getDate().getTime();
				long end = endDate.getTime();
				duration = end - start;
				notification.setDuration(duration);
				em.persist(notification);
				em.getTransaction().commit();
				logger.info("Successfully updated, event duration: " + duration + " ms");
				return "Successfully updated, event duration: " + duration + " ms";
			} else {
				return "Event with this id has been already closed";
			}

		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody String post(@Valid @RequestBody final EventCloseResource eventOccurrenceResource) {

		long expertId = eventOccurrenceResource.getId();
		Date endDate = eventOccurrenceResource.getDate();
		ConcurrentMap<Long, Long> map = TaskManager.get().getExpertIdToNmId();
		int retry = 5;
		while (!map.containsKey(expertId) && retry > 0) {
			logger.warn("Could not find the task, waiting, retries left: " + retry);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry--;
		}

		if (map.containsKey(expertId)) {

			long id = map.get(expertId);
			map.remove(expertId);

			logger.info("Update event with expertId: " + expertId + ", nmId: " + id + ", end date: " + endDate);
			return process(endDate, id);
		} else {
			logger.warn("Cannot find entry with given id within timeout");
			return "Cannot find entry with given id " + expertId;
		}
	}

}