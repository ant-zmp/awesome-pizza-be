FROM amazoncorretto:21
WORKDIR /app
COPY target/awesome-pizza-be-1.3.0.jar /app/app.jar
CMD ["java","-jar","app.jar"]
EXPOSE 8080
