FROM registry.gitlab.com/logius/beveiligd-koppelvlak-extern/base-images/eclipse-temurin:17-jre-alpine-opentelemetry

USER root

# Create user to comply to numeric UID for k8s. This workaround should be adopted in the base image.
RUN addgroup throttling && adduser -S -h /home/throttling -G throttling -u 1001 throttling && \
  chown -R throttling:throttling /opt/app && \
  chown -h throttling:throttling /home/throttling

USER 1001

COPY --chown=throttling:throttling target/*.jar /opt/app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-javaagent:opentelemetry-javaagent.jar","-XX:MaxRAMPercentage=70","-jar","/opt/app/app.jar"]
