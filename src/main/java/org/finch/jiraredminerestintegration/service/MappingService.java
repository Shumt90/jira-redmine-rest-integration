package org.finch.jiraredminerestintegration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.dao.StatusMappingDAO;
import org.finch.jiraredminerestintegration.model.StatusMapping;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.finch.jiraredminerestintegration.model.jira.JiraComment;
import org.finch.jiraredminerestintegration.model.jira.JiraIssue;
import org.finch.jiraredminerestintegration.model.jira.Status;
import org.finch.jiraredminerestintegration.model.redmine.CreationRedmineTask;
import org.finch.jiraredminerestintegration.model.redmine.RedmineTask;
import org.finch.jiraredminerestintegration.model.redmine.UpdateRedmineTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.finch.jiraredminerestintegration.config.JiraConstantConfig.JIRA_CONSTANT;
import static org.finch.jiraredminerestintegration.config.RedmineConstantConfig.REDMINE_CONSTANT;

@Service
@RequiredArgsConstructor
@Slf4j
public class MappingService {
    private final StatusMappingDAO statusMappingDAO;
    @Value("#{'${app.jira.base-url}'+'/browse/'}")
    private String issueLink;
    private static String LINK_PATTERN = "<a href=\".+?</a>";
    private static String URL_EXTRACTOR = "http.*\">";

    String mapComments(List<JiraComment> comments) {
        return comments.stream()
                .sorted(Comparator.comparing(JiraComment::getCreated))
                .map(this::mapComment)
                .collect(Collectors.joining("\n"));

    }

    private String mapComment(JiraComment jiraComment) {
        return String.format("-----------------------------------\n" +
                        "*From*: %s *at* %s\n>" +
                        "%s", jiraComment.getAuthor().getDisplayName(), DateTimeFormatter.RFC_1123_DATE_TIME.format(jiraComment.getCreated()),
                Stream.of(jiraComment.getBody().split("\n")).map(MappingService::toRedmineFormat).collect(Collectors.joining("\n>")));
    }

    public void writeStatuses(List<Status> statuses) {
        statuses.stream().flatMap(status -> status.getStatuses().stream()).map(Status::getStatusCategory).forEach(statusCategory -> {
                    StatusMapping statusMapping = statusMappingDAO.findById(statusCategory.getId()).orElseGet(StatusMapping::new);
                    statusMapping.setJiraId(statusCategory.getId());
                    statusMapping.setJiraName(statusCategory.getName());
                    statusMappingDAO.save(statusMapping);
                }
        );
    }

    private String status(JiraIssue jiraIssue) {
        return statusMappingDAO
                .findById(jiraIssue.getFields().getStatus().getStatusCategory().getId())
                .map(StatusMapping::getRedmineId)
                .orElseGet(REDMINE_CONSTANT::getDefaultStatusId);
    }

    public CreationRedmineTask createFieldMapping(JiraIssue jiraIssue, UserMapping assignee, String jiraComments) {

        return CreationRedmineTask.builder()
                .projectId(REDMINE_CONSTANT.getDefaultProjectId())
                .statusId(status(jiraIssue))
                .priorityId(REDMINE_CONSTANT.getDefaultPriorityId())
                .trackerId(REDMINE_CONSTANT.getDefaultTrackerId())
                .customFields(REDMINE_CONSTANT.getDefaultCustomFields())
                .assignedToId(assignee.getRedmineId())
                .description(description(jiraIssue.getFields().getDescription(), jiraComments, jiraIssue.getKey()))
                .subject(subject(jiraIssue))
                .build();
    }

    public UpdateRedmineTask updateFieldMapping(JiraIssue jiraIssue, UserMapping assignee, String jiraComments) {
        return UpdateRedmineTask.builder()
                .assignedToId(assignee.getRedmineId())
                .description(description(jiraIssue.getFields().getDescription(), jiraComments, jiraIssue.getKey()))
                .subject(subject(jiraIssue))
                .statusId(status(jiraIssue))
                .build();
    }


    public boolean taskEquals(JiraIssue jiraIssue, UserMapping assignee, RedmineTask foundRedmineTaks, String jiraComments) {

        log.trace("jiraIssue: {}, assignee: {}, foundRedmineTaks: {}, jiraComments: {}", jiraIssue,
                assignee,
                foundRedmineTaks,
                jiraComments);

        return foundRedmineTaks.getDescription()
                .replace("\n", "")
                .replace("\r", "")
                .equals(description(jiraIssue.getFields().getDescription(), jiraComments, jiraIssue.getKey())
                        .replace("\n", "")
                        .replace("\r", ""))
                &&
                foundRedmineTaks.getSubject().equals(subject(jiraIssue)) &&
                nonNull(foundRedmineTaks.getAssignedTo())
                && foundRedmineTaks.getAssignedTo().getId().equals(assignee.getRedmineId()) &&
                foundRedmineTaks.getStatus().getId().equals(status(jiraIssue));

    }

    private String description(String description, String comments, String jiraIssueKey) {
        return toRedmineFormat(issueLink +
                jiraIssueKey + "\n" +
                description + "\n" +
                comments)
                + "\n";
    }

    private static String toRedmineFormat(String in) {
        return unformattedSymbols(handleHref(handleSpan(in)));
    }

    private static String unformattedSymbols(String in) {
        return in.replace("<p>", "")
                .replace("</p>", "")
                .replace("<b>", "*\"")
                .replace("</b>", "\"*")
                .replace("<br>", "")
                .replace("</s>", "\"-")
                .replace("<s>", "-\"");
    }

    private static String handleHref(String inText) {

        String outText = inText;
        Matcher matcher = Pattern.compile(LINK_PATTERN).matcher(inText);

        while (matcher.find()) {
            String wrongFormat = inText.substring(matcher.start(), matcher.end());
            String rightFormat = wrongFormat
                    .replace("<a href=\"", "\"").
                            replace("\">", "\":")
                    .replace("</a>", "");

            outText = inText.replace(wrongFormat, rightFormat);

        }
        return outText;
    }

    private static String handleSpan(String inText) {
        String outText = inText;
        Matcher matcher = Pattern.compile("<span class=\"image-wrap\"><img src=\".+?</span>").matcher(inText);

        while (matcher.find()) {
            String spanBlok = inText.substring(matcher.start(), matcher.end());

            String newSpanBlok = spanBlok.replace("<span class=\"image-wrap\"><img src=\"", "");

            Matcher endSpanBlockPgn = Pattern.compile("^.+?.png").matcher(newSpanBlok);

            if (endSpanBlockPgn.find()) {
                newSpanBlok = newSpanBlok.substring(endSpanBlockPgn.start(), endSpanBlockPgn.end());
            }

            newSpanBlok = "!" + newSpanBlok + "!";
            outText = outText.replace(spanBlok, newSpanBlok);

        }

        return outText;
    }

    private String subject(JiraIssue jiraIssue) {
        return String.format("%s %s", jiraIssue.getKey(), jiraIssue.getFields().getSummary().replace(JIRA_CONSTANT.getDefaultProjectId(), ""));
    }

}
