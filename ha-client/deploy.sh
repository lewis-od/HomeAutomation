#!/bin/sh

# Directory to output logs to
LOG_DIR='logs'

# Colours for pretty printing
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "${BLUE}Building Docker image...${NC}"
docker build -t ha-client .

echo "${BLUE}Killing old container...${NC}"
docker kill home-automation > /dev/null 2>&1

killed_container=$?
if [ $killed_container -eq 0 ]; then
  echo "${GREEN}Container killed.${NC}"
else
  echo "${RED}Container not running.${NC}"
fi

docker rm home-automation > /dev/null 2>&1

log_location=`pwd`/$LOG_DIR
if ! [ -d $log_location ]; then
  echo "${BLUE}Creating log directory...${NC}"
  mkdir $log_location
  echo "${GREEN}Directory created at:${NC} $log_location"
fi

echo "${BLUE}Starting new container...${NC}"
container_id=$(docker run -d -p 8080:8080 \
  --mount type=bind,source=$log_location,target=/app/logs \
  --restart unless-stopped \
  --name home-automation \
  ha-client)

run_successful=$?
if [ $run_successful -eq 0 ]; then
  truncated_id=$(echo $container_id | cut -c 1-12)
  echo "${GREEN}Container started with ID:${NC} ${truncated_id}"
else
  echo "${RED}There was a problem starting the container.${NC}"
  echo $container_id
fi

