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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.Application;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;

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
		String[] sources = request.getParameterValues("source[]");
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

		List<LogicModuleView> filteredSources = new ArrayList<>();
		if (sources != null) {
			for (String source : sources) {
				LogicModuleView lm = LogicModuleView.valueOf(source);
				if (lm != null) {
					filteredSources.add(lm);
				}
			}
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

			Pair<List<Event>, Long> targetData = Application.get().getPersistenceManager().getEvents(startDate,
					endDate,filteredTypes,filteredSources,page,entries);

			request.setAttribute("events", targetData.getLeft());
			request.setAttribute("count", targetData.getRight());
			request.setAttribute("sources", LogicModuleView.values());
			request.setAttribute("eventTypes", EventType.values());
			request.getRequestDispatcher("/archive.jsp").forward(request, response);

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