# Build stage
FROM amazoncorretto:21-alpine
RUN gradle clean build

# Runtime stage
FROM amazoncorretto:21-alpine
COPY /build/libs/*.jar vpn-bridge.jar
ENTRYPOINT ["java", "-jar", "vpn-bridge.jar"]