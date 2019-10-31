package org.finch.jiraredminerestintegration.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.client.JiraClient;
import org.finch.jiraredminerestintegration.client.RedmineClient;
import org.finch.jiraredminerestintegration.model.JiraRedmineMapper;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.finch.jiraredminerestintegration.model.jira.JiraUser;
import org.finch.jiraredminerestintegration.model.jira.JiraWorkLog;
import org.finch.jiraredminerestintegration.model.redmine.RedmineWorkLog;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final PropertyClient propertyClient;
    private final MappingService mappingService;

    public void prepareAndUpdate() {
        Optional<Date> lastUpdate = propertyClient.getLastUpdate();

        Date now = new Date();

        if (lastUpdate.isEmpty()
                || Instant.now().toEpochMilli() - lastUpdate.get().toInstant().toEpochMilli() > 1000 * 60 * 60 * 12
        ) {
            log.warn("sync skip");
        } else {

            log.info("begin sync");

            syncIssues(lastUpdate.get());

            log.info("end sync. begin at: {}", now);
        }

        propertyClient.setLastUpdate(now);
    }


    private void syncIssues(Date lastUpdate) {
        log.info("sync from: {}", lastUpdate);
        UserMapping systemCred = credentialService.getSystemCred();

        jiraClient.searchUpdatedAfter(lastUpdate)
                .forEach(jiraIssue -> {
                    try {
                        log.info("handle {}", jiraIssue.getKey());

                        var assignee = Optional.ofNullable(jiraIssue.getFields().getAssignee())
                                .map(JiraUser::getKey)
                                .map(jiraUserKey -> userMappingService.getMapping(jiraUserKey))
                                .orElseGet(userMappingService::getSystem);

                        if (assignee.isPresent()) {

                            String jiraComments = mappingService.mapComments(jiraClient.getComments(jiraIssue.getKey()));

                            int redmineId = redmineClient.upsetTask(assignee.get(), jiraIssue, systemCred, jiraComments);
                            syncIssueWorkLog(jiraIssue.getKey(), redmineId);

                        } else {

                            log.debug("No mapping for user: {}. task missed: {}", jiraIssue.getFields().getAssignee(), jiraIssue.getKey());

                        }
                    } catch (RuntimeException e) {
                        log.error("Can't load {}", jiraIssue.getKey(), e);
                    }
                });
    }

    private void syncIssueWorkLog(String jiraIssueKey, int redmainIssueKey) {

        log.debug("syncIssueWorkLog jira: {}, redmine: {}", jiraIssueKey, redmainIssueKey);

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
                credentialService.getByJiraUser(jiraWorkLog.getAuthor().getKey())
                        .ifPresent(c -> redmineClient.createIssueWorkLog(
                                JiraRedmineMapper.mapWorkLog(jiraWorkLog, redmainIssueKey), c))

        );

    }

    private boolean timeLogIsEquals(RedmineWorkLog redmineWorkLog, JiraWorkLog jiraWorkLog) {
        return redmineWorkLog.getComments().contains(jiraWorkLog.getId())
                && JiraRedmineMapper.buildTimeLogComment(jiraWorkLog).equals(redmineWorkLog.getComments())
                && Math.abs(redmineWorkLog.getHours() * 60 * 60 - jiraWorkLog.getTimeSpentSeconds()) < 60;
    }


}
