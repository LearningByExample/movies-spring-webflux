#!/bin/sh -

set -o errexit

echo "doing maven build"
./mvnw clean package
echo "maven build done"

echo "building docker"
docker build . -t localhost:32000/movies-spring-webflux:0.0.1
echo "docker builded"

echo "publishing docker"
docker push localhost:32000/movies-spring-webflux
echo "docker published"
