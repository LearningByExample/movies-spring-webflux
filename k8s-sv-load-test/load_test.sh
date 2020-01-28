#!/bin/sh -

set -o errexit

if [ $# -ne 6 ]; then
  echo "Illegal number of parameters, usage : "
  echo " "
  echo "  $0 <ip> <port> <path> <users> <duration> <ramp up>"
  echo " "
  echo " example: "
  echo "  - $0 locahost 8080 /get/something 20 300 60"
  exit 2
fi

LOAD_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"
REPORT_DIR="$LOAD_DIR/report"

if [ -d "$REPORT_DIR" ]; then
  echo "deleting report directory"
  rm -Rf "$REPORT_DIR"
  echo "report directory deleted"
fi

echo "creating report directory"
mkdir "$REPORT_DIR"
echo "report directory created"

TEST_IP=$1
TEST_PORT=$2
TEST_URL=$3
NUM_USERS=$4
DURATION=$5
RAMP_UP=$6

echo "launching a load test on http://$TEST_IP:$TEST_PORT$TEST_URL with $NUM_USERS "\
  "user(s) during $DURATION seconds, ramping up during $RAMP_UP seconds"

HEAP="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m"
jmeter -n -t "$LOAD_DIR/load_test.jmx" -l "$REPORT_DIR/run.jtl" -e -o "$REPORT_DIR" -j "$LOAD_DIR/jmeter.log" \
  -JTEST_IP="$TEST_IP" \
  -JTEST_PORT="$TEST_PORT" \
  -JNUM_USERS="$NUM_USERS" \
  -JDURATION="$DURATION" \
  -JRAMP_UP="$RAMP_UP" \
  -JTEST_URL="$TEST_URL"

echo "load test done"

WEB_REPORT_PATH="file:///$REPORT_DIR/index.html"
echo "report available on $WEB_REPORT_PATH"
