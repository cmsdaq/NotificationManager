package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;

import cern.cms.daq.nm.persistence.EventType;

/**
 * Event occurrences servlet, used for presenting view with event occurrences
 * 
 * This servlet uses also API servlet for async requests (autoupdate mode)
 * 
 * @see EventOccurrencesAPIServlet
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class EventOccurrencesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(EventOccurrencesServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.debug("get occurrences layout");

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();

		Session session = em.unwrap(Session.class);
		Criteria eventCriteria = session.createCriteria(EventType.class);
		@SuppressWarnings("unchecked")
		List<EventType> eventTypeList = eventCriteria.list();


		try {

			request.setAttribute("eventTypes", eventTypeList);
			request.getRequestDispatcher("/event_occurrences.jsp").forward(request, response);

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