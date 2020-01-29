#!/bin/sh -

set -o errexit

BASE_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"

DEPS_DIR="$BASE_DIR/deps"

mkdir -p "$DEPS_DIR"

echo "extracting jar"
cd "$DEPS_DIR"
jar -xf ../*.jar
echo "jar extracted"

echo "generate JVM modules list"
EX_JVM_DEPS="jdk.crypto.ec,jdk.unsupported"
JVM_DEPS=$(jdeps -s --multi-release 11 -recursive -cp BOOT-INF/lib/*.jar BOOT-INF/classes | \
  grep -Po '(java|jdk)\..*' | \
  sort -u | \
  tr '\n' ',')
MODULES="$JVM_DEPS$EX_JVM_DEPS"
echo "$MODULES"
echo "JVM modules list generated"

echo "doing jlink"
jlink \
    --verbose \
    --module-path "$JAVA_HOME/jmods", \
    --add-modules "$MODULES" \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --strip-debug \
    --output "$DEPS_DIR/jre-jlink"

echo "jlink done"

cd "$BASE_DIR"
