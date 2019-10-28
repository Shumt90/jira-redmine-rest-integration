package org.finch.jiraredminerestintegration.model.redmine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssuePut {
    private UpdateRedmineTask issue;
}
