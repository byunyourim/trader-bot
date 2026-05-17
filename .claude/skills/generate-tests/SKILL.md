---
name: generate-tests
description: 테스트를 추가하거나 개선할 때 사용한다. 기존 테스트 프레임워크와 스타일을 따르며, 실제 회귀를 잡는 고가치 테스트를 만든다.
when_to_use: 테스트 작성, test coverage 개선, 버그 regression test 추가, API/UI/business logic 검증이 필요할 때
version: 1.0.0
---

# generate-tests

목표: 적은 테스트로 실제 회귀를 잘 잡는다.

## 워크플로우

```txt
1. 테스트할 동작 정의
2. 기존 테스트 패턴 확인
3. high-value case 선택
4. 테스트 작성
5. 가장 좁은 명령으로 검증
```

## 테스트 우선순위

- business logic
- API contract
- auth / permission
- persistence
- bug regression
- edge case
- critical UI behavior

## 규칙

- 새 테스트 프레임워크를 도입하지 않는다.
- 기존 helper/mock을 재사용한다.
- 구현 세부사항보다 동작을 검증한다.
- snapshot 남용을 피한다.
- flaky test를 만들지 않는다.

### 커버리지 확인 (필수)

테스트 작성 후 반드시 확인:
- 새로 추가한 코드 경로에 대응하는 테스트가 있는가
- happy path / failure path / edge case를 각각 커버했는가
- 기존 커버리지 대비 감소 없는가
- `npm run build` + `npm test` 전부 통과하는가 (실패 시 미완료)

## 출력 형식

```md
## 테스트 전략
## 추가/수정한 테스트
## 커버한 케이스
## 커버하지 못한 것
## 실행 명령
## 커버리지 변화
```

## 참고 문서

필요할 때만 읽는다.

- `references/test-patterns.md`
- `references/examples.md`
