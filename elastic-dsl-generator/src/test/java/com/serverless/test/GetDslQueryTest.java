package com.serverless.test;

import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.serverless.dsl.generator.function.GetDSLQuery;
import com.serverless.dsl.generator.model.ServerlessInput;
import com.serverless.dsl.generator.model.ServerlessOutput;

public class GetDslQueryTest {
	private GetDSLQuery getDslQuery;
    private Context testContext;

    @Before
    public void setUp() throws Exception {
    	getDslQuery = new GetDSLQuery();  
        testContext = new Context() {
            // implement all methods of this interface and setup your test context. 
            // For instance, the function name:
            @Override
            public String getFunctionName() {
                return "GetDSLQuery";
            }

			@Override
			public String getAwsRequestId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ClientContext getClientContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getFunctionVersion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CognitoIdentity getIdentity() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInvokedFunctionArn() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getLogGroupName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getLogStreamName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public LambdaLogger getLogger() {
				// TODO Auto-generated method stub
				return  null;
			}

			@Override
			public int getMemoryLimitInMB() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getRemainingTimeInMillis() {
				// TODO Auto-generated method stub
				return 0;
			}
        };
    }

    @Test
    public void should_handle_request() {
    	String expectedWithParamJson = "{\"query\":{\"indices\":{\"indices\":[\"mavismpx\"],\"query\":{\"filtered\":{\"query\":{\"query_string\":{\"query\":\"Obama\"}},\"filter\":{\"bool\":{\"must\":[{\"nested\":{\"filter\":{\"bool\":{\"must\":{\"or\":{\"filters\":[{\"and\":{\"filters\":[{\"term\":{\"availableAssets.assetTypes\":\"different type\"}},{\"range\":{\"availableAssets.bitrate\":{\"from\":null,\"to\":500000,\"include_lower\":true,\"include_upper\":true}}}]}},{\"and\":{\"filters\":[{\"term\":{\"availableAssets.assetTypes\":\"type one\"}},{\"term\":{\"availableAssets.bitrate\":3701064}}]}},{\"term\":{\"availableAssets.assetTypes\":\"other type\"}}]}}}},\"path\":\"availableAssets\"}},{\"query\":{\"query_string\":{\"query\":\"subType:video\"}}}]}}}},\"no_match_query\":{\"filtered\":{\"query\":{\"query_string\":{\"query\":\"Obama\"}},\"filter\":{\"query\":{\"query_string\":{\"query\":\"subType:video\"}}}}}}},\"fields\":\"etag\"}";
    String expectedWithNoParam = "Please provide query parameter q /filters /assetType to get DSL";   
    	ServerlessInput input = new ServerlessInput(); // your code for the factory of MyInput.
        // Testing without any query string parameter 
        ServerlessOutput outputWithNoParam = getDslQuery.handleRequest(input, testContext);
        System.out.println("unit test passed without query param");
        assertEquals(expectedWithNoParam, outputWithNoParam.getBody());
        Map<String, String> queryStringParameters = new HashMap<String, String>();
        queryStringParameters.put("q", "Obama");
        queryStringParameters.put("filters", "subType:video");
        queryStringParameters.put("assetType", "different type:[* TO 500000],type one:3701064,other type");
        input.setQueryStringParameters(queryStringParameters);
        ServerlessOutput outputWithParam = getDslQuery.handleRequest(input, testContext);
        try {
            JSONAssert.assertEquals(expectedWithParamJson, outputWithParam.getBody().trim(), true);
            System.out.println("unit test passed with param");
        	
        }catch(AssertionError | JSONException ae) {
        	System.out.println( ae);
        }
    }
}
