FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/consul /consul