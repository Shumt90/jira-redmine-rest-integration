package org.finch.jiraredminerestintegration.model;

import org.finch.jiraredminerestintegration.model.jira.JiraWorkLog;
import org.finch.jiraredminerestintegration.model.redmine.TimeEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.finch.jiraredminerestintegration.config.RedmineConstantConfig.REDMINE_CONSTANT;

public class JiraRedmineMapper {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static TimeEntry mapWorkLog(JiraWorkLog jiraWorkLog, int redmineIssueId) {
        return TimeEntry.builder()
                .activityId(REDMINE_CONSTANT.getDefaultActivityId())
                .comments(buildTimeLogComment(jiraWorkLog))
                .hours(jiraWorkLog.getTimeSpentSeconds() / 60.0 / 60.0)
                .spentOn(dtf.format(LocalDate.of(jiraWorkLog.getStarted().getYear(), jiraWorkLog.getStarted().getMonth(), jiraWorkLog.getStarted().getDayOfMonth())))
                .issueId(redmineIssueId)
                .build();

    }

    public static String buildTimeLogComment(JiraWorkLog jiraWorkLog) {
        return jiraWorkLog.getId() + "-" + jiraWorkLog.getComment();
    }
}
