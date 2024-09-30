FROM openlmis/service-base:7

COPY build/libs/*.jar /service.jar
COPY build/consul /consul