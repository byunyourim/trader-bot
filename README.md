# 📈 Trader Bot

한국투자증권 Open API 기반 자동매매/모니터링 대시보드.

---

## 🧱 Tech Stack

### Backend
- Java 21
- Spring Boot 3.5
- Spring WebFlux
- Spring Scheduling
- Spring Data JPA
- PostgreSQL
- Redis (Reactive)

### Frontend
- Next.js 15 (App Router)
- TypeScript
- React 19
- Tailwind CSS
- lightweight-charts

### Infra
- Docker / Docker Compose

### Broker API
- **Korea Investment Open API**
  - REST API: 주문, 잔고/계좌 조회
  - WebSocket API: 실시간 시세 · 체결 데이터

---

## 📁 프로젝트 구조

```
trader-bot/
├── backend/                # Spring Boot 서버
│   ├── build.gradle
│   └── src/main/java/com/byunyourim/traderbot/
│       ├── TraderBotApplication.java
│       ├── config/         # KisProperties, WebClientConfig
│       ├── kis/            # KisAuthService, KisRestClient, KisWebSocketClient
│       ├── account/        # 잔고/계좌 도메인
│       ├── order/          # 주문 도메인 (TBD)
│       ├── market/         # 시세 도메인 (TBD)
│       ├── scheduling/     # @Scheduled 작업
│       └── domain/         # JPA 엔티티 + Repository
│
├── frontend/               # Next.js 대시보드
│   ├── package.json
│   └── src/
│       ├── app/            # App Router (layout, page, globals.css)
│       └── components/     # PriceChart 등
│
├── infra/
│   └── docker-compose.yml  # PostgreSQL + Redis
│
├── .env.example
├── .gitignore
└── README.md
```

---

## ⚙️ 시작하기

### 1. 환경 변수 설정

```bash
cp .env.example .env
# .env 열어서 KIS_APP_KEY / KIS_APP_SECRET / KIS_ACCOUNT_NO 입력
```

> 🔑 한국투자증권 Open API 키 발급: https://apiportal.koreainvestment.com

### 2. 인프라(DB) 띄우기

```bash
docker compose -f infra/docker-compose.yml --env-file .env up -d
```

- PostgreSQL → `localhost:5432`
- Redis → `localhost:6379`

### 3. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

→ http://localhost:8080

테스트 엔드포인트:
- `GET /actuator/health` — 헬스체크
- `GET /api/accounts/balance` — 잔고 조회 (KIS 키 설정 필요)

### 4. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

→ http://localhost:3000

`next.config.mjs`의 rewrites 설정으로 `/api/*` 요청이 백엔드(`localhost:8080`)로 프록시됩니다.

---

## 🔌 Korea Investment Open API 연동

### REST 흐름

```
Client → POST /oauth2/tokenP  (appkey, appsecret)
       ← access_token (24h 유효)
       → Redis 캐시 저장
       → 이후 API 호출 시 Authorization 헤더에 부착
```

- `KisAuthService` — 토큰 발급 + Redis 캐싱
- `KisRestClient` — 잔고/주문 등 REST 호출 래퍼

### WebSocket 흐름

```
Client → POST /oauth2/Approval  (appkey, appsecret)
       ← approval_key
       → WS 연결 후 subscribe 메시지 전송
       ← 실시간 체결/호가 푸시
```

- `KisWebSocketClient` — WebSocket 연결 + 구독 관리 (스켈레톤)

### 모의/실전 구분
- `.env`의 `KIS_PAPER_TRADING=true` → 모의투자 (tr_id에 `V` 접두사)
- `false` → 실전투자

---

## 🛠️ 개발 명령어 모음

| 명령 | 위치 | 설명 |
|------|------|------|
| `docker compose -f infra/docker-compose.yml up -d` | root | DB 컨테이너 실행 |
| `docker compose -f infra/docker-compose.yml down` | root | DB 컨테이너 종료 |
| `./gradlew bootRun` | backend | Spring Boot 개발 모드 |
| `./gradlew test` | backend | 단위 테스트 |
| `./gradlew build` | backend | jar 빌드 |
| `npm run dev` | frontend | Next.js 개발 서버 |
| `npm run build` | frontend | 프로덕션 빌드 |

---

## 📚 참고 문서

- [Korea Investment Open API 포털](https://apiportal.koreainvestment.com)
- [Spring Boot 3.5 Reference](https://docs.spring.io/spring-boot/docs/3.5.x/reference/html/)
- [Next.js 15 Docs](https://nextjs.org/docs)
- [lightweight-charts](https://tradingview.github.io/lightweight-charts/)
