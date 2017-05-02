package org.rg.apigateway.generic;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.util.IOUtils;

import java.io.IOException;

public class GenericApiGatewayResponse {
    private final HttpResponse httpResponse;
    private final String body;

    public GenericApiGatewayResponse(HttpResponse httpResponse) throws IOException {
        this.httpResponse = httpResponse;
        this.body = IOUtils.toString(httpResponse.getContent());
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public String getBody() {
        return body;
    }
}