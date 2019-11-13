# Alexa Skill
Lambda function that hosts an Alexa skill for Easybulb lights

## Deployment
1. Follow [this](https://developer.amazon.com/docs/smarthome/steps-to-build-a-smart-home-skill.html) tutorial to set up the skill
2. Follow [this](https://developer.amazon.com/blogs/post/Tx3CX1ETRZZ2NPC/Alexa-Account-Linking-5-Steps-to-Seamlessly-Link-Your-Alexa-Skill-with-Login-wit) tutorial to set up account linking using "Log In with Amazon"
3. Add the `AmazonSQSFullAccess` policy to the IAM role created in step 1
4. Copy the contents of [easybulb_skill.py](easybulb_skill.py) into the lambda
5. Set the `SQS_QUEUE_URL` environment variable of the lambda function
