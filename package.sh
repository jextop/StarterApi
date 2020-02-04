#!/bin/bash

# package
mvn package -Dmaven.test.skip=true

# copy files
cp ./target/api-0.0.1-SNAPSHOT.jar ./deploy
