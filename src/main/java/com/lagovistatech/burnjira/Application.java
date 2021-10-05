package com.lagovistatech.burnjira;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Application {
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private Config config;
	private Jira jira = new Jira();
	private HashSet<Integer> isoDatesAsInt = new HashSet<>();
	private HashMap<Integer, List<Issue>> issuesByCreated = new HashMap<>();
	private HashMap<Integer, List<Issue>> issuesByCompleted = new HashMap<>();
	private HashSet<String> statusesSeen = new HashSet<>();
	private boolean allHasTimeEstimate = true;
	
	public static void main(String[] args) {
		try {
			Application app = new Application();
			app.execute(args);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void execute(String[] args) throws Exception {
		System.out.println("Loading...");

		loadConfig(args);
		configJiraConnection();	
	
		System.out.println("Loading from Jira...");
		List<Issue> issues = jira.search(config.getSearchJql());
		List<Integer> issuesCreated = new ArrayList<>(issues.size());
		List<Integer> issuesCompleted = new ArrayList<>(issues.size());

		System.out.println("Computing...");	
		for(Issue issue : issues) {
			if(!(issue.getFields() != null && issue.getFields().getTimeestimate() > 0))
				allHasTimeEstimate = false;
			
			int createdDateAsInt = Integer.parseInt(dateFormatter.format(issue.getFields().getCreated()));
			isoDatesAsInt.add(createdDateAsInt);
			issuesCreated.add(createdDateAsInt);

			if(!issuesByCreated.containsKey(createdDateAsInt))
				issuesByCreated.put(createdDateAsInt, new LinkedList<>());
			
			issuesByCreated.get(createdDateAsInt).add(issue);
			
			Date completed = getCompleted(issue);
			if(completed != null) {
				int completedAsInt = Integer.parseInt(dateFormatter.format(completed));
				isoDatesAsInt.add(completedAsInt);
				issuesCompleted.add(completedAsInt);
				
				if(!issuesByCompleted.containsKey(completedAsInt))
					issuesByCompleted.put(completedAsInt, new LinkedList<>());	
					
				issuesByCompleted.get(completedAsInt).add(issue);
				
				if(!issuesByCreated.containsKey(completedAsInt))
					issuesByCreated.put(completedAsInt, new LinkedList<>());
			} else
				issuesCompleted.add(0);
		}
		
		System.out.println("Saving...");	
		String timeStamp = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date(Instant.now().toEpochMilli()));
		outputStatuses(timeStamp);
		outputDateTable(timeStamp);
		outputIssues(issues, issuesCreated, issuesCompleted, timeStamp);

		System.out.println("DONE!");	
	}
	private long countCompleted(Integer date) {
		long ret = 0;
		
		if(issuesByCompleted.containsKey(date)) {
			List<Issue> issues = issuesByCompleted.get(date);
			if(config.getSized() && allHasTimeEstimate)
				for(Issue issue : issues)
					ret += issue.getFields().getTimeestimate();
			else
				ret += issues.size();
		}
		
		return ret;
	}
	private long countTotal(Integer date) {
		long ret = 0;
		
		if(issuesByCreated.containsKey(date)) {
			List<Issue> issues = issuesByCreated.get(date);
			if(config.getSized() && allHasTimeEstimate)
				for(Issue issue : issues)
					ret += issue.getFields().getTimeestimate();
			else
				ret += issues.size();
		}
		
		return ret;
	}
	private String outputCsv(String value, boolean isString) {
		boolean quote = isString || value.contains(",");
		
		String out = value;
		if(quote)
			out = "\"" + out.replace("\"", "\"\"") + "\"";
		
		return out;
	}
	private void loadConfig(String[] args) throws Exception {
		if(args.length != 1)
			throw new Exception("You must provide a config file name!");
		
		config = Config.load(Paths.get(args[0]));
	}
	private Date getCompleted(Issue issue) {
		if(issue.getChangelog() == null)
			return null;
		if(issue.getChangelog().getHistories() == null)
			return null;
		
		long instance = 0;
		for(IssueChangelogHistory history : issue.getChangelog().getHistories()) {
			if(history.getCreated() == null || history.getItems() == null)
				continue;
			
			long possibleDate = history.getCreated().toInstant().toEpochMilli();
			if(possibleDate < instance)
				continue;
			
			for(IssueChangelogHistoryItem item : history.getItems())
				if(item.getField().equalsIgnoreCase("status")) {
					if(item.getFromString() != null)
						statusesSeen.add(item.getFromString());
					
					if(item.getToString() != null)
						statusesSeen.add(item.getToString());
					
					for(String doneStatus : config.getCopletedStatuses())
						if(item.getToString().equalsIgnoreCase(doneStatus))
							instance = possibleDate;
				}
		}
		
		if(instance > 0)
			return new Date(instance);
		else
			return null;
	}
	private void configJiraConnection() {
		jira.setUrl(config.getJiraRestUrl());
		jira.setUser(config.getUser());
		jira.setPassword(config.getPasswordKey());		
	}

	private void outputIssues(List<Issue> issues, List<Integer> createList, List<Integer> completedList, String timestamp) throws Exception {
		StringBuilder output = new StringBuilder();
		
		output.append("Key,Description,Created,Completed,Size" + System.lineSeparator());
		
		for(int cnt = 0; cnt < issues.size(); cnt++) {
			Issue issue = issues.get(cnt);

			String key = issue.getKey();
			output.append(outputCsv(key, true));
			output.append(",");

			String description = issue.getFields().getSummary();
			output.append(outputCsv(description, true));
			output.append(",");

			int created = createList.get(cnt);
			output.append(formatIntDate(created));
			output.append(",");

			int completed = completedList.get(cnt);
			if(completed > 0)
				output.append(formatIntDate(completed));
			else
				output.append("");
			output.append(",");

			long size = issue.getFields().getTimeestimate();
			output.append(size);

			output.append(System.lineSeparator());
		}
		
		Files.write(
			Paths.get(config.getProjectName() + " " + timestamp + " Issues.csv"), 
			output.toString().getBytes(), 
			StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
		);
	}
	private void outputDateTable(String timestamp) throws Exception {
		Integer[] dates = isoDatesAsInt.toArray(new Integer[0]);
		Arrays.sort(dates);
		
		StringBuilder output = new StringBuilder();
		
		if(config.getSized() && allHasTimeEstimate)
			output.append("Date,Size Total,Size Completed" + System.lineSeparator());
		else
			output.append("Date,Total Count,Completed Count" + System.lineSeparator());
		
		long total = 0;
		long completed = 0;
		for(Integer date : dates) {
			total += countTotal(date);
			completed += countCompleted(date);
			
			output.append(formatIntDate(date) + ",");
			output.append(total + ",");
			output.append(completed);
			
			output.append(System.lineSeparator());
		}
		
		Files.write(
			Paths.get(config.getProjectName() + " " + timestamp + " Table.csv"), 
			output.toString().getBytes(), 
			StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
		);
	}
	private String formatIntDate(Integer date) {		
		// YYYYMMDD
		// 01234567
		
		String dateString = date.toString();
		return dateString.substring(0, 4) + "-" + dateString.substring(4, 6) + "-" + dateString.substring(6);
	}

	private void outputStatuses(String timestamp) throws Exception {
		String[] statuses = statusesSeen.toArray(new String[0]);
		Arrays.sort(statuses);
		
		StringBuilder output = new StringBuilder();
			output.append("Statuses" + System.lineSeparator());
		for(String status : statuses)
			output.append(outputCsv(status, true) + System.lineSeparator());
		
		Files.write(
			Paths.get(config.getProjectName() + " " + timestamp + " Statuses.csv"), 
			output.toString().getBytes(), 
			StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
		);
	}
}
