# 보안 리뷰 빠른 체크 (PR 리뷰용)

> PR 리뷰 중 보안 이슈를 빠르게 스캔하는 체크리스트.
> 심층 보안 분석이 필요하면 security-check 스킬을 사용한다.

## 즉시 확인

- [ ] 인증이 필요한 endpoint에 auth check가 있는가
- [ ] 권한이 필요한 작업에 permission check가 있는가
- [ ] user-scoped 데이터가 scope 없이 전체 조회되지 않는가
- [ ] 입력값 validation이 있는가 (request body, params)
- [ ] secret/API key가 코드·로그·응답에 하드코딩되지 않았는가
- [ ] destructive action(삭제, 초기화)에 권한 체크가 있는가
- [ ] 외부 URL 입력을 그대로 fetch하는 SSRF 패턴은 없는가

## Critical 발견 시

보안 이슈는 Verdict를 `request_changes`로 올리고 Critical 섹션에 기록한다.
심층 검토가 필요하면 사용자에게 `/security-check` 실행을 제안한다.
