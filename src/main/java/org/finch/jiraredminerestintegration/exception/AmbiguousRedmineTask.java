package org.finch.jiraredminerestintegration.exception;

public class AmbiguousRedmineTask extends RuntimeException {
    public AmbiguousRedmineTask(String jiraIssueId) {
        super(jiraIssueId);
    }
}
