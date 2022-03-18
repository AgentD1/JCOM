package tech.jaboc.jcom.mission.player;

import tech.jaboc.jcom.mission.common.*;

import java.util.List;

public class PlayerTeam {
	public MissionManagerProxy missionManagerProxy;
	public MissionRenderer renderer;
	
	public Map localMapCopy;
	public List<Team> teams;
	
	public String teamName;
	public int teamId = -1;
	
	public boolean isMyTurn = false;
	
	public PlayerTeam(MissionManagerProxy missionManagerProxy, String teamName, MissionRenderer renderer) {
		this.missionManagerProxy = missionManagerProxy;
		this.teamName = teamName;
		this.renderer = renderer;
	}
}
