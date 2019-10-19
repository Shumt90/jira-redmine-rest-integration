package org.finch.jiraredminerestintegration.model.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class UpdateRedmineTask {
    @Id
    private String id;//
    private String subject;
    private String description;
    @JsonProperty("assigned_to_id")
    private String assignedToId;
    @JsonProperty("status_id")
    private String statusId;


}
