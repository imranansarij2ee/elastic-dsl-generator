package com.serverless.dsl.generator.function;

import java.io.PrintWriter;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.lang3.ArrayUtils;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serverless.dsl.generator.model.ServerlessInput;
import com.serverless.dsl.generator.model.ServerlessOutput;
import com.serverless.dsl.generator.helpers.QueryBuilderHelper;
import com.serverless.dsl.generator.filters.AssetTypeFilter;
import com.serverless.dsl.generator.helpers.ElasticSearchIndices;


public class GetDSLQuery implements RequestHandler<ServerlessInput, ServerlessOutput> {
	private static final String[] DOCUMENT_TYPES = { ElasticSearchIndices.Type.NewsArticle.toString(),
			ElasticSearchIndices.Type.Recipe.toString(), ElasticSearchIndices.Type.Slideshow.toString(),
			ElasticSearchIndices.Type.VideoObject.toString(), ElasticSearchIndices.Type.Card.toString(),
			ElasticSearchIndices.Type.Playlist.toString() };
	private static final String Query_Param = "q";
	private static final String Filters = "filters";
	private static final String AssetTypes = "assetType";
	public static final char ASSET_TYPE_SEPARATOR = ',';
	private  Map<String, String> header = new HashMap();

	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		String query = StringUtils.EMPTY, queryString = StringUtils.EMPTY,filterQueryString =StringUtils.EMPTY, assetType = StringUtils.EMPTY;
		ServerlessOutput output = new ServerlessOutput();
		if(serverlessInput.getQueryStringParameters() != null) {
			queryString = serverlessInput.getQueryStringParameters().get(Query_Param);
			 filterQueryString = serverlessInput.getQueryStringParameters().get(Filters);
			 assetType = serverlessInput.getQueryStringParameters().get(AssetTypes);
		}
		
		
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().build(); 
		try {

			if (StringUtils.isEmpty(queryString) && StringUtils.isEmpty(filterQueryString) && StringUtils.isEmpty(assetType)) {
				output.setBody("Please provide query parameter q /filters /assetType to get DSL");

			} else {
				
				final QueryBuilderHelper queryBuilder = new QueryBuilderHelper();
				query = assetType == null || assetType == StringUtils.EMPTY
						? queryBuilder.findByQuery(DOCUMENT_TYPES, queryString, filterQueryString).toString()
						: queryBuilder.findByQuery(DOCUMENT_TYPES, queryString, filterQueryString, parseAssetTypes(assetType)).toString();
				header.put("Content-Type", "application/json");
				output.setHeaders(header);
				output.setBody(query);
			}
			output.setStatusCode(200);

		} catch (Exception e) {
			output.setStatusCode(500);
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			output.setBody(sw.toString());
		}

		return output;
	}
	
	private String toJSON(String query) {
		Gson gconverter = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gconverter.toJson(query);
	}

	private String[] parseAssetTypes(String assetType) {
		String[] assetTypes = StringUtils.split(assetType, ASSET_TYPE_SEPARATOR);
		return Arrays.stream(ArrayUtils.nullToEmpty(assetTypes)).anyMatch(StringUtils::isNotEmpty) ? assetTypes : null;
	}
}