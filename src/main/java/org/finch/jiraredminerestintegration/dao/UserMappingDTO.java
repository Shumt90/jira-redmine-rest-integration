package org.finch.jiraredminerestintegration.dao;

import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserMappingDTO extends JpaRepository<UserMapping, String> {
    @Query("from  UserMapping um where um.id='system'")
    Optional<UserMapping> getSystem();

    Optional<UserMapping> getById(String id);
}
