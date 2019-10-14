package org.finch.jiraredminerestintegration.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.client.JiraClient;
import org.finch.jiraredminerestintegration.client.RedmineClient;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class JiraRedmineIntegration {
    private final JiraClient jiraClient;
    private final RedmineClient redmineClient;
    private UserMappingService userMappingService;

    public void syncIssue(Date lastUpdate) {
        jiraClient.searchUpdatedAfter(lastUpdate).forEach(jiraIssue -> {

            String jiraUserKey = jiraIssue.getFields().getAssignee().getKey();
            Optional<UserMapping> userMapping = userMappingService.getMapping(jiraUserKey);

            if (userMapping.isPresent()) {

                redmineClient.upsetTask(userMapping.get(), jiraIssue);

            } else {

                log.warn("No mapping for user: {}", jiraUserKey);

            }
        });
    }

}
