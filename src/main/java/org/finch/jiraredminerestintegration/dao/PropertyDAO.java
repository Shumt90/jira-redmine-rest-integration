package org.finch.jiraredminerestintegration.dao;

import org.finch.jiraredminerestintegration.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyDAO extends JpaRepository<Property, String> {

    List<Property> findByIdIn(List<String> ids);
}
