package tech.jaboc.jcom.mission.player;

import javafx.geometry.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import tech.jaboc.jcom.Input;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

import java.util.Optional;

public class MissionRenderer {
	PlayerTeam team;
	
	public MissionRenderer(MissionManagerProxy missionManagerProxy) {
		team = new PlayerTeam(missionManagerProxy);
	}
	
	public void start() {
		team.missionManagerProxy.sendMessage(new TeamJoinRequest(new Team("Player", team.missionManagerProxy)));
	}
	
	Unit selectedUnit = null;
	
	public void draw(GraphicsContext g, double width, double height) {
		while (team.missionManagerProxy.getMessageCount() != 0) {
			Message message = team.missionManagerProxy.getNextMessage();
			if (message instanceof IPlayerTeamExecutable ptMessage) {
				ptMessage.executePlayerTeamAction(team);
			}
		}
		
		if (Input.getMouseButtonDown(MouseButton.PRIMARY.ordinal())) {
			Point2D relativeMousePos = Input.getMousePos().multiply(1.0 / 20.0);
			int tileX = (int) relativeMousePos.getX();
			int tileY = (int) relativeMousePos.getY();
			
			if (tileX >= 0 && tileX < 20 && tileY >= 0 && tileY < 20) {
				Optional<Unit> unitMaybe = team.localMapCopy.units.stream().filter(unit -> unit.x == tileX && unit.y == tileY).findAny();
				if (unitMaybe.isPresent()) {
					Unit unit = unitMaybe.get();
					if (unit.allegiance == 0) {
						selectedUnit = unit;
					} else {
						selectedUnit = null;
					}
				} else {
					selectedUnit = null;
				}
			} else {
				selectedUnit = null;
			}
		}
		if (Input.getMouseButtonDown(MouseButton.SECONDARY.ordinal())) {
			Point2D relativeMousePos = Input.getMousePos().multiply(1.0 / 20.0);
			int tileX = (int) relativeMousePos.getX();
			int tileY = (int) relativeMousePos.getY();
			
			if (selectedUnit != null) {
				Ability ability = new MoveAbility(selectedUnit.id, 0, tileX, tileY);
				team.missionManagerProxy.sendMessage(new UseAbilityAction(ability));
				selectedUnit = null;
			}
		}
		
		g.clearRect(0, 0, width, height);
		if (team.localMapCopy == null) {
			g.setTextBaseline(VPos.CENTER);
			g.setTextAlign(TextAlignment.CENTER);
			g.fillText("Map Not Yet Initialized", width / 2, height / 2);
			return;
		}
		
		for (int x = 0; x < team.localMapCopy.getLength(); x++) {
			for (int y = 0; y < team.localMapCopy.getWidth(); y++) {
				g.setFill(team.localMapCopy.tiles[x][y].color);
				g.fillRect(x * 20, y * 20, 20, 20);
			}
		}
		
		for (Unit u : team.localMapCopy.units) {
			if (selectedUnit == u) {
				g.setFill(Color.CYAN);
				g.fillOval(u.x * 20 + 2, u.y * 20 + 2, 16, 16);
			}
			g.setFill(u.allegiance == 0 ? Color.DARKGREEN : Color.DARKRED);
			g.fillOval(u.x * 20 + 5, u.y * 20 + 5, 10, 10);
		}
	}
}
