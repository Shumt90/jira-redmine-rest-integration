package org.finch.jiraredminerestintegration.redmineclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.model.Task;
import org.finch.jiraredminerestintegration.model.User;
import org.finch.jiraredminerestintegration.model.redmine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.finch.jiraredminerestintegration.config.RedmineConstantConfig.REDMINE_CONSTANT;

@RequiredArgsConstructor
@Service
@Slf4j
public class Client {
    private final RestTemplate client = new RestTemplateBuilder().errorHandler(new RedmineResponseErrorHandler()).build();
    private final ObjectMapper objectMapper;

    @Value("#{'${app.redmine.base-url}'+'/issues.json'}")
    private String issueUrl;

    @Value("#{'${app.redmine.base-url}'+'/issues.json'}")
    private String issueCreationUrl;

    @Value("#{'${app.redmine.base-url}'+'/time_entries.json'}")
    private String logTimeUrl;

    @Value("${app.redmine.key-path}")
    private String keyFile;

    @Value("${app.redmine.user-id}")
    private String userId;

    @SneakyThrows
    public List<RedmineTask> getAllTask() {

        int offset = 0;

        List<RedmineTask> result = new ArrayList<>();

        while (true) {

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueUrl)
                    .queryParam("key", getKey())
                    .queryParam("userId", userId)
                    .queryParam("limit", "100")
                    .queryParam("offset", String.valueOf(offset))
                    .build();

            ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));

            Issue issue = objectMapper.readValue(responseEntity.getBody(), Issue.class);

            result.addAll(issue.getIssues());
            if (result.size() < issue.getTotalCount()) {
                offset = result.size();
            } else {
                break;
            }
        }

        return result;
    }


    @SneakyThrows
    private String getKey() {
        return Files.readString(Path.of(keyFile));
    }

    @NonNull
    @SneakyThrows
    public String createNewTask(User assignee, Task task) {

        CreationRedmineTask redmineTask = CreationRedmineTask.builder()
                .projectId(REDMINE_CONSTANT.getDefaultProjectId())
                .statusId(REDMINE_CONSTANT.getDefaultStatusId())
                .priorityId(REDMINE_CONSTANT.getDefaultPriorityId())
                .trackerId(REDMINE_CONSTANT.getDefaultTrackerId())
                .customFields(REDMINE_CONSTANT.getDefaultCustomFields())
                .assignedToId(assignee.getRedmineId())
                .subject(String.format("%s %s", task.getId(), task.getTitle()))
                .build();

        IssueCretion issueCretion = IssueCretion.builder()
                .issue(redmineTask).build();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueCreationUrl)
                .queryParam("key", getKey()).build();

        ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(issueCretion));

        OneIssue issue = objectMapper.readValue(responseEntity.getBody(), OneIssue.class);

        log.info(String.format("Task created %s", issue.getIssue().getId()));
        return issue.getIssue().getId();
    }


    private ResponseEntity<String> exchange(UriComponents uriComponents, HttpMethod method, HttpEntity httpEntity) {

        ResponseEntity<String> responseEntity = client.exchange(uriComponents.toUriString(), method, httpEntity, String.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error(String.format("get error when sent request. url: %s, method: %s, entity: %s, body: %s", uriComponents.toUriString(), method, httpEntity, responseEntity.getBody()));
        }

        return responseEntity;
    }

}
