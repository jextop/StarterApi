#!/bin/bash

# ./build.sh
./stop.sh

# run image
# docker run --rm --name api -p 8011:8011 -d starter_api

docker port api
docker ps

# docker exec -it api bash
# docker logs api -f
