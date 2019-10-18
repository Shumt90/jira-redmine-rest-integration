package org.finch.jiraredminerestintegration.model.redmine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssuePost {
    private CreationRedmineTask issue;
}
