package cern.cms.daq.nm;

public enum Setting {

	EXPERT_BROWSER("expert.url"),
	LANDING("landing"),
	SOUND_URL("sound.url"),
	SOUND_PORT("sound.port"),
	SOUND_ENABLED("sound.enabled"),
	EXTERNAL_NOTIFICATION_PORT("external.notification.port");

	private final String code;

	private Setting(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
