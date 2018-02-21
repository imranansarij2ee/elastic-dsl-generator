package com.serverless.dsl.generator.helpers;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.Sort;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.IndicesQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class QueryBuilderHelper extends AbstractVideoSearchingRepository {
	protected Client elasticsearchClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress("http://52.5.90.133", 9200));
    private static final String RELEVANCY_SCORE = "_score";
    private static final String RELEVANCY = "relevancy";
    private static final String SORT_PROPERTY = ".sort";
    public static final String ETAG_FIELD = "etag";
    private static final String[] INDICES_CONTAINING_VIDEOS = ElasticSearchIndices.indices(ElasticSearchIndices.Type.VideoObject);
    private static final Map<String, String> TEXT_SORT_FIELDS = new HashMap<String, String>();

    private static final String HEADLINE_SORT_FIELD = "headline_sort";

    static {
        TEXT_SORT_FIELDS.put("headline", HEADLINE_SORT_FIELD);
        TEXT_SORT_FIELDS.put("source", "source".concat(SORT_PROPERTY));
        TEXT_SORT_FIELDS.put("publisher", "publisher".concat(SORT_PROPERTY));
        TEXT_SORT_FIELDS.put("servingSize", "servingSize".concat(SORT_PROPERTY));
    }
	
	public SearchRequestBuilder findByQuery(String[] types, String queryString, String filterQueryString) {
		String[] indices = ElasticSearchIndices.indices(types);
		SearchRequestBuilder searchRequestBuilder = prepareSearch(indices, types).setQuery(filteredQuery(queryString, filterQueryString));
		return searchRequestBuilder;
		
	}
	public SearchRequestBuilder findByQuery(String[] types, String queryString, String filterQueryString, String[] assetTypes) {
	    String[] indices = ElasticSearchIndices.indices(types);

        SearchRequestBuilder requestBuilder =
                prepareSearch(indices, types)
                        .setQuery(indicesQuery(queryString, filterQueryString, assetTypes));
		return requestBuilder;
		
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
    
    private IndicesQueryBuilder indicesQuery(String queryString, String filterQueryString, String[] assetTypes) {
        QueryBuilder noMatchQueryBuilder = filteredQuery(queryString, filterQueryString);
        QueryBuilder matchQueryBuilder = filteredQuery(queryString, filterQueryString, buildAssetTypeFilter(assetTypes));
        return new IndicesQueryBuilder(matchQueryBuilder, INDICES_CONTAINING_VIDEOS).noMatchQuery(noMatchQueryBuilder);
    }

}
