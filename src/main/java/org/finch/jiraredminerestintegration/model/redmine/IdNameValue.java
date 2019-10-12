package org.finch.jiraredminerestintegration.model.redmine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdNameValue {
    private String id;
    private String name;
    private String value;
}
