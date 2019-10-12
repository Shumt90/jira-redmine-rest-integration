package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class RedmineTask {
    @Id
    private String id;//
    private String subject;
    private IdNameValue project;
    private IdNameValue tracker;
    private IdNameValue status;
    private IdNameValue priority;
    private IdNameValue author;

    @JsonProperty("assigned_to")
    private IdNameValue assignedTo;

    private String description;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("done_ratio")
    private String doneRatio;

    @JsonProperty("custom_fields")
    private List<IdNameValue> customFields;

}
