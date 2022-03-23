package tech.jaboc.jcom.mission.common;

import java.io.Serializable;
import java.util.*;

public class Unit implements Serializable {
	public int allegiance;
	public int x, y;
	
	public int numActionsLeft; // Typically 1 (Does not include move actions)
	public int numMoveActionsLeft;
	public int numActions;
	public int numMoveActions;
	
	public boolean usedActionThisTurn = false;
	
	public List<AbilitySlot> abilitySlots = new ArrayList<>();
	
	public int movementDistance = 8;
	
	public final int id;
	
	public Unit(int allegiance, int x, int y, int numActions, int numMoveActions, int unitId) {
		this.allegiance = allegiance;
		this.x = x;
		this.y = y;
		
		this.numActions = numActions;
		numActionsLeft = numActions;
		this.numMoveActions = numMoveActions;
		numMoveActionsLeft = numMoveActions;
		
		id = unitId;
	}
	
	public boolean canUseAbility(ActionType actionType) {
		return switch (actionType) {
			case Free -> true;
			case Move -> numMoveActionsLeft > 0;
			case Action -> numActionsLeft > 0;
			case FullTurn -> numMoveActionsLeft >= numMoveActions && numActionsLeft > 0;
			case FullTurnStrict -> !usedActionThisTurn && numActionsLeft > 0;
		};
	}
	
	public void useAbility(ActionType actionType) {
		switch (actionType) {
			case FullTurn, FullTurnStrict -> {
				numMoveActionsLeft = 0;
				numActionsLeft = 0;
			}
			case Action -> {
				numActionsLeft--;
				numMoveActionsLeft = numActionsLeft;
			}
			case Move -> {
				numMoveActionsLeft--;
				if (numMoveActionsLeft < numActionsLeft)
					numActionsLeft = numMoveActionsLeft;
			}
		}
		usedActionThisTurn = true;
	}
	
	public void nextTurn() {
		numMoveActionsLeft = numMoveActions;
		numActionsLeft = numActions;
	}
	
	public enum ActionType {
		FullTurn,           // Must be used as the first ability in the turn. Ends this unit's turn.
		FullTurnStrict,     // Must be used as the first ability in the turn, including free actions. Ends this unit's turn.
		Action,             // Typically, only 1 action can be used per turn. Ends this unit's turn.
		Free,               // Can be used at any time during the turn. Does not end this unit's turn.
		Move,               // Can be used instead of movement, or as an action. Ends this unit's turn unless it was used instead of movement.
	}
}
