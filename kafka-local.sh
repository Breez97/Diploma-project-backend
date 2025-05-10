#!/bin/bash

set -e

if [[ "$1" == "up" ]]; then
	echo "Starting kafka in docker locally"
	docker compose -f docker-compose-kafka.yml up -d
elif [[ "$1" == "down" ]]; then
	echo "Stoping kafka in docker locally"
	docker compose -f docker-compose-kafka.yml down
else
	echo "Invalid parameter"
	echo "Valid: up/down"
fi
