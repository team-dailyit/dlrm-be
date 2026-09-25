# 들름 개발 가이드

## 프로젝트 소개

주변에 들를만한 곳을 추천해주는 지도 기반 서비스.

## 기술 스택

- 언어/프레임워크: Java 21, Spring Boot
- 빌드: Gradle (멀티모듈)
- DB: PostgreSQL + PostGIS, Flyway

## 실행 명령어

Docker가 실행 중이어야 한다. (`bootRun`은 docker compose로 DB를 띄우고, 테스트는 Testcontainers를 쓴다)

- 서비스(개발): `./gradlew :service:bootRun`
- 어드민(개발): `./gradlew :admin:bootRun`

## 프로젝트 규칙

### 핵심 디렉토리 구조

`core`(공통 도메인/설정), `service`, `admin` 세 모듈로 구성된 Gradle 멀티모듈 프로젝트다. 상세 구조는 `docs/folder-structure.md` 참고.

### 기타 규칙

필요할 때 해당 문서를 읽고 따른다.

- 폴더 구조 (`docs/folder-structure.md`): 새 파일·패키지를 만들거나 위치를 정할 때
- 코드 컨벤션 (`docs/code-convention.md`): 코드를 작성하거나 수정할 때
- 커밋 컨벤션 (`docs/commit-convention.md`): 브랜치 생성, 커밋, PR 작성 시
- 용어집 (`docs/glossary.md`): 도메인 개념에 이름을 붙이거나 새 용어가 생길 때

## 개발 흐름

1. JIRA 티켓 생성
2. [`start-task`] 티켓 보완, 브랜치 생성
3. 개발 진행
4. [`review-task`] 검증 + 코드 리뷰 (지적 사항을 반영하고, 필요하면 다시 실행)
5. [`commit-changes`] 커밋
6. [`create-pr`] PR 생성

SKILL이 있는 단계는 SKILL을 사용한다. 사용자가 현재 단계 작업을 마친 것으로 보이면, 다음 단계의 스킬 사용을 먼저 제안할 것.

hotfix(`main`에서 분기하는 긴급 수정)는 `start-task`, `create-pr`을 쓰지 않고 해당 단계를 수동으로 진행한다. `review-task`(`main` 기준)와 `commit-changes`는 쓸 수 있다. 자세한 내용은 `docs/commit-convention.md`의 hotfix 절 참고.

## 하지 말 것

<!--
  이 섹션은 아직 확정 안 됨. 팀 논의 후 점차 업데이트 예정
-->
