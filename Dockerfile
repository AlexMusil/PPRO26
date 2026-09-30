# Fáze 1: Sestavení aplikace v Maven kontejneru s JDK 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Nejprve zkopírujeme definici závislostí pro využití Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Zkopírování zdrojového kódu a sestavení balíčku
COPY src ./src
RUN mvn clean package -DskipTests

# Fáze 2: Minimální běhové prostředí s JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Přidání neprivilegovaného uživatele pro bezpečný běh
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
