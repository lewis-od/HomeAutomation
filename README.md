# Home Automation
My attempt at a home automation solution.

## Architecture
The code in `ha-alexa-skills` contains a Lambda function that acts as an [Alexa Smart Home skill](https://developer.amazon.com/docs/smarthome/understand-the-smart-home-skill-api.html).
This is triggered by voice commands to Alexa, and adds messages ("actions") to an SQS queue.

These actions are then consumed by the application in `ha-client` that runs on a Raspberry PI on my
local network.

The SQS messages are JSON of the format:
```json
{
  "service": "easybulb",
  "action": "SetBrightness",
  "value": "80"
}
```
The `action` parameter matches those from the [Alexa messages](https://developer.amazon.com/docs/smarthome/smart-home-skill-api-message-reference.html#lighting-and-tunable-lighting-control-messages).

## Setup
1. Create an SQS queue in `eu-west-1` called `home-automation`
2. Follow the instructions in [ha-alexa-skills](ha-alexa-skills)
3. Follow the instructions in [ha-client](ha-client)

## ToDo:
- [x] Look into making an Alexa skill
- [x] Tidy up Alexa skill code
- [x] Implement `SetBrightness` in Alexa skill
- [ ] Use `maven-shade-plugin` to minify client jar
- [x] Build for deployment using Docker
- [ ] Better AWS credential management in client
- [ ] Build Docker image using mvn
- [ ] Send client logs to log file
