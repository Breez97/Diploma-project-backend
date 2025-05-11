# --- Packaging ---
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY service-search/pom.xml ./service-search/
COPY service-users/pom.xml ./service-users/
COPY common-library/pom.xml ./common-library/

RUN mvn dependency:go-offline --batch-mode

COPY api-gateway ./api-gateway/
COPY service-search ./service-search/
COPY service-users ./service-users/
COPY common-library ./common-library/

RUN mvn package -pl service-search -am -DskipTests -Dmaven.test.skip=true --batch-mode

# --- Execution ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/service-users/target/service-users.jar /app/service-users.jar
ENTRYPOINT ["java", "-jar", "/app/service-users.jar"]