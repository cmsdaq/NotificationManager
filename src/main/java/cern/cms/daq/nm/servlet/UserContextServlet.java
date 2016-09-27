package cern.cms.daq.nm.servlet;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.persistence.DummyUser;

public abstract class UserContextServlet extends HttpServlet {

	private Logger logger = Logger.getLogger(UserContextServlet.class.getName());

	protected DummyUser getLoggedUser(HttpServletRequest request) {

		return getUserObject(request).getKey();
	}

	protected Entry<DummyUser, Boolean> getUserObject(HttpServletRequest request) {

		String username = request.getRemoteUser();
		
		if(username == null || username.equals("")){
			username = "guest";
		}

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();
		try {
			Session session = em.unwrap(Session.class);

			Criteria userCriteria = session.createCriteria(DummyUser.class);
			userCriteria.add(Restrictions.eq("username", username));
			List<DummyUser> userList = userCriteria.list();
			DummyUser dummyUser = null;
			boolean newComer = false;

			/*
			 * If logged user already has some configuration then load it from Notification Manager database
			 */
			if (userList.size() > 0) {
				dummyUser = userList.get(0);
			}

			/*
			 * If logged user never did any configuration then create and persist object
			 */
			else {
				em.getTransaction().begin();
				newComer = true;
				dummyUser = new DummyUser();
				dummyUser.setUsername(username);
				em.persist(dummyUser);
				em.getTransaction().commit();
			}
			Entry<DummyUser, Boolean> result = new AbstractMap.SimpleEntry<DummyUser, Boolean>(dummyUser, newComer);

			return result;
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}
}
