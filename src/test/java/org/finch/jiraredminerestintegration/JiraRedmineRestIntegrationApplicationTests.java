package org.finch.jiraredminerestintegration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpResponse;
import lombok.SneakyThrows;
import org.finch.jiraredminerestintegration.model.jira.Issue;
import org.finch.jiraredminerestintegration.model.jira.SearchResult;
import org.finch.jiraredminerestintegration.model.jira.WorkLogList;
import org.finch.jiraredminerestintegration.oauth1Client.OAuthClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JiraRedmineRestIntegrationApplicationTests {

    @Autowired
    private OAuthClient authClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void connectionTest() {

        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/rest/api/latest/project");


        JsonNode jsonNode = objectMapper.readTree(httpResponse.parseAsString());


        assertTrue(jsonNode.isArray());


    }

    @Test
    @SneakyThrows
    public void getWorkLog() {

        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/plugins/servlet/tempo-getWorklog/?dateFrom=2011-01-01&dateTo=2011-01-31&format=xml&diffOnly=false&tempoApiToken=give-me-token");


        JsonNode jsonNode = objectMapper.readTree(httpResponse.parseAsString());


        assertTrue(jsonNode.isArray());


    }

    @Test
    @SneakyThrows
    public void getJiraWorkLog() {

        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/rest/api/2/worklog/updated?since=1570684240");


        WorkLogList workLogList = objectMapper.readValue(httpResponse.parseAsString(), WorkLogList.class);


    }

    @Test
    @SneakyThrows
    public void search() {

        String jql = URLEncoder.encode("issuekey = S24-969", StandardCharsets.UTF_8);

        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/rest/api/2/search?jql=" + jql);

        String resp = httpResponse.parseAsString();

        SearchResult searchResult = objectMapper.readValue(resp, SearchResult.class);

        System.out.println(searchResult);

    }

    @Test
    @SneakyThrows
    public void issue() {


        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/rest/api/2/issue/S24-1589");

        String resp = httpResponse.parseAsString();

        System.out.println(resp);

        Issue issue = objectMapper.readValue(resp, Issue.class);

        System.out.println(issue);

    }

}
