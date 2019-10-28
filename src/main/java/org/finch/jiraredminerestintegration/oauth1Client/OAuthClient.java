package org.finch.jiraredminerestintegration.oauth1Client;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.finch.jiraredminerestintegration.service.PropertyClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.finch.jiraredminerestintegration.service.PropertyClient.*;

@Service
@AllArgsConstructor
public class OAuthClient {

    private final PropertyClient propertiesClient;
    private final JiraOAuthClient jiraOAuthClient;

    /**
     * Makes request to JIRA
     */
    @SneakyThrows
    public HttpResponse handleGetRequest(String url) {
        Map<String, String> properties = propertiesClient.getProperties();
        String tmpToken = properties.get(ACCESS_TOKEN);
        String secret = properties.get(SECRET);

        OAuthParameters parameters = jiraOAuthClient.getParameters(tmpToken, secret, properties.get(CONSUMER_KEY), properties.get(PRIVATE_KEY));
        return getResponseFromUrl(parameters, new GenericUrl(url));

    }

    /**
     * Authanticates to JIRA with given OAuthParameters and makes request to url
     */
    private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException, KeyManagementException, NoSuchAlgorithmException {

        NetHttpTransport transport = new NetHttpTransport.Builder().build();

        HttpRequestFactory requestFactory = transport.createRequestFactory(parameters);
        HttpRequest request = requestFactory.buildGetRequest(jiraUrl);
        request.setReadTimeout(1000 * 60 * 5);
        return request.execute();
    }
}
