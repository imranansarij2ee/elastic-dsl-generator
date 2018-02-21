package com.serverless.dsl.generator.helpers;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

import com.serverless.dsl.generator.filters.*;
import com.serverless.dsl.generator.model.InvalidQueryException;



public class VideoAssetTypeHelper {
	public static final String AVAILABLE_ASSET_TYPES = "availableAssets.assetTypes";
    public static final String AVAILABLE_ASSET_BITRATE  = "availableAssets.bitrate";
    
    private static final String SEPARATOR = ":";
    private static final String MISSING_ASSET_TYPE_MESSAGE = "Missing assetType value in filter '%s'";
    private static final String MISSING_BITRATE_FILTER_MESSAGE = "Missing bitrate filter value in filter '%s'";
    private static final String INVALID_BITRATE_RANGE_MESSAGE = "Invalid bitrate range in query '%s'";
    private static final String INVALID_NEGATIVE_BITRATE_MESSAGE = "Invalid negative bitrate argument '%s' in query '%s'";
    private static final String INVALID_BITRATE_ARGUMENT_MESSAGE = "Invalid bitrate argument '%s' in query '%s'";
    private static final String INVALID_QUERY_MESSAGE = "Invalid query: '%s'";

    public List<AssetTypeFilter> parseAssetTypesQueryStrings(String[] assetTypes) {
        if (assetTypes == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(assetTypes).map(this::parseAssetTypeQuery).collect(toList());
    }


    private AssetTypeFilter parseAssetTypeQuery(String assetTypeQuery) {
        if (!assetTypeQuery.contains(SEPARATOR)) {
            return new AssetTypeFilter(assetTypeQuery, new BitrateAnyFilter());
        } else {
            if (assetTypeQuery.indexOf(SEPARATOR) == 0) {
                throw new InvalidQueryException(String.format(MISSING_ASSET_TYPE_MESSAGE, assetTypeQuery));
            }
            if (assetTypeQuery.length() - assetTypeQuery.indexOf(SEPARATOR) == 1) {
                throw new InvalidQueryException(String.format(MISSING_BITRATE_FILTER_MESSAGE, assetTypeQuery));
            }
            String assetType = assetTypeQuery.substring(0, assetTypeQuery.indexOf(SEPARATOR));
            String filter = assetTypeQuery.substring(assetTypeQuery.indexOf(SEPARATOR) + 1);

            return new AssetTypeFilter(assetType, parseAvailableAssetBitrateQuery(filter));
        }
    }

    private BitrateFilter parseAvailableAssetBitrateQuery(String queryString) {
        NumericQueryParser parser = new NumericQueryParser(AVAILABLE_ASSET_BITRATE, new SimpleAnalyzer());
        Query query;
        try {
            query = parser.parse(queryString);
        } catch (NumberFormatException e) {
            throw new InvalidQueryException(String.format(INVALID_BITRATE_ARGUMENT_MESSAGE, e.getMessage(), queryString));

        } catch (ParseException e) {
            throw new InvalidQueryException(String.format(INVALID_QUERY_MESSAGE, queryString), e);
        }

        return processNumberQuery(query, queryString);

    }

    private BitrateFilter processNumberQuery(Query query, String queryString) {
        if (!(query instanceof NumericRangeQuery)) {
            return processBooleanQuery(queryString);
        }
        NumericRangeQuery numericQuery = (NumericRangeQuery) query;

        if (!numericQuery.includesMax() || !numericQuery.includesMin()) {
            throw new InvalidQueryException(String.format(INVALID_QUERY_MESSAGE, queryString));
        }
        Number minRange = numericQuery.getMin();
        Number maxRange = numericQuery.getMax();

        validateNumericQuery(queryString, minRange, maxRange);
        return extractBitrateFilter(minRange, maxRange);
    }

    private BitrateFilter processBooleanQuery(String queryString) {
        Integer exactRange;
        try {
            exactRange = Integer.valueOf(queryString);
        } catch (NumberFormatException e) {
            throw new InvalidQueryException(String.format(INVALID_BITRATE_ARGUMENT_MESSAGE, queryString, queryString));

        }
        if(exactRange < 0){
            throw new InvalidQueryException(String.format(INVALID_NEGATIVE_BITRATE_MESSAGE, queryString, queryString));
        }
        return new BitrateExactFilter(exactRange);
    }

    private BitrateFilter extractBitrateFilter(Number minRange, Number maxRange) {
        Integer from = minRange != null ? minRange.intValue() : null;
        Integer to = maxRange != null ? maxRange.intValue() : null;
        return new BitrateRangeFilter(from, to);
    }

    private void validateNumericQuery(String queryString, Number from, Number to) {
        validateNumericRange(from, queryString);
        validateNumericRange(to, queryString);

        if (from == null && to == null) {
            throw new InvalidQueryException(String.format(INVALID_BITRATE_RANGE_MESSAGE, queryString));
        }

        if (from != null && to != null && from.intValue() > to.intValue()) {
            throw new InvalidQueryException(String.format(INVALID_BITRATE_RANGE_MESSAGE, queryString));
        }
    }

    private void validateNumericRange(Number range, String queryString) {
        if (range != null && range.intValue() < 0) {
            throw new InvalidQueryException(String.format(INVALID_NEGATIVE_BITRATE_MESSAGE, range, queryString));
        }
    }

}