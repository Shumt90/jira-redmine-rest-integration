package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RedmineWorkLog {
    private String id;

    @JsonProperty("spent_on")
    private String spentOn;

    private float hours;

    private String comments;

}
