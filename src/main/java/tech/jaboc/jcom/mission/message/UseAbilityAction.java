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
		Unit unit = missionManager.map.getUnitFromId(ability.unitId);
		AbilitySlot abilitySlot = unit.abilitySlots.get(ability.abilityId);
		if (!abilitySlot.canUseAbilitySlot(unit))
			throw new DesyncException("Unable to use ability " + ability + " on Unit #" + unit.id);
		if (ability instanceof IMissionManagerExecutable mmExec) {
			abilitySlot.useAbility(unit);
			mmExec.executeMissionManagerAction(missionManager);
		}
	}
	
	@Override
	public void executePlayerTeamAction(PlayerTeam team) {
		Unit unit = team.localMapCopy.getUnitFromId(ability.unitId);
		AbilitySlot abilitySlot = unit.abilitySlots.get(ability.abilityId);
		if (ability instanceof IPlayerTeamExecutable pExec) {
			abilitySlot.useAbility(unit);
			pExec.executePlayerTeamAction(team);
		}
	}
	
	@Override
	public void executeAiTeamMessage(AiTeam team) {
		Unit unit = team.map.getUnitFromId(ability.unitId);
		AbilitySlot abilitySlot = unit.abilitySlots.get(ability.abilityId);
		if (ability instanceof IAiTeamExecutable aiExec) {
			abilitySlot.useAbility(unit);
			aiExec.executeAiTeamMessage(team);
		}
	}
}
