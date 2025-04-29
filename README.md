# Diploma Project

## API Documentation

```
http://localhost:8080/api-docs
```

## Local run
`.env` file configuration
```properties
# common settings
DB_HOST=HOSTNAME
DB_PORT=PORT_NUMBER (DEFAULT:5432)
DB_USER=USERNAME
DB_PASSWORD=PASSWORD

# --- PORTS ---
# service-api-gateway
API_GATEWAY_PORT=PORT_NUMBER (DEFAULT: 8080)

# service-vaadin
SERVICE_VAADIN_PORT=PORT_NUMBER (DEFAULT: 8081)

# service-search
SERVICE_SEARCH_PORT=PORT_NUMBER (DEFAULT: 8082)

# service-users
SERVICE_USERS_PORT=PORT_NUMBER (DEFAULT: 8083)
SERVICE_USERS_DB_NAME=DN_NAME
TOKEN_SIGN_IN_KEY=SECRET_VALUE
```