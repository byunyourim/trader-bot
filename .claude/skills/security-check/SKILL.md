---
name: security-check
description: 기능 구현 또는 PR에서 보안 위험을 검토할 때 사용한다. 인증, 권한, 입력 검증, secret 노출, 데이터 접근 범위, destructive action을 확인한다.
when_to_use: auth, permission, user data, payment, admin 기능, 외부 입력, webhook, file upload, API 변경을 검토할 때
version: 1.0.0
---

# security-check

목표: 실제 악용 가능한 보안/권한 문제를 찾는다.

## 워크플로우

```txt
1. 보호해야 할 자산 확인
2. actor와 permission 확인
3. 입력/출력 경계 확인
4. 데이터 접근 범위 확인
5. secret 노출 확인
6. 악용 시나리오 제시
7. 수정안 제시
```

## 우선순위

1. auth bypass
2. privilege escalation
3. user data leak
4. secret exposure
5. injection
6. unsafe destructive action
7. insecure webhook/file upload

## 규칙

- 과장하지 않는다.
- 실제 exploit path가 있는지 본다.
- 파일/endpoint/조건을 구체적으로 말한다.
- 보안 이슈와 일반 품질 이슈를 구분한다.
- 위험한 기능은 사용자 확인 없이 구현하지 않는다.

## 출력 형식

```md
## 보안 Verdict
pass | needs_fix | blocked

## Findings
### Critical
### High
### Medium
### Low

## 권장 수정
## 추가 검증
```

## 참고 문서

필요할 때만 읽는다.

- `references/security-checklist.md`
- `references/examples.md`
