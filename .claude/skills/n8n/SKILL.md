---
name: n8n
description: >
  n8n 워크플로 관리 스킬.
  트리거: 워크플로우 만들어줘, 워크플로우 생성, n8n 자동화,
  노드 추가, 워크플로우 수정, n8n 에러, n8n 디버깅
when_to_use: >
  사용자가 n8n 워크플로우 생성·수정·디버깅·자격증명 관리를 요청할 때.
version: "1.0"
---

## 모듈 라우팅

| 요청 의도 | 모듈 파일 |
|-----------|-----------|
| 워크플로우 생성·수정 요청 | references/workflow-create.md |
| 노드 타입·표현식·JSON 구조 질문 | references/nodes-and-expressions.md |
| 에러 발생·디버깅 요청 | references/debugging.md |
| 자격증명·보안 설정 | references/credentials.md |

복합 요청 우선순위: **workflow-create → debugging → nodes-and-expressions → credentials**

## 공통 규칙

1. **노드 type 추측 금지** — 반드시 `search_nodes`로 검색 후 정확한 값 사용
2. **검증 후 생성** — `n8n_validate_workflow` 통과 전까지 `n8n_create_workflow` 호출 금지
3. **비활성 생성** — 워크플로우는 항상 `"active": false`로 생성, 활성화는 사용자가 직접
4. **credentials 하드코딩 금지** — API 키·비밀번호는 parameters 안에 직접 작성 금지

## MCP 도구 목록

| 도구 | 설명 |
|------|------|
| `mcp__n8n-mcp__search_nodes` | 노드 타입 검색 |
| `mcp__n8n-mcp__n8n_validate_workflow` | 워크플로우 구조 검증 |
| `mcp__n8n-mcp__n8n_create_workflow` | 워크플로우 생성 |
| `mcp__n8n-mcp__n8n_test_workflow` | 워크플로우 테스트 실행 |
| `mcp__n8n-mcp__n8n_get_workflow` | 워크플로우 조회 |
| `mcp__n8n-mcp__n8n_update_partial_workflow` | 워크플로우 부분 수정 |
| `mcp__n8n-mcp__n8n_list_workflows` | 워크플로우 목록 조회 |
| `mcp__n8n-mcp__n8n_executions` | 실행 이력 조회 |
