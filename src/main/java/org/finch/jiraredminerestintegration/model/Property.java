package org.finch.jiraredminerestintegration.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Property {
    @Id
    private String id;
    @Column(length = 1000)
    private String value;

    @Override
    public String toString() {
        return "Property{hidden}";
    }

    public static Property of(String id) {
        Property property = new Property();
        property.setId(id);
        return property;
    }

}
