package tech.jaboc.jcom.mission.message;

public abstract class Message {
	public boolean isPriority() {
		return false;
	}
	
	public transient int teamFrom = -1;
}
