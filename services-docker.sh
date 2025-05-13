#!/bin/bash

# configuration
if [[ "$1" == "up" ]]; then
	chmod +x ./docker-entrypoint-initdb.d/init-db.sh
	docker network create docker-network
	docker compose -f docker-compose.yml up --build
elif [[ "$1" == "rerun" ]]; then
	docker compose -f docker-compose.yml up --build
elif [[ "$1" == "down" ]]; then
	docker compose -f docker-compose.yml down -v
	docker network rm docker-network
fi
