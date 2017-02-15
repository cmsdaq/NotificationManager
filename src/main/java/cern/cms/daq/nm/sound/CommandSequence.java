package cern.cms.daq.nm.sound;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CommandSequence")
public class CommandSequence {

	private List<Alarm> alarm;

	public List<Alarm> getAlarm() {
		return alarm;
	}

	public void setAlarm(List<Alarm> alarm) {
		this.alarm = alarm;
	}


}
