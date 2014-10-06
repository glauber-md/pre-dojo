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
public class RankingEntry implements Comparable<RankingEntry> {

	private static final int ONE_MINUTE = 60 * 1000;
	private int kills;
	private int deaths;
	private List<AwardTypeEnum> awards;
	private Map<Weapon,Integer> weaponUsage;
	private Weapon mostUsedWeapon;
	private int mostUsedWeaponAccumulator;
	private Date lastKillTime;
	private int killsInOneMinute;
	private int killStreakAccumulator;
	private int killStreak;
	
	public RankingEntry(int kills, int deaths) {
		this.kills = kills;
		this.deaths = deaths;
		awards = new ArrayList<AwardTypeEnum>();
		weaponUsage = new HashMap<Weapon,Integer>();
		killStreakAccumulator = this.kills;
		updateKillStreak(killStreakAccumulator);
	}
	
	public void addKill() {
		addKill(null);
	}
	
	public void addKill(Date currentKillTime) {
		if (this.lastKillTime == null && currentKillTime != null) {
			this.lastKillTime = currentKillTime;
			killsInOneMinute++;
		}
		if (currentKillTime != null && (currentKillTime.getTime() - this.lastKillTime.getTime()) <= ONE_MINUTE) {
			killsInOneMinute++;
		}
		kills++;
		killStreakAccumulator++;
		updateKillStreak(killStreakAccumulator);
	}
	
	public void addDeath() {
		deaths++;
		// KillStreak para; montante acumulado eh armazenado para exibicao
		updateKillStreak(killStreakAccumulator);
		killStreakAccumulator = 0;
	}

	private void updateKillStreak(int newValue) {
		if(newValue > 0 && newValue > killStreak) 
			this.killStreak = newValue;
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
	public Integer getKillsInOneMinute() {
		return killsInOneMinute;
	}

	/**
	 * @return the killstreak
	 */
	public Integer getKillStreak() {
		return killStreak;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int compareTo(RankingEntry o) {
		int cmp = 0;
		if(o == null)
			cmp = -1;
		// Compara Ranking de acordo com assassinatos, depois mortes, quantidade de premios, qtd de mortes no minuto, killstreak 
		cmp = this.getKills().compareTo(o.getKills());
		if(cmp == 0)
			cmp = o.getDeaths().compareTo(this.getDeaths());
		if(cmp == 0)
			cmp = ((Integer)this.getAwards().size()).compareTo(o.getAwards().size());
		if(cmp == 0)
			cmp = this.getKillsInOneMinute().compareTo(o.getKillsInOneMinute());
		if(cmp == 0)
			cmp = this.getKillStreak().compareTo(o.getKillStreak());
		
		return cmp;
	}
	
	@Override
	public int hashCode() {
		return this.kills + this.deaths * 9;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof RankingEntry) {
			RankingEntry o = (RankingEntry)obj;
			equals = o.getKills() == this.kills 
					&& o.getDeaths() == this.deaths 
					&& o.getKillsInOneMinute() == this.killsInOneMinute
					&& o.getMostUsedWeapon().equals(this.mostUsedWeapon)
					&& o.getKillStreak().equals(this.killStreak);
		}

		return equals;
	}
}
