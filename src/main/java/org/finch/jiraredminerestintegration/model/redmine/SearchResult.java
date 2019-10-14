package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private List<RedmineTask> results;
    @JsonProperty("total_count")
    private int totalCount;
    private int offset;
    private int limit;

}
