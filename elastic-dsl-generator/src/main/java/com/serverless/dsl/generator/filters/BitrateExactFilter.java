package com.serverless.dsl.generator.filters;

import org.elasticsearch.index.query.FilterBuilder;


import static org.elasticsearch.index.query.FilterBuilders.termFilter;

public class BitrateExactFilter implements BitrateFilter {
	 public static final String AVAILABLE_ASSET_BITRATE  = "availableAssets.bitrate";
    private final int value;

    public BitrateExactFilter(int value) {
        this.value = value;
    }

    @Override
    public boolean matches(Integer bitrate) {
        return bitrate != null && bitrate == value;
    }

    @Override
    public FilterBuilder getFilter() {
        return termFilter(AVAILABLE_ASSET_BITRATE, value);
    }

    public int getValue() {
        return value;
    }
}
