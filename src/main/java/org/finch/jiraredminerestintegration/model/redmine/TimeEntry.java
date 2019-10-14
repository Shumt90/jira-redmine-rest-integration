package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeEntry {

    @JsonProperty("issue_id")
    private int issueId;

    @JsonProperty("spent_on")
    private String spentOn;

    private double hours;

    @JsonProperty("activity_id")
    private int activityId;

    private String comments;
}
