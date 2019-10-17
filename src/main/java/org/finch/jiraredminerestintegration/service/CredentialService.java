package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.dao.UserMappingDAO;
import org.finch.jiraredminerestintegration.exception.SystemCredentialNotFound;
import org.finch.jiraredminerestintegration.exception.UserCredentialNotFound;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CredentialService {

    private UserMappingDAO userMappingDAO;

    UserMapping getSystemCred() {
        return userMappingDAO.getSystem().orElseThrow(SystemCredentialNotFound::new);
    }

    UserMapping getByJiraUser(String jiraUserID) {
        return userMappingDAO.findById(jiraUserID).orElseThrow(() -> new UserCredentialNotFound(jiraUserID));
    }
}
