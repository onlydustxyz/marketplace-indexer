#!/usr/bin/env sh

set -x

if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
  echo "ERROR - Missing spring-profiles-active parameter"
  exit 1
fi

jar=$1
shift

java -javaagent:/webapp/dd-java-agent.jar \
  -server \
  -XX:MaxRAMPercentage=75.0 \
  -XX:MaxMetaspaceSize=256m \
  -XX:+UseG1GC \
  -Xss100m \
  -XX:FlightRecorderOptions=stackdepth=256 \
  -Djava.security.egd=file:/dev/urandom \
  -Dliquibase.changelogLockPollRate=1 \
  -Ddd.profiling.enabled=true \
  -Ddd.logs.injection=true \
  -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE" \
  -jar "$jar" \
  "$@"