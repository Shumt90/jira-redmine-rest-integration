package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Issues {
    private List<RedmineTask> issues;
    private Integer limit;
    private Integer offset;

    @JsonProperty("total_count")
    private Integer totalCount;

}
