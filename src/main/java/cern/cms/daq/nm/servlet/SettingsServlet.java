package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.persistence.DummyUser;

public class SettingsServlet extends UserContextServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(SettingsServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String email = (String) request.getAttribute("ADFS_EMAIL");
		String phone = (String) request.getAttribute("ADFS_PHONENUMBER");

		Entry<DummyUser, Boolean> result = getUserObject(request);
		DummyUser dummyUser = result.getKey();
		boolean newComer = result.getValue();

		/* following variables are volatile - needs to be set up on demand */
		dummyUser.setCernEmail(email);
		dummyUser.setCernPhone(phone);

		logger.info("Logged user [username:" + dummyUser.getUsername() + ",new:" + newComer
				+ "] SAML attributes: [email: " + email + ", phone: " + phone + "]");
		request.setAttribute("user", dummyUser);

		request.getRequestDispatcher("/settings.jsp").forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String customEmail = request.getParameter("inputEmail");
		String customPhone = request.getParameter("inputPhone");

		String phoneRadio = request.getParameter("phoneRadio");
		String emailRadio = request.getParameter("emailRadio");

		Entry<DummyUser, Boolean> result = getUserObject(request);
		DummyUser dummyUser = result.getKey();
		boolean newComer = result.getValue();

		logger.info("User settings post request [username: " + dummyUser.getUsername() + ", customEmail:" + customEmail
				+ ", customPhone:" + customPhone + ", phoneRadio=" + phoneRadio + ", emailRadio=" + emailRadio + "]");

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();
		try {

			em.getTransaction().begin();
			if (!newComer) {
				dummyUser = em.find(DummyUser.class, dummyUser.getId());
			}
			dummyUser.setEmail(customEmail);
			dummyUser.setPhone(customPhone);
			dummyUser.setUseCustomPhone("other".equals(phoneRadio) ? true : false);
			dummyUser.setUseCustomEmail("other".equals(emailRadio) ? true : false);

			if (newComer)
				em.persist(dummyUser);
			logger.info("Persisting user " + dummyUser + "successful");
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		doGet(request, response);
	}

}