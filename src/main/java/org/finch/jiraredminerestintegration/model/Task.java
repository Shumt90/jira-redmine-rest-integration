package org.finch.jiraredminerestintegration.model;

import lombok.*;
import org.springframework.data.annotation.Id;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Task {
    @Id
    private String id;//
    private String title;
    private String body;
    private String reporter;
    private String assignee;
    private String redmineId;
}
