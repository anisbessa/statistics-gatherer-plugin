package org.jenkins.plugins.statistics.gatherer.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.marker.ObjectFieldsAppendingMarker;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomObjectFieldsAppendingMarker

        extends ObjectFieldsAppendingMarker {
    private static Logger logger = Logger.getLogger(CustomObjectFieldsAppendingMarker.class.getName());

    private static ObjectMapper objectMapper = new ObjectMapper();
    public CustomObjectFieldsAppendingMarker(Object object) {
        super(object);
    }

    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        logger.log(Level.INFO, "enriching Marker ...");
        if(generator.getCodec() == null) {
            generator.setCodec(objectMapper);
        }

        super.writeTo(generator);
    }
}
