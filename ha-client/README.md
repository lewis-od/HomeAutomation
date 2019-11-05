# Home Automation Client

Spring boot app to be ran on local network.

## Deployment

Add a file at `src/main/resources/AwsCredentials.properties` with 
the contents:

```
accessKey=<your AWS access key>
secretKey=<your AWS secret key>
```

Then run
```
docker build -t ha-client:latest .
docker run -p 8080:8080 ha-client:latest
```

