package org.finch.jiraredminerestintegration.model.redmine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
