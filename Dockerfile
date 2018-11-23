FROM openlmis/service-base:2

COPY build/libs/*.jar /service.jar
COPY build/consul /consul