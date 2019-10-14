package org.finch.jiraredminerestintegration.model.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TimeEntry {

    @JsonProperty("issue_id")
    private int issueId;

    @JsonProperty("spent_on")
    private String spentOn;

    private float hours;

    @JsonProperty("activity_id")
    private int activityId;

    private String comments;
}
