# 코드 컨벤션

프로젝트의 실제 코드가 이 문서보다 우선한다. 모르면 기존 파일을 먼저 읽는다.

## 반복 코드 분리 기준

| 상황 | 판단 |
|------|------|
| 3곳 이상 반복 | 분리 우선 검토 |
| 2곳 반복 — 일반 로직 | 분리하지 않는다 |
| 2곳 반복 — 권한·validation·business rule·금액·날짜 계산 | 분리할 수 있다 |
| 1곳에서만 사용 | 미래 확장만으로 추상화 금지 |

## 기본 원칙

- 기존 파일 구조와 naming을 우선한다.
- 새 abstraction은 중복이나 복잡도를 실제로 줄일 때만 만든다.
- public API, env 이름, DB schema는 꼭 필요할 때만 바꾼다.
- 에러 처리, 권한, 빈 상태, loading 상태를 빠뜨리지 않는다.

## 좋은 예 / 나쁜 예

### 기존 유틸 재사용

```ts
// 좋음
const user = await getCurrentUser();

// 나쁨 — 기존 auth 유틸 무시
const user = await fetch("/api/me").then(r => r.json());
```

### 명확한 이름

```ts
// 좋음
const activeSubscription = subscriptions.find(s => s.status === "active");

// 나쁨
const x = subscriptions.find(s => s.status === "active");
```

### UI와 비즈니스 로직 분리

```ts
// 좋음
const canDelete = canDeleteUser(currentUser, targetUser);

// 나쁨 — UI에 직접 권한 로직
{currentUser.role === "admin" && targetUser.id !== currentUser.id && (...)}
```
