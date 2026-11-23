# syntax=docker/dockerfile:1

# 빌드 단계: 멀티모듈이라 core·admin까지 포함한 전체 프로젝트가 필요하다
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
ARG MODULE=service
COPY . .
# 테스트(Testcontainers)와 Spotless(git 이력 필요)는 CI에서 하므로 jar만 만든다
# service는 plain jar를 만들지 않지만, 다른 모듈을 빌드할 때를 위해 plain jar를 걸러낸다
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :${MODULE}:bootJar --no-daemon \
    && find ${MODULE}/build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} app.jar \;

# 실행 단계
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /app/app.jar app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=prod
# 로그와 JVM 기본 시간대용 (영업 중 판단은 ClockConfig에서 Asia/Seoul로 따로 고정한다)
ENV TZ=Asia/Seoul
# 힙 최대 크기를 컨테이너 메모리의 75%로 제한한다 (기본값 25%)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
