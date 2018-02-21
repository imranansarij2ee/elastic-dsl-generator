package com.serverless.dsl.generator.filters;

import org.elasticsearch.index.query.FilterBuilder;

public interface BitrateFilter {
    boolean matches(Integer bitrate);

    FilterBuilder getFilter();
}