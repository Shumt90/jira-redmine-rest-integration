package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

@Data
public class WorkLogListElement {
    private String worklogId;
    private long updatedTime;
}
