package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

import java.util.List;

@Data
public class Status {
    private String name;
    private StatusCategory statusCategory;
    private List<Status> statuses;
}
