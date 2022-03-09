package tech.jaboc.jcom.mission.common;

public class Team {
	public String teamName;
	public MissionManagerProxy proxy;
	
	public Team(String teamName, MissionManagerProxy proxy) {
		this.teamName = teamName;
		this.proxy = proxy;
	}
}
