# Build stage
FROM amazoncorretto:21-alpine
RUN gradle clean build

# Runtime stage
FROM amazoncorretto:21-alpine
COPY --from=build /build/libs/*.jar vpn-bridge.jar
ENTRYPOINT ["java", "-jar", "vpn-bridge.jar"]