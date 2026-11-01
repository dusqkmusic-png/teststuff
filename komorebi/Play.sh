#!/bin/sh
cd "$(dirname "$0")" || exit 1
if [ "$(uname -s)" = Darwin ]; then
    exec java -XstartOnFirstThread -jar Neighborhood.jar "$@"
fi
exec java -jar Neighborhood.jar "$@"
