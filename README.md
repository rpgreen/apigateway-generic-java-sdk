# apigateway-generic-java-sdk

[![CircleCI](https://circleci.com/gh/rpgreen/apigateway-generic-java-sdk.svg?style=svg)](https://circleci.com/gh/rpgreen/apigateway-generic-java-sdk)

This is a simple generic Java client for Amazon API Gateway endpoints. It is useful when you don't necessarily want to generate a [strongly-typed SDK](https://aws.amazon.com/blogs/developer/api-gateway-java-sdk), such as when prototyping or scripting.

It is optimized to run from a Lambda function and does not require extra dependencies beyond the AWS SDK, which is already bundled in the Lambda runtime.

It does not make any assumptions about the wire format of your requests and responses. You are free to parse response bodies as you see fit, and the raw HTTP response data is included in the wrapped response.

## Features
* AWS SigV4 request signing. Supports APIs authenticated with IAM auth using standard AWSCredentialsProvider interface
* API Keys
* Custom headers 
* Throws exceptions for non-2xx response codes
* Compatibility with existing AWS SDK client configuration (connections, retry policies, etc)
* Runs in AWS Lambda functions with no additional dependencies

## Install

### Maven
```
<dependency>
  <groupId>ca.ryangreen</groupId>
  <artifactId>apigateway-generic-java-sdk</artifactId>
  <version>1.3</version>
</dependency>
```

### From source
```bash
git clone https://github.com/rpgreen/apigateway-generic-java-sdk.git

# Optional:
cd apigateway-generic-java-sdk
mvn install
```

## Examples
```java
GenericApiGatewayClient client = new GenericApiGatewayClientBuilder()
        .withClientConfiguration(new ClientConfiguration())
        .withCredentials(new EnvironmentVariableCredentialsProvider())
        .withEndpoint("https://XXXXXX.execute-api.us-east-1.amazonaws.com")
        .withRegion(Region.getRegion(Regions.fromName("us-east-1")))
        .withApiKey("XXXXXXXXXXXXXXX")
        .build();

Map<String, String> headers = new HashMap<>();
headers.put("Content-Type", "application/json");

try {
    GenericApiGatewayResponse response = client.execute(
            new GenericApiGatewayRequestBuilder()
                    .withBody(new ByteArrayInputStream("foo".getBytes()))
                    .withHttpMethod(HttpMethodName.POST)
                    .withHeaders(headers)
                    .withResourcePath("/stage/path").build());
    
    System.out.println("Response: " + response.getBody());
    System.out.println("Status: " + response.getHttpResponse().getStatusCode());
    
} catch (GenericApiGatewayException e) {   // exception thrown for any non-2xx response
    System.out.println(String.format("Client threw exception with message %s and status code %s", 
            e.getMessage(), e.getStatusCode()));
}

```
