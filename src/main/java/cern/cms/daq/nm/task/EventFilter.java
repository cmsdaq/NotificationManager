package cern.cms.daq.nm.task;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.Condition;
import cern.cms.daq.nm.SystemStatus;
import cern.cms.daq.nm.persistence.DummyUser;

public class EventFilter {

	private final EntityManagerFactory shiftEMF;

	private static Logger logger = Logger.getLogger(EventFilter.class);

	public EventFilter(EntityManagerFactory shiftEMF) {
		this.shiftEMF = shiftEMF;
	}

	public boolean conditionsSatisfied(DummyUser user, Set<Condition> userConditions) {

		if (!importanceConditionsSatisfied(userConditions)) {
			logger.debug("Importance conditions not satisfied for user " + user.getUsername());
			return false;
		}

		if (!shiftConditionsSatisfied(user, userConditions)) {
			logger.debug("Shift conditions not satisfied for user " + user.getUsername());
			return false;
		}

		if (!timeConditionsSatisfied(user, userConditions)) {
			logger.debug("Time conditions not satisfied for user " + user.getUsername());
			return false;
		}

		return true;
	}

	private boolean importanceConditionsSatisfied(Set<Condition> conditions) {

		if (conditions.contains(Condition.Importance_All))
			return true;

		if (conditions.contains(Condition.Importance_StableBeams) && SystemStatus.get().stableBeams)
			return true;

		if (conditions.contains(Condition.Importance_ShortToStableBeams) && SystemStatus.get().shortToStableBeams)
			return true;

		return false;
	}

	private boolean shiftConditionsSatisfied(DummyUser user, Set<Condition> conditions) {

		if (conditions.contains(Condition.Shift_Always))
			return true;

		if (conditions.contains(Condition.Shift_MyShift) && getUserShift().isShiftOn(user))
			return true;

		return false;
	}

	private boolean timeConditionsSatisfied(DummyUser user, Set<Condition> conditions) {

		boolean daytime = isDaytimeForUser(user);

		if (conditions.contains(Condition.Time_Daytime) && daytime)
			return true;

		if (conditions.contains(Condition.Time_Nighttime) && !daytime)
			return true;
		return false;
	}

	protected boolean isDaytimeForUser(DummyUser user) {

		// TODO make it user specific
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(Calendar.HOUR_OF_DAY) >= 8 && calendar.get(Calendar.HOUR_OF_DAY) <= 22) {
			return true;
		}

		return false;
	}

	protected UserShift getUserShift() {
		return new UserShift(this.shiftEMF);
	}

}
