package com.serverless.dsl.generator.helpers;



import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;


public abstract class AbstractSearchingRepository {
	protected Client elasticsearchClient;

    private static final int HIT_SIZE = 10;
    private static final String RELEVANCY_SCORE = "_score";
    private static final String RELEVANCY = "relevancy";
    private static final String SORT_PROPERTY = ".sort";

    private static final Map<String, String> TEXT_SORT_FIELDS = new HashMap<String, String>();
    public static final String ETAG_FIELD = "etag";
    private static final String HEADLINE_SORT_FIELD = "headline_sort";

    static {
        TEXT_SORT_FIELDS.put("headline", HEADLINE_SORT_FIELD);
        TEXT_SORT_FIELDS.put("source", "source".concat(SORT_PROPERTY));
        TEXT_SORT_FIELDS.put("publisher", "publisher".concat(SORT_PROPERTY));
        TEXT_SORT_FIELDS.put("servingSize", "servingSize".concat(SORT_PROPERTY));
    }

    protected SearchRequestBuilder prepareSearch(String[] indices, String[] types) {
        SearchRequestBuilder searchRequestBuilder = elasticsearchClient.prepareSearch(indices)
                .setTypes(types)
                .setIndicesOptions(IndicesOptions.lenientExpandOpen());

        // etag is required for building cache key
        searchRequestBuilder.addField(ETAG_FIELD);

        return searchRequestBuilder;
    }

    protected QueryBuilder filteredQuery(String queryString, String filterQueryString) {
        QueryBuilder queryBuilder = queryString == null ? QueryBuilders.matchAllQuery()
                : QueryBuilders.queryStringQuery(queryString);
        return filterQueryString == null ? queryBuilder : QueryBuilders.filteredQuery(queryBuilder,
                FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(filterQueryString)));
    }
 
}
