FROM openlmis/service-base:6.1

COPY build/libs/*.jar /service.jar
COPY build/consul /consul