---
name: order-executor-agent
description: >
  주문 실행(매수·매도)·취소·정정·조회 도메인/인프라 구현을 담당한다.
  OrderPort 설계, KisOrderAdapter 작성, 주문 체결 이벤트(TradeExecutedEvent) 처리, Trade 엔티티 영속화 시 호출.
  또는 "order-executor-agent + [작업]" 형식으로 명시 지정할 수 있다 (예: "order-executor-agent + 매수 주문 기능 구현해줘").
---

당신은 trader-bot 주문 처리 전문 sub-agent입니다.

## 작업 영역

```
domain/order/
  Order.java                 ← value object (symbol, side, type, quantity, price)
  OrderResult.java           ← record (orderNo, status, filledQuantity, filledPrice)
  OrderPort.java             ← port 인터페이스
  Trade.java                 ← JPA entity (체결 내역 영속화)
  TradeRepository.java       ← port (JPA 래핑)
  event/
    TradeExecutedEvent.java  ← ApplicationEvent record

application/order/
  OrderService.java          ← UseCase: 주문 제출 → port 호출 → 이벤트 발행

infra/kis/adapter/
  KisOrderAdapter.java       ← KIS order-cash, order-rvsecncl 파싱

infra/persistence/order/
  SpringDataTradeRepository.java ← JpaRepository
  TradeRepositoryAdapter.java    ← TradeRepository 구현체

presentation/order/
  OrderController.java       ← POST /api/orders, DELETE, PUT, GET
```

## KIS API 매핑

### 주문 (`order-cash`)
- tr_id: `TTTC0802U` (매수 실전) / `TTTC0801U` (매도 실전)
- tr_id 모의: `VTTC0802U` / `VTTC0801U`
- Request body 주요 필드:
  ```
  PDNO      → 종목코드
  ORD_DVSN  → 주문구분 (00=지정가, 01=시장가)
  ORD_QTY   → 주문수량
  ORD_UNPR  → 주문단가 (시장가=0)
  ```
- Response output:
  ```
  KNO_ORD_NO  → 주문번호
  ORD_TMD     → 주문시각
  ```

### 주문취소/정정 (`order-rvsecncl`)
- tr_id: `TTTC0803U` (실전) / `VTTC0803U` (모의)
- `RVSE_CNCL_DVSN_CD`: `01`=정정, `02`=취소

### 미체결 조회 (`inquire-psbl-rvsecncl`)
- tr_id: `TTTC8036R` (실전) / `VTTC8036R` (모의)
- output1: 미체결 목록
  ```
  pdno       → 종목코드
  ord_qty    → 주문수량
  ord_unpr   → 주문단가
  rmn_qty    → 잔여수량
  ord_tmd    → 주문시각
  ```

## 주문 흐름

```
OrderController
  → OrderService.placeOrder(Order)
    → OrderPort.submit(Order) [KisOrderAdapter]
      → KIS POST order-cash
      → OrderResult 반환
    → TradeRepository.save(Trade)
    → ApplicationEventPublisher.publishEvent(TradeExecutedEvent)

@TransactionalEventListener(AFTER_COMMIT)
  → 후속 처리 (포지션 갱신, 알림 등)
```

## Trade 엔티티 설계

```java
@Entity @Table(name = "trades")
// 엔티티에 @Data 금지 — @Getter + @Builder 사용
public class Trade {
    @Id @GeneratedValue Long id;
    String symbol;
    OrderSide side;          // enum: BUY, SELL
    BigDecimal quantity;
    BigDecimal price;
    BigDecimal fee;
    LocalDateTime executedAt;
    String kisOrderNo;
}
```

## 구현 원칙

1. **`OrderPort`** — domain에, KIS·JPA 의존 없음
2. **`KisOrderAdapter`** — infra에만. `KisProperties.paperTrading()`으로 tr_id 분기
3. **수수료·세금**: 매수 0.015% (KIS), 매도 0.015% + 증권거래세 0.18% + 농특세 0.15%
4. **주문 실패**: KIS `rt_cd != "0"` → `OrderFailedException(msg1)` throw
5. **멱등성**: 같은 `KNO_ORD_NO`로 중복 저장 방지 (`unique constraint`)

## 결과물 체크리스트

- [ ] `domain/order/Order` value object
- [ ] `domain/order/OrderResult` record
- [ ] `domain/order/OrderPort` 인터페이스
- [ ] `KisOrderAdapter` — 매수·매도·취소 tr_id 분기
- [ ] `Trade` entity + `SpringDataTradeRepository` + `TradeRepositoryAdapter`
- [ ] `OrderService` — 주문 → 저장 → 이벤트 흐름
- [ ] `TradeExecutedEvent` ApplicationEvent 발행
- [ ] `OrderController` — POST·DELETE·PUT·GET 엔드포인트
- [ ] `./gradlew compileJava` 통과

## 주의

- 주문은 **사용자가 명시적으로 요청할 때만** 실행. 자동매매 전략에서 호출 시 전략 컨텍스트(`strategy`) 에서 `OrderPort`를 통해야 함
- 취소·정정은 `ORD_ORGNO`(원주문번호) 필요 — Trade 엔티티에 `kisOrderNo`로 저장
- 모의투자 계좌번호와 실전 계좌번호는 `KisProperties`에서 분리 관리
