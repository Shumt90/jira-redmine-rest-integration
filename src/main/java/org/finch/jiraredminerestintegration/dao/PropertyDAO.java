package org.finch.jiraredminerestintegration.dao;

import org.finch.jiraredminerestintegration.model.Property;
import org.finch.jiraredminerestintegration.model.UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PropertyDAO extends JpaRepository<Property, String> {
    @Query("from  Property p where p.id='main'")
    Optional<Property> getMain();

}
