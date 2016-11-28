package cern.cms.daq.nm.sound;

import java.io.IOException;

public class SoundSystemManagerTest {

	public static void main(String args[]) throws IOException, InterruptedException {
		SoundSystemManager ssm = new SoundSystemManager("http://dvbu-pcintelsz", 50505);

		ssm.play(Sound.STATE_CHANGE_LHC_MACHINE_MODE);
		ssm.sayAndListen("LHC beam mode: Stable beams");
		Thread.sleep(1000);
		
		ssm.play(Sound.DROP);
		ssm.sayAndListen("No rate!");
		Thread.sleep(1000);

		ssm.play(Sound.DROP);
		ssm.sayAndListen("Backpressure!");
		Thread.sleep(1000);
		
		ssm.play(Sound.DARK_CHANGE);
		ssm.sayAndListen("LHC machine mode: Proton physic");
		Thread.sleep(1000);
		
		ssm.play(Sound.CROW);
		ssm.sayAndListen("Deadtime!");
		Thread.sleep(1000);
		

		ssm.play(Sound.STATE_CHANGE_LHC_MACHINE_MODE);
		ssm.sayAndListen("DAQ state: running");
		Thread.sleep(1000);
		
		ssm.play(Sound.DROP);
		ssm.sayAndListen("No rate!");
		Thread.sleep(1000);
	}
	
	

}