package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogTimeDTO {
    @JsonProperty("time_entry")
    private TimeEntry timeEntry;
}
