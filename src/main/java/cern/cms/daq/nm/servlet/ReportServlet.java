package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;

import cern.cms.daq.nm.persistence.NotificationOccurrence;

public class ReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();

		try {

			Session session = em.unwrap(Session.class);

			Criteria notificationOccurrenceCriteria = session.createCriteria(NotificationOccurrence.class);
			@SuppressWarnings("unchecked")
			List<NotificationOccurrence> notificationList = notificationOccurrenceCriteria.list();

			request.setAttribute("reports", notificationList);
			request.getRequestDispatcher("/reports.jsp").forward(request, response);

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