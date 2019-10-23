package org.finch.jiraredminerestintegration;

import lombok.SneakyThrows;
import org.finch.jiraredminerestintegration.client.JiraClient;
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
    private JiraClient jiraClient;

    @Test
    @SneakyThrows
    public void connectionTest() {

        assertTrue(jiraClient.isConnected());

    }

}
