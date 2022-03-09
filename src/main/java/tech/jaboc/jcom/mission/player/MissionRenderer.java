package tech.jaboc.jcom.mission.player;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import tech.jaboc.jcom.mission.common.*;
import tech.jaboc.jcom.mission.message.*;

public class MissionRenderer {
	MissionManagerProxy missionManagerProxy;
	Map localMapCopy;
	
	public MissionRenderer(MissionManagerProxy missionManagerProxy) {
		this.missionManagerProxy = missionManagerProxy;
	}
	
	public void start() {
		missionManagerProxy.sendMessage(new TeamJoinRequest(new Team("Player", missionManagerProxy)));
	}
	
	public void draw(GraphicsContext g, double width, double height) {
		while (missionManagerProxy.getMessageCount() != 0) {
			Message message = missionManagerProxy.getNextMessage();
			if (message instanceof MapSynchronizeAction mapSynchronizeAction) {
				localMapCopy = mapSynchronizeAction.map;
			}
		}
		
		g.clearRect(0, 0, width, height);
		if (localMapCopy == null) {
			g.setTextBaseline(VPos.CENTER);
			g.setTextAlign(TextAlignment.CENTER);
			g.fillText("Map Not Yet Initialized", width / 2, height / 2);
			return;
		}
		
		for (int x = 0; x < localMapCopy.getLength(); x++) {
			for (int y = 0; y < localMapCopy.getWidth(); y++) {
				g.setFill(localMapCopy.tiles[x][y].color);
				g.fillRect(x * 20, y * 20, 20, 20);
			}
		}
		
		for (Unit u : localMapCopy.units) {
			g.setFill(u.allegiance == 0 ? Color.DARKGREEN : Color.DARKRED);
			g.fillOval(u.x * 20 + 5, u.y * 20 + 5, 10, 10);
		}
	}
}
