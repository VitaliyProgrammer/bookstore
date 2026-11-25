# Production image, built by Render (and locally) straight from the repository.

# 1) Build the jar inside Docker: the host does not need Java or Maven, and target/ is not in git.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
# Dependencies first: this layer is cached until pom.xml changes, so code-only changes rebuild fast
COPY pom.xml checkstyle.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Tests and checkstyle already run in CI on every pull request; Testcontainers has no Docker here
RUN mvn -B -q package -DskipTests -Dcheckstyle.skip

# 2) Split the fat jar into layers (dependencies change rarely, application code often)
FROM eclipse-temurin:17-jre AS layers
WORKDIR /layers
COPY --from=build /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# 3) Runtime: JRE only (no Maven, no JDK), non-root user
FROM eclipse-temurin:17-jre
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring
WORKDIR /application
COPY --from=layers /layers/dependencies/ ./
COPY --from=layers /layers/spring-boot-loader/ ./
COPY --from=layers /layers/snapshot-dependencies/ ./
COPY --from=layers /layers/application/ ./
# Free hosting gives ~512 MB: size the heap from the container limit, use the leanest GC,
# and restart cleanly instead of limping on after an OutOfMemoryError
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
