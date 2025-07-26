FROM gradle:latest AS BUILD
WORKDIR /usr/app/

COPY . .
COPY /root/myscript/wireguard-install/wireguard-install.sh wireguard-install.sh

RUN gradle build

FROM amazoncorretto:21-alpine
WORKDIR /usr/app/

COPY --from=BUILD /usr/app/build/libs/*.jar vpn-bridge.jar

ENTRYPOINT ["java", "-jar", "vpn-bridge.jar"]