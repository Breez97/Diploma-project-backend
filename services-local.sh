#!/bin/bash

set -e

# start services
start_services() {

	if [ -f .env ]; then
		set -o allexport
		source .env
		set +o allexport
		echo ".env loaded successfully"
    else
		echo ".env file not found"
		exit 1
    fi

    mkdir -p logs

    start_service() {
		local service_dir=$1
		pushd "$service_dir" > /dev/null
		mvn spring-boot:run -Plocal > "../logs/${service_dir}.log" 2>&1 &
		echo $! >> ../.pids
		popd > /dev/null
    }

    start_service api-gateway
    start_service service-monitoring
    start_service service-notifications
    start_service service-search
    start_service service-users

    declare -A services=(
    	["api-gateway"]=$SERVER_PORT_API_GATEWAY
		["service-monitoring"]=$SERVER_PORT_SERVICE_MONITORING
		["service-notifications"]=$SERVER_PORT_SERVICE_NOTIFICATIONS
		["service-search"]=$SERVER_PORT_SERVICE_SEARCH
		["service-users"]=$SERVER_PORT_SERVICE_USERS
    )

    declare -a service_names
    declare -a service_ports
    declare -a service_statuses

    check_health() {
        local name=$1
        local port=$2
        local retries=30
        local count=0
        local url="http://localhost:${port}/actuator/health"

        until curl -s "$url" | grep -q "\"status\":\"UP\""; do
        	count=$((count + 1))
        	if [ $count -ge $retries ]; then
        		service_names+=("$name")
        		service_ports+=("$port")
        		service_statuses+=("DOWN")
        		return
        	fi
        	echo "Waiting for $name on $url... ($count/$retries)"
        	sleep 2
        	done

		service_names+=("$name")
		service_ports+=("$port")
      	service_statuses+=("UP")
    }

    for service in "${!services[@]}"; do
    	port="${services[$service]}"
    	check_health "$service" "$port"
    done

    printf "\n%-30s %-10s %-10s\n" "SERVICE" "PORT" "STATUS"
    printf "%-30s %-10s %-10s\n" "------------------------------" "----------" "----------"

    for i in "${!service_names[@]}"; do
    	printf "%-30s %-10s %-10s\n" "${service_names[$i]}" "${service_ports[$i]}" "${service_statuses[$i]}"
    done

    if printf "%s\n" "${service_statuses[@]}" | grep -q "DOWN"; then
    	echo -e "\nNot all of the services started successfully"
    	exit 1
    fi

    echo -e "\nAll of the services started successfully"
}

# stop services
stop_services() {
	./environment-local.sh down

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
}

# configuration
if [[ "$1" == "up" ]]; then
	chmod +x ./environment-local.sh
	./environment-local.sh up
	mvn clean install -DskipTests -U
	echo "Starting services"
	start_services
elif [[ "$1" == "up" && "$2" == "kafka" ]]; then
	chmod +x ./environment-local.sh
	./environment-local.sh up kafka
    mvn clean install -DskipTests -U
    echo "Starting services"
	start_services
elif [[ "$1" == "down" ]]; then
	echo "Stopping services"
	stop_services
	mvn clean
else
	echo "Invalid parameter"
	echo "Valid: up/down"
fi
