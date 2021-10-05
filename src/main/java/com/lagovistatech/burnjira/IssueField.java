package com.lagovistatech.burnjira;

import java.util.Date;

public class IssueField {
	private Date created;
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	private int timeestimate;
	public int getTimeestimate() {
		return timeestimate;
	}
	public void setTimeestimate(int timeestimate) {
		this.timeestimate = timeestimate;
	}
	
	private String summary;
	public String getSummary() { return summary; }
	public void setSummary(String summary) { this.summary = summary; }
}
