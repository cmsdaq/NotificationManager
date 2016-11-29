package cern.cms.daq.nm.task;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;

import cern.cms.daq.nm.EventOccurrenceResource;
import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.persistence.EventOccurrence;
import cern.cms.daq.nm.persistence.NotificationOccurrence;
import cern.cms.daq.nm.sound.SoundSystemManager;

public class TaskManager {

	private static TaskManager instance;

	private final ConcurrentLinkedQueue<EventOccurrenceResource> eventResourceBuffer;
	private final ConcurrentLinkedQueue<EventOccurrence> eventBuffer;
	private final ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer;

	private final ConcurrentMap<Long,Long> expertIdToNmId;

	private final TimerTask notificationTask;
	private final TimerTask dispatcherTask;
	private final TimerTask receiverTask;

	@SuppressWarnings("unused")
	private final TimerTask generatorTask;
	@SuppressWarnings("unused")
	private final TimerTask monitoringTask;
	

	private final SoundSystemManager soundSystemManager;

	private TaskManager(EntityManagerFactory notificationEMF, EntityManagerFactory shiftEMF) {
		eventResourceBuffer = new ConcurrentLinkedQueue<EventOccurrenceResource>();
		eventBuffer = new ConcurrentLinkedQueue<EventOccurrence>();
		notificationBuffer = new ConcurrentLinkedQueue<NotificationOccurrence>();
		expertIdToNmId = new ConcurrentHashMap<>();
		this.soundSystemManager = new SoundSystemManager("http://dvbu-pcintelsz", 50505);

		/*
		 * initialize main tasks
		 */
		receiverTask = new ReceiverTask(notificationEMF, eventResourceBuffer, eventBuffer,soundSystemManager);
		dispatcherTask = new DispatcherTask(notificationEMF, shiftEMF, eventBuffer, notificationBuffer);
		notificationTask = new NotificationTask(notificationEMF, notificationBuffer);

		generatorTask = new GeneratorTask(notificationEMF, eventBuffer);
		monitoringTask = new MonitoringTask();

	}

	public void schedule() {
		Timer t = new Timer();

		/*
		 * schedule main tasks
		 */
		t.scheduleAtFixedRate(receiverTask, 1000, 1000);
		t.scheduleAtFixedRate(dispatcherTask, 1000 * 10, 1000 * 10);
		t.scheduleAtFixedRate(notificationTask, 1000 * 20, 1000 * 30);

		/*
		 * other tasks
		 */
		// t.scheduleAtFixedRate(monitoringTask, 1000 * 5, 1000 * 5);
		// t.scheduleAtFixedRate(generatorTask, 0, 1000);
	}

	public static TaskManager get() {
		if (instance == null) {
			throw new NotificationException("Task manager needs to be first initialized");
		}
		return instance;
	}

	public static void initialize(EntityManagerFactory notificationEMF, EntityManagerFactory shiftEMF) {
		if (instance != null) {
			throw new NotificationException("Task manager has been already initialized");
		}
		instance = new TaskManager(notificationEMF, shiftEMF);
	}

	public ConcurrentLinkedQueue<NotificationOccurrence> getNotificationBuffer() {
		return notificationBuffer;
	}

	public ConcurrentLinkedQueue<EventOccurrence> getEventBuffer() {
		return eventBuffer;
	}

	public ConcurrentLinkedQueue<EventOccurrenceResource> getEventResourceBuffer() {
		return eventResourceBuffer;
	}

	public ConcurrentMap<Long,Long> getExpertIdToNmId() {
		return expertIdToNmId;
	}

	public SoundSystemManager getSoundSystemManager() {
		return soundSystemManager;
	}

}
