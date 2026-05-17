# Backend — Claude 작업 지침

기본 패키지: `com.byunyourim.traderbot`

---

## 명령어

| 작업 | 명령 |
|---|---|
| 서버 기동 | `./gradlew bootRun` |
| 컴파일 확인 | `./gradlew compileJava` |
| 전체 테스트 | `./gradlew test` |
| 포맷 적용 | `./gradlew :backend:spotlessApply` |
| 포맷 검사 | `./gradlew :backend:spotlessCheck` |

---

## 기술 스택

| 분류 | 기술 |
|---|---|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 3.5 |
| HTTP 클라이언트 | Spring WebFlux (`WebClient`) |
| DB 접근 | Spring Data JPA (동기) |
| 캐시 | Spring Data Redis (Reactive) |
| 코드 생성 | Lombok |
| API 문서 | Springdoc OpenAPI (Swagger UI) |
| DB | PostgreSQL 16 |
| 캐시 서버 | Redis 7 |
| 브로커 | 한국투자증권 Open API (REST + WebSocket) |
| 코드 포맷 | Spotless + Eclipse formatter |

---

## 아키텍처 Invariant

### DDD 4-Layer

`backend/src/main/java/com/byunyourim/traderbot/` 바로 아래에는 **정확히 4개 패키지만** 둔다:

```
traderbot/
├── TraderBotApplication.java
├── presentation/    Controller, Scheduler 등 entry point
├── application/     UseCase, Handler (도메인 조합), 도메인 예외
├── domain/          순수 도메인 — entity, value object, port(interface), domain event
└── infra/           KIS 어댑터, JPA 구현, Redis, WebClient 설정
```

의존 방향: `presentation → application → domain` / `infra → domain`.
**domain은 어디에도 의존하지 않는다.**

컨텍스트(`account`, `market`, `order`, `strategy`)는 각 레이어 안에서 하위 패키지로 분리.

### Port + Adapter 패턴

```
domain/account/AccountQueryPort          ← 인터페이스
infra/kis/adapter/KisAccountQueryAdapter ← 구현체 (@Component)
```

**KIS 호출은 절대 application/presentation에서 직접 하지 말 것.** 항상 `infra/kis/` 어댑터를 거쳐서.

### 새 컨텍스트 추가 순서

1. `domain/{context}/` — Port 인터페이스, Entity, Value Object, Domain Event (record)
2. `infra/kis/adapter/Kis{Context}Adapter` — Port 구현체
3. `infra/persistence/{context}/` — JPA Repository, 영속화 어댑터
4. `application/{context}/` — Handler, 도메인 예외 클래스
5. `presentation/{context}/` — Controller, Request/Response, Swagger 어노테이션

### 도메인 모델

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
- Application Handler는 도메인 객체를 조합·조율하는 역할만
- 도메인 로직이 application 레이어에 누수되면 도메인 객체로 이동시킬 것
- 도메인 코드에 Spring·JPA·KIS 의존성을 들이지 말 것

### YAGNI

- 현재 요청에 필요한 기능만 구현한다.
- 3곳 이상 반복되기 전에는 일반화하지 않는다. (권한/validation/business rule은 2곳 반복이어도 분리 가능)
- 요청하지 않은 확장성, 옵션, 플러그인 구조를 만들지 않는다.
- 요청한 기능 하나만 구현한다. 다른 곳에서 개선할 부분이 보여도 **수정하지 말고 사용자에게 알리기만** 한다.

**만들지 말아야 하는 것:** interface · factory · strategy pattern · plugin system · generic utility · base class

### API 응답 컨벤션

별도 공통 wrapper 없이 **컨텍스트별 Response 객체를 직접 반환**한다.

```java
// presentation/account/AccountBalanceResponse.java  (package-private)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
class AccountBalanceResponse {
    @Schema(description = "총 평가금액", example = "10000000")
    BigDecimal totalAmount;
}
```

- 페이징/목록 응답은 `MultipleResponse<T>` 사용
- Response 클래스는 Controller와 같은 패키지에 위치 (package-private)
- 에러 응답: `ErrorResponse` record (`error`, `message`) — 컨트롤러에서 직접 만들지 말고 `ExceptionController`에 위임
- 모든 도메인 예외는 `ApplicationException` 상속, 컨트롤러에서 try/catch 금지
- Controller 메서드마다 `@Operation`, 파라미터마다 `@Schema` 필수

### 비동기는 ApplicationEvent로

비동기 흐름(KIS WebSocket 시세 수신, 주문 체결 후속 처리)은 **`ApplicationEventPublisher`로 도메인 이벤트를 발행**하고 `@EventListener` / `@Async` / `@TransactionalEventListener`로 받는다.

- 이벤트 정의: `domain/{context}/event/` 에 record로
- 트랜잭션 커밋 후 처리: `@TransactionalEventListener(phase = AFTER_COMMIT)`
- **단순 동기 응답에는 이벤트 패턴 적용 금지.**

### KIS Open API

- **토큰**: Redis 캐싱 (`kis:access-token`, TTL `expires_in - 60s`)
- **모의/실전 분기**: `KIS_PAPER_TRADING=true|false` — `tr_id` 접두사 달라짐 (모의: `V`/`J`)
- **WebSocket**: 단일 인스턴스 — `@PostConstruct` + `AtomicBoolean`으로 중복 구독 방지
- **금액 파싱**: `new BigDecimal(str)` — double 경유 절대 금지. 금액·수량 연산에 double/float 사용 금지
- **실패 처리**: `rt_cd != "0"` 이면 `KisResponseException(msg1)` throw

```
rt_cd  : "0" = 성공, 그 외 = 실패
msg_cd : 에러 코드
msg1   : 에러 메시지
output / output1 / output2 : 실제 데이터 (문자열)
```

### 코드 스타일

- **Lombok**: `@RequiredArgsConstructor`, `@Slf4j`, `@Getter`, `@Builder` OK. `@Data`는 엔티티 금지
- **`@FieldDefaults(level = AccessLevel.PRIVATE)`**: Response/Request 클래스에 사용
- **record 우선**: 불변 DTO·이벤트·value object → record
- **null 대신 `Optional`**: 도메인 메서드 반환 시
- **주석은 WHY가 필요할 때만**: WHAT은 식별자로 표현
- **리플렉션 사용 금지**: `java.lang.reflect` 직접 사용 금지 (사유 설명 → 대안 제시 → 동의 후에만)

**스트림 · 메서드 체인 포맷** — `.` 앞에서 줄바꿈하고, 이후 모든 `.`을 첫 번째 `.`의 열에 수직 정렬. 체인 2개 이상이면 한 줄 금지.

```java
// 금지
List<String> result = list.stream().filter(s -> s.startsWith("A")).map(String::toLowerCase).collect(toList());

// 올바름
List<String> result = list.stream()
                          .filter(s -> s.startsWith("A"))
                          .map(String::toLowerCase)
                          .collect(toList());

return Optional.ofNullable(value)
               .filter(v -> v > 0)
               .map(BigDecimal::valueOf)
               .orElseThrow(NotFoundException::new);
```

---

## 테스트 규칙

- **기능 구현 시 단위 테스트 필수** — 구현과 같은 PR에 포함
- **커버리지 80% 이상** 이어야 머지 가능
- 단위 테스트: Mockito로 외부 의존성(KIS 어댑터, DB) mock
- 통합 테스트: `@SpringBootTest` + Testcontainers (PostgreSQL, Redis)
- 테스트 클래스 위치: 대상 클래스와 동일한 패키지 구조 (`src/test/java/...`)

---

## 보안 관심사

- SQL Injection 방지: JPQL/네이티브 쿼리 작성 시 파라미터 바인딩 필수, 문자열 결합 쿼리 금지
- 민감 정보(KIS API 키, 비밀번호, 토큰) 소스 코드 하드코딩 금지 → `.env`로 관리
- `.env` 파일 커밋 금지
- 로그에 계좌번호, 잔고, 주문 상세 등 민감 정보 원문 출력 금지
- 로그 레벨: `ERROR`(장애) · `WARN`(주의) · `INFO`(주요 흐름) · `DEBUG`(디버깅)
- KIS API 호출 요청/응답은 INFO 로깅, 주문 체결·취소는 감사 로그 필수 (언제·무엇·결과)
- 예외 발생 시 스택 트레이스 포함 ERROR 로그 필수

---

## 환경변수 전파 체인

```
.env
  │  (docker compose --env-file .env)
  ▼
infra/docker-compose.yml  →  environment: 블록으로 컨테이너에 주입
  │  (Spring Boot가 OS 환경변수로 수신)
  ▼
backend/src/main/resources/application.yml
  │  ${KIS_BASE_URL}, ${KIS_APP_KEY} … 형태로 참조
  ▼
infra/kis/KisProperties (@ConfigurationProperties(prefix = "kis"))
  │  record 필드로 타입 안전하게 바인딩
  ▼
KisAuthService / KisRestClient / KisWebSocketClient
```

- 새 환경변수 추가 시: `.env.example` → `docker-compose.yml` → `application.yml` → `KisProperties` 순서로 모두 반영
- `KIS_PAPER_TRADING=false` 변경 시 실제 계좌 주문 발생 — 반드시 확인 후 변경

---

## git 커밋 컨벤션

### 커밋 · 푸시 규칙

- 커밋과 푸시는 **사용자가 명시적으로 요청할 때만** 수행한다.
- 커밋 요청이 오면 먼저 `./gradlew compileJava` 실행 → 빌드 정상이어야만 커밋 진행
- 테스트 코드가 포함된 경우 `./gradlew test`도 통과해야 커밋

### 메시지 형식

Conventional Commits (한국어):

```
타입: 제목 (#이슈번호)

- 어떤 로직을 어떻게 수정했는지 상세하게
- 변경 이유, 영향 범위 포함
```

타입: `feat` · `fix` · `refactor` · `test` · `docs` · `chore`

```
feat: KIS 잔고 조회 어댑터 구현 (#12)

- KisAccountQueryAdapter에서 inquire-balance 엔드포인트 응답 파싱
- output 필드에서 BigDecimal 변환 시 double 경유 없이 new BigDecimal(str) 사용
- rt_cd != "0" 이면 KisResponseException throw
```

### 브랜치 전략 (Git Flow)

```
main       ← 릴리즈 브랜치
develop    ← 통합 개발 브랜치
feature/*  ← 기능 개발 브랜치 (develop에서 분기)
```

- 새 기능: `develop`에서 `feature/{이슈번호}-{기능명}` 으로 분기
- 완료 후 `develop`으로 PR
- 릴리즈 준비: `develop` → `main` PR
