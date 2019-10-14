package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.dao.UserMappingDTO;
import org.finch.jiraredminerestintegration.exception.SystemCredentialNotFound;
import org.finch.jiraredminerestintegration.exception.UserCredentialNotFound;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CredentialService {

    private UserMappingDTO userMappingDTO;

    UserMapping getSystemCred() {
        return userMappingDTO.getSystem().orElseThrow(SystemCredentialNotFound::new);
    }

    UserMapping getByJiraUser(String jiraUserID) {
        return userMappingDTO.findById(jiraUserID).orElseThrow(() -> new UserCredentialNotFound(jiraUserID));
    }
}
