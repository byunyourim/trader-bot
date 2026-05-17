# Infra — Claude 작업 지침

---

## 서비스 구성

| 서비스 | 이미지 | 포트 | 역할 |
|---|---|---|---|
| PostgreSQL 16 | `postgres:16-alpine` | 5432 | 거래 기록(Trade) JPA 동기 영속화 |
| Redis 7 | `redis:7-alpine` | 6379 | KIS 액세스 토큰 캐시 (`kis:access-token`) |

- 두 서비스 모두 `restart: unless-stopped` — 수동 종료 전까지 자동 재시작
- Redis는 `appendonly yes` — 컨테이너 재시작 시 데이터 유지
- 볼륨: `postgres_data`, `redis_data` — Docker 관리 named volume

---

## 환경변수 (.env)

루트의 `.env` 파일로 관리한다. **절대 커밋하지 말 것.**
전체 변수 목록은 `.env.example` 참고 — 복사 후 값을 채워 `.env`로 사용한다.

```bash
cp .env.example .env
```

| 변수 | 설명 |
|---|---|
| `POSTGRES_HOST` / `POSTGRES_PORT` / `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | PostgreSQL 접속 정보 |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | Redis 접속 정보 |
| `SERVER_PORT` | 백엔드 포트 (기본 8080) |
| `KIS_BASE_URL` | KIS REST API 베이스 URL |
| `KIS_WS_URL` | KIS WebSocket URL |
| `KIS_APP_KEY` / `KIS_APP_SECRET` | KIS API 키 (apiportal.koreainvestment.com 발급) |
| `KIS_ACCOUNT_NO` / `KIS_ACCOUNT_PRODUCT_CODE` | 계좌번호 / 상품코드(일반: 01) |
| `KIS_PAPER_TRADING` | `true`=모의투자, `false`=실전투자 |
| `BACKEND_URL` | 프론트엔드에서 바라보는 백엔드 URL |

---

## 명령어

```bash
# 기동 (백그라운드)
docker compose -f infra/docker-compose.yml --env-file .env up -d

# 로그 확인
docker compose -f infra/docker-compose.yml logs -f

# 개별 서비스 로그
docker compose -f infra/docker-compose.yml logs -f postgres
docker compose -f infra/docker-compose.yml logs -f redis

# 종료 (데이터 유지)
docker compose -f infra/docker-compose.yml down

# 종료 + 볼륨 삭제 (데이터 초기화)
docker compose -f infra/docker-compose.yml down -v

# 헬스체크 상태 확인
docker compose -f infra/docker-compose.yml ps
```

---

## 헬스체크

두 서비스 모두 헬스체크가 설정되어 있다. 백엔드 기동 전 `healthy` 상태인지 확인한다.

```bash
# 상태 확인 (STATUS 컬럼이 healthy 이어야 함)
docker ps --format "table {{.Names}}\t{{.Status}}"
```

| 서비스 | 헬스체크 방식 |
|---|---|
| PostgreSQL | `pg_isready -U {USER} -d {DB}` |
| Redis | `redis-cli ping` |

---

## 주의

- `.env` 파일은 절대 커밋하지 말 것 (`.gitignore`에 등록 확인)
- `KIS_PAPER_TRADING=false` 로 변경 시 **실제 계좌로 주문**이 나간다 — 반드시 확인 후 변경
- 볼륨 삭제(`down -v`)는 DB 데이터 전체 초기화 — 운영 환경에서 절대 실행 금지
- docker-compose.yml 수정 시 `infra/CLAUDE.md`의 서비스 표도 함께 업데이트
