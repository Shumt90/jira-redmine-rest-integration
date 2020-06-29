package org.finch.jiraredminerestintegration.client;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.exception.AmbiguousRedmineTask;
import org.finch.jiraredminerestintegration.exception.RedmineServerException;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.finch.jiraredminerestintegration.model.jira.JiraIssue;
import org.finch.jiraredminerestintegration.model.redmine.CreationRedmineTask;
import org.finch.jiraredminerestintegration.model.redmine.IssuePost;
import org.finch.jiraredminerestintegration.model.redmine.IssuePut;
import org.finch.jiraredminerestintegration.model.redmine.LogTimeDTO;
import org.finch.jiraredminerestintegration.model.redmine.OneIssue;
import org.finch.jiraredminerestintegration.model.redmine.RedmineTask;
import org.finch.jiraredminerestintegration.model.redmine.RedmineWorkLog;
import org.finch.jiraredminerestintegration.model.redmine.RedmineWorkLogs;
import org.finch.jiraredminerestintegration.model.redmine.SearchResult;
import org.finch.jiraredminerestintegration.model.redmine.TimeEntry;
import org.finch.jiraredminerestintegration.model.redmine.UpdateRedmineTask;
import org.finch.jiraredminerestintegration.service.MappingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedmineClient {

  private final RestTemplate client = new RestTemplateBuilder().errorHandler(new RedmineResponseErrorHandler()).build();
  private final ObjectMapper objectMapper;
  private final MappingService mappingService;

  @Value("#{'${app.redmine.base-url}'+'/issues/%s.json'}")
  private String issueUrl;

  @Value("#{'${app.redmine.base-url}'+'/search.json'}")
  private String searchUrl;

  @Value("#{'${app.redmine.base-url}'+'/issues.json'}")
  private String issueCreationUrl;

  @Value("#{'${app.redmine.base-url}'+'/issues/%s.json'}")
  private String issueUpdateUrl;

  @Value("#{'${app.redmine.base-url}'+'/time_entries.json'}")
  private String logTimeUrl;

  @Value("#{'${app.redmine.base-url}'+'/time_entries/%s.json'}")
  private String deleteTimeUrl;

  @SneakyThrows
  private Optional<RedmineTask> searchTask(String jiraIssueId, UserMapping credential) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(searchUrl)
        .queryParam("key", credential.getRedmineKey())
        .queryParam("limit", "100")
        .queryParam("offset", "0")
        .queryParam("q", jiraIssueId)
        .build();

    ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));

    SearchResult searchResult = objectMapper.readValue(responseEntity.getBody(), SearchResult.class);

    List<String> appropriated = new ArrayList<>();
    for (RedmineTask task : searchResult.getResults()) {
      if (task.getTitle().contains(jiraIssueId)) {

        appropriated.add(task.getId());

      }
    }

    if (appropriated.size() == 1) {
      return Optional.of(getOneTask(appropriated.get(0), credential));
    } else if (appropriated.size() > 1) {
      throw new AmbiguousRedmineTask(jiraIssueId);
    }

    return Optional.empty();
  }

  @SneakyThrows
  private RedmineTask getOneTask(String taskId, UserMapping credential) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(String.format(issueUrl, taskId))
        .queryParam("key", credential.getRedmineKey())
        .build();

    ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));
    OneIssue oneIssue = objectMapper.readValue(responseEntity.getBody(), OneIssue.class);

    return oneIssue.getIssue();

  }

  public int upsetTask(UserMapping assignee, JiraIssue jiraIssue, UserMapping credential, String jiraComments) {

    int redmineTaskId;

    Optional<RedmineTask> searchedTask = searchTask(jiraIssue.getKey(), credential);
    if (searchedTask.isPresent()) {

      redmineTaskId = updateTask(jiraIssue, searchedTask.get(), assignee, credential, jiraComments);

    } else {

      redmineTaskId = createNewTask(jiraIssue, assignee, credential, jiraComments);

    }

    return redmineTaskId;
  }

  @SneakyThrows
  private int updateTask(JiraIssue task, RedmineTask foundRedmineTaks, UserMapping assignee, UserMapping credential, String jiraComments) {

    if (!mappingService.taskEquals(task, assignee, foundRedmineTaks, jiraComments)) {

      UpdateRedmineTask redmineTask = mappingService.updateFieldMapping(task, assignee, jiraComments);

      IssuePut issueUpdate = IssuePut.builder()
          .issue(redmineTask).build();

      UriComponents uriComponents = UriComponentsBuilder.fromUriString(String.format(issueUpdateUrl, foundRedmineTaks.getId()))
          .queryParam("key", credential.getRedmineKey()).build();

      log.trace("update redmine issue {}, body: {}", foundRedmineTaks.getId(), issueUpdate);
      exchange(uriComponents, HttpMethod.PUT, new HttpEntity<>(issueUpdate));

      log.info(String.format("Task updated. jira: %s, redmine: %s", task.getKey(), foundRedmineTaks.getId()));
    } else {
      log.debug("Skip task because no change: {}", foundRedmineTaks.getId());
    }
    return Integer.parseInt(foundRedmineTaks.getId());

  }

  @SneakyThrows
  private int createNewTask(JiraIssue task, UserMapping assignee, UserMapping credential, String jiraComments) {

    CreationRedmineTask redmineTask = mappingService.createFieldMapping(task, assignee, jiraComments);

    IssuePost issueCretion = IssuePost.builder()
        .issue(redmineTask).build();

    UriComponents uriComponents = UriComponentsBuilder.fromUriString(issueCreationUrl)
        .queryParam("key", credential.getRedmineKey()).build();

    ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(issueCretion));

    IssuePost updatedRedmineTask = objectMapper.readValue(responseEntity.getBody(), IssuePost.class);

    log.info(String.format("Task created. jira: %s, redmine: %s", task.getKey(), updatedRedmineTask.getIssue().getId()));

    return Integer.parseInt(updatedRedmineTask.getIssue().getId());
  }


  private ResponseEntity<String> exchange(UriComponents uriComponents, HttpMethod method, HttpEntity httpEntity) {

    ResponseEntity<String> responseEntity = client.exchange(uriComponents.toUriString(), method, httpEntity, String.class);
    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      throw new RedmineServerException(String.format("get error when sent request. url: %s, method: %s, entity: %s, body: %s", uriComponents.toUriString(), method, httpEntity, responseEntity.getBody()));
    }

    return responseEntity;
  }

  @SneakyThrows
  public List<RedmineWorkLog> getIssueWorkLog(int taskId, UserMapping userMapping) {

    int read = 0;
    int total;

    var result = new ArrayList<RedmineWorkLog>();

    do {
      UriComponents uriComponents = UriComponentsBuilder.fromUriString(logTimeUrl)
          .queryParam("key", userMapping.getRedmineKey())
          .queryParam("issue_id", taskId)
          .queryParam("offset", read)
          .build();

      ResponseEntity<String> responseEntity = exchange(uriComponents, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));

      var cur = objectMapper.readValue(responseEntity.getBody(), RedmineWorkLogs.class);

      if (cur.getTimeEntries() == null) {
        break;
      }

      result.addAll(cur.getTimeEntries());

      total = cur.getTotalCount();

      read += cur.getTimeEntries().size();

    } while (read < total);

    log.trace("get time log from redmine. count: {}, data: {}", result.size(), result);

    return result;

  }

  @SneakyThrows
  public void deleteIssueWorkLog(String workLogId, UserMapping credential) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(String.format(deleteTimeUrl, workLogId))
        .queryParam("key", credential.getRedmineKey()).build();

    exchange(uriComponents, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()));

    log.info("delete time entry {}", workLogId);

  }

  @SneakyThrows
  public void createIssueWorkLog(TimeEntry timeEntry, UserMapping credential) {
    if (isNull(credential.getRedmineKey())) {
      log.warn("Redmine key not set, skip time log. jira user: {}, task: {}", credential.getId(), timeEntry.getIssueId());
      return;
    }

    UriComponents uriComponents = UriComponentsBuilder.fromUriString(logTimeUrl)
        .queryParam("key", credential.getRedmineKey()).build();

    LogTimeDTO logTimeDTO = LogTimeDTO.builder().timeEntry(timeEntry).build();

    exchange(uriComponents, HttpMethod.POST, new HttpEntity<>(logTimeDTO));

    log.info("create time entry {}", timeEntry);

  }

}
