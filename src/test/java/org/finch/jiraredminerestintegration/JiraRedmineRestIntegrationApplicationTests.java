package org.finch.jiraredminerestintegration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpResponse;
import lombok.SneakyThrows;
import org.finch.jiraredminerestintegration.oauth1Client.OAuthClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}
