package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

@Data
public class JiraIssue {
    private String key;
    private IssueFields fields;
}
