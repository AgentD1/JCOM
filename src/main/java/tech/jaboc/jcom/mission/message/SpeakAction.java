package tech.jaboc.jcom.mission.message;

public class SpeakAction extends Message {
	public String message;
	
	public SpeakAction(String message) {
		this.message = message;
	}
}
