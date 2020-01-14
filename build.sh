#!/bin/bash

# package and copy
./copy.sh

# docker image
docker-compose build
