package ca.ryangreen.examples;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import ca.ryangreen.apigateway.generic.GenericApiGatewayClient;
import ca.ryangreen.apigateway.generic.GenericApiGatewayClientBuilder;
import ca.ryangreen.apigateway.generic.GenericApiGatewayException;
import ca.ryangreen.apigateway.generic.GenericApiGatewayRequestBuilder;
import ca.ryangreen.apigateway.generic.GenericApiGatewayResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Main implements RequestStreamHandler {

    // this function is exposed by /hello and is authenticated with IAM
    public void hello(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ProxyResponse resp = new ProxyResponse("200", "{\"message\" : \"Hello World\"}");

        String responseString = new ObjectMapper(new JsonFactory()).writeValueAsString(resp);

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseString);
        writer.close();
    }

    // this function is exposed by /sdk-example and is unauthenticated
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        final GenericApiGatewayClient client = new GenericApiGatewayClientBuilder()
                .withClientConfiguration(new ClientConfiguration())
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withEndpoint("https://0g7n3beoyi.execute-api.us-west-2.amazonaws.com") // replace with your API ID
                .withRegion(Region.getRegion(Regions.fromName("us-west-2")))
                .build();

        GenericApiGatewayResponse apiResponse;
        ProxyResponse resp;
        try {
            apiResponse = client.execute(  // throws exception for non-2xx response
                    new GenericApiGatewayRequestBuilder()
                            .withHttpMethod(HttpMethodName.GET)
                            .withResourcePath("/Prod/hello").build());

            System.out.println("Response: " + apiResponse.getBody());
            System.out.println("Status: " + apiResponse.getHttpResponse().getStatusCode());

            resp = new ProxyResponse("200", apiResponse.getBody());

        } catch (GenericApiGatewayException e) {
            System.out.println("Client threw exception " + e);
            resp = new ProxyResponse("400", e.getMessage());
        }

        String responseString = new ObjectMapper(new JsonFactory()).writeValueAsString(resp);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseString);
        writer.close();
    }

    private class ProxyResponse {
        private String statusCode;
        private String body;

        public ProxyResponse(String statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }
    }
}
