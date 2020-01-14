#!/bin/bash

# package
./package.sh

# copy files
cd ./deploy
# cp ../src/main/resources/application.yml ./
cp ../target/api-0.0.1-SNAPSHOT.jar ./
cd ..
