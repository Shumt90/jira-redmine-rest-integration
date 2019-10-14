package org.finch.jiraredminerestintegration.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class UserMapping {
    @Id
    private String id;
    private String redmineId;
    private String redmineKey;
}
