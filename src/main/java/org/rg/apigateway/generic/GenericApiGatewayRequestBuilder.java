package org.rg.apigateway.generic;

import com.amazonaws.http.HttpMethodName;

import java.io.InputStream;
import java.util.Map;

public class GenericApiGatewayRequestBuilder {
    private HttpMethodName httpMethod;
    private String resourcePath;
    private InputStream body;
    private Map<String, String> headers;

    public GenericApiGatewayRequestBuilder withHttpMethod(HttpMethodName name) {
        httpMethod = name;
        return this;
    }

    public GenericApiGatewayRequestBuilder withResourcePath(String path) {
        resourcePath = path;
        return this;
    }

    public GenericApiGatewayRequestBuilder withBody(InputStream content) {
        this.body = content;
        return this;
    }

    public GenericApiGatewayRequestBuilder withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public GenericApiGatewayRequest build() {
//        Validate.notNull(httpMethod, "HTTP Method cannot be null");
//        Validate.notNull(resourcePath, "Resource path cannot be null");
        return new GenericApiGatewayRequest(httpMethod, resourcePath, body, headers);
    }
}