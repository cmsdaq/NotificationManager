package cern.cms.daq.nm.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.BeforeClass;

import cern.cms.daq.nm.Condition;
import cern.cms.daq.nm.persistence.Channel;
import cern.cms.daq.nm.persistence.Configuration;
import cern.cms.daq.nm.persistence.DummyUser;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventStatus;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.NotificationOccurrence;
import cern.cms.daq.nm.persistence.NotificationStatus;

/**
 * odpalam dispatcher z: - buforem zdarzen (testowe dane) - buforem notyfikaji
 * (pusty - wypelnione do testow) - konfiguracja uzytkownikow - bez bazy danych
 * 
 * @author Maciej Gladki (maciej.szymon.gladki@cern.ch)
 *
 */
public class DispatcherTaskTestBase {

	protected static DispatcherTaskStub dispatcher;
	protected static ConcurrentLinkedQueue<Event> eventBuffer;
	protected static ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer;

	static List<Configuration> confs;
	public static List<DummyUser> users;

	static Event event1;
	static Event event2;
	static Event event3;

	static Configuration conf1;
	static Configuration conf2;
	static Configuration conf3;

	static DummyUser user1;

	static DummyUser user2;
	static DummyUser user3;
	protected static EventType et1;
	protected static EventType et2;
	public static boolean userShift = true;
	public static boolean daytime = true;

	@BeforeClass
	public static void init() {

		eventBuffer = new ConcurrentLinkedQueue<Event>();
		notificationBuffer = new ConcurrentLinkedQueue<NotificationOccurrence>();
		dispatcher = new DispatcherTaskStub(eventBuffer, notificationBuffer);

		set();
	}

	public static boolean beforeShift = true;
	public static boolean afterShift = true;

	public static void setAfterShift(boolean afterShift) {
		DispatcherTaskTestBase.afterShift = afterShift;
	}

	@After
	public void clearBuffer() {
		while (!notificationBuffer.isEmpty())
			notificationBuffer.remove();
	}

	private static void set() {
		user1 = new DummyUser();
		user1.setUsername("mg");
		user2 = new DummyUser();
		user2.setUsername("hs");
		user3 = new DummyUser();
		user3.setUsername("rm");

		/*
		 * event types
		 */
		et1 = EventType.Single;
		et2 = EventType.ConditionStart;

		/*
		 * event occurrences
		 */
		String event1msg = "ev1";
		String event2msg = "ev2";
		String event3msg = "ev3";
		event1 = new Event();
		event1.setMessage(event1msg);
		event1.setEventType(et1);
		event2 = new Event();
		event2.setMessage(event2msg);
		event2.setEventType(et2);
		event3 = new Event();
		event3.setMessage(event3msg);
		event3.setEventType(et1);

		/*
		 * configurations
		 */
		conf1 = new Configuration();
		conf1.setUser(user1);
		conf1.setEventTypes(new HashSet<EventType>(Arrays.asList(et1)));
		conf1.setChannels(new HashSet<Channel>(Arrays.asList(Channel.email, Channel.sms)));
		conf1.setConditions(new HashSet<Condition>(Arrays.asList(Condition.Importance_All, Condition.Shift_Always,
				Condition.Time_Daytime, Condition.Time_Nighttime)));
	}

	protected Configuration create(DummyUser user, EventType eventType, Channel channel, Condition... conditions) {
		Configuration conf = new Configuration();
		conf.setUser(user1);
		conf.setEventTypes(new HashSet<EventType>(Arrays.asList(eventType)));
		conf.setChannels(new HashSet<Channel>(Arrays.asList(channel)));
		conf.setConditions(new HashSet<Condition>(Arrays.asList(conditions)));
		return conf;
	}

	protected NotificationOccurrence create(Channel channel, DummyUser user, Event eventOccurrence) {
		NotificationOccurrence a = new NotificationOccurrence();
		a.setChannel(channel);
		a.setStatus(NotificationStatus.Pending);
		a.setUser(user);
		a.setEventOccurrence(eventOccurrence);
		return a;
	}

	/**
	 * Get notification buffer as set.
	 * 
	 * @return notification buffer as set
	 */
	protected Set<NotificationOccurrence> bufferAsSet() {
		Set<NotificationOccurrence> result = new HashSet<NotificationOccurrence>();
		ConcurrentLinkedQueue<NotificationOccurrence> tmp = new ConcurrentLinkedQueue<NotificationOccurrence>(
				notificationBuffer);
		while (!tmp.isEmpty()) {
			NotificationOccurrence curr = tmp.poll();
			result.add(curr);
		}
		return result;
	}

}

class DispatcherTaskStub extends DispatcherTask {

	public DispatcherTaskStub(ConcurrentLinkedQueue<Event> eventBuffer,
			ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer) {
		super(eventBuffer, notificationBuffer);

	}

	@Override
	protected void start() {
		System.out.println("start stub");
	}

	@Override
	protected void stop() {

		System.out.println("stop stub");
	}

	@Override
	protected void updateEventStatus(Event event, EventStatus status) {

		System.out.println("update stub");
	}

	@Override
	protected void persistNotificationOccurrence(NotificationOccurrence notificationOccurrence) {

		System.out.println("persist stub");
	}

	@Override
	protected List<DummyUser> getDummyUsers() {

		System.out.println("get user list stub");

		return DispatcherTaskTestBase.users;
	}

	/**
	 * Simulate database access
	 */
	@Override
	protected List<Configuration> getConfiguration(DummyUser user, Event eventOccurrence) {

		System.out.println("get conf stub");

		List<Configuration> result = new ArrayList<Configuration>();

		for (Configuration conf : DispatcherTaskTestBase.confs) {
			if (conf.getUser().getUsername() == user.getUsername()
					&& conf.getEventTypes().contains(eventOccurrence.getEventType())) {
				result.add(conf);
			}
		}

		return result;
	}

	@Override
	protected EventFilter getEventFilter() {
		return new EventFilterStub(null);

	}
}

class UserShiftStub extends UserShift {

	public UserShiftStub(EntityManagerFactory shiftEMF) {
		super(shiftEMF);
	}

	@Override
	public boolean isShiftOn(DummyUser user) {
		return DispatcherTaskTestBase.userShift;
	}

}

class EventFilterStub extends EventFilter {
	public EventFilterStub(EntityManagerFactory shiftEMF) {
		super(shiftEMF);
	}

	@Override
	protected boolean isDaytimeForUser(DummyUser user) {
		return DispatcherTaskTestBase.daytime;
	}

	@Override
	protected UserShift getUserShift() {
		return new UserShiftStub(null);
	}
}
