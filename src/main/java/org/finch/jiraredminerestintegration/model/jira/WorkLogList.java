package org.finch.jiraredminerestintegration.model.jira;

import lombok.Data;

import java.util.List;

@Data
public class WorkLogList {
    private List<WorkLogListElement> values;
    private Boolean lastPage;
}
