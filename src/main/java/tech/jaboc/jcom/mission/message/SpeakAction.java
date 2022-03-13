package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.player.PlayerTeam;

import java.util.*;

public class SpeakAction extends Message implements IPlayerTeamExecutable, IAiTeamExecutable {
	public String message;
	
	public SpeakAction(String message) {
		this.message = message;
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		if (team.justSent) {
			team.justSent = false;
			return;
		}
		System.out.println("Ai received " + message);
		
		// https://www.baeldung.com/java-random-string
		
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 32;
		Random random = new Random();
		
		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		
		team.missionManagerProxy.sendMessage(new SpeakRequest(generatedString));
		
		team.justSent = true;
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		System.out.println("Ran");
		if (team.justSent) {
			team.justSent = false;
			return;
		}
		System.out.println("Player received " + message);
		System.out.print("Enter a string: ");
		SpeakRequest request = new SpeakRequest(new Scanner(System.in).nextLine());
		team.missionManagerProxy.sendMessage(request);
		team.justSent = true;
	}
}
