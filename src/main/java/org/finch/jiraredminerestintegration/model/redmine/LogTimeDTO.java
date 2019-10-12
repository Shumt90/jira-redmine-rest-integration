package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LogTimeDTO {
    @JsonProperty("time_entry")
    private TimeEntry timeEntry;
}
