# Marketplace Aggregator

### API Documentation

```
http://localhost:8080/api-docs
```

### Local run
1. Configure `.env` for local environment or `docker.env` for docker environment (parameters for configuration showed below)
2. To run all microservices use a script:
    ```shell
   chmod +x ./services-local.sh
    ./services-local.sh up
    ```
   or with docker:
    ```shell
   chmod +x ./services-docker.sh
    ./services-docker.sh up
    ```
3. <b>*(Optional)*</b> To stop all microservices use a script:
    ```shell
   chmod +x ./services-local.sh
    ./services-local.sh down
    ```
   or with docker:
    ```shell
   chmod +x ./services-docker.sh
   ./services-docker.sh down
    ```

#### `.env` file configuration
```properties
# common settings #
## postgres ##
SPRING_DATASOURCE_HOST_LOCAL=localhost
SPRING_DATASOURCE_HOST_DOCKER=postgres
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_USERNAME=USERNAME
SPRING_DATASOURCE_PASSWORD=PASSWORD
## redis ##
SPRING_DATA_REDIS_HOST_LOCAL=localhost
SPRING_DATA_REDIS_HOST_DOCKER=redis
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_TIMEOUT=2000ms
## kafka ##
SPRING_KAFKA_HOST_LOCAL=localhost
SPRING_KAFKA_HOST_DOCKER=kafka
SPRING_KAFKA_PORT_LOCAL=9092
SPRING_KAFKA_PORT_DOCKER=29092
### consumer ###
SPRING_KAFKA_CONSUMER_GROUP_ID_MONITORING=service-monitoring-group
SPRING_KAFKA_CONSUMER_GROUP_ID_NOTIFICATIONS=service-notifications-group
### kafka topics ###
KAFKA_TOPIC_USER_FAVORITES=user_favorites_topic
KAFKA_TOPIC_USER_NOTIFICATIONS=user_notifications_topic
KAFKA_TOPIC_USER_PRICE_ALERTS=user_price_alerts_topic
## mail ##
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=USERNAME
SPRING_MAIL_PASSWORD=PASSWORD
## allowed origins ##
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://192.168.1.72:3000 (e.g. React)
## path to images ##
PATH_TO_IMAGES=./uploads/avatars

# services #
## service-api-gateway ##
SPRING_APPLICATION_NAME_API_GATEWAY=API-GATEWAY
SERVER_PORT_API_GATEWAY=PORT_NUMBER (e.g. 8080)

## service-search ##
SPRING_APPLICATION_NAME_SERVICE_SEARCH=SERVICE-SEARCH
SERVER_PORT_SERVICE_SEARCH=PORT_NUMBER (e.g. 8081)

## service-users ##
SPRING_APPLICATION_NAME_SERVICE_USERS=SERVICE-USERS
SERVER_PORT_SERVICE_USERS=PORT_NUMBER (e.g. 8082)
SERVICE_USERS_DB_NAME=service-users
SKIP_CODE_VERIFICATION=false (DEFAULT: false)
JWT_SECRET=SECRET_KEY
JWT_ACCESS_TOKEN_EXPIRATION_MS=TIME_EXPIRATION (e.g. 900000 # 15 minutes)
JWT_REFRESH_TOKEN_EXPIRATION_MS=TIME_EXPIRATION (604800000 # 7 days)

## service-monitoring ##
SPRING_APPLICATION_NAME_SERVICE_MONITORING=SERVICE-MONITORING
SERVER_PORT_SERVICE_MONITORING=PORT_NUMBER (e.g. 8083)
SERVICE_MONITORING_DB_NAME=service-monitoring
TEST_NOTIFICATIONS_EMAIL=TEST_EMAIL

## service-notifications ##
SPRING_APPLICATION_NAME_SERVICE_NOTIFICATIONS=SERVICE-NOTIFICATIONS
SERVER_PORT_SERVICE_NOTIFICATIONS=PORT_NUMBER (e.g. 8084)
SERVICE_NOTIFICATIONS_DB_NAME=service-notifications
DISABLE_NOTIFICATIONS_GENERAL=false (DEFAULT: false)
```

### Response
For better usage all the microservices use the same template of response:

#### Success (2xx)
```json
{
    "timestamp": "2025-05-06T22:01:37.380081165",
    "status": "success",
    "message": "Info message",
    "data": []
}
```

#### Error (3xx, 4xx, 5xx)
```json
{
    "timestamp": "2025-05-06T22:02:01.407325821",
    "status": "error",
    "message": "Error message",
    "data": null
}
```
