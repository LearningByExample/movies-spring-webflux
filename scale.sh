#!/bin/sh -

set -o errexit

if [ $# -ne 1 ]; then
  echo "Illegal number of parameters, usage : "
  echo " "
  echo "  $0 <replicas>"
  echo " "
  echo " examples: "
  echo "  - $0 5"
  echo "  - $0 0"
  exit 2
fi

KUBECMD="kubectl"
if [ -x "$(command -v microk8s.kubectl)" ]; then
  KUBECMD="microk8s.kubectl"
fi

DEPLOYMENT_NAME="movies-spring-webflux"
WANTED_REPLICAS="$1"
REPLICAS="0"
PREVIOUS_REPLICAS="-1"

replicas() {
  REPLICAS=$($KUBECMD get "deployment/$DEPLOYMENT_NAME" -o jsonpath='{.status.readyReplicas}')
  if test -z "$REPLICAS"; then
    REPLICAS="0"
  fi
}

echo "checking deployment: $DEPLOYMENT_NAME"

replicas
if [ "$REPLICAS" -eq "$WANTED_REPLICAS" ]; then
  echo "we dont need to scale allready got $REPLICAS replicas ready"
  exit 0
fi

echo "scaling deployment: $DEPLOYMENT_NAME to $WANTED_REPLICAS replicas"

$KUBECMD scale "deployment/$DEPLOYMENT_NAME" --replicas "$WANTED_REPLICAS"

START=$(date +%s.%N)

while true; do
  if [ "$REPLICAS" != "$PREVIOUS_REPLICAS" ]; then
    echo "waiting for scaling: got $REPLICAS replicas, want $WANTED_REPLICAS"
    PREVIOUS_REPLICAS="$REPLICAS"
  fi
  if [ "$REPLICAS" -eq "$WANTED_REPLICAS" ]; then
    END=$(date +%s.%N)
    DIFF=$(echo "$END - $START" | bc)
    echo "scaling complete, $REPLICAS ready, in $DIFF seconds"

    exit 0
  fi
  replicas
done
