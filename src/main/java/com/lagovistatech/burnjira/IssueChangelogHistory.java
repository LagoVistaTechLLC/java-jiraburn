package com.lagovistatech.burnjira;

import java.util.Date;
import java.util.List;

public class IssueChangelogHistory {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private Date created;
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	private List<IssueChangelogHistoryItem> items;
	public List<IssueChangelogHistoryItem> getItems() {
		return items;
	}
	public void setItems(List<IssueChangelogHistoryItem> items) {
		this.items = items;
	}
}
