---
name: review-pr
description: PR, diff, 변경 파일, 커밋 전 코드 리뷰에 사용한다. 스타일 지적보다 버그, 회귀, 보안, 데이터 손실, 테스트 누락을 우선 검토한다.
when_to_use: pull request 리뷰, git diff 리뷰, 구현 후 검토, merge 전 위험 확인이 필요할 때
version: 1.0.0
---

# review-pr

목표: 의미 있는 위험만 찾아서 리뷰한다.

## 리뷰 우선순위

1. correctness bug
2. security / permission
3. data loss / migration risk
4. API breaking change + 사이드 이펙트 (변경 파일을 import하는 다른 모듈 영향)
5. performance regression
6. edge case
7. missing test / 커버리지 감소
8. 빌드 및 전체 테스트 통과 여부
9. maintainability

## 규칙

- 사소한 스타일 취향은 지적하지 않는다.
- 기존 프로젝트 컨벤션을 모르면 단정하지 않는다.
- 모든 지적은 파일, 문제, 영향, 수정안을 포함한다.
- 심각한 문제가 없으면 없다고 말한다.
- 변경 범위 밖의 문제는 "후속 작업"으로 분리한다.

## 출력 형식

```md
## Verdict
approve | comment | request_changes

## Critical
## High
## Medium
## Low
## Missing Tests
## Safe Follow-ups
```

## 참고 문서

필요할 때만 읽는다.

- `references/severity.md`
- `references/examples.md`
- `references/security-checklist.md`
