package org.finch.jiraredminerestintegration.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.finch.jiraredminerestintegration.model.jira.JiraIssue;
import org.finch.jiraredminerestintegration.model.redmine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.finch.jiraredminerestintegration.config.JiraConstantConfig.JIRA_CONSTANT;
import static org.finch.jiraredminerestintegration.config.RedmineConstantConfig.REDMINE_CONSTANT;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedmineClient {
    private final RestTemplate client = new RestTemplateBuilder().errorHandler(new RedmineResponseErrorHandler()).build();
    private final ObjectMapper objectMapper;

    @Value("#{'${app.redmine.base-url}'+'/issues.json'}")
    private String issueUrl;

    @Value("#{'${app.redmine.base-url}'+'/search.json'}")
    private String searchUrl;

    @Value("#{'${app.redmine.base-url}'+'/issues.json'}")
    private String issueCreationUrl;

    @Value("#{'${app.redmine.base-url}'+'/time_entries.json'}")
    private String logTimeUrl;

    @Value("#{'${app.redmine.base-url}'+'/time_entries/%s.json'}")
    private String deleteTimeUrl;

    @Value("${app.redmine.key-path}")
    private String keyFile;

    @SneakyThrows
    public Optional<RedmineTask> searchTask(String jiraIssueId, UserMapping credential) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(searchUrl)
                .queryParam("key", credential.getRedmineKey())
                .queryParam("limit", "100")
                .queryParam("offset", "0")
                .queryParam("q", jiraIssueId)
                .build();

        ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));

        SearchResult searchResult = objectMapper.readValue(responseEntity.getBody(), SearchResult.class);

        for (RedmineTask task : searchResult.getResults()) {
            if (task.getTitle().contains(jiraIssueId)) {
                return Optional.of(task);
            }
        }

        return Optional.empty();
    }

    public void upsetTask(UserMapping assignee, JiraIssue jiraIssue, UserMapping credential) {

        Optional<RedmineTask> searchedTask = searchTask(jiraIssue.getKey(), credential);
        if (searchedTask.isPresent()) {

            updateTask(jiraIssue, assignee, credential);

        } else {

            createNewTask(jiraIssue, assignee, credential);

        }
    }

    @SneakyThrows
    private void updateTask(JiraIssue task, UserMapping assignee, UserMapping credential) {

        CreationRedmineTask redmineTask = fieldMapping(task, assignee);

        IssuePostPut issueUpdate = IssuePostPut.builder()
                .issue(redmineTask).build();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueCreationUrl)
                .queryParam("key", credential.getRedmineKey()).build();

        exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(issueUpdate));

        log.info(String.format("Task updated %s", task.getKey()));

    }

    @SneakyThrows
    private void createNewTask(JiraIssue task, UserMapping assignee, UserMapping credential) {

        CreationRedmineTask redmineTask = fieldMapping(task, assignee);

        IssuePostPut issueCretion = IssuePostPut.builder()
                .issue(redmineTask).build();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueCreationUrl)
                .queryParam("key", credential.getRedmineKey()).build();

        exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(issueCretion));


        log.info(String.format("Task created %s", task.getKey()));
    }

    private CreationRedmineTask fieldMapping(JiraIssue jiraIssue, UserMapping assignee) {
        return CreationRedmineTask.builder()
                .projectId(REDMINE_CONSTANT.getDefaultProjectId())
                .statusId(REDMINE_CONSTANT.getDefaultStatusId())
                .priorityId(REDMINE_CONSTANT.getDefaultPriorityId())
                .trackerId(REDMINE_CONSTANT.getDefaultTrackerId())
                .customFields(REDMINE_CONSTANT.getDefaultCustomFields())
                .assignedToId(assignee.getRedmineId())
                .description(jiraIssue.getFields().getDescription())
                .subject(String.format("%s %s", jiraIssue.getKey(), jiraIssue.getFields().getSummary().replace(JIRA_CONSTANT.getDefaultProjectId(), "")))
                .build();
    }

    private ResponseEntity<String> exchange(UriComponents uriComponents, HttpMethod method, HttpEntity httpEntity) {

        ResponseEntity<String> responseEntity = client.exchange(uriComponents.toUriString(), method, httpEntity, String.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error(String.format("get error when sent request. url: %s, method: %s, entity: %s, body: %s", uriComponents.toUriString(), method, httpEntity, responseEntity.getBody()));
        }

        return responseEntity;
    }

    @SneakyThrows
    public List<RedmineWorkLog> getIssueWorkLog(int taskId, UserMapping userMapping) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(logTimeUrl)
                .queryParam("key", userMapping.getRedmineKey())
                .queryParam("issue_id", taskId).build();

        ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));
        System.out.println(responseEntity.getBody());
        return objectMapper.readValue(responseEntity.getBody(), RedmineWorkLogs.class).getTimeEntries();

    }

    @SneakyThrows
    public void deleteIssueWorkLog(String workLogId, UserMapping credential) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(String.format(deleteTimeUrl, workLogId))
                .queryParam("key", credential.getRedmineKey()).build();

        exchange(uriComponents, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()));

        log.info("delete time entry {}", workLogId);

    }

    @SneakyThrows
    public void createIssueWorkLog(TimeEntry timeEntry, UserMapping credential) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(logTimeUrl)
                .queryParam("key", credential.getRedmineKey()).build();

        LogTimeDTO logTimeDTO = LogTimeDTO.builder().timeEntry(timeEntry).build();

        exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(logTimeDTO));

        log.info("create time entry {}", timeEntry);

    }

}
