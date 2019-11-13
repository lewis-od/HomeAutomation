# Home Automation Client

Spring boot app to be ran on local network.

## Deployment

Clone this repo on to the Raspberry Pi

Edit `easybulb.ip` in `src/main/resources/application.properties` to
be the IP address of the Easybulb box on your local network

Add a file at `src/main/resources/AwsCredentials.properties` with 
the contents:

```
accessKey=<your AWS access key>
secretKey=<your AWS secret key>
```

Finally run `./deploy.sh`

