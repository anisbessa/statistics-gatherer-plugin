package org.jenkins.plugins.statistics.gatherer.custom;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import com.fasterxml.jackson.core.JsonGenerator;
import com.internetitem.logback.elasticsearch.ClassicElasticsearchPublisher;
import com.internetitem.logback.elasticsearch.config.ElasticsearchProperties;
import com.internetitem.logback.elasticsearch.config.HttpRequestHeaders;
import com.internetitem.logback.elasticsearch.config.Settings;
import com.internetitem.logback.elasticsearch.util.ErrorReporter;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Marker;

import java.io.IOException;
import java.util.Iterator;

public class CustomClassicElasticsearchPublisher extends ClassicElasticsearchPublisher {
    public CustomClassicElasticsearchPublisher(Context context, ErrorReporter errorReporter, Settings settings, ElasticsearchProperties properties, HttpRequestHeaders headers) throws IOException {
        super(context, errorReporter, settings, properties, headers);
    }

    @Override
    protected void serializeCommonFields(JsonGenerator gen, ILoggingEvent event) throws IOException {
        super.serializeCommonFields(gen, event);
        writeLogstashMarkerIfNecessary(gen, event.getMarker());
    }

    private void writeLogstashMarkerIfNecessary(JsonGenerator generator, Marker marker) throws IOException {

        if (marker != null && marker instanceof LogstashMarker) {
            ((LogstashMarker) marker).writeTo(generator);


            if (marker.hasReferences()) {
                for (Iterator<?> i = marker.iterator(); i.hasNext();) {
                    Marker next = (Marker) i.next();
                    writeLogstashMarkerIfNecessary(generator, next);
                }
            }
        }
    }

}
