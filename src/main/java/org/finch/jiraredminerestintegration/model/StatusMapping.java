package org.finch.jiraredminerestintegration.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class StatusMapping {
    @Id
    private Integer jiraId;
    private String redmineId;
    private String jiraName;
    private String redmineName;
}
