package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.finch.jiraredminerestintegration.model.Property;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
public class Task {
    private final JiraRedmineIntegration jiraRedmineIntegration;
    private final PropertyClient propertyClient;

    @Scheduled(fixedDelay = 1000 * 10)
    public void syncByUpdateDate() {
        Property property = propertyClient.getMain();

        Date lastUpdate = property.getLastUpdate();
        property.setLastUpdate(new Date());

        if (isNull(lastUpdate)
                || Instant.now().toEpochMilli() - lastUpdate.toInstant().toEpochMilli() > 1000 * 60 * 60 * 12
        ) {
            log.warn("sync skip");
        } else {

            log.info("begin sync");
            property.setLastUpdate(new Date());
            propertyClient.setProperty(property);
            jiraRedmineIntegration.syncIssues(lastUpdate);

            log.info("end sync");
        }

        propertyClient.setProperty(property);
    }
}
