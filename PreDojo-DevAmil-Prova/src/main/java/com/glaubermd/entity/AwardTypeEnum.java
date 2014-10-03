package com.glaubermd.entity;

/** 
 * Representa os Tipos de Premios presentes no sistema. 
 * @author glauber_md
 *
 */
public enum AwardTypeEnum {

	KILL_STREAK("Kill Streak"),
	MOST_KILLS_IN_ONE_MINUTE("Most Kills in One Minute"),
	IMMORTAL("Didn't die on This Match");
	private String description;
	
	
	private AwardTypeEnum(String desc) {
		this.description = desc;
	}


	/**
	 * @return the status
	 */
	public String getDescription() {
		return description;
	}
	
}
