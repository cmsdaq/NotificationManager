package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;

import cern.cms.daq.nm.persistence.Event;

/**
 * Event occurrences servlet API, used for async requests in autoupdate mode.
 * 
 * This API is used by main servlet for event occurrences view.
 * 
 * @see EventArchiveServlet
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class EventOccurrencesAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(EventOccurrencesAPIServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.debug("get JSON occurrences " + request.getQueryString());

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();

		Session session = em.unwrap(Session.class);

		String startRange = request.getParameter("start");
		String endRange = request.getParameter("end");
		String entriesParameter = request.getParameter("entries");
		String pageParameter = request.getParameter("page");
		String lastEntryParameter = request.getParameter("last");
		String dashboardParameter = request.getParameter("dashboard");

		List<Long> eventTypeIds = new ArrayList<>();
		if (request.getParameter("eventTypes[]") != null) {
			String[] eventTypes = request.getParameterValues("eventTypes[]");

			logger.info("params : " + eventTypes.length);
			if (eventTypes != null)
				for (String eventType : eventTypes)
					try {
						eventTypeIds.add(Long.parseLong(eventType));
					} catch (NumberFormatException e) {
						logger.warn("Wrong event type id '" + eventTypes + "'");
					}
		} else {
			logger.debug("No event type parameters");
		}

		try {

			Criteria elementsCriteria = session.createCriteria(Event.class);
			Criteria elementsCountCriteria = session.createCriteria(Event.class);

			elementsCriteria.addOrder(Order.desc("date"));
			elementsCriteria.addOrder(Order.desc("id"));

			/*
			 * process pagination
			 */
			if (entriesParameter != null && pageParameter != null) {
				int entries = Integer.parseInt(entriesParameter);
				int page = Integer.parseInt(pageParameter);
				elementsCriteria.setFirstResult((page - 1) * entries);
				elementsCriteria.setMaxResults(entries);
			}

			/* Filter event type */
			Disjunction disjunction = Restrictions.disjunction();
			for (Long id : eventTypeIds) {
				logger.info("restric to type id : " + id);
				Criterion cur = Restrictions.eq("eventType.id", id);
				disjunction.add(cur);
			}
			
			if(dashboardParameter != null && Boolean.parseBoolean(dashboardParameter)){
				disjunction.add(Restrictions.eq("display", true));
			}
			
			elementsCriteria.add(disjunction);
			elementsCountCriteria.add(disjunction);

			/*
			 * process date
			 */
			if (startRange != null && endRange != null) {
				try {
					Long startTime = Long.parseLong(startRange);
					Long endTime = Long.parseLong(endRange);
					if (startTime != -1L) {
						Date startDate = new Date(startTime);
						Date endDate = new Date(endTime);

						logger.debug("dates: " + startDate + " - " + endDate);

						elementsCriteria.add(Restrictions.between("date", startDate, endDate));
						elementsCountCriteria.add(Restrictions.between("date", startDate, endDate));
					}
				} catch (NumberFormatException e) {
					logger.warn("problem parsing dates");
				}
			}

			/*
			 * process automated condition
			 */
			if (lastEntryParameter != null && lastEntryParameter != "") {
				try {
					Long newestId = Long.parseLong(lastEntryParameter);
					elementsCriteria.add(Restrictions.gt("id", newestId));
					elementsCountCriteria.add(Restrictions.gt("id", newestId));
				} catch (NumberFormatException e) {
					logger.warn("problem parsing newest entry id: " + lastEntryParameter);
				}
			}

			@SuppressWarnings("unchecked")
			List<Event> eventList = elementsCriteria.list();

			elementsCountCriteria.setProjection(Projections.rowCount());
			elementsCountCriteria.setFirstResult(0);
			Long count = (Long) elementsCountCriteria.uniqueResult();

			logger.debug("Total number of new event occurrences: " + count);

			Map<String, Object> options = new LinkedHashMap<String, Object>();
			options.put("results", eventList);
			options.put("total", count);

			String json = new Gson().toJson(options);

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