package org.rg.apigateway.generic;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.http.JsonResponseHandler;
import com.amazonaws.internal.auth.DefaultSignerProvider;
import com.amazonaws.protocol.json.JsonOperationMetadata;
import com.amazonaws.protocol.json.SdkStructuredPlainJsonFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GenericApiGatewayClient extends AmazonWebServiceClient {
    private static final String API_GATEWAY_SERVICE_NAME = "execute-api";
    private static final String API_KEY_HEADER = "x-api-key";

    private final ExecutionContext executionContext;
    private final JsonResponseHandler<GenericApiGatewayResponse> responseHandler;
    private final HttpResponseHandler<AmazonServiceException> errorResponseHandler;
    private String apiKey;

    GenericApiGatewayClient(ClientConfiguration clientConfiguration, String endpoint, Region region,
                            AWSCredentialsProvider credentials, String apiKey, AmazonHttpClient client) {
        super(clientConfiguration);
        this.apiKey = apiKey;
        setRegion(region);
        setEndpoint(endpoint);

        final AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(API_GATEWAY_SERVICE_NAME);
        signer.setRegionName(region.getName());

        executionContext = ExecutionContext.builder().withSignerProvider(
                new DefaultSignerProvider(this, signer)).build();
        executionContext.setCredentialsProvider(credentials);
        executionContext.setSigner(signer);

        final JsonOperationMetadata metadata = new JsonOperationMetadata().withHasStreamingSuccessResponse(false).withPayloadJson(false);
        final Unmarshaller<GenericApiGatewayResponse, JsonUnmarshallerContext> responseUnmarshaller = in -> new GenericApiGatewayResponse(in.getHttpResponse());
        responseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createResponseHandler(metadata, responseUnmarshaller);
        JsonErrorUnmarshaller defaultErrorUnmarshaller = new JsonErrorUnmarshaller(GenericApiGatewayException.class, null) {
            @Override
            public AmazonServiceException unmarshall(JsonNode jsonContent) throws Exception {
                return new GenericApiGatewayException(jsonContent.toString());
            }
        };
        errorResponseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createErrorResponseHandler(Collections.singletonList(defaultErrorUnmarshaller), null);

        if (client != null) {
            this.client = client;
        }
    }

    public GenericApiGatewayResponse execute(GenericApiGatewayRequest request) {
        return execute(request.getHttpMethod(), request.getResourcePath(), request.getHeaders(), request.getBody());
    }

    private GenericApiGatewayResponse execute(HttpMethodName method, String resourcePath, Map<String, String> headers, InputStream content) {
        DefaultRequest request = new DefaultRequest(API_GATEWAY_SERVICE_NAME);
        request.setHttpMethod(method);
        request.setContent(content);
        request.setEndpoint(this.endpoint);
        request.setResourcePath(resourcePath);

        if (headers == null) {
            headers = new HashMap<>();
        }
        if (apiKey != null) {
            final Map<String, String> headersWithApiKey = new HashMap<>();
            headers.forEach(headersWithApiKey::put);
            headersWithApiKey.put(API_KEY_HEADER, apiKey);
            request.setHeaders(headersWithApiKey);
        } else {
            request.setHeaders(headers);
        }

        return this.client.execute(request, responseHandler, errorResponseHandler, executionContext).getAwsResponse();
    }

    @Override
    protected String getServiceNameIntern() {
        return API_GATEWAY_SERVICE_NAME;
    }
}


