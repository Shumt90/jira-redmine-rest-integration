package org.finch.jiraredminerestintegration.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class UserMapping {
    @Id
    private String id;
    private String redmineId;
}
