package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import cern.cms.daq.nm.persistence.EventOccurrence;

public class DashboardServlet extends UserContextServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(DashboardServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

			request.getRequestDispatcher("/dashboard.jsp").forward(request, response);


	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}