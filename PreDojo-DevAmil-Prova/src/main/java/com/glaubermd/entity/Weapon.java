package com.glaubermd.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Representa uma Arma utilizada pelo Jogador.
 * @author glauber_md
 *
 */
public class Weapon {

	private String name;
	
	public Weapon(String name) {
		this.name = name;
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
	
	@Override
	public int hashCode() {
		return (this.name != null) ? this.name.length() * 9 : 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if (obj instanceof Weapon && ((Weapon) obj).getName().equals(this.name))
			equals = true;
		return equals;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
