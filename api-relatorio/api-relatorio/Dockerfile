FROM eclipse-temurin:21-jre

WORKDIR /

COPY target/*.jar relatorio.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","relatorio.jar"]