#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USER" --password-stdin
docker push "$DOCKER_USER/ha-client"
