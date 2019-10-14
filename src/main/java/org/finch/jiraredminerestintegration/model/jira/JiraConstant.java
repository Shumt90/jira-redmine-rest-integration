package org.finch.jiraredminerestintegration.model.jira;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JiraConstant {
    private String defaultProjectId;

}
