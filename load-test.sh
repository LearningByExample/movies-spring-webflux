#!/bin/sh -

set -o errexit

if [ $# -ne 3 ]; then
    echo "Illegal number of parameters, usage : "
    echo " "
    echo "  $0 <users> <duration> <ramp up>"
    echo " "
    echo " example: "
    echo "  - $0 20 300 60"
    exit 2
fi

SERVICE="movies-spring-webflux"
TEST_URL="/movies/sci-fi"
NUM_USERS=$1
DURATION=$2
RAMP_UP=$3

./k8s-sv-load-test/k8s-sv-load-test.sh $SERVICE $TEST_URL $NUM_USERS $DURATION $RAMP_UP
