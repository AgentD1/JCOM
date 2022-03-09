package tech.jaboc.jcom.mission.message;

public class SpeakRequest extends Message {
	public String text;
	
	public SpeakRequest(String text) {
		this.text = text;
	}
}
