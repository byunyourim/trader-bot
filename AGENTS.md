# Trader Bot — Codex 작업 지침

한국투자증권(KIS) Open API 기반 자동매매·모니터링 대시보드.
Codex는 이 파일을 지침으로 사용한다. Claude는 `CLAUDE.md` 를 쓰며, 두 지침은 같은 규칙을 공유한다.

---

## 프로젝트 목표

기능 완성 자체보다 **개발 과정의 역량**에 초점을 둔다.

1. 설계 능력 향상 — AI에 다 맡기지 않고, 확장성 있는 설계를 사용자가 주도한다.
2. 코딩 능력 향상
3. AI 활용 — 클로드/코덱스로 의사결정·구현을 잘 활용 (다 AI로 찍어내는 게 목표가 아님).
4. 의사결정 기록 — 결정을 남겨 이어서 작업 가능하게 (`docs/adr/`).
5. 인프라 개선

→ 기능을 빨리 찍어내기보다 **설계 대안을 제시하고 사용자가 결정하도록 돕는다.**

---

## 언어

- 대화·설명: 한국어
- 코드·변수명·주석: 영어
- 커밋 메시지: 한국어, Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, `chore:`)

## Git (중요)

- **커밋·푸시·PR 머지를 직접 실행하지 않는다.** 명령어만 제시하고 사용자가 수행한다.
- PR 생성은 사용자가 요청하면 도와줄 수 있다.

## 코딩 원칙

- 요청한 범위만 수정. 주변 코드 임의 리팩토링 금지.
- 불필요한 주석·docstring 금지. 불필요한 코드 만들지 않기 (YAGNI, 최소 구현).
- 하드코딩 금지: 자격증명·URL·설정값은 환경변수/설정 파일로 주입.
- 로그에 개인정보·비밀정보(비밀번호·키·토큰·계좌번호·PII) 절대 남기지 않기. 에러 메시지 경유 유출 포함.
- 큰 구조 변경(모노레포 분리 등)은 사용자 명시 지시 없이 제안만, 실행 금지.
- 기존 파일 이동·삭제 전 확인.

---

## 스택 요약

- Backend: Java 21, Spring Boot 3.5, WebFlux, Spring Data JPA, Lombok
- Frontend: Next.js 15 (App Router), TypeScript, React 19, Tailwind
- DB: PostgreSQL 16 (JPA, 동기), Redis 7 (Reactive)
- Broker: 한국투자증권 Open API (REST + WebSocket)
- Infra: Docker Compose

## 아키텍처 (DDD 레이어)

`backend/src/main/java/com/byunyourim/traderbot/` 아래:

```
presentation/   # 컨트롤러 (REST/WS)
application/     # 유스케이스
domain/          # 도메인 모델 (account/market/order) — 빈혈 모델 금지
infra/           # KIS 어댑터, DB 영속화
```

- 의존 방향: presentation → application → domain, infra → domain. domain은 다른 레이어를 모른다.
- 금액·수량은 `BigDecimal`. KIS 호출은 infra 어댑터를 경유.

## 개발 명령어

| 작업 | 명령 |
|---|---|
| 인프라 기동 | `docker compose -f infra/docker-compose.yml --env-file .env up -d` |
| 백엔드 실행 | `cd backend && ./gradlew bootRun` |
| 백엔드 테스트 | `cd backend && ./gradlew test` |
| 컴파일 확인 | `cd backend && ./gradlew compileJava` |
| 프론트 실행 | `cd frontend && npm run dev` |

구현 후 `./gradlew compileJava` (또는 `test`) 로 빌드 통과를 확인한다.

---

## 작업 흐름

이슈 기반으로 진행한다.

```
0. 설계 논의
1. GitHub 이슈 생성 (스펙 요약 첨부)
2. 브랜치 생성
3. 구현 계획
4. 구현 + 테스트 (기능 단위로 함께)
5. 리뷰
6. PR → develop 머지 (본문에 Closes #N)
7. 머지되면 이슈 자동 닫힘 + 브랜치 삭제
```

### 브랜치 규칙

`type/slug` (이슈 번호는 넣지 않는다. 이슈-PR 연결은 PR 본문 `Closes #N` 이 담당).

- 타입: `feat/` `fix/` `refactor/` `docs/` `chore/`
- 슬러그: 영어 소문자 + 하이픈, 2~4단어. 예: `feat/order-execution`

### 의사결정 기록 (ADR)

설계·기술 결정이 확정되면 `docs/adr/<area>/YYYY-MM-DD-<english-slug>.md` 로 남긴다.

- area: market / order / account / infra / arch
- 불변(immutable) + supersede: 옛 ADR 수정 금지, 새 ADR로 대체
- 본문 한글. 템플릿·규칙: `docs/adr/template.md`, `docs/adr/README.md`
