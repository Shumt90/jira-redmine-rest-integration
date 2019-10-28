package org.finch.jiraredminerestintegration.model.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JiraConstant {
    private String defaultProjectId;

}
