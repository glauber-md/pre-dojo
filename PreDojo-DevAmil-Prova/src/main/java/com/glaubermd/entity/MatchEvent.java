package com.glaubermd.entity;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Evento da partida.
 * @author glauber_md
 *
 */
public class MatchEvent {

	private Date time;
	private String log;
	private Player assassin;
	private Player victim;
	private ActionEnum action;
	private Weapon weapon;

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	/**
	 * @return the log
	 */
	public String getLog() {
		return log;
	}

	/**
	 * @param log the log to set
	 */
	public void setLog(String log) {
		this.log = log;
	}
	
	/**
	 * @return the assassin
	 */
	public Player getAssassin() {
		return assassin;
	}

	/**
	 * @param assassin the assassin to set
	 */
	public void setAssassin(Player assassin) {
		this.assassin = assassin;
	}

	/**
	 * @return the victim
	 */
	public Player getVictim() {
		return victim;
	}

	/**
	 * @param victim the victim to set
	 */
	public void setVictim(Player victim) {
		this.victim = victim;
	}

	/**
	 * @return the action
	 */
	public ActionEnum getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(ActionEnum action) {
		this.action = action;
	}

	/**
	 * @return the weapon
	 */
	public Weapon getWeapon() {
		return weapon;
	}

	/**
	 * @param weapon the weapon to set
	 */
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
