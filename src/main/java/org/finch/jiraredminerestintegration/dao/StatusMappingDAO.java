package org.finch.jiraredminerestintegration.dao;

import org.finch.jiraredminerestintegration.model.StatusMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusMappingDAO extends JpaRepository<StatusMapping, Integer> {
}
