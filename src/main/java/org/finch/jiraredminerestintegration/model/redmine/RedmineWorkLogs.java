package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class RedmineWorkLogs {

    @JsonProperty("time_entries")
    private List<RedmineWorkLog> timeEntries;

    @JsonProperty("total_count")
    private int totalCount;
}
