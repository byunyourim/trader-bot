# 에러 분석 기준

## 먼저 볼 것

- 실패한 명령
- 에러 메시지 원문
- stack trace 최상단과 최하단
- 최근 변경 파일
- 환경 변수 / 설정 변경 여부
- dependency version 변경 여부

## 자주 있는 원인

### TypeScript
- nullable mismatch
- 잘못된 generic inference
- stale generated type
- import path mismatch

### API
- request schema 불일치
- response shape 변경
- auth header 누락
- env 누락

### UI
- undefined state 접근
- async loading state 누락
- stale closure
- key mismatch

### DB
- migration 미적용
- nullable/required 불일치
- transaction 누락
