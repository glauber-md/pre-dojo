package com.glaubermd.entity;

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
