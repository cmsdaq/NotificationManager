package cern.cms.daq.nm.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.Condition;
import cern.cms.daq.nm.Constants;
import cern.cms.daq.nm.persistence.Channel;
import cern.cms.daq.nm.persistence.Configuration;
import cern.cms.daq.nm.persistence.DummyUser;
import cern.cms.daq.nm.persistence.EventType;

public class ConfigurationServlet extends UserContextServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ConfigurationServlet.class);

	List<Configuration> configurationList;
	List<Channel> channelList;
	List<Condition> conditionList;
	List<EventType> eventTypeList;

	@SuppressWarnings("unchecked")
	private void update(EntityManager em, HttpServletRequest request, DummyUser user) {
		/* All configuration entries */

		Session session = em.unwrap(Session.class);
		Criteria configurationCriteria = session.createCriteria(Configuration.class);
		configurationCriteria.add(Restrictions.eq("user", user));
		configurationCriteria.addOrder(Order.asc("id"));
		configurationList = configurationCriteria.list();

		Criteria eventCriteria = session.createCriteria(EventType.class);
		eventTypeList = eventCriteria.list();

		request.setAttribute("configurations", configurationList);

		/* All channels */
		channelList = Arrays.asList(Channel.values());
		request.setAttribute("channelList", channelList);

		/* All conditions */
		conditionList = Arrays.asList(Condition.values());

		/* importance conditions */
		request.setAttribute("conditionList", Constants.importanceConditionsList);

		/* shift conditions */
		request.setAttribute("shiftConditionList", Constants.shiftConditionsList);

		/* time conditions */
		request.setAttribute("timeConditionList", Constants.timeConditionsList);

		request.setAttribute("eventTypeList", eventTypeList);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		DummyUser user = getLoggedUser(request);

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();

		try {

			update(em, request, user);

			/* for each configuration fill settings from database */
			HashMap<Long, HashMap<EventType, Boolean>> eventMode = new HashMap<>();
			HashMap<Long, HashMap<Integer, Boolean>> channelMode = new HashMap<>();
			HashMap<Long, HashMap<Integer, Boolean>> conditionMode = new HashMap<>();
			for (Configuration configuration : configurationList) {
				Set<EventType> currentEventList = configuration.getEventTypes();
				Set<Channel> currentChannelList = configuration.getChannels();
				Set<Condition> currentConditionList = configuration.getConditions();

				/* fill settings for events */
				HashMap<EventType, Boolean> eventSetting = new HashMap<>();
				for (EventType eventType : eventTypeList) {

					if (currentEventList.contains(eventType)) {
						eventSetting.put(eventType, true);
					} else {
						eventSetting.put(eventType, false);
					}

					eventMode.put(configuration.getId(), eventSetting);
				}

				/* fill settings for channels */
				HashMap<Integer, Boolean> channelSetting = new HashMap<Integer, Boolean>();
				for (Channel channel : channelList) {

					if (currentChannelList.contains(channel)) {
						channelSetting.put(channel.ordinal(), true);
					} else {
						channelSetting.put(channel.ordinal(), false);
					}

					channelMode.put(configuration.getId(), channelSetting);
				}

				/* fill settings for conditions */
				HashMap<Integer, Boolean> conditionSetting = new HashMap<Integer, Boolean>();
				for (Condition condition : conditionList) {

					if (currentConditionList.contains(condition)) {
						conditionSetting.put(condition.ordinal(), true);
					} else {
						conditionSetting.put(condition.ordinal(), false);
					}

					conditionMode.put(configuration.getId(), conditionSetting);
				}
			}
			/* built configuration pass to page context */
			request.setAttribute("eventMode", eventMode);
			request.setAttribute("channelMode", channelMode);
			request.setAttribute("conditionMode", conditionMode);

			request.getRequestDispatcher("/configuration.jsp").forward(request, response);

		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		DummyUser dummyUser = getLoggedUser(request);

		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
		EntityManager em = emf.createEntityManager();

		try {

			Session session = em.unwrap(Session.class);

			Criteria configurationCriteria = session.createCriteria(Configuration.class);
			@SuppressWarnings("unchecked")
			List<Configuration> configurationList = configurationCriteria.list();

			em.getTransaction().begin();

			String[] records = request.getParameterValues("entry[]");
			List<Long> existingConfsFromForm = new ArrayList<>();
			List<String> confsToAddOrEdit = new ArrayList<>();

			if (records != null)
				for (String record : records) {
					try {
						existingConfsFromForm.add(Long.parseLong(record));
						confsToAddOrEdit.add(record);
					} catch (NumberFormatException e) {
						confsToAddOrEdit.add(record);
					}
				}

			/*
			 * remove block
			 */
			List<Long> confIdsToRemove = new ArrayList<>();
			for (Configuration conf : configurationList) {
				if (!existingConfsFromForm.contains(conf.getId())) {
					confIdsToRemove.add(conf.getId());

					Configuration confToRemove = em.find(Configuration.class, conf.getId());
					em.remove(confToRemove);
				}
			}
			/* end of remove block */

			/*
			 * new/edit block
			 */
			for (String confId : confsToAddOrEdit) {
				Set<Condition> conditionSet = new HashSet<>();
				Set<Channel> channelSet = new HashSet<>();
				Set<EventType> eventTypeSet = new HashSet<>();

				String[] events = request.getParameterValues("event_conf_" + confId);

				if (events == null) {
					doGet(request, response);
					return;
				}
				for (String event : events) {
					// TODO set event
					Long id = Long.parseLong(event);
					EventType eventType = em.find(EventType.class, id);
					eventTypeSet.add(eventType);
				}

				String[] importanceConfs = request.getParameterValues("importance_conf_" + confId);
				if (importanceConfs != null)
					for (String importanceConf : importanceConfs) {
						try {
							int id = Integer.valueOf(importanceConf);
							conditionSet.add(Condition.values()[id]);
						} catch (NumberFormatException e) {
							logger.warn("Problem processing post request " + e.getMessage());
						}
					}

				String[] shiftConfs = request.getParameterValues("shift_conf_" + confId);
				if (shiftConfs != null)
					for (String shiftConf : shiftConfs) {
						try {
							int id = Integer.valueOf(shiftConf);
							conditionSet.add(Condition.values()[id]);
						} catch (NumberFormatException e) {
							logger.warn("Problem processing post request " + e.getMessage());
						}
					}

				String[] timeConfs = request.getParameterValues("time_conf_" + confId);

				if (timeConfs != null)
					for (String timeConf : timeConfs) {
						try {
							int id = Integer.valueOf(timeConf);
							conditionSet.add(Condition.values()[id]);
						} catch (NumberFormatException e) {
							logger.warn("Problem processing post request " + e.getMessage());
						}
					}

				String[] channelConfs = request.getParameterValues("channel_conf_" + confId);

				if (channelConfs != null)
					for (String channelConf : channelConfs) {
						try {
							int id = Integer.valueOf(channelConf);
							channelSet.add(Channel.values()[id]);
						} catch (NumberFormatException e) {
							logger.warn("Problem processing post request " + e.getMessage());
						}
					}

				Configuration conf1;

				try {
					Long id = Long.valueOf(confId);
					conf1 = em.find(Configuration.class, id);
					logger.debug("Configuration will be updated");
				} catch (NumberFormatException e) {
					logger.debug("New configuration will be added ");
					conf1 = new Configuration();
				}

				conf1.setChannels(channelSet);
				conf1.setConditions(conditionSet);
				conf1.setEventTypes(eventTypeSet);

				if (conf1.getUser() == null)
					conf1.setUser(dummyUser);
				em.persist(conf1);

			}

			em.getTransaction().commit();

		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		doGet(request, response);
	}

}