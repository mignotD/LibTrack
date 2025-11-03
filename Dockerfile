FROM maven:3.8-openjdk-11 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline
COPY backend/src ./src
RUN mvn clean package -DskipTests

FROM node:18-alpine AS frontend-build
WORKDIR /app
COPY frontend/package.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=backend-build /app/target/libtrack-api.jar app.jar
COPY --from=frontend-build /app/dist /app/static
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
