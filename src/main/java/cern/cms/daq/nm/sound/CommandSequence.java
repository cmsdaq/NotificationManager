package cern.cms.daq.nm.sound;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CommandSequence")
public class CommandSequence {

	private Alarm alarm;

	public Alarm getAlarm() {
		return alarm;
	}

	@XmlElement
	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

}
