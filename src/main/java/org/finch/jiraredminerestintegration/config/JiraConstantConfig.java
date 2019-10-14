package org.finch.jiraredminerestintegration.config;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.model.jira.JiraConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
public class JiraConstantConfig {
    public static JiraConstant JIRA_CONSTANT;

    @PostConstruct
    public void init() {

        JIRA_CONSTANT = JiraConstant.builder()
                .defaultProjectId("S24")//Sport24 — Поддержка
                .build();
    }
}
