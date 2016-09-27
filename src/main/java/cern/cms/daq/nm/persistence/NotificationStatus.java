package cern.cms.daq.nm.persistence;

public enum NotificationStatus {

	Success("Success"), Failure("Failure"), Pending("Pending");
	
	private NotificationStatus(String name){
		this.name = name;
	}
	
	private final String name;

	public String getName() {
		return name;
	}

}
