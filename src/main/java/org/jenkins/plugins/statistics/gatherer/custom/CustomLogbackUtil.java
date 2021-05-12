package org.jenkins.plugins.statistics.gatherer.custom;

import jenkins.model.Jenkins;
import org.jenkins.plugins.statistics.gatherer.StatisticsConfiguration;
import org.jenkins.plugins.statistics.gatherer.util.JSONUtil;
import org.jenkins.plugins.statistics.gatherer.util.Logback;
import org.jenkins.plugins.statistics.gatherer.util.LogbackFactory;
import org.jenkins.plugins.statistics.gatherer.util.PropertyLoader;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogbackUtil {
    public static final String STATISTICS_GATHERER_LOGGER = "statistics-gatherer";
    public static final String LOGBACK_PLUGIN_NAME = "logback-nats-appender";
    public static final CustomLogbackUtil INSTANCE = new CustomLogbackUtil();

    private static Logger logger = Logger.getLogger(CustomLogbackUtil.class.getName());

    private Logback logback;

    public static void info(Object object) {
        INSTANCE.logInfo(object);
    }

    public void logInfo(Object object) {
        if (PropertyLoader.getShouldSendToLogbackElastic() ) {
            try {
                if (logback == null) {
                    logback = CustomLogbackFactory.create(STATISTICS_GATHERER_LOGGER);
                }

                logger.log(Level.INFO, "sending object to elastic : " + object  );
                logback.log(new CustomObjectFieldsAppendingMarker(object), "message");
                //logback.log(JSONUtil.convertToJson(object));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Unable to find a valid implementation of Logback", e);
            }
        }
    }


    public Logback getLogback() {
        return logback;
    }
}
