#!/bin/bash

# package and copy files
./package.sh

# docker image
docker-compose build
