---
name: refactor-module
description: 지저분하거나 큰 모듈, 중복 코드, 읽기 어려운 코드, 책임이 섞인 코드를 정리할 때 사용한다. 동작을 유지하면서 작은 단위로 리팩토링한다.
when_to_use: 레거시 코드 개선, 중복 제거, 큰 파일 분리, naming 정리, 책임 분리가 필요할 때
version: 1.0.0
---

# refactor-module

목표: 동작을 바꾸지 않고 구조와 가독성을 개선한다.

## 워크플로우

```txt
1. 리팩토링 목표 확인
2. 변경 경계 설정 + 사이드 이펙트 범위 파악
3. 기존 동작 파악
4. 작은 단계로 수정
5. 테스트/검증 (빌드 필수)
6. 결과 요약
```

## 규칙

- 동작 변경을 하지 않는다.
- public API를 유지한다.
- 관련 없는 cleanup을 섞지 않는다.
- speculative abstraction을 만들지 않는다.
- 리팩토링 범위를 작게 유지한다.
- 위험하면 먼저 테스트를 추가한다.

### 사이드 이펙트 차단 (필수)

리팩토링 전:
- 수정 대상 모듈을 import하는 모든 파일 파악
- export되는 함수/타입 목록 확인 — 이름·시그니처 변경 시 사용처 전부 수정

리팩토링 후 검증 순서 — **전부 통과해야 완료**:
1. `npm run build` — 타입 오류·컴파일 오류 확인 (실패 시 미완료)
2. `npm test` — 기존 테스트 전부 통과 (회귀 없음 확인)
3. 커버리지 감소 없는지 확인

## 리팩토링 대상

- 중복 로직
- 너무 큰 함수/컴포넌트
- 불명확한 이름
- UI와 비즈니스 로직 혼합
- 강한 coupling
- 반복되는 validation/error handling

## 출력 형식

```md
## 리팩토링 목표
## 변경 내용
## 동작 유지 근거
## 테스트 / 검증
## 리스크
```

## 참고 문서

필요할 때만 읽는다.

- `references/refactor-patterns.md`
- `references/verification.md`
- `references/examples.md`
- `references/anti-patterns.md`
- `references/code-conventions.md`
