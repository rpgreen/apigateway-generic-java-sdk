package org.rg.apigateway.generic;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;

public class GenericApiGatewayClientBuilder {
    private String endpoint;
    private Region region;
    private AWSCredentialsProvider credentials;
    private ClientConfiguration clientConfiguration;
    private String apiKey;

    public String getEndpoint() {
        return endpoint;
    }

    public GenericApiGatewayClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public Region getRegion() {
        return region;
    }

    public GenericApiGatewayClientBuilder withRegion(Region region) {
        this.region = region;
        return this;
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public GenericApiGatewayClientBuilder withClientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        return this;
    }

    public AWSCredentialsProvider getCredentials() {
        return credentials;
    }

    public GenericApiGatewayClientBuilder withCredentials(AWSCredentialsProvider credentials) {
        this.credentials = credentials;
        return this;
    }

    public GenericApiGatewayClientBuilder withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public GenericApiGatewayClient build() {
//            assertNotEmpty(endpoint, "Endpoint cannot be null");
//            assertNotEmpty(region, "Region cannot be null");
        return new GenericApiGatewayClient(clientConfiguration, endpoint, region, credentials, apiKey);
    }

}
