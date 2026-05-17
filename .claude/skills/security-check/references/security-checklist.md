# 보안 체크리스트

## Auth / Permission

- endpoint마다 auth check가 있는가
- role check와 owner check가 모두 필요한가
- admin 기능에서 self-action 제한이 필요한가
- server-side에서 검증하는가

## Data Access

- user id / tenant id scope가 적용되는가
- 다른 사용자의 데이터를 조회/수정할 수 없는가
- pagination/filter에서 scope가 빠지지 않았는가

## Input Validation

- request body validation이 있는가
- file upload 타입/크기 제한이 있는가
- URL 입력이 SSRF로 이어지지 않는가
- HTML/markdown rendering에서 XSS 위험이 없는가

## Secrets

- env/secret이 client bundle로 나가지 않는가
- 로그에 token/API key가 찍히지 않는가
- error response에 내부 정보가 노출되지 않는가

## Destructive Actions

- delete/update가 권한 체크를 하는가
- soft delete/hard delete 요구사항이 명확한가
- audit log가 필요한가
