FROM openjdk:11-oracle

ENTRYPOINT java -Dfile.encoding=UTF8 -jar /app.jar

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar