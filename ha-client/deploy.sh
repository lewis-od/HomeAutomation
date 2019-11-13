#!/bin/sh

docker build -t ha-client .
docker kill ha-client
docker run -d -p 8080:8080 --restart unless-stopped ha-client

