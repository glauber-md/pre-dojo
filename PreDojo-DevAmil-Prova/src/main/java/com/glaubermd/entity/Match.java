package com.glaubermd.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Match {

	private Integer id;
	private Date start;
	private Date end;
	private List<MatchEvent> events;
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * @return the events
	 */
	public List<MatchEvent> getEvents() {
		return events;
	}
	/**
	 * @param events the events to set
	 */
	public void setEvents(List<MatchEvent> events) {
		this.events = events;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
