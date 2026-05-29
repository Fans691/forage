#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TOMCAT_HOME="${TOMCAT_HOME:-}"

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
  echo "WAR not found at $WAR_PATH"
  exit 1
fi

if [[ -x "$TOMCAT_HOME/bin/shutdown.sh" ]]; then
  "$TOMCAT_HOME/bin/shutdown.sh" || true
fi

rm -rf "$TOMCAT_HOME/webapps/forage"
cp "$WAR_PATH" "$TOMCAT_HOME/webapps/forage.war"

if [[ -x "$TOMCAT_HOME/bin/startup.sh" ]]; then
  "$TOMCAT_HOME/bin/startup.sh"
else
  echo "startup.sh not found in $TOMCAT_HOME/bin"
  exit 1
fi

echo "Deployed to http://localhost:8080/forage/"
