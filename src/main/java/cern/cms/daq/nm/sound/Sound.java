package cern.cms.daq.nm.sound;

public enum Sound {

	
	STATE_CHANGE_LHC_BEAM_MODE("DingDong.wav"),
	STATE_CHANGE_LHC_MACHINE_MODE("U2Bell.wav"),
	STATE_CHANGE_DAQ("IntroLivingonMyOwn.wav"),
	
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

}
