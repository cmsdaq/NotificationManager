package cern.cms.daq.nm.task;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.Application;
import cern.cms.daq.nm.EventResource;
import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.Setting;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.NotificationOccurrence;
import cern.cms.daq.nm.sound.SoundSystemManager;

public class TaskManager {

	private static TaskManager instance;

	private final ConcurrentLinkedQueue<EventResource> eventResourceBuffer;
	private final ConcurrentLinkedQueue<Event> eventBuffer;
	private final ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer;

	private final TimerTask notificationTask;
	private final TimerTask dispatcherTask;
	private final TimerTask receiverTask;

	@SuppressWarnings("unused")
	private final TimerTask generatorTask;
	@SuppressWarnings("unused")
	private final TimerTask monitoringTask;

	private final SoundSystemManager soundSystemManager;

	private final Logger logger = Logger.getLogger(TaskManager.class);

	private final Timer timer;

	private TaskManager(EntityManagerFactory notificationEMF, EntityManagerFactory shiftEMF) {

		this.timer = new Timer();
		eventResourceBuffer = new ConcurrentLinkedQueue<EventResource>();
		eventBuffer = new ConcurrentLinkedQueue<Event>();
		notificationBuffer = new ConcurrentLinkedQueue<NotificationOccurrence>();

		boolean soundEnabled = false;
		String soundUrl = "";
		int soundPort = 0;
		String soundEnabledProp = (String) Application.get().getProp().get(Setting.SOUND_ENABLED.getCode());

		try {
			soundEnabled = Boolean.parseBoolean(soundEnabledProp);
		} catch (NumberFormatException e) {
			logger.error("Cannot parse sound port", e);
		}

		if (soundEnabled) {

			logger.info("Sound enabled, parsing url and port");
			String soundProp = (String) Application.get().getProp().get(Setting.SOUND_URL.getCode());
			String soundPortProp = (String) Application.get().getProp().get(Setting.SOUND_PORT.getCode());
			try {
				soundPort = Integer.parseInt(soundPortProp);
			} catch (NumberFormatException e) {
				logger.error("Cannot parse sound port", e);
			}

			if (soundProp != "" && soundPort != 0) {
				soundUrl = soundProp;
			}

			logger.info("Initializing sound system with url: " + soundUrl + ":" + soundPort);
			this.soundSystemManager = new SoundSystemManager(soundUrl, soundPort);
		} else {
			logger.info("Sound system is disabled");
			this.soundSystemManager = null;
		}

		/*
		 * initialize main tasks
		 */
		receiverTask = new ReceiverTask(notificationEMF, eventResourceBuffer, eventBuffer, soundSystemManager,
				soundEnabled);
		dispatcherTask = new DispatcherTask(notificationEMF, shiftEMF, eventBuffer, notificationBuffer);
		notificationTask = new NotificationTask(notificationEMF, notificationBuffer);

		generatorTask = new GeneratorTask(notificationEMF, eventBuffer);
		monitoringTask = new MonitoringTask();

	}

	public void schedule() {

		/*
		 * schedule main tasks
		 */
		timer.scheduleAtFixedRate(receiverTask, 1000, 1000);
		timer.scheduleAtFixedRate(dispatcherTask, 1000 * 10, 1000 * 10);
		timer.scheduleAtFixedRate(notificationTask, 1000 * 20, 1000 * 30);

		/*
		 * other tasks
		 */
		// t.scheduleAtFixedRate(monitoringTask, 1000 * 5, 1000 * 5);
		// t.scheduleAtFixedRate(generatorTask, 0, 1000);
	}

	public void stopTasks() {
		logger.info("Canceling scheduled tasks");
		timer.cancel();
		logger.info("Taskds canceled sucessfully");
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

	public ConcurrentLinkedQueue<Event> getEventBuffer() {
		return eventBuffer;
	}

	public ConcurrentLinkedQueue<EventResource> getEventResourceBuffer() {
		return eventResourceBuffer;
	}

	public SoundSystemManager getSoundSystemManager() {
		return soundSystemManager;
	}

}
