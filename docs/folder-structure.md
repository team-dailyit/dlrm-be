# 폴더 구조

## 모듈 구성

Gradle 멀티모듈 프로젝트로, `core` / `service` / `admin` 세 모듈로 구성된다.

- `core`: 서비스·어드민 공통 도메인/설정 (`java-library`, 단독 실행 불가)
- `service`: 사용자용 API 서버 (Spring Boot 애플리케이션)
- `admin`: 운영자용 어드민 API 서버 (Spring Boot 애플리케이션)

`service`, `admin`은 각자 `core`에 의존한다.

## core 패키지 구조

- `config`: 공통 설정 및 빈 (전역 예외 처리, 공통 응답 포맷 등)
- `domain`: JPA 엔티티
- `repository`: Spring Data JPA 레포지토리

## core 리소스·테스트 지원

- `src/main/resources/db/migration`: Flyway 마이그레이션. DB 스키마는 `core`에서만 관리하므로 `service`/`admin`에 두지 않는다.
- `src/testFixtures/.../testsupport`: 모듈 공통 테스트 지원 (`PostgresTestContainer` 등). `service`/`admin` 테스트는 `testFixtures(project(':core'))`로 가져다 쓴다.

## service / admin 패키지 구조

<!--
  아직 확정 안 됨. 현재는 각 모듈에 Spring Boot 진입점(`ServiceApplication`, `AdminApplication`)만 있는 상태.
  Controller/Service 등 세부 패키지 컨벤션은 개발하면서 점차 채울 예정.
-->

## 디렉토리 트리

```
dlrm/
├── core/
│   └── src/
│       ├── main/java/com/dailyit/dlrm/core/
│       │   ├── config/       # 공통 설정 및 빈
│       │   ├── domain/       # JPA 엔티티
│       │   └── repository/   # Spring Data JPA 레포지토리
│       ├── main/resources/db/migration/                     # Flyway 마이그레이션
│       └── testFixtures/java/com/dailyit/dlrm/core/testsupport/  # 공통 테스트 지원
├── service/
│   └── src/main/java/com/dailyit/dlrm/service/
│       └── ServiceApplication.java
└── admin/
    └── src/main/java/com/dailyit/dlrm/admin/
        └── AdminApplication.java
```
