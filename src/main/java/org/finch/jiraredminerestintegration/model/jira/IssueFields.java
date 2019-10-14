package org.finch.jiraredminerestintegration.model.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IssueFields {
    private String description;
    private String summary;
    private Status status;
    private JiraUser assignee;

    @JsonProperty("customfield_19407")
    private String allUpdate;

    private String updated;

}
