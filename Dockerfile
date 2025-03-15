FROM amazoncorretto:21-alpine

COPY build/libs/vpn-bridge-1.0.0.jar vpn-bridge-1.0.0.jar

ENV JMX_PORT=9010
ENV JAVA_OPTS="-Dcom.sun.management.jmxremote=true \
               -Dcom.sun.management.jmxremote.port=$JMX_PORT \
               -Dcom.sun.management.jmxremote.ssl=false \
               -Dcom.sun.management.jmxremote.authenticate=false"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /vpn-bridge-1.0.0.jar"]