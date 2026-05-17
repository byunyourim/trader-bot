# 워크플로우 생성 모듈

## 6단계 처리 플로우

"워크플로우 만들어줘" 요청이 오면 아래 순서를 반드시 지킨다.

| 단계 | 작업 | 도구 |
|------|------|------|
| 1. 요청 분석 | 트리거·처리 로직·출력 목표 파악. 모호하면 즉시 질문 | 대화 |
| 2. 노드 검색 | 필요한 노드 타입을 검색해 정확한 `type` 값 확인 | `search_nodes` |
| 3. JSON 구성 | 노드·connections JSON 초안 작성 (→ nodes-and-expressions.md 참조) | 내부 생성 |
| 4. 검증 | 구조 오류 사전 검증 | `n8n_validate_workflow` |
| 5. 생성 + 테스트 | 생성 후 테스트 실행, 실패 시 에러 분석 후 재시도 | `n8n_create_workflow` → `n8n_test_workflow` |
| 6. 결과 보고 | 워크플로우 ID, 노드 구성 요약, 활성화 방법 안내 | 대화 |

## 워크플로우 JSON 뼈대

```json
{
  "name": "워크플로우 이름",
  "active": false,
  "nodes": [
    {
      "id": "uuid-v4",
      "name": "노드이름",
      "type": "n8n-nodes-base.webhook",
      "typeVersion": 2,
      "position": [240, 300],
      "parameters": {}
    }
  ],
  "connections": {
    "노드이름": {
      "main": [[{ "node": "다음노드이름", "type": "main", "index": 0 }]]
    }
  },
  "settings": {
    "executionOrder": "v1",
    "saveManualExecutions": true
  }
}
```

## 핵심 규칙

- 노드 `id`: UUID v4 형식, 중복 불가
- `connections` 키 = 노드 `name`과 정확히 일치
- `typeVersion`: `search_nodes` 결과에서 확인한 최신 버전 사용
- 노드 간격 최소 `position` 차이 200px
- 노드 수 20개 초과 시 서브 워크플로우 분리 먼저 제안
