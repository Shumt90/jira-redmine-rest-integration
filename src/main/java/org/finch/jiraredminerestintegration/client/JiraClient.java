package org.finch.jiraredminerestintegration.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.model.jira.*;
import org.finch.jiraredminerestintegration.oauth1Client.OAuthClient;
import org.finch.jiraredminerestintegration.service.MappingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.finch.jiraredminerestintegration.model.DataFormat.DATE_FORMAT;


@RequiredArgsConstructor
@Service
@Slf4j
public class JiraClient {
    private final ObjectMapper objectMapper;
    private final OAuthClient authClient;
    private final MappingService mappingService;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/latest/project'}")
    private String projectUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/project/%s/statuses'}")
    private String projectStatuses;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/search?jql='}")
    private String searchUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/issue/'}")
    private String issueUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/issue/%s/worklog'}")
    private String issueWorkLogUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/issue/%s/comment?maxResults=500'}")
    private String issueWCommentsUrl;



    @SneakyThrows
    public boolean isConnected() {
        HttpResponse httpResponse = authClient.handleGetRequest(projectUrl);
        JsonNode jsonNode = objectMapper.readTree(httpResponse.parseAsString());
        return jsonNode.isArray();
    }

    @SneakyThrows
    public void initAllStatuses(String issueKey) {
        HttpResponse httpResponse = authClient.handleGetRequest(String.format(projectStatuses, issueKey));
        String resp = httpResponse.parseAsString();

        mappingService.writeStatuses(objectMapper.readValue(resp, new TypeReference<List<Status>>() {
        }));
    }

    @SneakyThrows
    public List<JiraIssue> searchUpdatedAfter(Date lastUpdate) {
        String jql = URLEncoder.encode("project=S24 AND updated > '" + DATE_FORMAT.format(lastUpdate) + "'", StandardCharsets.UTF_8);

        HttpResponse httpResponse = authClient.handleGetRequest(searchUrl + jql);

        String resp = httpResponse.parseAsString();

        SearchResult searchResult = objectMapper.readValue(resp, SearchResult.class);

        log.info("get {} task for update: {}",searchResult.getIssues().size(), searchResult.getIssues().stream().map(JiraIssue::getKey).collect(toList()));
        return searchResult.getIssues();
    }

    @SneakyThrows
    public JiraIssue getIssue(String issueKey) {
        HttpResponse httpResponse = authClient.handleGetRequest(issueUrl + issueKey);

        String resp = httpResponse.parseAsString();

        System.out.println(resp);

        return objectMapper.readValue(resp, JiraIssue.class);

    }

    @SneakyThrows
    public List<JiraComment> getComments(String issueKey) {
        HttpResponse httpResponse = authClient.handleGetRequest(String.format(issueWCommentsUrl, issueKey));

        String resp = httpResponse.parseAsString();
        return objectMapper
                .readValue(resp, SearchResult.class)
                .getComments();

    }

    @SneakyThrows
    public List<JiraWorkLog> getIssueWorkLog(String issueKey) {
        HttpResponse httpResponse = authClient.handleGetRequest(String.format(issueWorkLogUrl, issueKey));

        String resp = httpResponse.parseAsString();
        return objectMapper
                .readValue(resp, SearchResult.class)
                .getWorklogs();

    }
}
