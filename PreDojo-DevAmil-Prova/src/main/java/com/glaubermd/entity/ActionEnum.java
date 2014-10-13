package com.glaubermd.entity;


/**
 * Representa as acoes possiveis de um Jogador.
 * @author glauber_md
 *
 */
public enum ActionEnum {

	KILL("killed"),
	REVIVE("revived"),
	CURE("cured"),
	CURSE("cursed"),
	HASTEN("hastened"),
	KICK_OUT("kicked out"),
	BAN("banned");
	
	private String actionDescription;
	
	ActionEnum(String action) {
		this.actionDescription = action;
	}

	public String getActionDescription() {
		return actionDescription;
	}
	
}
