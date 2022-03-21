package tech.jaboc.jcom.mission.player;

import javafx.geometry.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.Contract;
import tech.jaboc.jcom.*;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

import java.util.*;

public class MissionRenderer {
	PlayerTeam team;
	Game parent;
	
	public MissionRenderer(MissionManagerProxy missionManagerProxy, String teamName, Game parent) {
		team = new PlayerTeam(missionManagerProxy, teamName, this);
		this.parent = parent;
	}
	
	public void start() {
		team.missionManagerProxy.sendMessage(new TeamJoinRequest(new Team(team.teamName, team.missionManagerProxy)));
	}
	
	Unit selectedUnit = null;
	
	Animation currentAnimation = null;
	
	int frame = 0;
	
	FloodFiller.FloodFillResult floodFillTiles;
	
	public void draw(GraphicsContext g, double width, double height) {
		frame++;
		while (team.missionManagerProxy.getPriorityMessageCount() != 0) {
			Message message = team.missionManagerProxy.getNextPriorityMessage();
			if (message instanceof IPlayerTeamExecutable ptMessage) {
				ptMessage.executePlayerTeamAction(team);
			}
		}
		
		while (currentAnimation == null && team.missionManagerProxy.getMessageCount() != 0) {
			Message message = team.missionManagerProxy.getNextMessage();
			if (message instanceof IPlayerTeamExecutable ptMessage) {
				ptMessage.executePlayerTeamAction(team);
			}
		}
		
		if (team.isMyTurn && currentAnimation == null) {
			if (Input.getMouseButtonDown(MouseButton.PRIMARY.ordinal())) {
				Point2D relativeMousePos = Input.getMousePos().multiply(1.0 / 20.0);
				int tileX = (int) relativeMousePos.getX();
				int tileY = (int) relativeMousePos.getY();
				
				if (tileX >= 0 && tileX < 20 && tileY >= 0 && tileY < 20) {
					Optional<Unit> unitMaybe = team.localMapCopy.units.stream().filter(unit -> unit.x == tileX && unit.y == tileY).findAny();
					if (unitMaybe.isPresent()) {
						Unit unit = unitMaybe.get();
						if (unit.allegiance == team.teamId) {
							selectedUnit = unit;
							floodFillTiles = floodFillUnit(selectedUnit);
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
					FloodFiller.FloodFillTile tile = new FloodFiller.FloodFillTile(tileX, tileY, 0);
					if (floodFillTiles == null) {
						floodFillTiles = floodFillUnit(selectedUnit);
					}
					if (floodFillTiles.prev.get(tile) != null) {
						ArrayList<MoveAbility.Step> steps = new ArrayList<>();
						steps.add(new MoveAbility.Step(tile.x, tile.y));
						while (true) {
							FloodFiller.FloodFillTile newTile = floodFillTiles.prev.get(tile);
							
							if (newTile == null) break;
							steps.add(new MoveAbility.Step(newTile.x, newTile.y));
							
							tile = newTile;
						}
						
						Collections.reverse(steps);
						
						Ability ability = new MoveAbility(selectedUnit.id, 0, tileX, tileY, steps);
						team.missionManagerProxy.sendMessage(new UseAbilityAction(ability));
						
						floodFillTiles = null;
					}
				}
			}
			
			if (Input.getKeyDown(KeyCode.TAB.getCode())) {
				team.missionManagerProxy.sendMessage(new NextTurnAction(-1));
			}
		}
		
		g.clearRect(0, 0, width, height);
		if (team.localMapCopy == null) {
			g.setTextBaseline(VPos.CENTER);
			g.setTextAlign(TextAlignment.CENTER);
			g.fillText("Map Not Yet Initialized", width / 2, height / 2);
			return;
		}
		
		
		if (currentAnimation != null) {
			boolean finished = currentAnimation.draw(g, 1.0 / 60.0);
			if (finished) currentAnimation = null;
		} else {
			drawMap(g);
			drawUnits(g);
		}
		
		if (!team.isMyTurn) {
			g.setFill(Color.rgb(0xE5, 0x50, 0x50));
			g.fillText("ENEMY ACTIVITY", width / 2.0, height - 20);
		}
	}
	
	void drawParallelogramAtPosition(GraphicsContext g, double xPos, double yPos) {
		g.setStroke(Color.BLACK);
		g.setLineJoin(StrokeLineJoin.ROUND);
		g.setLineWidth(2.0);
		double[] xs = new double[] { 10 + xPos, 25 + xPos, 15 + xPos, xPos };
		double[] ys = new double[] { yPos, yPos, 10 + yPos, 10 + yPos };
		g.fillPolygon(xs, ys, 4);
		g.strokePolygon(xs, ys, 4);
	}
	
	public Animation getUnitMovementAnimation(Unit u, double startPosX, double startPosY, double endPosX, double endPosY) {
		return new Animation() {
			double currentPosX = startPosX, currentPosY = startPosY;
			double currentTime = 0.0;
			final double endTime = Math.hypot(endPosX - startPosX, endPosY - startPosY) / 20.0;
			final double xSpeed = (endPosX - startPosX) / endTime;
			final double ySpeed = (endPosY - startPosY) / endTime;
			
			@Override
			public boolean draw(GraphicsContext g, double deltaTime) {
				drawMap(g);
				
				currentTime += deltaTime;
				currentPosX += xSpeed * deltaTime;
				currentPosY += ySpeed * deltaTime;
				g.setFill(u.allegiance == team.teamId ? Color.DARKGREEN : Color.DARKRED);
				g.fillOval(currentPosX * 20 + 5, currentPosY * 20 + 5, 10, 10);
				
				for (Unit unit : team.localMapCopy.units) {
					if (unit == u) {
						continue;
					}
					drawUnit(g, unit);
				}
				return currentTime >= endTime;
			}
		};
	}
	
	public void playUnitMovementAnimation(Unit u, MoveAbility ability) {
		List<Animation> animations = new ArrayList<>(ability.steps.size() - 1);
		for (int i = 1; i < ability.steps.size(); i++) {
			System.out.println("ability.steps.get(i) = " + ability.steps.get(i));
			System.out.println("ability.steps.get(i - 1) = " + ability.steps.get(i - 1));
			animations.add(getUnitMovementAnimation(u, ability.steps.get(i - 1).x, ability.steps.get(i - 1).y, ability.steps.get(i).x, ability.steps.get(i).y));
		}
		floodFillTiles = null;
		currentAnimation = new SequentialAnimation(animations);
	}
	
	
	void drawMap(GraphicsContext g) {
		for (int x = 0; x < team.localMapCopy.getLength(); x++) {
			for (int y = 0; y < team.localMapCopy.getWidth(); y++) {
				g.setFill(team.localMapCopy.tiles[x][y].color);
				g.fillRect(x * 20, y * 20, 20, 20);
			}
		}
	}
	
	void drawUnits(GraphicsContext g) {
		for (Unit u : team.localMapCopy.units) {
			drawUnit(g, u);
		}
	}
	
	void drawUnit(GraphicsContext g, Unit u) {
		if (selectedUnit == u) {
			g.setFill(Color.BLACK);
			g.fillOval(u.x * 20 + 2, u.y * 20 + 2, 16, 16);
			int baseX = u.x * 20 + 20, baseY = u.y * 20 - 10;
			g.setFill(selectedUnit.numMoveActionsLeft > 1 ? Color.BLUE : Color.BLACK);
			drawParallelogramAtPosition(g, baseX, baseY);
			g.setFill(selectedUnit.numMoveActionsLeft > 0 ? Color.BLUE : Color.BLACK);
			drawParallelogramAtPosition(g, baseX + 25, baseY);
			
			Point2D worldPoint = parent.screenPointToWorld(Input.getMousePos());
			int mouseTileX = (int) (worldPoint.getX() / 20);
			int mouseTileY = (int) (worldPoint.getY() / 20);
			
			g.setFill(Color.rgb(0, 255, 0, 0.5));
			g.fillRect(mouseTileX * 20, mouseTileY * 20, 20, 20);
			
			g.setStroke(Color.GREEN);
			g.setLineWidth(2.0f);
			
			if (floodFillTiles == null) {
				floodFillTiles = floodFillUnit(selectedUnit);
			}
			
			Optional<FloodFiller.FloodFillTile> maybeTile = floodFillTiles.accessibleTiles.stream().filter(t -> t.x == mouseTileX && t.y == mouseTileY).findFirst();
			if (maybeTile.isPresent()) {
				FloodFiller.FloodFillTile tile = maybeTile.get();
				while (true) {
					int oldX = tile.x, oldY = tile.y;
					FloodFiller.FloodFillTile newTile = floodFillTiles.prev.get(tile);
					if (newTile == null) break;
					
					g.strokeLine(oldX * 20 + 10, oldY * 20 + 10, newTile.x * 20 + 10, newTile.y * 20 + 10);
					
					tile = newTile;
				}
			}
		}
		g.setFill(u.allegiance == team.teamId ? Color.DARKGREEN : Color.DARKRED);
		g.fillOval(u.x * 20 + 5, u.y * 20 + 5, 10, 10);
	}
	
	@Contract(pure = true)
	FloodFiller.FloodFillResult floodFillUnit(Unit u) {
		return FloodFiller.floodFill(team.localMapCopy, team.localMapCopy.tiles[u.x][u.y], 100);
	}
}
