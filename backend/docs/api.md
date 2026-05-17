# Trader Bot — API 명세

KIS Open API 기반 자동매매·모니터링 대시보드의 backend API 목록.

- Base URL: `http://localhost:8080`
- 인증: (TBD — 단일 사용자 가정, 추후 결정)
- 응답 포맷: JSON
- 실시간 푸시: WebSocket

## 우선순위 정의

| Tag | 의미 |
|---|---|
| **P1** | MVP — 모니터링 대시보드 1차 |
| **P2** | 2차 — 수동 주문 기능 |
| **P3** | 3차 — 자동매매·전략 |

---

## REST API

### Account — 계좌·잔고

| 우선 | 메서드 | 경로 | 설명 | KIS API |
|---|---|---|---|---|
| P1 | GET | `/api/accounts/balance` | 잔고 스냅샷 (예수금 + 평가금 + 보유종목) | `inquire-balance` |
| P1 | GET | `/api/accounts/positions` | 보유 종목 리스트 | `inquire-balance` |
| P2 | GET | `/api/accounts/cash` | 매수 가능 금액 | `inquire-psbl-order` |
| P3 | GET | `/api/accounts/profit` | 일/누적 손익 | DB 집계 |

### Market — 시세

| 우선 | 메서드 | 경로 | 설명 | KIS API |
|---|---|---|---|---|
| P1 | GET | `/api/market/quote/{symbol}` | 현재가 조회 | `inquire-price` |
| P1 | GET | `/api/market/candles/{symbol}?interval=D` | 캔들(일봉/분봉) — lightweight-charts용 | `inquire-daily-itemchartprice` |
| P2 | GET | `/api/market/orderbook/{symbol}` | 호가창 | `inquire-asking-price` |
| P2 | GET | `/api/market/symbols/search?q=` | 종목 검색 | KIS 종목 마스터 / DB |

### Order — 주문

| 우선 | 메서드 | 경로 | 설명 | KIS API |
|---|---|---|---|---|
| P2 | POST | `/api/orders` | 매수/매도 주문 | `order-cash` |
| P2 | DELETE | `/api/orders/{orderNo}` | 주문 취소 | `order-rvsecncl` |
| P2 | PUT | `/api/orders/{orderNo}` | 정정 주문 | `order-rvsecncl` |
| P2 | GET | `/api/orders?status=pending` | 미체결 주문 조회 | `inquire-psbl-rvsecncl` |
| P2 | GET | `/api/orders/history` | 체결 내역 (일별) | `inquire-daily-ccld` |

### Strategy — 자동매매 전략

| 우선 | 메서드 | 경로 | 설명 |
|---|---|---|---|
| P3 | GET | `/api/strategies` | 등록된 전략 목록 |
| P3 | POST | `/api/strategies` | 전략 생성 (조건·종목·수량) |
| P3 | PUT | `/api/strategies/{id}` | 전략 수정 |
| P3 | DELETE | `/api/strategies/{id}` | 전략 삭제 |
| P3 | POST | `/api/strategies/{id}/enable` | 활성화 |
| P3 | POST | `/api/strategies/{id}/disable` | 비활성화 |
| P3 | POST | `/api/strategies/{id}/backtest` | 백테스트 실행 |

### System — 시스템

| 우선 | 메서드 | 경로 | 설명 |
|---|---|---|---|
| P1 | GET | `/actuator/health` | 헬스체크 |
| P2 | GET | `/api/system/kis-status` | KIS 연결 상태·토큰 TTL |

---

## WebSocket

| 우선 | 경로 | 설명 |
|---|---|---|
| P1 | `/ws/market/ticks?symbols=...` | 실시간 체결가 푸시 (KIS WS → 클라이언트 fan-out) |
| P2 | `/ws/market/orderbook?symbols=...` | 실시간 호가 푸시 |
| P3 | `/ws/orders` | 내 주문 체결 알림 |

---

## P1 (MVP) 구현 순서

1. `GET /api/accounts/balance` — 잔고 (현재 절반 구현, `AccountBalance` 도메인 파싱만 추가)
2. `GET /api/market/quote/{symbol}` — 현재가
3. `GET /api/market/candles/{symbol}` — 차트 데이터 (frontend `PriceChart` 연동)
4. `WS /ws/market/ticks` — 실시간 푸시

위 4개로 모니터링 대시보드 1차 완성.

---

## DDD 컨텍스트 매핑

| API 컨텍스트 | 패키지 |
|---|---|
| Account | `presentation/account` · `application/account` · `domain/account` |
| Market | `presentation/market` · `domain/market` |
| Order | `presentation/order` (TBD) · `domain/order` |
| Strategy | `presentation/strategy` (TBD) · `domain/strategy` (TBD) |

KIS 연동은 모두 `infra/kis/` 어댑터에서 처리하고, port 인터페이스는 각 도메인 패키지에 둔다.
