package com.serverless.dsl.generator.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.serverless.dsl.generator.filters.*;

import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.nestedFilter;
import static org.elasticsearch.index.query.FilterBuilders.orFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;

public abstract class AbstractVideoSearchingRepository extends AbstractSearchingRepository{
    public static final String AVAILABLE_ASSET_TYPES = "availableAssets.assetTypes";
    public static final String AVAILABLE_ASSET_BITRATE  = "availableAssets.bitrate";
    public static final String AVAILABLE_ASSETS_FIELD = "availableAssets";
    private static final List<String> blacklistedAssetTypes = Collections.emptyList();

    public static final VideoAssetTypeHelper videoAssetTypeHelper = new VideoAssetTypeHelper();

    protected FilteredQueryBuilder filteredQuery(String queryString, String filterQueryString, FilterBuilder... filters) {
        BoolFilterBuilder filterBuilder = boolFilter();

        if (filters != null) {
            Arrays.stream(filters)
                    .filter(Objects::nonNull)
                    .forEach(filterBuilder::must);
        }

        if (filterQueryString != null) {
            filterBuilder.must(FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(filterQueryString)));
        }

        QueryBuilder queryBuilder = queryString == null ? QueryBuilders.matchAllQuery() :
                QueryBuilders.queryStringQuery(queryString);

        return QueryBuilders.filteredQuery(queryBuilder, filterBuilder);
    }

    protected FilterBuilder buildAssetTypeFilter(String[] assetTypes) {
        if (ArrayUtils.isEmpty(assetTypes)) {
            return null;
        }

        List<FilterBuilder> assetTypesFilter = new ArrayList<>();

        videoAssetTypeHelper.parseAssetTypesQueryStrings(assetTypes)
                .forEach(assetType -> filterAssetTypeFilters(assetType, assetTypesFilter));

        BoolFilterBuilder builder = boolFilter();

        if (CollectionUtils.isNotEmpty(assetTypesFilter)) {
            builder.must(orFilter(assetTypesFilter.toArray(new FilterBuilder[0])));
        }

        blacklistedAssetTypes.forEach(t -> builder.mustNot(termFilter(AVAILABLE_ASSET_TYPES, t)));
        return nestedFilter(AVAILABLE_ASSETS_FIELD, builder);
    }


    protected void filterAssetTypeFilters(AssetTypeFilter filter, List<FilterBuilder> assetTypesFilter) {
        // Lowercasing asset type, including blacklisted
        // Necessary to properly filter out resources
        FilterBuilder assetTypeFilter = termFilter(AVAILABLE_ASSET_TYPES, filter.getAssetType().toLowerCase(Locale.ROOT));
        if (filter.getBitrateFilter().getFilter() == null) {
            // Lowercasing asset type, including blacklisted
            // Necessary to properly filter out resources
            assetTypesFilter.add(assetTypeFilter);
        } else {
            assetTypesFilter.add(andFilter(assetTypeFilter, filter.getBitrateFilter().getFilter()));
        }
    }

    protected boolean isAllowedAssetType(String s) {
        return blacklistedAssetTypes.isEmpty() || !blacklistedAssetTypes.contains(s.toLowerCase(Locale.ROOT));
    }

    protected boolean isBlacklistedAssetType(String s) {
        return !isAllowedAssetType(s);
    }

  

}

