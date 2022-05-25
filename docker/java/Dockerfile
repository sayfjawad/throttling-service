FROM eclipse-temurin:11

ARG OPEN_TELEMETRY_JAVA_AGENT_VERSION=v1.10.0
ARG OPEN_TELEMETRY_JAVA_AGENT=opentelemetry-javaagent.jar

USER root

RUN apt update -y && apt install -y wget
RUN wget --no-check-certificate https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/${OPEN_TELEMETRY_JAVA_AGENT_VERSION}/${OPEN_TELEMETRY_JAVA_AGENT} -O opentelemetry-javaagent.jar

RUN mkdir -p /opt/app

COPY docker/java/config/ .

# Create user to comply to numeric UID for k8s. This workaround should be adopted in the base image.
RUN groupadd throttling && useradd -rm -g throttling -d /home/throttling -u 1001 throttling && \
  chown -R throttling:throttling /opt/app && \
  chown -h throttling:throttling /home/throttling

RUN apt remove -y wget && \
 rm -rf /var/cache/apt/*

USER 1001

COPY target/*.jar app.jar

EXPOSE 9999
ENTRYPOINT ["java","-javaagent:opentelemetry-javaagent.jar","-XX:MaxRAMPercentage=70","-jar","/app.jar"]
