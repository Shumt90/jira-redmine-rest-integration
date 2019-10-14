package org.finch.jiraredminerestintegration.model.jira;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class JiraWorkLog {
    private JiraUser author;
    private String comment;
    private int timeSpentSeconds;
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime started;
}
