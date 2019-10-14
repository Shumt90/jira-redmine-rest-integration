package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RedmineWorkLogs {
    @JsonProperty("time_entries")
    private List<RedmineWorkLog> timeEntries;
}
