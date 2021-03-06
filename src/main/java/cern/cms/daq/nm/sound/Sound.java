package cern.cms.daq.nm.sound;

public enum Sound {

	OTHER("", "Other"),
	DEFAULT("added-complete.wav", "Default"),
	STATE_CHANGE_LHC_BEAM_MODE("U2Bell.wav", "LHC beam"),
	STATE_CHANGE_LHC_MACHINE_MODE("DingDong.wav", "LHC machine"),
	STATE_CHANGE_DAQ("IntroLivingonMyOwn.wav", "DAQ state"),
	NEW_RUN("ItsAKindOfMagic.wav", "New run"),

	DCS("DCS_1.wav", "DCS sound"),
	WBM("WBM_1.wav", "WBM sound"),
	DQM("DQM_1.wav", "DQM sound"),
	EXTERNAL_DEFAULT("U2Bell.wav", "External alarm"),
	END("U2SingelBell.wav", "End"),

	KNOWN("added-info.wav", "Known"),
	DEADTIME("BigDisgrace.wav", "Deadtime"),
	NO_RATE_WHEN_EXPECTED("SadThatItTurnedOutBad.wav","No rate when expected"),
	NO_RATE_WHEN_EXPECTED2("SadWorldWithoutYou.wav","No rate when expected"),
	SUBSYSTEM_RUNNING_DEGRADED("AnotherOneBitesTheDust.wav","Subsystem running degraded"),
	COMPLETED("added-complete.wav", "Completed"),
	DARK_CHANGE("added-dark3.wav", "Dark change"),
	CROW("added-crow.wav", "Crow"),
	DROP("added-hard-drop.wav", "Drop"),

	;

	private final String filename;

	private final String displayName;

	private Sound(String filename, String displayName) {
		this.filename = filename;
		this.displayName = displayName;
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
		return OTHER;
	}

	public static Sound getById(int id) {
		if (Sound.values().length >= id) {
			return Sound.values()[id];
		}
		return DEFAULT;
	}

	public String getDisplayName() {
		return displayName;
	}
}
