package tech.jaboc.jcom.mission.message;

import tech.jaboc.jcom.mission.ai.AiTeam;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.manager.MissionManager;
import tech.jaboc.jcom.mission.player.PlayerTeam;

import java.io.*;

public class UseAbilityAction extends Message implements IMissionManagerExecutable, IPlayerTeamExecutable, IAiTeamExecutable {
	public Ability ability;
	
	public UseAbilityAction(Ability ability) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(ability);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			this.ability = (Ability) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void executeMissionManagerAction(MissionManager missionManager) {
		if (ability instanceof IMissionManagerExecutable mmExec) {
			mmExec.executeMissionManagerAction(missionManager);
		}
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		if (ability instanceof IPlayerTeamExecutable pExec) {
			pExec.executePlayerTeamAction(team);
		}
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		if (ability instanceof IAiTeamExecutable aiExec) {
			aiExec.executeAiTeamMessage(team);
		}
	}
}
