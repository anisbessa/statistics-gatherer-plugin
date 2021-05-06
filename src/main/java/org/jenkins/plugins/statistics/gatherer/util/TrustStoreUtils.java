package org.jenkins.plugins.statistics.gatherer.util;

import com.amazonaws.util.StringInputStream;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jenkins.plugins.statistics.gatherer.StatisticsConfiguration;
import org.jenkins.plugins.statistics.gatherer.model.build.BuildStats;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.jenkins.plugins.statistics.gatherer.util.RestClientUtil.APPLICATION_JSON;

public class TrustStoreUtils {
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT = "accept";
    public static final String CONTENT_TYPE = "Content-Type";

    public static void main(String[] args) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, KeyManagementException {
        TrustStoreUtils ts = new TrustStoreUtils();
        InputStream pemToTrustInputstream;
        String pem = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDFTCCAf2gAwIBAgIEH+gPiTANBgkqhkiG9w0BAQsFADA7MQswCQYDVQQGEwJG\n" +
                "UjEOMAwGA1UECBMFUGFyaXMxDjAMBgNVBAcTBVBhcmlzMQwwCgYDVQQDEwNqd3Qw\n" +
                "HhcNMjEwNTAzMTU1MDAyWhcNMjEwODAxMTU1MDAyWjA7MQswCQYDVQQGEwJGUjEO\n" +
                "MAwGA1UECBMFUGFyaXMxDjAMBgNVBAcTBVBhcmlzMQwwCgYDVQQDEwNqd3QwggEi\n" +
                "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCv4mq0F6r64zJg7SEV+MTut6gl\n" +
                "uuVIozpvbSKDTRZABKdN5JedNysNYS3tg3SDz7u8XboZJxyNMk/1pCzAbB0GYeh2\n" +
                "Gsv4y01BPStD5dw9jzpqtzgHgNhDDfjpI5cfl2Exk6OlS+39qOdkeVZZYndSrkvU\n" +
                "SjyDsEZAd2mdu93Xu9UkxUKvTe0Zk7bOxbWn0EU6zmaTmjg9GclC48p5qfH6WOoN\n" +
                "luDVDrvReo7q7Dksi5gUofRi/J3b0bK6u+GKaXWo1GEl1n6wJUTB61yRcQKizIbc\n" +
                "g9FfqfFlioZd+a0cgc6hAaDXSMcosPNJ4Zxi1yXbmCl5RcI06dW9slwmfT75AgMB\n" +
                "AAGjITAfMB0GA1UdDgQWBBT6ndaVKfrZl3uaSbXzD48iFUihLjANBgkqhkiG9w0B\n" +
                "AQsFAAOCAQEAROi2hPaqipeutwkYV81M8064np935UXvgfbvfereFWzPPHO5NgO/\n" +
                "pGaLi2dSxaeaeWbgQdSHgEbPZctxIxY/5ynCs1ycXCY9zkPXalZAtkaocvaZfFQv\n" +
                "m4os1OL1t+PgAoloB2sImYORqVTcYEj8pT3BrtNIUNtSgn2u1Kx6uhacq1J0KeIV\n" +
                "EBCTp0ocgRFHjepGyzuUyB9WVqx0Wi0iHu0j11QwmnH1lRqc0nBugHm5dlGCUXuY\n" +
                "/E68+LXWi2LR1mOPuDhPrHC1/OyV39aZ5HpdEC2xwtGWwxd2gp9JgpKbqgJau0Tk\n" +
                "rhToLJAziw90ePBi7VWnWhh0MPOTELssZQ==\n" +
                "-----END CERTIFICATE-----\n";

        pemToTrustInputstream = new StringInputStream(pem);
        //pemToTrustInputstream = new FileInputStream("/Users/anisbessa/dev/workspace/jenkins-monitoring/certificates/jwt.pem");
        ts.configureTrustStore(pemToTrustInputstream);
        BuildStats buildStats = new BuildStats();
        buildStats.setStartedUserId("anis");
        buildStats.setJobName("myjobname");
        //StatisticsConfiguration.get().setShouldSendApiHttpRequests(true);
        postToService2("https://localhost:9702", buildStats);
    }

    private static void postToService2(String url, BuildStats object) {
        try {
            String jsonToPost = JSONUtil.convertToJson(object);
            Unirest.post(url)
                    .header(ACCEPT, APPLICATION_JSON)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .body(jsonToPost)
                    .asJsonAsync(new Callback<JsonNode>() {

                        public void failed(UnirestException e) {
                            System.out.println();
                            LOGGER.log(Level.WARNING, "The request for url " + url + " has failed.", e);
                        }

                        public void completed(HttpResponse<JsonNode> response) {
                            int responseCode = response.getStatus();
                            System.out.println("ok");
                            LOGGER.log(Level.INFO, "The request for url " + url + " completed with status " + responseCode);
                        }

                        public void cancelled() {
                            LOGGER.log(Level.INFO, "The request for url " + url + " has been cancelled");
                        }

                    });
        } catch (Throwable e) {
            System.out.println(e);
        }
    }


    public void configureTrustStore(InputStream pemToTrust) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
            CertificateException, IOException {
        X509TrustManager jreTrustManager = getJreTrustManager();

        X509TrustManager myTrustManager = getMyTrustManagerFromCertificate(pemToTrust);

        X509TrustManager mergedTrustManager = createMergedTrustManager(jreTrustManager, myTrustManager);
        setSystemTrustManager(mergedTrustManager);
    }

    private X509TrustManager getJreTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        return findDefaultTrustManager(null);
    }

    private X509TrustManager getMyTrustManagerFromCertificate(InputStream pemToTrust) throws FileNotFoundException, KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException {
        //Put everything after here in your function.
        KeyStore keyStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);//Make an empty store


        //FileInputStream("/Users/anisbessa/dev/workspace/jenkins-monitoring/certificates/stackexchange.com.pem");
        BufferedInputStream bis = new BufferedInputStream(pemToTrust);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (bis.available() > 0) {
            Certificate cert = cf.generateCertificate(bis);
            keyStore.setCertificateEntry("custom-pem " + bis.available(), cert);
        }

        return findDefaultTrustManager(keyStore);
    }

    private X509TrustManager getMyTrustManagerFromKeystore() throws FileNotFoundException, KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException {
        // Adapt to load your keystore
        try (FileInputStream myKeys = new FileInputStream("truststore.jks")) {
            KeyStore myTrustStore = KeyStore.getInstance("jks");
            myTrustStore.load(myKeys, "password".toCharArray());

            return findDefaultTrustManager(myTrustStore);
        }
    }

    private X509TrustManager findDefaultTrustManager(KeyStore keyStore)
            throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore); // If keyStore is null, tmf will be initialized with the default trust store

        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        return null;
    }

    private X509TrustManager createMergedTrustManager(X509TrustManager jreTrustManager,
                                                      X509TrustManager customTrustManager) {
        return new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // If you're planning to use client-cert auth,
                // merge results from "defaultTm" and "myTm".
                return jreTrustManager.getAcceptedIssuers();
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    System.out.println("checking server trusted ... ");
                    customTrustManager.checkServerTrusted(chain, authType);
                } catch (CertificateException e) {
                    // This will throw another CertificateException if this fails too.
                    jreTrustManager.checkServerTrusted(chain, authType);
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // If you're planning to use client-cert auth,
                // do the same as checking the server.
                jreTrustManager.checkClientTrusted(chain, authType);
            }

        };
    }

    private void setSystemTrustManager(X509TrustManager mergedTrustManager)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { mergedTrustManager }, null);

        // You don't have to set this as the default context,
        // it depends on the library you're using.
        //SSLContext.setDefault(sslContext);
    }
}
