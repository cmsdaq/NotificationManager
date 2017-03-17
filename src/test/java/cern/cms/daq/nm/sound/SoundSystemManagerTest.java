package cern.cms.daq.nm.sound;

import java.io.IOException;

public class SoundSystemManagerTest {

	public static void main(String args[]) throws IOException, InterruptedException {
		SoundSystemConnector ssm = new SoundSystemConnector("http://dvbu-pcintelsz", 50505);

		ssm.play(Sound.STATE_CHANGE_LHC_MACHINE_MODE.getFilename());
		ssm.sayAndListen("LHC beam mode: Stable beams");
		Thread.sleep(1000);

		ssm.play(Sound.DROP.getFilename());
		ssm.sayAndListen("No rate!");
		Thread.sleep(1000);

		ssm.play(Sound.DROP.getFilename());
		ssm.sayAndListen("Backpressure!");
		Thread.sleep(1000);

		ssm.play(Sound.DARK_CHANGE.getFilename());
		ssm.sayAndListen("LHC machine mode: Proton physic");
		Thread.sleep(1000);

		ssm.play(Sound.CROW.getFilename());
		ssm.sayAndListen("Deadtime!");
		Thread.sleep(1000);

		ssm.play(Sound.STATE_CHANGE_LHC_MACHINE_MODE.getFilename());
		ssm.sayAndListen("DAQ state: running");
		Thread.sleep(1000);

		ssm.play(Sound.DROP.getFilename());
		ssm.sayAndListen("No rate!");
		Thread.sleep(1000);
	}

}