package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.player.PlayerTeam;

import java.io.*;

public class MapSynchronizeAction extends Message implements IAiTeamExecutable, IPlayerTeamExecutable {
	public Map map;
	
	public MapSynchronizeAction(Map map) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(map);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			this.map = (Map) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		team.map = map;
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		team.localMapCopy = map;
	}
}
