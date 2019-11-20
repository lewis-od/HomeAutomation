#!/bin/sh

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color 

echo "${GREEN}Building Docker image...${NC}"
docker build -t ha-client .

echo "${GREEN}Killing old container...${NC}"
docker kill home-automation &> /dev/null

found_container=$?
if [ $found_container -eq 0 ]
then
  docker rm home-automation > /dev/null
else
  echo "${RED}Container not running.${NC}"
fi

echo "${GREEN}Starting new container...${NC}"
docker run -d -p 8080:8080 \
  -v "$(pwd)"/logs:/app/logs \
  --restart unless-stopped \
  --name home-automation \
  ha-client

run_successful=$?
if [ $run_successful -eq 0 ]
then
  echo "${GREEN}Done."
else
  echo "${RED}There was a problem starting the container."
fi

