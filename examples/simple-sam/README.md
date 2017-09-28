## Install

This example deploys a serverless API with 2 resources. One of the routes ("/sdk-example") is publicly available and executes
a Lambda function that uses the generic SDK to make a request to the other IAM-authenticated resource ("/hello").

## Build
First run `mvn install` in the apigateway-generic-java-sdk directory to place the jar in your local maven repository.

## Deploy

Replace AWS_ACCOUNT_ID in swagger.yaml and run :
```
mvn package
export AWS_DEFAULT_REGION=us-west-2
aws cloudformation package \
    --template-file template.yaml \
    --s3-bucket [YOUR_S3_BUCKET] \
    --output-template-file packaged-template.yaml

aws cloudformation deploy \
    --template-file packaged-template.yaml \
    --stack-name [YOUR_STACK_NAME] \
    --capabilities CAPABILITY_IAM


```
## Run
```
curl https://[YOUR_API_ID].execute-api.us-west-2.amazonaws.com/Prod/sdk-example/hello

```

