package cern.cms.daq.nm.task;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.Application;
import cern.cms.daq.nm.EventResource;
import cern.cms.daq.nm.NotificationException;
import cern.cms.daq.nm.Setting;
import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.NotificationOccurrence;
import cern.cms.daq.nm.sound.SoundConfigurationReader;
import cern.cms.daq.nm.sound.SoundDispatcher;
import cern.cms.daq.nm.sound.SoundSelector;
import cern.cms.daq.nm.sound.SoundSystemConnector;
import cern.cms.daq.nm.sound.SoundTrigger;

public class TaskManager {

	private static TaskManager instance;

	private final ConcurrentLinkedQueue<EventResource> eventResourceBuffer;
	private final ConcurrentLinkedQueue<Event> eventBuffer;
	private final ConcurrentLinkedQueue<NotificationOccurrence> notificationBuffer;
	private final ConcurrentLinkedQueue<Event> soundBuffer;

	private final TimerTask notificationTask;
	private final TimerTask dispatcherTask;
	private final TimerTask receiverTask;
	private final TimerTask soundSendingTask;

	@SuppressWarnings("unused")
	private final TimerTask generatorTask;
	@SuppressWarnings("unused")
	private final TimerTask monitoringTask;

	private final Logger logger = Logger.getLogger(TaskManager.class);

	private final Timer timer;

	private TaskManager(EntityManagerFactory notificationEMF, EntityManagerFactory shiftEMF) {

		this.timer = new Timer();
		eventResourceBuffer = new ConcurrentLinkedQueue<EventResource>();
		eventBuffer = new ConcurrentLinkedQueue<Event>();
		notificationBuffer = new ConcurrentLinkedQueue<NotificationOccurrence>();
		soundBuffer = new ConcurrentLinkedQueue<Event>();

		boolean soundEnabled = false;

		String soundEnabledProp = (String) Application.get().getProp().get(Setting.SOUND_ENABLED.getCode());

		try {
			soundEnabled = Boolean.parseBoolean(soundEnabledProp);
		} catch (NumberFormatException e) {
			logger.error("Cannot parse sound port", e);
		}

		SoundTrigger trigger = new SoundTrigger();
		SoundSelector selector = new SoundSelector();
		SoundConfigurationReader reader = new SoundConfigurationReader();
		selector.configure(reader.readSoundSelectConfigurations(Application.get().getProp()));
		trigger.configure(reader.readSoundTriggerConfigurations(Application.get().getProp()));

		SoundDispatcher soundDispatcher = new SoundDispatcher(soundBuffer, trigger, selector, soundEnabled);

		SoundSystemConnector connector = SoundSystemConnector.buildSoundSystemConnector();
		
		/*
		 * initialize main tasks
		 */
		receiverTask = new ReceiverTask(notificationEMF, eventResourceBuffer, eventBuffer, soundDispatcher);
		dispatcherTask = new DispatcherTask(notificationEMF, shiftEMF, eventBuffer, notificationBuffer);
		notificationTask = new NotificationTask(notificationEMF, notificationBuffer);
		soundSendingTask = new SoundSenderTask(notificationEMF, soundBuffer, connector);

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
		timer.scheduleAtFixedRate(soundSendingTask, 1000 * 1 , 1000);

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

}
