package org.finch.jiraredminerestintegration.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finch.jiraredminerestintegration.dao.PropertyDAO;
import org.finch.jiraredminerestintegration.model.Property;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.finch.jiraredminerestintegration.model.DataFormat.DATE_FORMAT;

@Slf4j
@Service
@AllArgsConstructor
public class PropertyClient {
    private static String LAST_UPDATE = "last-update";
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String PRIVATE_KEY = "private_key";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SECRET = "secret";
    public static final String JIRA_HOME = "jira_home";
    private PropertyDAO propertyDAO;

    @SneakyThrows
    Optional<Date> getLastUpdate() {
        Optional<String> sDate = propertyDAO.findById(LAST_UPDATE).map(Property::getValue);

        if (sDate.isPresent()) {
            return Optional.of(DATE_FORMAT.parse(sDate.get()));
        } else {
            return Optional.empty();
        }
    }


    void setLastUpdate(Date date) {
        Property property = propertyDAO.findById(LAST_UPDATE).orElseGet(() -> Property.of(LAST_UPDATE));
        property.setValue(DATE_FORMAT.format(date));
        propertyDAO.save(property);
    }

    public Map<String, String> getProperties() {
        return propertyDAO.findByIdIn(List.of(CONSUMER_KEY, PRIVATE_KEY, REQUEST_TOKEN, ACCESS_TOKEN, SECRET, JIRA_HOME))
                .stream().collect(Collectors.toMap(Property::getId, Property::getValue));
    }
}
