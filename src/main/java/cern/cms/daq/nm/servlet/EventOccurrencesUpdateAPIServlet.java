package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import cern.cms.daq.nm.persistence.EventOccurrence;

/**
 * Event occurrences servlet API, used for async requests in autoupdate mode.
 * 
 * This API is used by main servlet for event occurrences view.
 * 
 * @see EventOccurrencesServlet
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class EventOccurrencesUpdateAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(EventOccurrencesUpdateAPIServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();

		String[] ids = request.getParameterValues("ids[]");

		try {
			Map<Long, Long> map = new HashMap<>();

			for (String idString : ids) {
				Long currentId = Long.parseLong(idString);

				EventOccurrence eventOccurrence = em.find(EventOccurrence.class, currentId);
				
				if(eventOccurrence == null){
					logger.warn("Error retrieving event occurrence with id " + currentId);
				}

				Long duration = eventOccurrence.getDuration();
				map.put(currentId, duration);
			}

			String json = new Gson().toJson(map);

			logger.debug("Response JSON: " + json);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);

		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}