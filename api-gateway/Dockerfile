# --- Packaging --- #
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY common-library/pom.xml ./common-library/
COPY service-monitoring/pom.xml ./service-monitoring/
COPY service-notifications/pom.xml ./service-notifications/
COPY service-search/pom.xml ./service-search/
COPY service-users/pom.xml ./service-users/

RUN mvn dependency:go-offline --batch-mode

COPY api-gateway ./api-gateway/
COPY common-library ./common-library/
COPY service-monitoring ./service-monitoring/
COPY service-notifications ./service-notifications
COPY service-search ./service-search/
COPY service-users ./service-users/

RUN mvn package -pl api-gateway -am -DskipTests -Dmaven.test.skip=true --batch-mode

# --- Execution --- #
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/api-gateway/target/api-gateway.jar /app/api-gateway.jar
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]