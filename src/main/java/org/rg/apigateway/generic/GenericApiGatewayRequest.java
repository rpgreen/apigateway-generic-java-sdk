package org.rg.apigateway.generic;

import com.amazonaws.http.HttpMethodName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class GenericApiGatewayRequest {
    private final HttpMethodName httpMethod;
    private final String resourcePath;
    private final InputStream body;
    private final Map<String, String> headers;

    public GenericApiGatewayRequest(HttpMethodName httpMethod, String resourcePath,
                                    InputStream body, Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.body = body;
        this.headers = headers;
    }

    public HttpMethodName getHttpMethod() {
        return httpMethod;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public InputStream getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
