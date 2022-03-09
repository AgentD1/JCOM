package tech.jaboc.jcom.mission.common;

import tech.jaboc.jcom.mission.manager.MissionManager;
import tech.jaboc.jcom.mission.message.Message;

import java.util.concurrent.*;

public class MissionManagerProxy {
	MissionManager missionManager;
	
	BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
	
	public MissionManagerProxy(MissionManager missionManager) {
		this.missionManager = missionManager;
	}
	
	/**
	 * Receives a message from the missionManager. Should only be called by the missionManager
	 *
	 * @param message The message to receive
	 */
	public void receiveMessage(Message message) {
		messages.add(message);
	}
	
	public int getMessageCount() {
		return messages.size();
	}
	
	public Message getNextMessage() {
		try {
			return messages.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Message getNextMessage(long timeout, TimeUnit timeUnit) {
		try {
			return messages.poll(timeout, timeUnit);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendMessage(Message message) {
		try {
			if (!missionManager.incomingMessages.offer(message, 50, TimeUnit.MILLISECONDS)) {
				throw new InterruptedException("MissionManager insertion failed!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
