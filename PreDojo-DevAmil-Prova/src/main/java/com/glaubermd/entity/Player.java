package com.glaubermd.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Representa um Jogador do sistema.
 * @author glauber_md
 *
 */
public class Player implements Comparable<Player> {

	private String name;
	private RankingEntry ranking;
	
	public Player(String name) {
		this.name = name;
		this.ranking = new RankingEntry(0, 0);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the ranking
	 */
	public RankingEntry getRanking() {
		return ranking;
	}

	/**
	 * @param ranking the ranking to set
	 */
	public void setRanking(RankingEntry ranking) {
		this.ranking = ranking;
	}

	@Override
	public int hashCode() {
		return (this.name != null) ? this.name.length() * 7 : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof Player && ((Player) obj).getName().equals(this.name))
			equals = true;
		return equals;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int compareTo(Player o) {
		int cmp = 0;
		if(this.name != null && this.ranking != null) {
			// Compara Player por Ranking, depois por Nome
			cmp = this.ranking.compareTo(o.getRanking());
			if(cmp == 0)
				cmp = this.name.compareTo(o.getName());
		}
		
		return cmp;
	}
}
