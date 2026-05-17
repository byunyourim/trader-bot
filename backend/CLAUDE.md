# Backend — Claude 작업 지침

기본 패키지: `com.byunyourim.traderbot`

---

## 아키텍처 — DDD 4-Layer

`backend/src/main/java/com/byunyourim/traderbot/` 바로 아래에는 **정확히 4개 패키지만** 둔다:

```
traderbot/
├── TraderBotApplication.java
├── presentation/    Controller, Scheduler 등 entry point
├── application/     UseCase, Service, Handler (도메인 조합), 도메인 예외
├── domain/          순수 도메인 — entity, value object, port(interface), domain event
└── infra/           KIS 어댑터, JPA 구현, Redis, WebClient 설정
```

의존 방향: `presentation → application → domain` / `infra → domain`.
**domain은 어디에도 의존하지 않는다.**

컨텍스트(`account`, `market`, `order`, `strategy`)는 각 레이어 안에서 하위 패키지로 분리.

**Port + 구현체 분리 패턴:**
```
domain/account/AccountQueryPort          ← 인터페이스
infra/kis/adapter/KisAccountQueryAdapter ← 구현체 (@Component)
```

**KIS 호출은 절대 application/presentation에서 직접 하지 말 것.** 항상 `infra/kis/` 어댑터를 거쳐서.

---

## 새 컨텍스트 추가 체크리스트

새 컨텍스트(예: `strategy`, `notification`)를 추가할 때 순서:

1. `domain/{context}/` — Port 인터페이스, Entity, Value Object, Domain Event (record)
2. `infra/kis/adapter/Kis{Context}Adapter` — Port 구현체
3. `infra/persistence/{context}/` — JPA Repository, 영속화 어댑터
4. `application/{context}/` — Handler/Service, 도메인 예외 클래스
5. `presentation/{context}/` — Controller, Request/Response, Swagger 어노테이션

---

## 현재 파일 맵

| 파일 | 상태 | 비고 |
|---|---|---|
| `infra/kis/KisAuthService` | 골격 | Redis 토큰 캐싱 |
| `infra/kis/KisRestClient` | 골격 | WebClient 래퍼 |
| `infra/kis/KisWebSocketClient` | 골격 | 단일 인스턴스 |
| `infra/kis/KisProperties` | 완성 | `@ConfigurationProperties` record |
| `infra/kis/adapter/KisAccountQueryAdapter` | 골격 | `inquire-balance` 매핑 |
| `domain/account/AccountBalance` | 골격 | 도메인 record |
| `domain/account/AccountQueryPort` | 골격 | port 인터페이스 |
| `application/account/AccountQueryService` | 골격 | |
| `presentation/account/AccountController` | 골격 | `GET /v1/accounts/balance` |
| `presentation/market/MarketController` | 골격 | quote·candles |
| `presentation/market/MarketDataScheduler` | 골격 | 주기적 시세 갱신 |
| `domain/market/event/MarketTickEvent` | 완성 | record |
| `domain/order/event/TradeExecutedEvent` | 완성 | record |
| `domain/order/Trade` | 골격 | JPA entity |
| `domain/order/TradeRepository` | 골격 | port |

**P1 미구현:** `AccountBalance` 파싱, `MarketQueryPort` + 어댑터, WS fan-out

---

## API 응답 컨벤션

### 성공 응답

별도 공통 wrapper 없이 **컨텍스트별 Response 객체를 직접 반환**한다.

```java
// presentation/account/AccountBalanceResponse.java
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
class AccountBalanceResponse {
    @Schema(description = "총 평가금액", example = "10000000")
    BigDecimal totalAmount;
    // ...
}
```

- 페이징/목록 응답은 `MultipleResponse<T>` 사용 (`error`, `message`, `data` 필드)
- Response 클래스는 Controller와 같은 패키지에 위치 (package-private)

### 에러 응답

`ErrorResponse` record (`error`, `message`, `data`) 사용. 컨트롤러에서 직접 만들지 말고 `ExceptionController`에서 처리.

```
{
  "error": "NOT_FOUND",
  "message": "계좌를 찾을 수 없습니다.",
  "data": null
}
```

### 예외 처리

- 모든 도메인 예외는 `ApplicationException`을 상속
- 컨텍스트별 예외: `application/{context}/AccountNotFoundException extends ApplicationException`
- 컨트롤러에서 try/catch로 응답 가공 금지 — `ExceptionController`에 위임
- `IllegalArgumentException` → 400, `ApplicationException` → 예외가 지정한 HttpStatus

### Swagger 어노테이션

Controller 메서드마다 `@Operation`, 파라미터마다 `@Schema`를 반드시 붙인다.

```java
@Operation(summary = "잔고 조회", description = "계좌 잔고 스냅샷 조회 API", parameters = {
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
})
@GetMapping
AccountBalanceResponse getBalance(...) { ... }
```

---

## 비동기는 ApplicationEvent로

비동기 흐름(KIS WebSocket 시세 수신, 주문 체결 후속 처리 등)은 **Spring `ApplicationEventPublisher`로 도메인 이벤트를 발행**하고 `@EventListener` / `@Async` / `@TransactionalEventListener`로 받는다.

- 이벤트 정의: `domain/{context}/event/` 에 record로
- 발행: infra 어댑터 또는 application service
- 트랜잭션 커밋 후 처리: `@TransactionalEventListener(phase = AFTER_COMMIT)`
- **단순 동기 응답에는 이벤트 패턴 적용 금지.** 비동기·팬아웃이 의미 있을 때만.

---

## KIS Open API

- **토큰**: Redis 캐싱 (`kis:access-token`, TTL `expires_in - 60s`)
- **모의/실전 분기**: `.env`의 `KIS_PAPER_TRADING=true|false` — `tr_id` 접두사 달라짐 (`V`/`J` = 모의)
- **`KisProperties`**: `@ConfigurationProperties(prefix = "kis")` record
- **WebSocket**: 단일 인스턴스 — `@PostConstruct` + `AtomicBoolean` 또는 `@ConditionalOnProperty`로 중복 구독 방지

**KIS 응답 공통 구조:**
```
rt_cd  : "0" = 성공, 그 외 = 실패
msg_cd : 에러 코드
msg1   : 에러 메시지
output / output1 / output2 : 실제 데이터 (문자열)
```

**금액 파싱 규칙:** `new BigDecimal(str)` — double 경유 절대 금지.
**실패 처리:** `rt_cd != "0"` 이면 `KisResponseException(msg1)` throw.

---

## 테스트 전략

- **기능 구현 시 단위 테스트 필수** — 구현과 같은 PR에 포함
- **커버리지 80% 이상** 이어야 머지 가능
- 단위 테스트: Mockito로 외부 의존성(KIS 어댑터, DB) mock
- 통합 테스트: `@SpringBootTest` + Testcontainers (PostgreSQL, Redis) 사용
- 테스트 클래스 위치: 대상 클래스와 동일한 패키지 구조 (`src/test/java/...`)

---

## YAGNI 원칙

- 현재 요청에 필요한 기능만 구현한다.
- 미래에 필요할 것 같은 기능은 만들지 않는다.
- 실제 중복이 생기기 전에는 추상화하지 않는다.
- 3곳 이상 반복되기 전에는 일반화하지 않는다.
- 단, 권한/validation/business rule은 2곳 반복이어도 분리할 수 있다.
- 요청하지 않은 확장성, 옵션, 플러그인 구조를 만들지 않는다.

**만들지 말아야 하는 것 (아직 필요 없는):**
interface · factory · strategy pattern · plugin system · config option · generic utility · base class · service layer

**만들어도 되는 것:**
- 지금 요청 기능에 직접 필요한 코드
- 현재 이미 3곳 이상 반복되는 로직
- 권한/validation/business rule처럼 실수하면 위험한 공통 로직
- 테스트를 위해 분리해야 하는 순수 함수
- 기존 프로젝트가 이미 사용하는 패턴

---

## 도메인 모델 원칙

**빈혈 도메인 모델(Anemic Domain Model) 금지.**

도메인 객체는 데이터 컨테이너가 아니라 비즈니스 로직을 직접 담아야 한다.

```java
// 금지 — Service에 로직, 도메인은 getter/setter만
class OrderService {
    void execute(Order order) {
        if (order.getStatus() == PENDING) order.setStatus(EXECUTED);
    }
}

// 올바름 — 로직이 도메인 안에
class Order {
    void execute() {
        if (this.status != PENDING) throw new IllegalStateException();
        this.status = EXECUTED;
    }
}
```

- 유효성 검사, 상태 전이, 불변식(invariant) 보호는 도메인 객체 메서드 안에
- Application Service/Handler는 도메인 객체를 조합·조율하는 역할만
- 도메인 로직이 application 레이어에 누수되면 도메인 객체로 이동시킬 것

---

## 코드 스타일

- **Lombok**: `@RequiredArgsConstructor`, `@Slf4j`, `@Getter`, `@Builder` OK. `@Data`는 엔티티 금지.
- **`@FieldDefaults(level = AccessLevel.PRIVATE)`**: Response/Request 클래스에 사용
- **record 우선**: 불변 DTO·이벤트·value object → record
- **null 대신 `Optional`**: 도메인 메서드 반환 시
- **주석은 WHY가 필요할 때만**: WHAT은 식별자로 표현
- **scaffolding 요청 시 구현 코드를 채우지 말 것**: 빈 시그니처만

---

## 개발 원칙

### 리플렉션 사용 금지

- `java.lang.reflect` 패키지의 직접 사용을 금지한다 (Spring 프레임워크 내부 사용은 제외).
- 리플렉션이 불가피한 경우: 사유 설명 → 대안 제시 → 사용자 동의 후에만 사용한다.

### 코드 절제

- 불필요한 상속, 의존성, 설정, 코드 추가를 금지한다.
- 과도한 추상화보다는 적절한 수준의 중복을 허용한다.

### 작업 범위 엄수

- 요청한 기능 하나만 구현한다. 요청하지 않은 파일은 건드리지 않는다.
- 작업 중 다른 곳에서 개선할 부분이 보여도 **수정하지 말고 사용자에게 알리기만** 한다.
- 리팩토링, 포맷팅, 이름 변경 등 부수적 변경을 요청 없이 포함하지 않는다.

### 커밋 · 푸시 규칙

- 커밋과 푸시는 **사용자가 명시적으로 요청할 때만** 수행한다. 작업 완료 후 자동으로 커밋하지 않는다.
- 커밋 요청이 오면 먼저 `./gradlew compileJava`를 실행한다. 빌드가 정상이어야만 커밋을 진행한다.
- 테스트 코드가 포함된 변경의 경우 `./gradlew test`도 통과해야 커밋한다.
- 빌드 실패 시 커밋을 중단하고 에러 내용을 사용자에게 보고한다.

### 보안 우선

- SQL Injection 방지: JPQL/네이티브 쿼리 작성 시 파라미터 바인딩 필수, 문자열 결합 쿼리 금지
- 민감 정보(KIS API 키, 비밀번호, 토큰) 소스 코드 하드코딩 금지 → `.env`로 관리
- `.env` 파일 커밋 금지
- 로그에 계좌번호, 잔고, 주문 상세 등 민감 정보 원문 출력 금지

### 로깅

- 로그 레벨 구분: `ERROR`(장애) · `WARN`(주의) · `INFO`(주요 흐름) · `DEBUG`(디버깅)
- KIS API 호출 요청/응답은 추적 가능한 수준으로 INFO 로깅
- 주문 체결·취소 등 주요 이벤트는 감사 로그 필수 (언제, 무엇을, 결과)
- 예외 발생 시 스택 트레이스 포함 ERROR 로그 필수

### 문서 동기화

- 아키텍처·레이어 구조·기술 스택 변경 시 `backend/CLAUDE.md`와 `README.md`를 함께 업데이트한다.
- 문서와 실제 코드 간 불일치를 발견하면 즉시 수정한다.

---

## 커밋 메시지 규칙

Conventional Commits (한국어) 형식:

```
타입: 제목 (#이슈번호)

- 어떤 로직을 어떻게 수정했는지 상세하게
- 변경 이유, 영향 범위 포함
```

타입: `feat` · `fix` · `refactor` · `test` · `docs` · `chore`

예시:
```
feat: KIS 잔고 조회 어댑터 구현 (#12)

- KisAccountQueryAdapter에서 inquire-balance 엔드포인트 응답 파싱
- output 필드에서 BigDecimal 변환 시 double 경유 없이 new BigDecimal(str) 사용
- rt_cd != "0" 이면 KisResponseException throw
```

---

## 브랜치 전략 (Git Flow)

```
main       ← 릴리즈 브랜치
develop    ← 통합 개발 브랜치
feature/*  ← 기능 개발 브랜치 (develop에서 분기)
```

- 새 기능: `develop`에서 `feature/{이슈번호}-{기능명}` 으로 분기
- 완료 후 `develop`으로 PR
- 릴리즈 준비: `develop` → `main` PR

---

## 작업 시 주의

- `traderbot/` 바로 아래에 새 패키지를 만들지 말 것 (4-layer 외 패키지 금지)
- 도메인 코드에 Spring·JPA·KIS 의존성을 들이지 말 것 (port로 추상화)
- 컨트롤러에서 예외를 직접 catch해서 응답 가공 금지
- 금액·수량 연산에 double/float 사용 금지 → `BigDecimal`만 사용
- API 명세: `backend/docs/api.md`
