# 테스트 패턴

## Unit Test

사용:
- 순수 함수
- validation
- business rule
- formatter/parser

## Integration Test

사용:
- API route
- DB query + service
- auth/permission flow

## UI Test

사용:
- form interaction
- loading/error state
- user-visible behavior

## E2E Test

사용:
- critical path
- 결제/가입/로그인 같은 핵심 흐름

## 케이스 선택

기본:
1. happy path
2. failure path
3. boundary case
4. permission case
5. regression case
