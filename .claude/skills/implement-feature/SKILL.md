---
name: implement-feature
description: 기능을 새로 개발하거나 기존 기능을 수정할 때 사용한다. 모호성 확인, 관련 코드 탐색, 최소 구현, 테스트/검증, 결과 요약을 수행한다.
when_to_use: 새 기능 추가, 기존 기능 변경, UI/API/비즈니스 로직 구현 요청이 있을 때
version: 1.0.0
---

# implement-feature

목표: 기존 아키텍처와 컨벤션을 유지하면서 요청된 기능을 가장 작고 안전한 변경으로 구현한다.

## 워크플로우

```txt
1. 요청 이해
2. 모호하면 질문
3. 관련 코드만 탐색
4. 최소 구현 계획
5. 구현
6. 테스트 / 검증
7. 결과 요약
```

## 1. 요청 이해

빠르게 파악한다.

- 원하는 기능
- 사용자에게 보이는 동작
- 영향 받는 화면 / API / 모듈
- 데이터 / 인증 / 권한 영향
- 테스트 필요 여부

이 단계에서 코드를 수정하지 않는다.

## 2. 모호하면 질문

다음이 불명확하면 먼저 질문한다.

- 정확한 기대 동작
- 대상 화면 / API / 모듈
- 권한 규칙
- 삭제 / 결제 / 개인정보 / 보안 처리
- 외부 서비스 연동 세부사항

사소한 구현 디테일은 질문하지 않는다. 합리적으로 가정하고 결과에 명시한다.

형식:

```md
## 확인 필요
1. ...
2. ...
```

## 3. 관련 코드만 탐색

탐색 순서:

1. 비슷한 기존 기능
2. route / page / controller
3. service / domain logic
4. schema / types / contracts
5. tests
6. config는 필요한 경우만

충분한 맥락을 얻으면 멈춘다. 전체 repo를 훑지 않는다.

## 4. 최소 구현 계획

짧게만 작성한다.

```md
## 계획
- 수정 파일:
- 구현 내용:
- 테스트:
- 리스크:
```

긴 설계 문서나 JSON 계획은 만들지 않는다.

## 5. 구현 규칙

- 기존 패턴을 우선 재사용한다.
- 새 파일보다 기존 파일 수정을 우선한다.
- 새 dependency는 꼭 필요할 때만 추가한다.
- 관련 없는 리팩토링은 하지 않는다.
- public API는 꼭 필요할 때만 바꾼다.
- loading / empty / error / permission 상태를 고려한다.
- 기존 architecture 스타일을 유지한다.

### 사이드 이펙트 차단 (필수)

구현 전 아래를 확인한다:

- 수정 파일을 import하는 다른 모듈 파악
- 변경하는 함수/타입의 호출 위치 전수 확인
- public API·export 시그니처가 바뀌면 모든 사용처 함께 수정
- 공유 유틸/서비스 수정 시 다른 기능에 미치는 영향 명시

## 6. 테스트 / 검증 (모두 필수)

테스트가 필요한 경우:

- business logic 변경
- API 동작 변경
- auth / permission 변경
- persistence 변경
- security 영향
- bug-prone flow

검증 순서 — **전부 통과해야 완료**:

1. focused unit/integration test
2. typecheck (`npm run build`에 포함)
3. **빌드** (`npm run build`) — 실패 시 작업 미완료, 원인 수정 후 재시도
4. 전체 테스트 (`npm test`) — 기존 테스트 회귀 없는지 확인
5. **커버리지 확인** — 새로 추가한 코드에 대응하는 테스트가 있는지, 기존 대비 커버리지가 감소하지 않는지 확인

## 7. 결과 요약

```md
## 요약
## 변경 파일
## 테스트 / 검증
## 가정
## 리스크
```

## 최소 라우팅

- auth 기능: middleware, session, token, permission 먼저 확인
- DB 기능: schema, migration, repository, validation 먼저 확인
- API 기능: route, validator, request/response contract 먼저 확인
- UI 기능: 기존 component, shared UI, state, loading/error 먼저 확인
- async 기능: retry, cache invalidation, race condition 먼저 확인

## 참고 문서

필요할 때만 읽는다.

- `references/project-structure.md` — 아키텍처·핵심 모듈·수정 주의사항
- `references/routing.md` — 기능 유형별 탐색 순서 (auth/DB/API/UI)
- `references/code-conventions.md` — 반복 코드 분리 기준·명명·패턴
- `references/anti-patterns.md` — 피해야 할 패턴
- `references/verification.md` — 사이드 이펙트·빌드·커버리지 체크리스트
- `references/examples.md` — 구현 흐름 예시
