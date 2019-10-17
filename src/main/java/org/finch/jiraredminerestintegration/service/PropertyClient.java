package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.dao.PropertyDAO;
import org.finch.jiraredminerestintegration.model.Property;
import org.springframework.stereotype.Service;

import javax.el.PropertyNotFoundException;

@Slf4j
@Service
@AllArgsConstructor
public class PropertyClient {
    private PropertyDAO propertyClient;

    Property getMain(){
        return propertyClient.getMain().orElseThrow(PropertyNotFoundException::new);
    }


    public void setProperty(Property property) {
        propertyClient.save(property);
    }
}
