package cern.cms.daq.nm.task;

import java.text.DecimalFormat;
import java.util.TimerTask;

public class MonitoringTask extends TimerTask {
	
	static public String customFormat(Long value) {
		DecimalFormat myFormatter = new DecimalFormat("###,###,###");
		String output = myFormatter.format(value);
		return output;
	}

	public void run() {
		System.out.println("--------------------------------------");
		System.out.println("Used Memory   :  "
				+ customFormat((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + " bytes");
		System.out.println("Free Memory   : " + customFormat(Runtime.getRuntime().freeMemory()) + " bytes");
		//System.out.println("Total Memory  : " + customFormat(Runtime.getRuntime().totalMemory()) + " bytes");
		//System.out.println("Max Memory    : " + customFormat(Runtime.getRuntime().maxMemory()) + " bytes");
		//System.out.println("--------------------------------------");

	}

}
