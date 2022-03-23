package tech.jaboc.jcom.mission.ai;

import tech.jaboc.jcom.mission.common.Map;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

import java.util.*;

public class AiTeam {
	public MissionManagerProxy missionManagerProxy;
	
	public AiTeam(MissionManagerProxy missionManagerProxy, String teamName) {
		this.missionManagerProxy = missionManagerProxy;
		this.teamName = teamName;
	}
	
	public Map map;
	public List<Team> teams;
	
	public String teamName;
	public int teamId = -1;
	
	Random random = new Random();
	
	public void gameLoop() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team(teamName, missionManagerProxy)));
		
		while (true) {
			Message message = missionManagerProxy.getNextMessage();
			if (message instanceof IAiTeamExecutable atMessage) {
				atMessage.executeAiTeamMessage(this);
			}
		}
	}
	
	public void executeTurn() {
		for (Unit unit : map.units) {
			if (unit.allegiance == teamId) {
				FloodFiller.FloodFillResult result = FloodFiller.floodFill(map, map.tiles[unit.x][unit.y], unit.movementDistance,
						EnumSet.of(FloodFiller.FloodFillOptions.ALLOW_PASSAGE_THROUGH_OCCUPIED));
				List<FloodFiller.FloodFillTile> tiles = new ArrayList<>(result.accessibleTiles);
				Collections.shuffle(tiles);
				for (FloodFiller.FloodFillTile tile : tiles) {
					FloodFiller.FloodFillTile finalTile = tile; // java cringe
					if (map.units.stream().anyMatch(u -> u.x == finalTile.x && u.y == finalTile.y)) {
						continue;
					}
					
					ArrayList<MoveAbility.Step> steps = new ArrayList<>();
					steps.add(new MoveAbility.Step(tile.x, tile.y));
					while (true) {
						FloodFiller.FloodFillTile newTile = result.prev.get(tile);
						
						if (newTile == null) break;
						steps.add(new MoveAbility.Step(newTile.x, newTile.y));
						
						tile = newTile;
					}
					
					Collections.reverse(steps);
					
					Ability ability = new MoveAbility(unit.id, 0, finalTile.x, finalTile.y, steps);
					missionManagerProxy.sendMessage(new UseAbilityAction(ability));
					System.out.println(tile.x == finalTile.x);
					break;
				}
			}
		}
		
		missionManagerProxy.sendMessage(new NextTurnAction(-1));
	}
}
