#!/bin/sh -

set -o errexit

if [ $# -ne 5 ]; then
  echo "Illegal number of parameters, usage : "
  echo " "
  echo "  $0 <service> <path> <users> <duration> <ramp up>"
  echo " "
  echo " example: "
  echo "  - $0 k8s-service /get/something 20 300 60"
  exit 2
fi

KUBECMD="kubectl"
if [ -x "$(command -v microk8s.kubectl)" ]; then
  KUBECMD="microk8s.kubectl"
fi

LOAD_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"

SERVICE=$1
TEST_URL=$2
SERVICE_CLUSTER_IP=$($KUBECMD get "service/$SERVICE" -o jsonpath='{.spec.clusterIP}')
SERVICE_PORT=$($KUBECMD get "service/$SERVICE" -o jsonpath='{.spec.ports.*.targetPort}')
NUM_USERS=$3
DURATION=$4
RAMP_UP=$5

$LOAD_DIR/load_test.sh $SERVICE_CLUSTER_IP $SERVICE_PORT $TEST_URL $NUM_USERS $DURATION $RAMP_UP