package org.finch.jiraredminerestintegration.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Property {
    @Id
    private String id;
    private Date lastUpdate;


}
