# Trader Bot — Claude 작업 지침

한국투자증권(KIS) Open API 기반 자동매매·모니터링 대시보드.
이 파일은 모든 세션에서 자동 로드된다. 컨벤션 위반 작업을 하기 전에 사용자에게 확인할 것.

---

## 스택 요약

- **Backend**: Java 21, Spring Boot 3.5, WebFlux, Spring Data JPA, Lombok → `backend/CLAUDE.md`
- **Frontend**: Next.js 15 (App Router), TypeScript, React 19, Tailwind → `frontend/CLAUDE.md`
- **DB**: PostgreSQL 16 (JPA, 동기), Redis 7 (Reactive)
- **Broker**: 한국투자증권 Open API (REST + WebSocket)
- **Infra**: Docker Compose → `infra/CLAUDE.md`

---

## 개발 명령어

| 작업 | 명령 |
|---|---|
| 인프라 기동 | `docker compose -f infra/docker-compose.yml --env-file .env up -d` |
| 백엔드 실행 | `cd backend && ./gradlew bootRun` |
| 백엔드 테스트 | `cd backend && ./gradlew test` |
| 컴파일 확인 | `cd backend && ./gradlew compileJava` |
| 프론트 실행 | `cd frontend && npm run dev` |

---

## Custom Agents & Commands

### Sub-agents (`.claude/agents/`)

| Agent | 호출 시점 |
|---|---|
| `kis-parser` | KIS 응답 필드 매핑, 신규 엔드포인트 연동 |
| `backtest-analyst` | 백테스트 코드 작성·결과 분석 |
| `market-data-agent` | 시세 REST·WebSocket fan-out 구현 |
| `order-executor-agent` | 주문 실행·취소·조회 도메인/인프라 |

### 슬래시 커맨드 (`.claude/commands/`)

| 커맨드 | 설명 |
|---|---|
| `/check-arch` | DDD 레이어 의존성 위반 grep 검사 |
| `/scaffold <context>` | 새 DDD 컨텍스트 빈 패키지 트리 생성 |
| `/impl-p1` | P1 엔드포인트 구현 상태 체크 + 다음 작업 안내 |

---

## 작업 흐름

이슈 기반으로 진행한다.

```
0. 설계 논의 (brainstorm → 스펙)
1. GitHub 이슈 생성 (스펙 요약 첨부)
2. 브랜치 생성
3. 구현 계획 (plan)
4. 구현 + 테스트 (기능 단위로 함께)
5. 리뷰
6. PR → develop 머지 (본문에 `Closes #N`)
7. 머지되면 이슈 자동 닫힘 + 브랜치 삭제
```

### 브랜치 규칙

`type/slug` (이슈 번호는 넣지 않는다. 이슈-PR 연결은 PR 본문 `Closes #N` 이 담당).

- 타입: `feat/` `fix/` `refactor/` `docs/` `chore/`
- 슬러그: 영어 소문자 + 하이픈, 2~4단어
- 예: `feat/adr-recording`, `fix/order-timeout`

### 의사결정 기록

설계·기술 결정이 확정되면 `/adr` 로 ADR 문서를 남긴다 (`docs/adr/`). 규칙은 `docs/adr/README.md`.

## 공통 주의

- 모노레포 분리(backend/batch/common) 같은 큰 구조 변경은 사용자 명시 지시 없이 제안만, 실행 금지
- 기존 파일을 옮기거나 삭제할 때는 먼저 확인
