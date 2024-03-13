#FROM gradle:8.6-jdk-graal-jammy
#COPY ./ ./
#RUN gradle clean build --no-daemon
#CMD java -jar build/libs/*.jar

# 빌드
FROM gradle:8.6-jdk-graal-jammy AS builder
RUN mkdir -p /build

# 작업 디렉토리 설정
WORKDIR /build

# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

COPY . /build
RUN gradle build -x test --parallel

# 실행
FROM ghcr.io/graalvm/jdk-community:17

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /build/build/libs/*.jar app.jar

ENTRYPOINT [                                                \
    "java",                                                 \
    "-jar",                                                 \
    "-Dspring.profiles.active=deploy",                      \
    "-Dsun.net.inetaddr.ttl=0",                             \
    "app.jar"              \
]