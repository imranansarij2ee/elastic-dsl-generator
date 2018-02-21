package com.serverless.dsl.generator.filters;

import org.elasticsearch.index.query.FilterBuilder;

public class BitrateAnyFilter implements BitrateFilter {
	   public boolean matches(Integer bitrate) {
	        return true;
	    }

	    @Override
	    public FilterBuilder getFilter() {
	        return null;
	    }
}
