package org.finch.jiraredminerestintegration.api;

import lombok.RequiredArgsConstructor;
import org.finch.jiraredminerestintegration.service.JiraRedmineIntegration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Api {
    private final JiraRedmineIntegration jiraRedmineIntegration;

    @PostMapping("/sync")
    public void sync() {
        jiraRedmineIntegration.prepareAndUpdate();
    }
}
