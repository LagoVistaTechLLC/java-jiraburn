package com.lagovistatech.burnjira;

import java.util.List;

public class SearchRequest {
	private String jql;
	public String getJql() {
		return jql;
	}
	public void setJql(String jql) {
		this.jql = jql;
	}

	private int maxResults;
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	private List<String> fields;
	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	private int startAt;
	public int getStartAt() {
		return startAt;
	}
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}
}
