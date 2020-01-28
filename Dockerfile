FROM openjdk:11 AS builder

COPY target/*.jar /app/app.jar
COPY jlink.sh /app/jlink.sh

WORKDIR /app
RUN ./jlink.sh

FROM openjdk:11-jre-slim

RUN rm -rf /usr/local/openjdk-11
COPY --from=builder /app/deps/jre-jlink /usr/local/openjdk-11
COPY --from=builder /app/app.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]