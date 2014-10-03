package com.glaubermd.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Representa o Ranking do Jogador.
 * @author glauber_md
 *
 */
public class RankingEntry {

	private static final int ONE_MINUTE = 60 * 1000;
	private int kills;
	private int deaths;
	private List<AwardTypeEnum> awards;
	private Map<Weapon,Integer> weaponUsage;
	private Weapon mostUsedWeapon;
	private int mostUsedWeaponAccumulator;
	private Date lastKillTime;
	private int killsInOneMinute;
	
	public RankingEntry(int kills, int deaths) {
		this.kills = kills;
		this.deaths = deaths;
		awards = new ArrayList<AwardTypeEnum>();
		weaponUsage = new HashMap<Weapon,Integer>();
	}
	
	public void addKill() {
		kills += 1;
	}
	
	public void addKill(Date currentKillTime) {
		if (this.lastKillTime == null) {
			this.lastKillTime = currentKillTime;
			killsInOneMinute++;
		}
		if ((currentKillTime.getTime() - this.lastKillTime.getTime()) <= ONE_MINUTE) {
			killsInOneMinute++;
		}
		kills += 1;
	}
	
	public void addDeath() {
		deaths += 1;
	}
	
	public void addAward(AwardTypeEnum award) {
		awards.add(award);
	}
	
	public void recordWeaponUsage(Weapon weapon) {
		// Se utilizacao da arma ja esta registrada
		if (weaponUsage.containsKey(weapon)) {
			// Adiciona mais uma utilizacao
			weaponUsage.put(weapon, weaponUsage.get(weapon) + 1);
		} else {
			// Senao adiciona primeira utilizacao
			weaponUsage.put(weapon, 1);
		}
		// Se utilizacao dessa arma e maior que as utilizacoes registradas de outras armas
		if (weaponUsage.get(weapon) > mostUsedWeaponAccumulator) {
			// Adiciona arma como mais utilzada
			mostUsedWeaponAccumulator = weaponUsage.get(weapon);
			mostUsedWeapon = weapon;
		}
	}
	
	/**
	 * @return the kills
	 */
	public Integer getKills() {
		return kills;
	}
	/**
	 * @param kills the kills to set
	 */
	public void setKills(Integer kills) {
		this.kills = kills;
	}
	/**
	 * @return the deaths
	 */
	public Integer getDeaths() {
		return deaths;
	}
	/**
	 * @param deaths the deaths to set
	 */
	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}

	/**
	 * @return the awards
	 */
	public List<AwardTypeEnum> getAwards() {
		return awards;
	}
	
	/**
	 * @return the mostUsedWeapon
	 */
	public Weapon getMostUsedWeapon() {
		return mostUsedWeapon;
	}

	/**
	 * @return the killsInOneMinute
	 */
	public int getKillsInOneMinute() {
		return killsInOneMinute;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
