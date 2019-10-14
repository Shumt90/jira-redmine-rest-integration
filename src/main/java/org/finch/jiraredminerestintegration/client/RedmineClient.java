package org.finch.jiraredminerestintegration.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.finch.jiraredminerestintegration.model.jira.JiraIssue;
import org.finch.jiraredminerestintegration.model.redmine.CreationRedmineTask;
import org.finch.jiraredminerestintegration.model.redmine.IssuePostPut;
import org.finch.jiraredminerestintegration.model.redmine.RedmineTask;
import org.finch.jiraredminerestintegration.model.redmine.SearchResult;
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

import java.nio.file.Files;
import java.nio.file.Path;
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

    @Value("${app.redmine.key-path}")
    private String keyFile;

    @SneakyThrows
    public Optional<RedmineTask> searchTask(String jiraIssueId) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(searchUrl)
                .queryParam("key", getKey())
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


    @SneakyThrows
    private String getKey() {
        return Files.readString(Path.of(keyFile)).replace("\n", "");
    }

    public void upsetTask(UserMapping assignee, JiraIssue jiraIssue) {

        Optional<RedmineTask> searchedTask = searchTask(jiraIssue.getKey());
        if (searchedTask.isPresent()) {

            updateTask(jiraIssue, assignee);

        } else {

            createNewTask(jiraIssue, assignee);

        }
    }

    @SneakyThrows
    private void updateTask(JiraIssue task, UserMapping assignee) {

        CreationRedmineTask redmineTask = fieldMapping(task, assignee);

        IssuePostPut issueUpdate = IssuePostPut.builder()
                .issue(redmineTask).build();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueCreationUrl)
                .queryParam("key", getKey()).build();

        exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(issueUpdate));

        log.info(String.format("Task updated %s", task.getKey()));

    }

    @SneakyThrows
    private void createNewTask(JiraIssue task, UserMapping assignee) {

        CreationRedmineTask redmineTask = fieldMapping(task, assignee);

        IssuePostPut issueCretion = IssuePostPut.builder()
                .issue(redmineTask).build();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueCreationUrl)
                .queryParam("key", getKey()).build();

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

}
