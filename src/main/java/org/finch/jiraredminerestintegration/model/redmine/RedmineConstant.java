package org.finch.jiraredminerestintegration.model.redmine;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
public class RedmineConstant {
    @Id
    private String id;
    private String defaultProjectId;
    private String defaultTrackerId;
    private String defaultStatusId;
    private String defaultPriorityId;
    private List<IdNameValue> defaultCustomFields;
    private int defaultActivityId;

}
