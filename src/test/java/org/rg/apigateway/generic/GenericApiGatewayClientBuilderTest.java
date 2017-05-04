package org.rg.apigateway.generic;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.junit.Assert;
import org.junit.Test;

public class GenericApiGatewayClientBuilderTest {
    @Test
    public void testBuild_happy() throws Exception {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

        GenericApiGatewayClient client = new GenericApiGatewayClientBuilder()
                .withClientConfiguration(new ClientConfiguration())
                .withCredentials(credentials)
                .withEndpoint("https://foobar.execute-api.us-east-1.amazonaws.com")
                .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
                .withApiKey("12345")
                .build();

        Assert.assertEquals("Wrong service name","execute-api", client.getServiceNameIntern());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuild_noEndpoint() throws Exception {
        new GenericApiGatewayClientBuilder()
                .withClientConfiguration(new ClientConfiguration())
                .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuild_noRegion() throws Exception {
        new GenericApiGatewayClientBuilder()
                .withClientConfiguration(new ClientConfiguration())
                .withEndpoint("https://foobar.execute-api.us-east-1.amazonaws.com")
                .build();
    }

}