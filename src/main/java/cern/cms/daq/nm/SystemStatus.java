package cern.cms.daq.nm;

public class SystemStatus {

	public boolean stableBeams;
	public boolean shortToStableBeams;

	private SystemStatus() {
	}

	private static SystemStatus instance;

	public static SystemStatus get() {
		if (instance == null)
			instance = new SystemStatus();
		return instance;
	}

}
