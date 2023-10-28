FROM eclipse-temurin:19-alpine as findik-sepeti-dependency

#Set the working directory within the container
WORKDIR /workdir/server

#Copy the gradle files
COPY build.gradle /workdir/server/build.gradle
COPY settings.gradle /workdir/server/settings.gradle
COPY gradlew /workdir/server/gradlew
COPY gradle /workdir/server/gradle

# Build the Gradle application
RUN ./gradlew clean dependencies

FROM findik-sepeti-dependency as findik-septi-build
WORKDIR /workdir/server
#Copy the source code
COPY src /workdir/server/src

RUN ./gradlew clean assemble
CMD ["java", "-jar", "./build/libs/findiksepeti-1.0.0.jar"]

RUN mkdir -p "/workdir/server/uploads"

EXPOSE 8080
