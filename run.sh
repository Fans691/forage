#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TOMCAT_HOME="${TOMCAT_HOME:-}"

if [[ -z "$TOMCAT_HOME" && -d "/opt/tomcat" ]]; then
  TOMCAT_HOME="/opt/tomcat"
fi

if [[ -z "$TOMCAT_HOME" ]]; then
  echo "TOMCAT_HOME is not set. Example: export TOMCAT_HOME=/opt/tomcat"
  exit 1
fi

if [[ ! -d "$TOMCAT_HOME/webapps" ]]; then
  echo "Invalid TOMCAT_HOME: $TOMCAT_HOME"
  exit 1
fi

cd "$PROJECT_DIR"

mvn clean package

WAR_PATH="$PROJECT_DIR/target/forage.war"
if [[ ! -f "$WAR_PATH" ]]; then
  WAR_PATH="$PROJECT_DIR/target/forage-0.0.1-SNAPSHOT.war"
fi
if [[ ! -f "$WAR_PATH" ]]; then
  echo "WAR not found in $PROJECT_DIR/target"
  exit 1
fi

if pgrep -f "org.apache.catalina.startup.Bootstrap" >/dev/null 2>&1; then
  if [[ -x "$TOMCAT_HOME/bin/shutdown.sh" ]]; then
    "$TOMCAT_HOME/bin/shutdown.sh" || true
  fi
fi

rm -rf "$TOMCAT_HOME/webapps/forage"
cp "$WAR_PATH" "$TOMCAT_HOME/webapps/forage.war"

if [[ -x "$TOMCAT_HOME/bin/startup.sh" ]]; then
  if ! pgrep -f "org.apache.catalina.startup.Bootstrap" >/dev/null 2>&1; then
    "$TOMCAT_HOME/bin/startup.sh"
  fi
else
  echo "startup.sh not found in $TOMCAT_HOME/bin"
  exit 1
fi

echo "Deployed to http://localhost:8080/forage/"
