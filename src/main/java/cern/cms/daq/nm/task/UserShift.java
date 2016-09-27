package cern.cms.daq.nm.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cern.cms.daq.nm.persistence.DummyUser;
import cern.cms.daq.nm.persistence.ShiftOccurrence;

public class UserShift {

	private static Logger logger = Logger.getLogger(UserShift.class);

	private final EntityManagerFactory shiftEMF;

	public UserShift(EntityManagerFactory shiftEMF) {
		this.shiftEMF = shiftEMF;
	}

	/**
	 * Temporary function to go back in time by 1 year and get use of test shift
	 * data
	 * 
	 * @return
	 */
	protected Date getNow() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		Date d = cal.getTime();
		logger.debug("Pretending now is one year before" + d);
		return d;
	}

	/**
	 * Temporary function to use existing shifters data
	 * TODO: remove this
	 * @return
	 */
	private Long getFakeUserId(DummyUser user) {
		Long userId = user.getId();

		if (userId == 1) {
			logger.debug("User " + user.getUsername() + " will be hsakulin for shift test");
			userId = 448336L;
		}
		return userId;
	}

	public boolean isShiftOn(DummyUser user) {

		EntityManager em = shiftEMF.createEntityManager();
		Session session = em.unwrap(Session.class);

		Date d = getNow();
		int timeAfter = getCorrectionAfter();
		int timeBefore = getCorrectionBefore();

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.HOUR, -timeAfter);
		Date customHourBack = cal.getTime();
		cal.setTime(d);
		cal.add(Calendar.HOUR, timeBefore);
		Date customHourForward = cal.getTime();

		Criteria cr = session.createCriteria(ShiftOccurrence.class);
		cr.add(Restrictions.eq("userId", getFakeUserId(user)));
		cr.add(Restrictions.le("start", customHourForward));
		cr.add(Restrictions.ge("end", customHourBack));
		@SuppressWarnings("unchecked")
		List<ShiftOccurrence> a = cr.list();
		em.close();
		return getResult(a, user);

	}

	/**
	 * Correction of shift end time. Example <b>+2</b> will still notify for 2h
	 * after shift ends, <b>-2</b> will stop notify 2h before shift ends
	 * 
	 * @return
	 */
	protected int getCorrectionAfter() {
		// TODO: get correction from user db
		return 0;
	}

	/**
	 * Correction of shift start time. Example: <b>+2</b> will start notify 2h
	 * before shift starts, <b>-2</b> will start notify 2h after shift starts
	 * 
	 * @return
	 */
	protected int getCorrectionBefore() {
		// TODO: get correction from user db
		return 0;
	}

	private boolean getResult(List<ShiftOccurrence> a, DummyUser user) {
		if (a.size() == 0) {
			logger.warn("No shift information found for user " + user.getUsername());
			return false;
		} else {
			logger.debug("Found shift entry for user " + user.getUsername());
			for (ShiftOccurrence so : a) {
				logger.debug(so);
			}
			return true;
		}
	}

}
