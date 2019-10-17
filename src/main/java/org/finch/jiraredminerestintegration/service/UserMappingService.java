package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.dao.UserMappingDAO;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserMappingService {
    private UserMappingDAO userMappingDAO;
    Optional<UserMapping> getMapping(String key) {
        return userMappingDAO.findById(key);
    }
}
