FROM amazoncorretto:23-alpine
LABEL maintainer="jpierresamra@gmail.com"
VOLUME /Dentist
ADD /target/dentist-service-customer-0.0.1.jar app.jar
#RUN apt-get update && apt-get install -y curl
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
CMD ["echo","Dentist Customer Service Image Created"] 