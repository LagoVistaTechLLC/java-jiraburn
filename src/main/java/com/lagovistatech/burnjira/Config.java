package com.lagovistatech.burnjira;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {
	private String projectName;
	public String getProjectName() { return projectName; }
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	private String jiraRestUrl;
	public String getJiraRestUrl() {
		return jiraRestUrl;
	}
	public void setJiraRestUrl(String jiraRestUrl) {
		this.jiraRestUrl = jiraRestUrl;
	}

	private String user;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	private String passwordKey;
	public String getPasswordKey() {
		return passwordKey;
	}
	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}
	
	private String searchJql;
	public String getSearchJql() { return this.searchJql; }
	public void setSearchJql(String searchJql) { this.searchJql = searchJql; }
	
	private List<String> completedStatuses;
	public List<String> getCopletedStatuses() {
		return completedStatuses;	
	}
	public void setCompletedStatuses(List<String> completedStatuses) { this.completedStatuses = completedStatuses; }
	
	private boolean sized = false;
	public boolean getSized() { return sized; }
	public void setSized(boolean sized) { this.sized = sized; }
	
	public static Config load(Path file) throws Exception {
		String contents = Files.readString(file);
		
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return om.readValue(contents, Config.class);
	}
}
