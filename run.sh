#!/bin/bash

# ./build.sh
./stop.sh

# run image
# docker run --rm --name api -p 8011:8011 -d StarterApi

# run image with volumes
DATA_PATH=$PWD
docker run --rm --name api -p 8011:8011 --link mysql:db --link redis:cache --link rabbit:mq -v $DATA_PATH/deploy:/root/code -w /root/code -d starter_api

docker port api
docker ps

# docker exec -it api bash
# docker logs api -f
