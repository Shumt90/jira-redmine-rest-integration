package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

@Data
public class StatusCategory {
    private int id;
    private String name;
    private String key;
}
