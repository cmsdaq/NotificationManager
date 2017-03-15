package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventType;

/**
 * Event archive servlet
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class EventArchiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(EventArchiveServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String paginationEntriesPerPage = request.getParameter("entries");
		String paginationCurrentPage = request.getParameter("page");
		String[] types = request.getParameterValues("type[]");
		String startRange = request.getParameter("start");
		String endRange = request.getParameter("end");

		List<EventType> filteredTypes = new ArrayList<>();
		if (types != null) {
			for (String type : types) {
				EventType e = EventType.getByName(type);
				logger.info("Types: " + types);
				logger.info("parsed: " + e);
				filteredTypes.add(e);
			}
		}
		if (filteredTypes.size() == 0) {
			filteredTypes = Arrays.asList(EventType.values());
		}
		try {
			int entries = 20;
			if (paginationEntriesPerPage != null) {
				entries = Integer.parseInt(paginationEntriesPerPage);
			}
			int page = 1;
			if (paginationCurrentPage != null) {
				page = Integer.parseInt(paginationCurrentPage);
			}
			Date startDate = null, endDate = null;
			if (startRange != null)
				startDate = DatatypeConverter.parseDateTime(startRange).getTime();

			if (endRange != null)
				endDate = DatatypeConverter.parseDateTime(endRange).getTime();
			logger.info("Range parsed : " + startDate + " - " + endDate);

			logger.info("Requested events: entriesPerPage: " + paginationEntriesPerPage + ", currentPage: "
					+ paginationCurrentPage);

			EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
			EntityManager em = emf.createEntityManager();

			Session session = em.unwrap(Session.class);

			try {
				Criteria eventCriteria = session.createCriteria(Event.class);
				eventCriteria.addOrder(Order.desc("date"));
				eventCriteria.add(Restrictions.in("eventType", filteredTypes));
				if (startDate != null && endDate != null)
					eventCriteria.add(Restrictions.between("date", startDate, endDate));

				/* process pagination */
				eventCriteria.setFirstResult((page - 1) * entries);
				eventCriteria.setMaxResults(entries);

				@SuppressWarnings("unchecked")
				List<Event> eventTypeList = eventCriteria.list();

				eventCriteria.setProjection(Projections.rowCount());
				eventCriteria.setFirstResult(0);
				Long count = (Long) eventCriteria.uniqueResult();

				request.setAttribute("events", eventTypeList);
				request.setAttribute("count", count);
				request.setAttribute("eventTypes", EventType.values());
				request.getRequestDispatcher("/archive.jsp").forward(request, response);

			} finally {
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
				em.close();
			}
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"One or more parameters missing or could not be parsed: " + e);
		} catch (NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "One or more parameters missing: " + e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}