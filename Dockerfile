FROM amazoncorretto:21-alpine

COPY build/libs/vpn-bridge-1.0.0.jar vpn-bridge-1.0.0.jar

ENTRYPOINT ["java","-jar","/vpn-bridge-1.0.0.jar"]