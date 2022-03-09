package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.common.Team;

public class TeamJoinRequest extends Message {
	public Team team;
	
	public TeamJoinRequest(Team team) {
		this.team = team;
	}
}
