package cern.cms.daq.nm;

public enum Setting {

	EXPERT_BROWSER("expert.url"),
	LANDING("landing"),
	SOUND_URL("sound.url"),
	SOUND_PORT("sound.port"),
	SOUND_ENABLED("sound.enabled"),
	EXTERNAL_NOTIFICATION_PORT("external.notification.port"),
	DAQVIEW_LINK("daqview.link"),
	DAQVIEW_SETUP("daqview.setup"),
	
	DATABASE_USER("hibernate.connection.username"),
	DATABASE_PASSWORD("hibernate.connection.password"),
	DATABASE_URL("hibernate.connection.url"),
	DATABASE_DRIVER("hibernate.connection.driver_class"),
	DATABASE_MODE("hibernate.hbm2ddl.auto"),
	

	WEBSOCKET_NM("websocket.nm.url"),
	WEBSOCKET_EXPERT("websocket.expert.url"),
	;

	private final String code;

	private Setting(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
