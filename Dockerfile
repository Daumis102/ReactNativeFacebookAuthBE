# Building the app
FROM eclipse-temurin:21-jdk-alpine as build

RUN addgroup demogroup; adduser  --ingroup demogroup --disabled-password demo # Create user for limiting Docker not to run as root
USER demo

WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN ./gradlew build -x test
RUN #mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../*.jar)

# Running the app
FROM eclipse-temurin:21-jdk-alpine

RUN addgroup demogroup; adduser  --ingroup demogroup --disabled-password demo # Create user for limiting Docker not to run as root
USER demo

VOLUME /tmp
#ARG DEPENDENCY=/workspace/app/build/libs/dependency
## Copy files from build
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

COPY --from=build /workspace/app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app/app.jar", "--FACEBOOK_APP_ID=${FACEBOOK_APP_ID_ENV}","--FACEBOOK_APP_CLIENT_SECRET=${FACEBOOK_APP_CLIENT_SECRET_ENV}","--spring.profiles.active=dev"]
