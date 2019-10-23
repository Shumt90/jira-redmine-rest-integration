package org.finch.jiraredminerestintegration.oauth1Client;

import lombok.SneakyThrows;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class HttpClientProvider {

    @SneakyThrows
    public static void disableSslVerification() {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, getTrustingManager(), new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier());
    }

    private static TrustManager[] getTrustingManager() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

        }};
    }

    private static HostnameVerifier hostnameVerifier() {
        return new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
}
