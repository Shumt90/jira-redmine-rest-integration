package org.finch.jiraredminerestintegration.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.model.jira.JiraIssue;
import org.finch.jiraredminerestintegration.model.jira.JiraWorkLog;
import org.finch.jiraredminerestintegration.model.jira.SearchResult;
import org.finch.jiraredminerestintegration.oauth1Client.OAuthClient;
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

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/latest/project'}")
    private String projectUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/search?jql='}")
    private String searchUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/issue/'}")
    private String issueUrl;

    @Value("#{'${app.jira.base-url}'+'/rest/api/2/issue/%s/worklog'}")
    private String issueWorkLogUrl;

    @SneakyThrows
    public boolean isConnected() {
        HttpResponse httpResponse = authClient.handleGetRequest(projectUrl);
        JsonNode jsonNode = objectMapper.readTree(httpResponse.parseAsString());
        return jsonNode.isArray();
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

        return objectMapper.readValue(resp, JiraIssue.class);

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
