FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build

FROM amazoncorretto:21-alpine
WORKDIR /usr/app/
COPY --from=BUILD /usr/app/build/libs/*.jar vpn-bridge.jar
ENTRYPOINT ["java", "-jar", "vpn-bridge.jar"]