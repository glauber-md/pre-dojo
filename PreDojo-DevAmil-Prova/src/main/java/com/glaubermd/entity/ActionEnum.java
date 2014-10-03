package com.glaubermd.entity;

/**
 * Representa as acoes possiveis de um Jogador.
 * @author glauber_md
 *
 */
public enum ActionEnum {

	KILL("killed");
	// REVIVED
	// CURED
	
	private String action;
	
	ActionEnum(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

}
