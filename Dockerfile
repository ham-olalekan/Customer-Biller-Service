FROM openjdk:8-jdk-alpine as builder

ENV WORKDIR=/srv/app

RUN mkdir -p $WORKDIR

ENV PORT=8080

WORKDIR $WORKDIR

COPY build.gradle settings.gradle gradlew gradle.properties ./
COPY gradle ./gradle
COPY src ./src

RUN ./gradlew build

FROM openjdk:8-jdk-alpine
ENV WORKDIR=/srv/app

RUN mkdir -p $WORKDIR
WORKDIR $WORKDIR
copy --from=builder /srv/app/build/libs/*-all.jar ./app.jar
COPY config.yml ./config.yml

EXPOSE 8080

CMD java -jar -Djava.security.egd=file:/dev/./urandom -Dserver.port=$PORT app.jar server config.yml
