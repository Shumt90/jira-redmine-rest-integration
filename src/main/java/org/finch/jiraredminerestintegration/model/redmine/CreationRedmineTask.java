package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
public class CreationRedmineTask {
    @Id
    private String id;//
    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("tracker_id")
    private String trackerId;

    @JsonProperty("status_id")
    private String statusId;

    @JsonProperty("priority_id")
    private String priorityId;

    private String subject;
    private String description;

    @JsonProperty("assigned_to_id")
    private String assignedToId;

    @JsonProperty("author_id")
    private String authorId;

    @JsonProperty("custom_fields")
    private List<IdNameValue> customFields;

}
