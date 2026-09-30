# Fáze 1: Sestavení aplikace v Maven kontejneru s JDK 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Zkopírování definice projektu a zdrojových kódů
COPY pom.xml .
COPY src ./src

# Přímé sestavení spustitelného JAR balíčku (bez stahování nepoužitých cloudových pluginů)
RUN mvn clean package -DskipTests

# Fáze 2: Minimální běhové prostředí s JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Přidání neprivilegovaného uživatele pro bezpečný provoz
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
