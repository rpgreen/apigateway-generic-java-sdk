## Install

This example deploys a serverless API with 2 resources. One of the routes ("/sdk-example")is publicly available and executes
a Lambda function that uses the generic SDK to make a request to the other IAM-authenticated resource ("/hello").

Run `mvn install` in the apigateway-generic-java-sdk directory
Run
```
mvn package 
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