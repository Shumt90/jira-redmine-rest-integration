package org.finch.jiraredminerestintegration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("prod")
public class Task {
    private final JiraRedmineIntegration jiraRedmineIntegration;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void syncByUpdateDate() {
        jiraRedmineIntegration.prepareAndUpdate();
    }
}
