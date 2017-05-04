package org.rg.apigateway.generic;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.apache.client.impl.SdkHttpClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;

public class GenericApiGatewayClientTest {
    private GenericApiGatewayClient client;
    private SdkHttpClient mockClient;
    @Before
    public void setUp() throws IOException {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

        mockClient = Mockito.mock(SdkHttpClient.class);
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("test payload".getBytes()));
        resp.setEntity(entity);
        Mockito.doReturn(resp).when(mockClient).execute(any(HttpUriRequest.class), any(HttpContext.class));

        ClientConfiguration clientConfig = new ClientConfiguration();

        client = new GenericApiGatewayClientBuilder()
                .withClientConfiguration(clientConfig)
                .withCredentials(credentials)
                .withEndpoint("https://foobar.execute-api.us-east-1.amazonaws.com")
                .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
                .withApiKey("12345")
                .withHttpClient(new AmazonHttpClient(clientConfig, mockClient, null))
                .build();
    }

    @Test
    public void testExecute_happy() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Account-Id", "fubar");
        headers.put("Content-Type", "application/json");
        GenericApiGatewayResponse response = client.execute(
                new GenericApiGatewayRequestBuilder()
                        .withBody(new ByteArrayInputStream("test request".getBytes()))
                        .withHttpMethod(HttpMethodName.POST)
                        .withHeaders(headers)
                        .withResourcePath("/test/orders").build());

        assertEquals("Wrong response body", "test payload", response.getBody());
        assertEquals("Wrong response status", 200, response.getHttpResponse().getStatusCode());

        Mockito.verify(mockClient, times(1)).execute(argThat(new LambdaMatcher<>(
                        x -> (x.getMethod().equals("POST")
                                && x.getFirstHeader("Account-Id").getValue().equals("fubar")
                                && x.getFirstHeader("x-api-key").getValue().equals("12345")
                                && x.getFirstHeader("Authorization").getValue().startsWith("AWS4")
                                && x.getURI().toString().equals("https://foobar.execute-api.us-east-1.amazonaws.com/test/orders")))),
                any(HttpContext.class));
    }

    @Test
    public void testExecute_noApiKey_noCreds() throws IOException {
        client = new GenericApiGatewayClientBuilder()
                .withEndpoint("https://foobar.execute-api.us-east-1.amazonaws.com")
                .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
                .withClientConfiguration(new ClientConfiguration())
                .withHttpClient(new AmazonHttpClient(new ClientConfiguration(), mockClient, null))
                .build();

        GenericApiGatewayResponse response = client.execute(
                new GenericApiGatewayRequestBuilder()
                        .withBody(new ByteArrayInputStream("test request".getBytes()))
                        .withHttpMethod(HttpMethodName.POST)
                        .withResourcePath("/test/orders").build());

        assertEquals("Wrong response body", "test payload", response.getBody());
        assertEquals("Wrong response status", 200, response.getHttpResponse().getStatusCode());

        Mockito.verify(mockClient, times(1)).execute(argThat(new LambdaMatcher<>(
                        x -> (x.getMethod().equals("POST")
                                && x.getFirstHeader("x-api-key") == null
                                && x.getFirstHeader("Authorization") == null
                                && x.getURI().toString().equals("https://foobar.execute-api.us-east-1.amazonaws.com/test/orders")))),
                any(HttpContext.class));
    }

    @Test
    public void testExecute_non2xx_exception() throws IOException {
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 404, "Not found"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("{\"message\" : \"error payload\"}".getBytes()));
        resp.setEntity(entity);
        Mockito.doReturn(resp).when(mockClient).execute(any(HttpUriRequest.class), any(HttpContext.class));

        Map<String, String> headers = new HashMap<>();
        headers.put("Account-Id", "fubar");
        headers.put("Content-Type", "application/json");

        try {
            client.execute(
                    new GenericApiGatewayRequestBuilder()
                            .withBody(new ByteArrayInputStream("test request".getBytes()))
                            .withHttpMethod(HttpMethodName.POST)
                            .withHeaders(headers)
                            .withResourcePath("/test/orders").build());

            Assert.fail("Expected exception");
        } catch (GenericApiGatewayException e) {
            assertEquals("Wrong status code", 404, e.getStatusCode());
            assertEquals("Wrong exception message", "{\"message\":\"error payload\"}", e.getErrorMessage());
        }
    }


}
