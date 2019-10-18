package org.finch.jiraredminerestintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.finch.jiraredminerestintegration.client.JiraClient;
import org.finch.jiraredminerestintegration.client.RedmineClient;
import org.finch.jiraredminerestintegration.service.JiraRedmineIntegration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JiraRedmineRestIntegrationApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedmineClient client;

    @Autowired
    private JiraClient jiraClient;

    @Autowired
    private JiraRedmineIntegration jiraRedmineIntegration;

    @Test
    @SneakyThrows
    public void connectionTest() {

        assertTrue(jiraClient.isConnected());

    }

   /* @Test
    @SneakyThrows
    public void getWorkLog() {

        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/plugins/servlet/tempo-getWorklog/?dateFrom=2011-01-01&dateTo=2011-01-31&format=xml&diffOnly=false&tempoApiToken=give-me-token");


        JsonNode jsonNode = objectMapper.readTree(httpResponse.parseAsString());


        assertTrue(jsonNode.isArray());


    }*/

/*    @Test
    @SneakyThrows
    public void getJiraWorkLog() {

        HttpResponse httpResponse = authClient.handleGetRequest("http://jira.stoloto.ru/rest/api/2/worklog/updated?since=1570684240");


        WorkLogList workLogList = objectMapper.readValue(httpResponse.parseAsString(), WorkLogList.class);


    }*/

    @Test
    @SneakyThrows
    public void sync() {

        Instant date = Instant.now().minus(1, ChronoUnit.HOURS);

        jiraRedmineIntegration.syncIssues(Date.from(date));

    }

    @Test
    @SneakyThrows
    public void issue() {


        jiraClient.getIssue("S24-1589");

    }



    @Test
    @SneakyThrows
    public void upsertTask() {


        System.out.println(URLDecoder.decode("updated%20>%20%272019%2F10%2F14%2001%3A52%27", StandardCharsets.UTF_8));
        System.out.println(URLDecoder.decode("updated+>+%272019%2F10%2F14+01%3A52%27", StandardCharsets.UTF_8));
        System.out.println(URLEncoder.encode("updated > '2019/10/14 01:52'", StandardCharsets.UTF_8));
        System.out.println(URLEncoder.encode("updated > '2019/10/14 01:52'", StandardCharsets.UTF_16));

    }

    @Test
    @SneakyThrows
    public void getWorkLog() {

        System.out.println(jiraClient.getIssueWorkLog("S24-969"));

    }


    @Test
    @SneakyThrows
    public void syncIssueWorkLog() {

        jiraRedmineIntegration.syncIssueWorkLog("S24-1588", 23252);

    }

}
