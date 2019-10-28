# ha-serverless

Lambda function that adds home automation actions to an SQS queue

## Deployment
To deploy using CloudFormation, run:

```bash
sam build
sam package --template-file template.yaml --s3-bucket <bucket-name> --output-template-file packaged.yaml
sam deploy --template-file ./packaged.yaml --stack-name home-automation --capabilities CAPABILITY_IAM
```

To tear down:
```bash
aws cloudformation delete-stack --stack-name home-automation
```

Note: `sam deploy` is an alias for `aws cloudformation deploy`

# ToDo
- [ ] Add JSON message to queue based on event
- [ ] Require API key for authentication/authorisation
