package com.lagovistatech.burnjira;

import java.util.List;

public class IssueChangelog {
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

	private List<IssueChangelogHistory> histories;
	public List<IssueChangelogHistory> getHistories() {
		return histories;
	}
	public void setHistories(List<IssueChangelogHistory> histories) {
		this.histories = histories;
	}
}
