# EasyFind (Diploma Project)



### [Frontend (React)](https://github.com/Breez97/Diploma-project-frontend)

### API Documentation

```
http://localhost:8080/api-docs
```

### Local run
1. Configure `.env` (parameters for configuration showed below)
2. To run all microservices use a script:
    ```shell
    ./services.sh up
    ```
   or with docker:
    ```shell
    docker compose up --build
    ```
3. *(Optional)* To stop all microservices use a script:
    ```shell
    ./services.sh down
    ```
   or with docker:
    ```shell
   docker compose down
    ```

#### `.env` file configuration
```properties
# common settings
## postgres
SPRING_DATASOURCE_HOST=HOSTNAME (e.g., localhost)
SPRING_DATASOURCE_PORT=PORT_NUMBER (DEFAULT: 5432)
SPRING_DATASOURCE_USERNAME=USERNAME
SPRING_DATASOURCE_PASSWORD=PASSWORD
## redis
SPRING_REDIS_HOST=HOSTNAME (e.g., localhost)
SPRING_REDIS_PORT=PORT_NUMBER (DEFAULT: 6379)
SPRING_REDIS_TIMEOUT=2000ms
## kafka
SPRING_KAFKA_HOST=HOSTNAME (e.g., localhost)
SPRING_KAFKA_PORT=PORT_NUMBER (DEFAULT:9092)
### producer
SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
### consumer
SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER=org.apache.kafka.common.serialization.StringDeserializer
SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER=org.springframework.kafka.support.serializer.JsonDeserializer
SPRING_KAFKA_CONSUMER_MONITORING_GROUP_ID=service-monitoring-group (CAN BE CHANGED)
SPRING_KAFKA_CONSUMER_NOTIFICATIONS_GROUP_ID=service-notifications-group (CAN BE CHANGED)
### kafka topics
APP_KAFKA_TOPIC_USER_FAVORITES=user_favorites_topic (CAN BE CHANGED)
APP_KAFKA_TOPIC_USER_NOTIFICATIONS=user_notifications_topic (CAN BE CHANGED)
APP_KAFKA_TOPIC_USER_PRICE_ALERTS=user_price_alerts_topic (CAN BE CHANGED)
## mail
SPRING_MAIL_HOST=HOST (e.g., smtp.gmail.com)
SPRING_MAIL_PORT=PORT_NUMBER (DEFAULT: 587)
SPRING_MAIL_USERNAME=USERNAME
SPRING_MAIL_PASSWORD=PASSWORD
## allowed origins
CORS_ALLOWED_ORIGINS=ALLOWED_ORIGINS (e.g., React: http://localhost:3000,http://192.168.1.72:3000)
## path to images (folder will be created locally in service-users)
## path: service-users/uploads/avatars
PATH_TO_IMAGES=./uploads/avatars

# services
## service-api-gateway
API_GATEWAY_PORT=PORT_NUMBER (DEFAULT: 8080)

## service-search
SERVICE_SEARCH_PORT=PORT_NUMBER (DEFAULT: 8081)

## service-users
SERVICE_USERS_PORT=PORT_NUMBER (DEFAULT: 8082)
SERVICE_USERS_DB_NAME=DB_NAME (DEFAULT: service-users)
SKIP_CODE_VERIFICATION=false (DEFAULT: false) (Disable connection to mail and OTP code varification)
JWT_SECRET=JWT_SECRET
JWT_ACCESS_TOKEN_EXPIRATION_MS=900000 # 15 minutes
JWT_REFRESH_TOKEN_EXPIRATION_MS=604800000 # 7 days

## service-monitoring
SERVICE_MONITORING_PORT=PORT_NUMBER (DEFAULT: 8083)
SERVICE_MONITORING_DB_NAME=DB_NAME (DEFAULT: service-monitoring)

## service-notifications
SERVICE_NOTIFICATIONS_PORT=PORT_NUMBER (DEFAULT: 8084)
SERVICE_NOTIFICATIONS_DB_NAME=DB_NAME (DEFAULT: service-notifications)
DISABLE_NOTIFICATIONS_GENERAL=false (DEFAULT: false) (Disable connection to mail and sending notifications)
TEST_NOTIFICATIONS_EMAIL=TEST_EMAIL
```

### Response
For better usage all the microservices use the same template of response:

#### Success
```json
{
    "timestamp": "2025-05-06T22:01:37.380081165",
    "status": "success",
    "message": "Info message",
    "data": []
}
```

#### Error
```json
{
    "timestamp": "2025-05-06T22:02:01.407325821",
    "status": "error",
    "message": "Error message",
    "data": null
}
```
