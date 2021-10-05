package com.lagovistatech.burnjira;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.lagovistatech.rest.AcceptHeader;
import com.lagovistatech.rest.AuthorizationHeader;
import com.lagovistatech.rest.ContentTypeHeader;
import com.lagovistatech.rest.JsonContentType;
import com.lagovistatech.rest.JsonRequest;
import com.lagovistatech.rest.JsonResponse;
import com.lagovistatech.rest.Method;
import com.lagovistatech.rest.Nothing;

public class Jira {
	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	private String user;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	private String password;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public List<Issue> search(String jql) throws Exception {
		HashMap<String, SearchIssue> issuesByKey = new HashMap<>();
		
		SearchResults results = searchFetch(jql, 0);
		int requireIssues = results.getTotal();
		for(SearchIssue issue : results.getIssues())
			issuesByKey.put(issue.getKey(), issue);
		
		while(issuesByKey.size() < requireIssues) {
			int lastSize = issuesByKey.size();
			
			results = searchFetch(jql, issuesByKey.size());
			for(SearchIssue issue : results.getIssues())
				issuesByKey.put(issue.getKey(), issue);
			
			if(lastSize == issuesByKey.size())
				throw new Exception("When requesting more search results, not unseen records were returned!");
		}
		
		LinkedList<Issue> ret = new LinkedList<>();
		for(SearchIssue issue : issuesByKey.values())
			ret.add(this.loadIssue(issue.getKey()));
		
		return ret;
	}
	private static final int SEARCH_MAX_RESULTS = 1000;
	private SearchResults searchFetch(String jql, int offset) throws Exception {
		LinkedList<String> fields = new LinkedList<>();
		fields.add("key");
		
		SearchRequest query = new SearchRequest();
		query.setJql(jql);
		query.setFields(fields);
		query.setMaxResults(SEARCH_MAX_RESULTS);
		query.setStartAt(offset);
		
		JsonRequest<SearchRequest, SearchResults> client = new JsonRequest<>(SearchResults.class);
		client.setUrl(url + "/api/3/search");
		client.setHeader(new AuthorizationHeader(user, password));
		client.setHeader(new ContentTypeHeader(JsonContentType.instance));
		client.setHeader(new AcceptHeader(JsonContentType.instance));
		client.setMethod(Method.POST);
		client.setBody(query);
		
		JsonResponse<SearchResults> resp = client.send();
		return resp.getBody();		
	}

	public Issue loadIssue(String key) throws Exception {
		JsonRequest<Nothing, Issue> client = new JsonRequest<>(Issue.class);
		client.setUrl(url + "/api/3/issue/" + key + "?fields=created,summary,timeestimate&expand=changelog");
		client.setHeader(new AuthorizationHeader(user, password));
		client.setHeader(new ContentTypeHeader(JsonContentType.instance));
		client.setHeader(new AcceptHeader(JsonContentType.instance));
		client.setMethod(Method.GET);
		
		JsonResponse<Issue> resp = client.send();
		return resp.getBody();		
	}
}