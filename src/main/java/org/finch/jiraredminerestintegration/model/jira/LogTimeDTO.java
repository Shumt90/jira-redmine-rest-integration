package org.finch.jiraredminerestintegration.model.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LogTimeDTO {
    @JsonProperty("time_entry")
    private TimeEntry timeEntry;
}
