# Home Automation Client

Spring boot app to be ran on local network.

Before running, add a file called `AwsCredentials.properties`
to `src/main/resources` with the contents:

```
accessKey=<your AWS access key>
secretKey=<your AWS secret key>
```

## ToDo
- [ ] Use `maven-shade-plugin` to minify jar
- [ ] Build for deployment using Docker
- [ ] Implememt more actions

