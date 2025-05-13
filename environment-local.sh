#!/bin/bash

set -e

chmod +x ./docker-entrypoint-initdb.d/init-db.sh

# set environment
set_environment() {
	if [ -f .env ]; then
        set -o allexport
        source .env
        set +o allexport
        echo ".env loaded successfully"
    else
        echo ".env file not found"
        exit 1
    fi
}

# run local environment
if [[ "$1" == "up" ]]; then
	set_environment
	echo "Starting environment in docker"
	docker compose -f docker-compose-environment-local.yml up -d
elif [[ "$1" == "up" && "$2" == "kafka" ]]; then
	set_environment
	echo "Starting environment in docker: only kafka"
	docker compose -f docker-compose-environment-local.yml up -d kafka
elif [[ "$1" == "down" ]]; then
	echo "Stoping environment in docker"
	docker compose -f docker-compose-environment-local.yml down -v
else
	echo "Invalid parameter"
	echo "Valid: up/down"
fi
