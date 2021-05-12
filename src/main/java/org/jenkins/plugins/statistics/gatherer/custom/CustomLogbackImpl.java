package org.jenkins.plugins.statistics.gatherer.custom;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import org.jenkins.plugins.statistics.gatherer.StatisticsConfiguration;
import org.jenkins.plugins.statistics.gatherer.custom.CustomElasticsearchAppender;
import org.jenkins.plugins.statistics.gatherer.util.Logback;
import org.jenkins.plugins.statistics.gatherer.util.PropertyLoader;
import org.jenkins.plugins.statistics.gatherer.util.URLSha;
import org.slf4j.Marker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.jenkins.plugins.statistics.gatherer.StatisticsConfiguration.ELASTIC_URL_PROPERTY;

public class CustomLogbackImpl implements Logback {
    private Logger logger;
    private URLSha loggerSha;
    private String loggerName;

    @Override
    public Logback setLoggerName(String loggerName) {
        try {
            this.loggerName = loggerName;
            URL configurationUrl = getConfigurationURL();
            initLogger(configurationUrl, loggerName);
            return this;
        } catch (JoranException e) {
            throw new RuntimeException("Unable to configure logger", e);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to find LOGBack XML configuration", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to open LOGBack XML", e);
        }
    }

    @Override
    public String getLoggerName() {
        return loggerName;
    }

    public URLSha getLastSha() {
        return loggerSha;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private void initLogger(URL configurationUrl, String loggerName) throws JoranException, IOException {

        System.setProperty(ELASTIC_URL_PROPERTY, StatisticsConfiguration.get().getLogbackElasticUrl());

        LoggerContext loggerContext = new LoggerContext();

        ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
        contextInitializer.configureByResource(configurationUrl);
        this.loggerSha = new URLSha(configurationUrl);
        this.logger = loggerContext.getLogger(loggerName);
    }

    private URL getConfigurationURL() throws MalformedURLException {
        return getClass().getClassLoader().getResource("logback-elastic.xml");
    }

    @Override
    public void log(String msg) {
        if (logger != null) {
            logger.info(msg);
        }
    }

    @Override
    public void log(Marker marker, String msg) {
        if (logger != null) {
            logger.info(marker, msg);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean refresh() throws Exception {
        if (loggerSha == null) {
            return false;
        }

        URL configurationUrl = getConfigurationURL();
        URLSha latestSha1 = new URLSha(getConfigurationURL());
        if (!latestSha1.equals(loggerSha) || hasElasticUrlChanged()) {
            initLogger(configurationUrl, loggerName);
            return true;
        }

        return false;
    }

    private boolean hasElasticUrlChanged() {
        String currentElasticUrl = System.getProperty(ELASTIC_URL_PROPERTY);
        return currentElasticUrl != null && (! currentElasticUrl.equals(StatisticsConfiguration.get().getLogbackElasticUrl()));
    }
}
