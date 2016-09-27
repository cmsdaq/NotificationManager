package cern.cms.daq.nm.persistence;

public enum EventStatus {

	Received("Received"), Dispatched("Dispatched"), Notified("Notified");
	
	private EventStatus(String name){
		this.name = name;
	}
	
	private final String name;

	public String getName() {
		return name;
	}

}
