package cern.cms.daq.nm.task;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.Channel;
import cern.cms.daq.nm.persistence.DummyUser;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventStatus;
import cern.cms.daq.nm.persistence.NotificationOccurrence;
import cern.cms.daq.nm.persistence.NotificationStatus;

/*
 * pobiera z bufora chciane notyfikacje i wysyla
 * dodatkowa funkcja jest unikanie podwojnych notyfikacji
 */
public class NotificationTask extends TimerTask {

	private final ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer;

	private final EntityManagerFactory emf;
	private EntityManager em;
	private NotificationService notificationService;

	private static final Logger logger = Logger.getLogger(NotificationTask.class);

	public NotificationTask(EntityManagerFactory emf,
			ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer) {
		this.notificationBuffer = notificationBuffer;
		this.emf = emf;
		this.notificationService = new NotificationService();
	}

	@Override
	public void run() {

		if (!notificationBuffer.isEmpty()) {
			int i = 0;
			final int bufferSize = notificationBuffer.size();
			final Long startTime = System.currentTimeMillis();
			logger.info(
					new StringBuilder("Run notification task, ").append(bufferSize).append(" notifications to send"));

			Set<Long> eventOccurrencesIds = new HashSet<>();

			em = emf.createEntityManager();
			while (!notificationBuffer.isEmpty() && i < bufferSize) {
				i++;
				NotificationOccurrence current = notificationBuffer.poll();
				eventOccurrencesIds.add(current.getEventOccurrence().getId());

				Channel channel = current.getChannel();
				if (channel == Channel.email)
					sendEmail(current);
				if (channel == Channel.sms)
					sendSMS(current);
				if (channel == Channel.push)
					sendPush(current);

			}

			/*
			 * update related event statuses
			 */
			em.getTransaction().begin();
			for (Long eventOccurrenceId : eventOccurrencesIds) {
				Event eventOccurrence = em.find(Event.class, eventOccurrenceId);
				eventOccurrence.setStatus(EventStatus.Notified);
			}
			em.getTransaction().commit();

			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();

			final Long endTime = System.currentTimeMillis();
			logger.info(new StringBuilder("Notification processed ").append(bufferSize).append(" events in ")
					.append(endTime - startTime).append(" ms."));
		}
	}

	private void updateStatus(NotificationOccurrence no, NotificationStatus status, String message) {
		NotificationOccurrence notificationOccurrence = em.find(NotificationOccurrence.class, no.getId());

		em.getTransaction().begin();
		notificationOccurrence.setStatus(status);
		notificationOccurrence.setMessage(message);
		notificationOccurrence.setDate(new Date());
		em.getTransaction().commit();
	}

	private void sendSMS(NotificationOccurrence notificationOccurrence) {

		updateStatus(notificationOccurrence, NotificationStatus.Failure, "SMS service unavailable.");

		// TODO create sms service
		logger.debug("SMS will not be sent to user " + notificationOccurrence.getUser().getUsername()
				+ ", SMS service unavailable.");

		// notificationService.send(notificationOccurrence.getUser().getPhone(),
		// notificationOccurrence.getEventOccurrence().getMessage());
	}

	private void sendPush(NotificationOccurrence notificationOccurrence) {

		updateStatus(notificationOccurrence, NotificationStatus.Failure, "Push service unavailable.");

		// TODO create sms service
		logger.debug("Push will not be sent to user " + notificationOccurrence.getUser().getUsername()
				+ ", Push service unavailable.");
	}

	private void sendEmail(NotificationOccurrence notificationOccurrence) {

		DummyUser user = notificationOccurrence.getUser();
		try {
			notificationService.send(user.getEmail(), notificationOccurrence.getEventOccurrence().getMessage());
			updateStatus(notificationOccurrence, NotificationStatus.Success, null);
			logger.trace("Email successfully sent to user " + user.getUsername() + ", " + user.getEmail());
		} catch (NotificationException e) {

			updateStatus(notificationOccurrence, NotificationStatus.Failure, e.getMessage());
			logger.warn("Email could not be sent to user " + user.getUsername() + ", " + user.getEmail());
		}
	}

}
