package cern.cms.daq.nm.task;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.persistence.Channel;
import cern.cms.daq.nm.persistence.Configuration;
import cern.cms.daq.nm.persistence.DummyUser;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventStatus;
import cern.cms.daq.nm.persistence.NotificationOccurrence;
import cern.cms.daq.nm.persistence.NotificationStatus;

/**
 * 
 * This task is responsible for taking the event occurrences from buffer,
 * dispatching notifications to users based on their preferences. When all
 * user's conditions are met the notification occurrence is generated and put to
 * buffer for sender task
 * 
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class DispatcherTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(DispatcherTask.class);

	private final ConcurrentLinkedQueue<Event> eventBuffer;
	private final ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer;
	private final EntityManagerFactory notificationEMF;
	private final EntityManagerFactory shiftEMF;
	private EntityManager em;
	private Session session;

	private final EventFilter eventFilter;

	public DispatcherTask(
			ConcurrentLinkedQueue<Event> eventBuffer,
			ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer) {

		this.notificationEMF = null;
		this.shiftEMF = null;
		this.eventBuffer = eventBuffer;
		this.notificationBuffer = notificationBuffer;
		this.eventFilter = new EventFilter(this.shiftEMF);
	}

	@Override
	public void run() {

		if (!eventBuffer.isEmpty()) {
			final int bufferSize = eventBuffer.size();
			final Long startTime = System.currentTimeMillis();
			logger.debug(new StringBuilder("Run dispatcher task, ").append(bufferSize).append(" events on queue"));

			int i = 0;

			start();

			while (!eventBuffer.isEmpty() && i < bufferSize) {
				i++;
				Event eventOccurrence = eventBuffer.poll();

				logger.debug("Dispatching event " + eventOccurrence.getEventType().getDescription() + " ("
						+ eventOccurrence.getMessage() + ")");

				List<DummyUser> userList = getDummyUsers();

				for (DummyUser user : userList) {

					List<Configuration> configurations = getConfiguration(user, eventOccurrence);

					/*
					 * Sprawdz wszystkie konfiguracje tylko dla tego typu
					 * eventu. TODO: wyslij tylko raz nawet jak sie powtarzaja
					 */
					for (Configuration conf : configurations) {

						EventFilter ef = getEventFilter();
						boolean send = ef.conditionsSatisfied(user, conf.getConditions());

						if (send) {

							StringBuilder sb = new StringBuilder("All conditions satisfied for user ")
									.append(user.getUsername()).append(", for channel(s) ");
							for (Channel channel : conf.getChannels()) {
								NotificationOccurrence notificationOccurrence = new NotificationOccurrence();
								notificationOccurrence.setChannel(channel);
								notificationOccurrence.setEventOccurrence(eventOccurrence);
								notificationOccurrence.setUser(user);
								notificationOccurrence.setStatus(NotificationStatus.Pending);
								notificationBuffer.add(notificationOccurrence);
								persistNotificationOccurrence(notificationOccurrence);

							}
							logger.debug(sb.append(conf.getChannels()).toString());
						}
					}
				}
				updateEventStatus(eventOccurrence, EventStatus.Dispatched);
			}
			stop();

			final Long endTime = System.currentTimeMillis();
			final Long endMemo = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
			logger.info(new StringBuilder("Dispatcher processed ").append(bufferSize).append(" events in ")
					.append(endTime - startTime).append(" ms."));
		}
	}

	protected void start() {
		em = notificationEMF.createEntityManager();
		em.getTransaction().begin();
		session = em.unwrap(Session.class);
	}

	protected void stop() {
		em.getTransaction().commit();

		if (em.getTransaction().isActive())
			em.getTransaction().rollback();
		em.close();

	}

	protected void updateEventStatus(Event event, EventStatus status) {
		Event eventOccurrence = em.find(Event.class, event.getId());
		eventOccurrence.setStatus(status);
	}

	protected void persistNotificationOccurrence(NotificationOccurrence notificationOccurrence) {
		em.persist(notificationOccurrence);
	}

	@SuppressWarnings("unchecked")
	protected List<DummyUser> getDummyUsers() {
		Criteria userCriteria = session.createCriteria(DummyUser.class);
		return userCriteria.list();
	}

	@SuppressWarnings("unchecked")
	protected List<Configuration> getConfiguration(DummyUser user, Event eventOccurrence) {
		Criteria cr = session.createCriteria(Configuration.class);
		cr.add(Restrictions.eq("user", user));
		// cr.add(Restrictions.in("eventTypes",
		// eventOccurrence.getEventType()));

		cr.createAlias("eventTypes", "eventType");
		cr.add(Restrictions.eq("eventType", eventOccurrence.getEventType()));

		return cr.list();
	}

	protected EventFilter getEventFilter() {
		return eventFilter;
	}

}