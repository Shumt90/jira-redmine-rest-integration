package org.finch.jiraredminerestintegration;

import org.finch.jiraredminerestintegration.oauth1Client.HttpClientProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class JiraRedmineRestIntegrationApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        HttpClientProvider.disableSslVerification();
        SpringApplication.run(JiraRedmineRestIntegrationApplication.class, args);
    }

}
