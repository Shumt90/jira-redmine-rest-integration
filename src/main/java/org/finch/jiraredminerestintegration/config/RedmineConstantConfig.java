package org.finch.jiraredminerestintegration.config;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.model.redmine.IdNameValue;
import org.finch.jiraredminerestintegration.model.redmine.RedmineConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@AllArgsConstructor
public class RedmineConstantConfig {
    public static RedmineConstant REDMINE_CONSTANT;

    @PostConstruct
    public void init() {

        REDMINE_CONSTANT = RedmineConstant.builder()
                .defaultProjectId("100")//Sport24 — Поддержка
                .defaultStatusId("23")//"В разработке"
                .defaultPriorityId("4")//"Нормальный"
                .defaultTrackerId("2")//"Улучшение"
                .defaultCustomFields(List.of(IdNameValue.builder().id("1").name("Важность").value("1").build()))
                .defaultActivityId(9)
                .build();
    }
}
