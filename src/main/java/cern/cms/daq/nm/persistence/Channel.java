package cern.cms.daq.nm.persistence;

public enum Channel {
	sms("SMS"), email("e-mail"), push("push");

	private Channel(String name) {
		this.name = name;
	}

	public int getOrdinal() {
		return this.ordinal();
	}

	private final String name;

	public String getName() {
		return name;
	}

}
