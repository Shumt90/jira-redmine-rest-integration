package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.dao.UserMappingDAO;
import org.finch.jiraredminerestintegration.exception.SystemCredentialNotFound;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CredentialService {

    private UserMappingDAO userMappingDAO;

    public UserMapping getSystemCred() {
        return userMappingDAO.getSystem().orElseThrow(SystemCredentialNotFound::new);
    }

    Optional<UserMapping> getByJiraUser(String jiraUserID) {
        return userMappingDAO.findById(jiraUserID);
    }
}
