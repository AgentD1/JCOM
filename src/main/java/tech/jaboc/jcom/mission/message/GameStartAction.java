package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.player.PlayerTeam;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GameStartAction extends Message implements IAiTeamExecutable, IPlayerTeamExecutable {
	public Team[] teams;
	
	public GameStartAction(Team[] teams) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(teams);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			this.teams = (Team[]) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		team.teams = Arrays.stream(teams).collect(Collectors.toList());
		for (int i = 0; i < teams.length; i++) {
			Team t = teams[i];
			if(t.teamName.equals(team.teamName)) {
				team.teamId = i;
				break;
			}
		}
		if(team.teamId == -1) throw new DesyncException("No team found with my team name!");
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		team.teams = Arrays.stream(teams).collect(Collectors.toList());
		for (int i = 0; i < teams.length; i++) {
			Team t = teams[i];
			if(t.teamName.equals(team.teamName)) {
				team.teamId = i;
				break;
			}
		}
		if(team.teamId == -1) throw new DesyncException("No team found with my team name!");
	}
}
