package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
