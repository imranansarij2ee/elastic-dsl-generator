package com.serverless.dsl.generator.filters;

import org.elasticsearch.index.query.FilterBuilder;
import org.springframework.util.Assert;

import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;

public class BitrateRangeFilter implements BitrateFilter {
	public static final String AVAILABLE_ASSET_BITRATE  = "availableAssets.bitrate";
    private final Integer from;
    private final Integer to;

    public BitrateRangeFilter(Integer from, Integer to) {
        //At least one should not be null
        Assert.isTrue(from != null || to != null);

        //from should be <= to
        if (from != null && to != null) {
            Assert.isTrue(from <= to);
        }
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(Integer bitrate) {
        if (bitrate == null) {
            return false;
        }
        if (from != null && bitrate < from) {
            return false;
        }

        if (to != null && bitrate > to) {
            return false;
        }

        return true;
    }

    @Override
    public FilterBuilder getFilter() {
        return rangeFilter(AVAILABLE_ASSET_BITRATE)
                .gte(from)
                .lte(to);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}

