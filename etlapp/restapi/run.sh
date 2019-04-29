#!/usr/bin/env bash

sbt dist
cp -f ./target/universal/restapi-1.0-SNAPSHOT.zip ./docker/restapi/
docker-compose up --build -d