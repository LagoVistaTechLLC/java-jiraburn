package com.lagovistatech.burnjira;

public class Issue {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	private IssueChangelog changelog;
	public IssueChangelog getChangelog() {
		return changelog;
	}
	public void setChangelog(IssueChangelog changelog) {
		this.changelog = changelog;
	}

	private IssueField fields;
	public IssueField getFields() {
		return fields;
	}
	public void setFields(IssueField fields) {
		this.fields = fields;
	}
}
