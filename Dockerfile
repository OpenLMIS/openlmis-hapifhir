FROM openlmis/service-base:3

COPY build/libs/*.jar /service.jar
COPY build/consul /consul