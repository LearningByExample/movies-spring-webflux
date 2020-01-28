#!/bin/sh -

set -o errexit

KUBECMD="kubectl"
if [ -x "$(command -v microk8s.kubectl)" ]; then
  KUBECMD="microk8s.kubectl"
fi

echo "deleting previous versions"
$KUBECMD delete all --selector=app=movies-spring-webflux
echo "previous version deleted"

echo "create deployment"
$KUBECMD create -f deployment.yml
echo "deployment created"
