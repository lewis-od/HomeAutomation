# Home Automation
My attempt at a home automation solution.

## Architecture
The eventual aim is to have some AWS Lambda functions that can be called via
HTTP, which will add some 'Actions' to an SQS queue.

These actions will then be consumed by a client running on my local network
(probably on a Raspberry Pi), which can communicate with various devices.


## ToDo:
- [ ] Add parent `pom.xml` to root dir, with `ha-client` and `ha-serverless` as sub projects
- [ ] Extract common code to `ha-core` project
- [ ] Actually implement `ha-serverless`
- [ ] Look into making an Alexa skill
