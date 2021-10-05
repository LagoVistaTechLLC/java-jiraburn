package com.lagovistatech.burnjira;

import java.util.List;

public class SearchResults {
	private String expand;
	public String getExpand() {
		return expand;
	}
	public void setExpand(String expand) {
		this.expand = expand;
	}

	private int startAt;
	public int getStartAt() {
		return startAt;
	}
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	private int maxResults;
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	private int total;
	public int getTotal() { 
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	private List<SearchIssue> issues;
	public List<SearchIssue> getIssues() {
		return issues;
	}
	public void setIssues(List<SearchIssue> issues) {
		this.issues = issues;
	}
}
