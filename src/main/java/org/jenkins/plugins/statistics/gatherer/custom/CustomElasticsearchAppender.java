package org.jenkins.plugins.statistics.gatherer.custom;

import com.internetitem.logback.elasticsearch.ClassicElasticsearchPublisher;
import com.internetitem.logback.elasticsearch.ElasticsearchAppender;
import com.internetitem.logback.elasticsearch.config.Settings;

import java.io.IOException;

public class CustomElasticsearchAppender extends ElasticsearchAppender {

    public CustomElasticsearchAppender() {
    }

    public CustomElasticsearchAppender(Settings settings) {
        super(settings);
    }
    @Override
    protected ClassicElasticsearchPublisher buildElasticsearchPublisher() throws IOException {
        return new CustomClassicElasticsearchPublisher(this.getContext(), this.errorReporter, this.settings, this.elasticsearchProperties, this.headers);
    }
}
