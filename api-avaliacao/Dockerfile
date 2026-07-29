FROM eclipse-temurin:21-jre

WORKDIR /

COPY target/*.jar avaliacao.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","avaliacao.jar"]