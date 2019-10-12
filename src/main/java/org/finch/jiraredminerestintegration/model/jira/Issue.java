package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

@Data
public class Issue {
    private String key;
    private IssueFields fields;
}
