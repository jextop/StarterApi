#!/bin/bash

# package and copy files
./package.sh

java -Dfile.encoding=utf-8 -jar ./target/api-0.0.1-SNAPSHOT.jar
