package org.finch.jiraredminerestintegration.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.client.JiraClient;
import org.finch.jiraredminerestintegration.client.RedmineClient;
import org.finch.jiraredminerestintegration.model.JiraRedmineMapper;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.finch.jiraredminerestintegration.model.jira.JiraWorkLog;
import org.finch.jiraredminerestintegration.model.redmine.RedmineWorkLog;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class JiraRedmineIntegration {
    private final JiraClient jiraClient;
    private final RedmineClient redmineClient;
    private UserMappingService userMappingService;
    private CredentialService credentialService;


    public void syncIssues(Date lastUpdate) {
        log.info("sync from: {}", lastUpdate);
        UserMapping systemCred = credentialService.getSystemCred();

        jiraClient.searchUpdatedAfter(lastUpdate).forEach(jiraIssue -> {

            String jiraUserKey = jiraIssue.getFields().getAssignee().getKey();
            Optional<UserMapping> userMapping = userMappingService.getMapping(jiraUserKey);

            if (userMapping.isPresent()) {

                int redmineId= redmineClient.upsetTask(userMapping.get(), jiraIssue, systemCred);
                syncIssueWorkLog(jiraIssue.getKey(),redmineId);

            } else {

                log.debug("No mapping for user: {}", jiraUserKey);

            }
        });
    }

    public void syncIssueWorkLog(String jiraIssueKey, int redmainIssueKey) {

        UserMapping systemCred = credentialService.getSystemCred();

        List<JiraWorkLog> jiraWorkLogs = jiraClient.getIssueWorkLog(jiraIssueKey);
        List<RedmineWorkLog> redmineWorkLogs = redmineClient.getIssueWorkLog(redmainIssueKey, systemCred);

        List<RedmineWorkLog> forDelete = new ArrayList<>();
        List<JiraWorkLog> forCreation = new ArrayList<>();

        redmineWorkLogs.forEach(redmineWorkLog -> {
            boolean found = false;
            for (JiraWorkLog jiraWorkLog : jiraWorkLogs) {
                if (timeLogIsEquals(redmineWorkLog, jiraWorkLog)) {
                    found = true;
                }
            }
            if (!found) {
                forDelete.add(redmineWorkLog);
            }
        });

        jiraWorkLogs.forEach(jiraWorkLog -> {
            boolean found = false;
            for (RedmineWorkLog redmineWorkLog : redmineWorkLogs) {
                if (timeLogIsEquals(redmineWorkLog, jiraWorkLog)) {
                    found = true;
                }
            }
            if (!found) {
                forCreation.add(jiraWorkLog);
            }
        });
        forDelete.stream().map(RedmineWorkLog::getId).forEach(redmineWorkLogId -> redmineClient.deleteIssueWorkLog(redmineWorkLogId, systemCred));
        forCreation.forEach(jiraWorkLog ->
                redmineClient.createIssueWorkLog(
                        JiraRedmineMapper.mapWorkLog(jiraWorkLog, redmainIssueKey),
                        credentialService.getByJiraUser(jiraWorkLog.getAuthor().getKey())));

    }

    private boolean timeLogIsEquals(RedmineWorkLog redmineWorkLog, JiraWorkLog jiraWorkLog) {
        return redmineWorkLog.getComments().contains(jiraWorkLog.getId())
                && JiraRedmineMapper.buildTimeLogComment(jiraWorkLog).equals(redmineWorkLog.getComments())
                && Math.abs(redmineWorkLog.getHours() * 60 * 60 - jiraWorkLog.getTimeSpentSeconds()) < 60;
    }




}
