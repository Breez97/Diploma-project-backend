#!/bin/bash

set -e

if [ ! -f .pids ]; then
  echo "No running services found"
  exit 0
fi

while read pid; do
  kill "$pid" > /dev/null 2>&1 || true
done < .pids

rm -f .pids
rm -rf logs

echo "All services stopped"
