package org.finch.jiraredminerestintegration.oauth1Client;

import com.google.api.client.auth.oauth.OAuthParameters;
import org.springframework.stereotype.Service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.finch.jiraredminerestintegration.oauth1Client.PropertiesClient.JIRA_HOME;


@Service
public class JiraOAuthClient {

    private final JiraOAuthTokenFactory oAuthGetAccessTokenFactory;

    public JiraOAuthClient(PropertiesClient propertiesClient) {
        String jiraBaseUrl = propertiesClient.getProperties().get(JIRA_HOME);
        this.oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(jiraBaseUrl);
    }

    /**
     * Creates OAuthParameters used to make authorized request to JIRA
     */
    OAuthParameters getParameters(String tmpToken, String secret, String consumerKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, KeyManagementException {
        JiraOAuthGetAccessToken oAuthAccessToken = oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(tmpToken, secret, consumerKey, privateKey);
        return oAuthAccessToken.createParameters();
    }
}
