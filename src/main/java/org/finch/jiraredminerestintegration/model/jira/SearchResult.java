package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private int startAt;
    private int maxResults;
    private int total;
    private List<JiraIssue> issues;
}
