FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Datadog APM 자바 에이전트.
# 코드를 고치지 않고 Spring MVC, JDBC, MongoDB, Redis 호출을 트레이스로 잡는다.
# 버전을 고정하려면 아래 주소를 maven central 주소로 바꾸면 된다.
# https://repo1.maven.org/maven2/com/datadoghq/dd-java-agent/<버전>/dd-java-agent-<버전>.jar
ADD --chown=spring:spring https://dtdg.co/latest-java-tracer /app/dd-java-agent.jar

COPY --from=builder --chown=spring:spring /workspace/build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080

# 에이전트는 DD_AGENT_HOST를 못 찾으면 트레이스만 포기하고 앱은 그대로 뜬다.
# 로컬에서 Datadog 없이 돌릴 때는 DD_TRACE_ENABLED=false를 넘기면 조용해진다.
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "/app/app.jar"]
