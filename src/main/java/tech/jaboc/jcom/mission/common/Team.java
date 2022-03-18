package tech.jaboc.jcom.mission.common;

import java.io.Serializable;

public class Team implements Serializable {
	public String teamName;
	public transient MissionManagerProxy proxy;
	
	public int id;
	
	public Team(String teamName, MissionManagerProxy proxy) {
		this.teamName = teamName;
		this.proxy = proxy;
	}
}
