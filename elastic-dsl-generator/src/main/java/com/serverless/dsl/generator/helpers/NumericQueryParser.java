package com.serverless.dsl.generator.helpers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

public class NumericQueryParser extends QueryParser {

    public NumericQueryParser(String field, Analyzer analyzer) {
        super(field, analyzer);
    }

    @Override
    protected Query getRangeQuery(String field,
                                  String part1,
                                  String part2,
                                  boolean startInclusive,
                                  boolean endInclusive) throws ParseException {
        return NumericRangeQuery.newLongRange(field, parseLong(part1), parseLong(part2), startInclusive, endInclusive);
    }

    private Long parseLong(String number) {
        if (number == null) {
            return null;
        }
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(number);
        }
    }
}
