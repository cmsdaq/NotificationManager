package cern.cms.daq.nm.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import cern.cms.daq.nm.Condition;
import cern.cms.daq.nm.persistence.Channel;

/**
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 */
public class DispatcherTaskTest extends DispatcherTaskTestBase {

	@Test
	public void userNotInterestedInEventType() {
		eventBuffer.add(event1);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_Always, Condition.Time_Daytime,
				Condition.Time_Nighttime, Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(0, notificationBuffer.size());
	}

	@Test
	public void notifyAlways() {
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_Always, Condition.Time_Daytime,
				Condition.Time_Nighttime, Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(1, notificationBuffer.size());
		assertTrue(bufferAsSet().contains(create(Channel.sms, user1, event2)));
	}

	@Test
	public void conditionTimeDaytimeTest() {
		daytime = true;
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_Always, Condition.Time_Daytime,
				Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(1, notificationBuffer.size());
		assertTrue(bufferAsSet().contains(create(Channel.sms, user1, event2)));
	}

	@Test
	public void conditionTimeDaytimeTest2() {
		daytime = false;
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_Always, Condition.Time_Daytime,
				Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(0, notificationBuffer.size());
	}

	@Test
	public void conditionTimeNighttimeTest() {
		daytime = false;
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_Always, Condition.Time_Nighttime,
				Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(1, notificationBuffer.size());
		assertTrue(bufferAsSet().contains(create(Channel.sms, user1, event2)));
	}

	@Test
	public void conditionTimeNighttimeTest2() {
		daytime = true;
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_Always, Condition.Time_Nighttime,
				Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(0, notificationBuffer.size());
	}

	@Test
	public void conditionShiftMyShiftTest() {
		userShift = true;
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Shift_MyShift, Condition.Time_Nighttime,
				Condition.Time_Daytime, Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(1, notificationBuffer.size());
		assertTrue(bufferAsSet().contains(create(Channel.sms, user1, event2)));
	}

	@Test
	public void conditionShiftMyShiftTest2() {
		userShift = false;
		eventBuffer.add(event2);
		confs = Arrays.asList(create(user1, et2, Channel.sms, Condition.Time_Nighttime, Condition.Time_Daytime,
				Condition.Importance_All));
		users = Arrays.asList(user1);
		dispatcher.run();
		assertEquals(0, notificationBuffer.size());
	}

}
