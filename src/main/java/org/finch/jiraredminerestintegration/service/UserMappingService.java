package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserMappingService {
    Optional<UserMapping> getMapping(String key) {

        return Optional.of(UserMapping.builder().id(key).redmineId("310").build());
    }
}
