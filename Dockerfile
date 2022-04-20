FROM openlmis/service-base:6

COPY build/libs/*.jar /service.jar
COPY build/consul /consul