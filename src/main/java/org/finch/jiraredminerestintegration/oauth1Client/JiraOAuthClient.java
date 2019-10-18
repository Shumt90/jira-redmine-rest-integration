package org.finch.jiraredminerestintegration.oauth1Client;

import com.google.api.client.auth.oauth.OAuthParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;



@Service
public class JiraOAuthClient {

    private final JiraOAuthTokenFactory oAuthGetAccessTokenFactory;

    public JiraOAuthClient(@Value("#{'${app.jira.base-url}'}") String jiraBaseUrl) {
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
