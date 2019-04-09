FROM openjdk:8
VOLUME /tmp
ADD target/flux-of-tweet-app-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch app.jar'
ENTRYPOINT ["java","-jar","app.jar"]