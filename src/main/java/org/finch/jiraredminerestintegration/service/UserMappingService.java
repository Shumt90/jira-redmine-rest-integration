package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.dao.UserMappingDTO;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserMappingService {
    private UserMappingDTO userMappingDTO;
    Optional<UserMapping> getMapping(String key) {
        return userMappingDTO.findById(key);
    }
}
