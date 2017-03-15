package cern.cms.daq.nm.sound;

public enum Sound {

	DEFAULT("added-hard-drop.wav"),
	STATE_CHANGE_LHC_BEAM_MODE("U2Bell.wav"),
	STATE_CHANGE_LHC_MACHINE_MODE("DingDong.wav"),
	STATE_CHANGE_DAQ("IntroLivingonMyOwn.wav"),
	NEW_RUN("ItsAKindOfMagic.wav"),

	DCS("DCS_1.wav"),
	WBM("WBM_1.wav"),
	DQM("DQM_1.wav"),

	KNOWN("added-info.wav"),
	DEADTIME("added-pulse.wav"),
	COMPLETED("added-complete.wav"),
	DARK_CHANGE("added-dark3.wav"),
	CROW("added-crow.wav"),
	DROP("added-hard-drop.wav");

	private final String filename;

	private Sound(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public static Sound getByFilename(String filename) {
		for (Sound sound : Sound.values()) {
			if (sound.getFilename().equals(filename)) {
				return sound;
			}
		}
		return DEFAULT;
	}

	public static Sound getById(int id) {
		if (Sound.values().length >= id) {
			return Sound.values()[id];
		}
		return DEFAULT;
	}
}
