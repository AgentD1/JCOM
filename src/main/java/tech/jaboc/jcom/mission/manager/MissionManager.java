package tech.jaboc.jcom.mission.manager;

import javafx.scene.paint.Color;
import tech.jaboc.jcom.mission.common.Map;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MissionManager {
	int expectedTeams;
	int currentTeamId = 0;
	List<Team> teams = new ArrayList<>();
	int currentTurnId = 0;
	
	public BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();
	
	AtomicBoolean gameStarted = new AtomicBoolean(false);
	
	public Map map;
	
	public MissionManager(int expectedTeams) {
		this.expectedTeams = expectedTeams;
		
		int length = 20, width = 20;
		Tile[][] tiles = new Tile[length][width];
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < width; y++) {
				tiles[x][y] = new Tile();
				tiles[x][y].x = x;
				tiles[x][y].y = y;
				tiles[x][y].passable = Math.random() < 0.75;
				if (tiles[x][y].passable)
					tiles[x][y].color = Color.hsb(Math.random() * 255, 0.1, 1);
				else
					tiles[x][y].color = Color.BLACK;
			}
		}
		
		List<Unit> units = new ArrayList<>();
		
		int numUnits = (int) (Math.random() * 6) + 2;
		for (int i = 0; i < numUnits; i++) {
			int x, y;
			while (true) {
				x = (int) (Math.random() * length);
				y = (int) (Math.random() * width);
				final int fx = x, fy = y; // i hate java
				if (units.stream().noneMatch(unit -> unit.x == fx && unit.y == fy)) break;
			}
			
			Unit newUnit = new Unit((int) (Math.random() * 2), x, y, 1, 2, i);
			newUnit.abilitySlots.add(new AbilitySlot(Unit.ActionType.Move));
			units.add(newUnit);
		}
		
		map = new Map(20, 20);
		map.units = units;
		map.tiles = tiles;
	}
	
	public void start() {
		System.out.println("Mission Manager started!");
		while (!gameStarted.get()) {
			try {
				Message message = incomingMessages.poll(50, TimeUnit.MILLISECONDS);
				if (message == null) continue;
				if (message instanceof TeamJoinRequest teamJoinRequest) {
					System.out.println("TeamJoinRequest!! " + teamJoinRequest.team.teamName);
					teams.add(teamJoinRequest.team);
					teamJoinRequest.team.id = currentTeamId;
					currentTeamId++;
					expectedTeams--;
					
					teamJoinRequest.team.proxy.receiveMessage(new MapSynchronizeAction(map));
					
					if (expectedTeams == 0) {
						gameStarted.set(true);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Team[] teamsArray = teams.toArray(new Team[0]);
		SendMessageToTeams(new GameStartAction(teamsArray));
		SendMessageToTeams(new NextTurnAction(currentTurnId));
		System.out.println("Game started!");
		while (gameStarted.get()) {
			try {
				Message message = incomingMessages.poll(50, TimeUnit.MILLISECONDS);
				if (message == null) continue;
				if (message instanceof IMissionManagerExecutable mmMessage) {
					mmMessage.executeMissionManagerAction(this);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void nextTurn() {
		currentTurnId++;
		currentTurnId %= teams.size();
		SendMessageToTeams(new NextTurnAction(currentTurnId));
	}
	
	public void SendMessageToTeams(Message message) {
		for (Team team : teams) {
			team.proxy.receiveMessage(message);
		}
	}
	
	public List<Team> getTeams() {
		return Collections.unmodifiableList(teams);
	}
}
