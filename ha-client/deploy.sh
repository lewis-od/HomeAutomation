#!/bin/sh

docker build -t ha-client .
docker kill home-automation
docker rm home-automation
docker run -d -p 8080:8080 -v "$(pwd)"/logs:/app/logs --restart unless-stopped --name home-automation ha-client
