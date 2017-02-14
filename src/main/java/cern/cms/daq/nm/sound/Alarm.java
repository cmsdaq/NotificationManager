package cern.cms.daq.nm.sound;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class Alarm {

	private String sender;

	private String sound;

	private String talk;

	private String text;

	public String getSender() {
		return sender;
	}

	@XmlAttribute
	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSound() {
		return sound;
	}

	@XmlAttribute
	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getTalk() {
		return talk;
	}

	@XmlAttribute
	public void setTalk(String talk) {
		this.talk = talk;
	}

	@Override
	public String toString() {
		return "Alarm [sender=" + sender + ", sound=" + sound + ", talk=" + talk + ", text=" + text + "]";
	}

	public String getText() {
		return text;
	}

	@XmlValue
	public void setText(String text) {
		this.text = text;
	}

}
