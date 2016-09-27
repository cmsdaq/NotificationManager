package cern.cms.daq.nm;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cern.cms.daq.nm.task.TaskManager;

public class Main {
	private static final String NOTIFICATION_PERSISTENCE_UNIT_NAME = "notifications";
	private static final String SHIFT_PERSISTENCE_UNIT_NAME = "shifts";
	private static EntityManagerFactory factory;
	private static EntityManagerFactory factory2;

	public static void main(String[] args) {
		factory = Persistence.createEntityManagerFactory(NOTIFICATION_PERSISTENCE_UNIT_NAME);
		factory2 = Persistence.createEntityManagerFactory(SHIFT_PERSISTENCE_UNIT_NAME);

		Initializer.initDefaults(factory);
		TaskManager.initialize(factory, factory2);
		TaskManager.get().schedule();

	}

}